package initiate.lambda

import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.s3.AmazonS3
import initiate.appian.exceptions.AppianException
import initiate.appian.models.UpdateDocumentStatusRequest
import initiate.appian.services.AppianHelperService
import initiate.appian.services.AppianService
import initiate.appian_foia.models.QueryFoiaRequest
import initiate.appian_foia.models.QueryFoiaResponse
import initiate.appian_foia.models.UpdateDocumentTypeFoia
import initiate.appian_foia.services.AppianFoiaHelperService
import initiate.appian_foia.services.AppianFoiaService
import initiate.aws_integration.handlers.ExceptionHandler
import initiate.aws_integration.modules.AWSModule
import initiate.aws_integration.services.MessageService
import initiate.common.constants.SubmissionConstants
import initiate.common.modules.EnvironmentModule
import initiate.common.utils.HttpUtils.bodyToString
import initiate.hyperscience.models.Submission
import initiate.hyperscience.models.SubmissionRequest
import initiate.hyperscience.services.HyperScienceService
import initiate.mappers.FoiaDocumentSubmissionRequestMapper
import org.apache.http.HttpStatus
import java.io.IOException

class InitiateServiceFoiaUnknown(
    amazonS3: AmazonS3,
    private val appianService: AppianService,
    private val environment: EnvironmentModule,
    hyperScienceService: HyperScienceService,
    private val hyperScienceInputMessageService: MessageService,
    private val outputMessageService: MessageService
) : InitiateService(
    amazonS3,
    appianService,
    environment,
    hyperScienceService,
    hyperScienceInputMessageService,
    outputMessageService
) {
    override fun initiateHyperScience(input: Any, context: Context, documentType: String?): String {
        println("Input: $input")
        return try {
            logBeforeFunction("Query Foia Appian for Docs with Unknown docTypes")
            val ocrReadyDocuments = queryAppianForIndexingReadyDocuments()
            println("Foia Appian query doc result: " + ocrReadyDocuments.packets)
            logAfterFunction("Query Foia Appian for Docs with Unknown docTypes")
            val returnMessage = mapAppianDocumentsToHsRequests(ocrReadyDocuments)
                .mapNotNull { removeDocumentsThatLackUploads(it) }
                .mapNotNull { removeDocumentsThatAreTooLarge(it) }
                .mapNotNull { determineIfSubmissionExistsForDocument(it) }
                .map {
                    if (it is SubmissionRequest) moveSubmissionRequestFileToS3(it)
                    else it
                }
                .onEach {
                    if (it is SubmissionRequest) putMessageOnHyperScienceQueue(it)
                    else putMessageOnOutputQueue(it as Submission)
                }
                .onEach { updateDocumentToInProgress(it) }
                .printSQSMessageCount()
            returnMessage
        } catch (ase: AmazonServiceException) {
            ExceptionHandler.logAmazonServiceException(ase)
            throw ase
        } catch (ace: AmazonClientException) {
            ExceptionHandler.logAmazonClientException(ace)
            throw ace
        } catch (e: Exception) {
            println("Error Message: ${e.message} of class ${e.javaClass}")
            throw e
        }
    }

    private fun queryAppianForIndexingReadyDocuments(): QueryFoiaResponse {
        return try {
            val queryFoiaRequest = buildQueryFoiaRequestUnknown()
            println("Query Document Request: $queryFoiaRequest")
            val call = (appianService as AppianFoiaService).queryForDocuments(queryFoiaRequest)
            println("Query Doc Request body: " + call.request().bodyToString())
            println("Query Doc Request url: " + call.request().url())
            val response = call.execute()
            when {
                response.isSuccessful -> {
                    val responseBody = response.body()
                    println("Appian Response Body after successful call is: $responseBody")
                    val queryDocumentResponse =
                        AWSModule.objectMapper.readValue(responseBody!!.string(), QueryFoiaResponse::class.java)
                    println("Appian Body after successful call is: $queryDocumentResponse")
                    queryDocumentResponse
                }
                response.code() == HttpStatus.SC_NOT_FOUND -> {
                    println("Appian query documents returned 404, returning empty response")
                    val rawErrorBody = response.errorBody()
                    val errorBody = if (rawErrorBody != null) rawErrorBody.string() else "no error body"
                    println("Querying FOIA Appian for ready documents returned code ${HttpStatus.SC_NOT_FOUND} with errorBody: $errorBody")
                    QueryFoiaResponse()
                }
                else -> {
                    val code = response.code()
                    val rawErrorBody = response.errorBody()
                    val errorBody = if (rawErrorBody != null) rawErrorBody.string() else "no error body"
                    throw AppianException(
                        "Querying Appian for ready documents returned code "
                                + code
                                + "with errorBody: "
                                + errorBody
                    )
                }
            }
        } catch (ioException: IOException) {
            System.err.println(ioExceptionMessage(ioException))
            throw AppianException(ioException)
        }
    }

    private fun buildQueryFoiaRequestUnknown(): QueryFoiaRequest {
        return QueryFoiaRequest(
            docStatus = docStatusIndexing ?: SubmissionConstants.READY_FOR_INDEXING,
            batchSize = batchSize,
            startIndex = null
        )
    }

    private val docStatusIndexing: String?
        get() = environment.getSystemEnvironmentVariable("DOC_STATUS_INDEXING")

    private fun mapAppianDocumentsToHsRequests(
        ocrReadyDocuments: QueryFoiaResponse
    ): Sequence<SubmissionRequest> {
        return FoiaDocumentSubmissionRequestMapper(ocrReadyDocuments, machineOnly, isDocTypeKnown = false)
            .toDomain()
            .asSequence()
            .onEach { println("$it") }
    }

    private val newStatus = SubmissionConstants.READY_FOR_OCR
    private val newDocType = SubmissionConstants.DOC_TYPE_CORRESPONDENCE

    override fun removeDocumentsThatLackUploads(req: SubmissionRequest): SubmissionRequest? {
        if (req.hasEcmDocIdOfZero()) {

            System.err.println(
                "Request ${req.externalId} has no document uploaded. " +
                        "Setting to status $newStatus with docType $newDocType"
            )
            AppianFoiaHelperService.callAppianToUpdateDocumentType(
                UpdateDocumentTypeFoia(req.parsedExternalId(), SubmissionConstants.DOC_TYPE_CORRESPONDENCE),
                appianService as AppianFoiaService
            )
            AppianHelperService.callAppianToUpdateDocumentStatus(
                UpdateDocumentStatusRequest(req.parsedExternalId(), newStatus),
                appianService
            )
            return null
        }
        return req
    }

    override fun removeDocumentsThatAreTooLarge(req: SubmissionRequest): SubmissionRequest? {
        if (req.isFileSizeTooLarge()) {
            System.err.println(
                "Request ${req.externalId} has fileSize of ${req.metadata.fileSize} which is too large. " +
                        "Setting to status $newStatus with docType $newDocType"
            )
            AppianFoiaHelperService.callAppianToUpdateDocumentType(
                UpdateDocumentTypeFoia(req.parsedExternalId(), SubmissionConstants.DOC_TYPE_CORRESPONDENCE),
                appianService as AppianFoiaService
            )
            AppianHelperService.callAppianToUpdateDocumentStatus(
                UpdateDocumentStatusRequest(req.parsedExternalId(), newStatus),
                appianService
            )
            return null
        }
        return req
    }
}
