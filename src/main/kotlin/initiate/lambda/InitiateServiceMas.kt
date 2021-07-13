package initiate.lambda

import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.s3.AmazonS3
import initiate.appian.exceptions.AppianException
import initiate.appian.models.QueryDocumentRequest
import initiate.appian.models.QueryDocumentResponse
import initiate.appian.services.AppianService
import initiate.aws_integration.handlers.ExceptionHandler
import initiate.aws_integration.modules.AWSModule
import initiate.aws_integration.services.MessageService
import initiate.common.modules.EnvironmentModule
import initiate.common.utils.HttpUtils.bodyToString
import initiate.hyperscience.models.Submission
import initiate.hyperscience.models.SubmissionRequest
import initiate.hyperscience.services.HyperScienceService
import initiate.mappers.DocumentSubmissionRequestMapper
import java.io.IOException

class InitiateServiceMas(
    amazonS3: AmazonS3,
    private val appianService: AppianService,
    environment: EnvironmentModule,
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
        println("docType: ${documentType!!}")
        return try {
            logBeforeFunction("Query Appian for Docs")
            val ocrReadyDocuments = queryAppianForOcrReadyDocuments(documentType)
            println("Appian query doc result is: ${ocrReadyDocuments.packets}")
            logAfterFunction("Query Appian for Docs")
            val returnMessage = mapAppianDocumentsToHsRequests(ocrReadyDocuments)
                .mapNotNull { removeDocumentsThatLackUploads(it) }
                .map { determineIfSubmissionExistsForDocument(it) }
                .map {
                    if (it is SubmissionRequest) moveSubmissionRequestFileToS3(it)
                    else it
                }
                .onEach {
                    if (it is SubmissionRequest) putMessageOnHyperScienceQueue(it)
                    else putMessageOnOutputQueue(it as Submission)
                }
                .onEach { updateDocumentToInProgress(it) }
                .printSQSMessageCount(documentType)
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

    fun queryAppianForOcrReadyDocuments(
        documentType: String, startIndex: Int? = null
    ): QueryDocumentResponse {
        return try {
            val queryDocumentRequest = buildQueryDocumentRequest(documentType, startIndex)
            println("Query Document Request: $queryDocumentRequest")
            val call = appianService.queryForDocuments(queryDocumentRequest)
            println("Query Doc Request body: " + call.request().bodyToString())
            println("Query Doc Request url: " + call.request().url())
            val response = call.execute()
            if (response.isSuccessful) {
                val responseBody = response.body()
                println("Appian Response Body after successful call is: $responseBody")
                val queryDocumentResponse =
                    AWSModule.objectMapper.readValue(responseBody!!.string(), QueryDocumentResponse::class.java)
                println("Appian Body after successful call is: $queryDocumentResponse")
                queryDocumentResponse
            } else {
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
        } catch (ioException: IOException) {
            System.err.println(ioExceptionMessage(ioException))
            throw AppianException(ioException)
        }
    }

    private fun buildQueryDocumentRequest(documentType: String, startIndex: Int?): QueryDocumentRequest {
        return QueryDocumentRequest(
            docType = documentType,
            docStatus = docStatus,
            isLite = isLite,
            batchSize = batchSize,
            startIndex = startIndex
        )
    }

    private fun mapAppianDocumentsToHsRequests(
        ocrReadyDocuments: QueryDocumentResponse
    ): Sequence<SubmissionRequest> {
        return DocumentSubmissionRequestMapper(ocrReadyDocuments, machineOnly)
            .toDomain()
            .onEach { println("$it") }
    }
}
