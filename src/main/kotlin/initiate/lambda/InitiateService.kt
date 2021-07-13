package initiate.lambda

import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import initiate.appian.exceptions.AppianException
import initiate.appian.models.UpdateDocumentStatusRequest
import initiate.appian.services.AppianHelperService
import initiate.appian.services.AppianService
import initiate.aws_integration.handlers.ExceptionHandler
import initiate.aws_integration.services.MessageService
import initiate.aws_integration.services.implementations.MessageServiceImpl
import initiate.common.constants.SubmissionConstants
import initiate.common.modules.EnvironmentModule
import initiate.common.utils.HttpUtils.bodyToString
import initiate.hyperscience.exceptions.HyperScienceException
import initiate.hyperscience.models.Submission
import initiate.hyperscience.models.SubmissionNotification
import initiate.hyperscience.models.SubmissionRequest
import initiate.hyperscience.services.HyperScienceService
import initiate.hyperscience.traits.ExternalIdAble
import org.apache.http.HttpStatus
import java.io.IOException
import java.io.InputStream

abstract class InitiateService(
    private val amazonS3: AmazonS3,
    private val appianService: AppianService,
    private val environment: EnvironmentModule,
    private val hyperScienceService: HyperScienceService,
    private val hyperScienceInputMessageService: MessageService,
    private val outputMessageService: MessageService
) {
    abstract fun initiateHyperScience(input: Any, context: Context, documentType: String? = null): String

    protected open fun removeDocumentsThatLackUploads(req: SubmissionRequest): SubmissionRequest? {
        if (req.hasEcmDocIdOfZero()) {
            val newStatus = req.getFailureStatus()
            System.err.println("Request ${req.externalId} has no document uploaded. Setting to $newStatus")
            AppianHelperService.callAppianToUpdateDocumentStatus(
                UpdateDocumentStatusRequest(req.parsedExternalId(), newStatus),
                appianService
            )
            return null
        }
        return req
    }

    protected open fun removeDocumentsThatAreTooLarge(req: SubmissionRequest): SubmissionRequest? {
        if (req.isFileSizeTooLarge()) {
            val newStatus = req.getFailureStatus()
            System.err.println("Request ${req.externalId} has fileSize of ${req.metadata.fileSize} which is too large. Setting to $newStatus")
            AppianHelperService.callAppianToUpdateDocumentStatus(
                UpdateDocumentStatusRequest(req.parsedExternalId(), newStatus),
                appianService
            )
            return null
        }
        return req
    }

    protected fun determineIfSubmissionExistsForDocument(req: SubmissionRequest): ExternalIdAble {
        val shouldSendDupsToUpdate = environment.getSystemEnvironmentVariable("SHOULD_SEND_DUPS_TO_UPDATE")?.toBoolean()
        println("SHOULD_SEND_DUPS_TO_UPDATE is $shouldSendDupsToUpdate")
        if (shouldSendDupsToUpdate == true) {
            val submission = getSubmission(req.externalId)
            if (submission != null) {
                println("Error - Request ${req.externalId} has already been processed in HyperScience. Setting to ${SubmissionConstants.OCR_IN_PROGRESS}")
                println("Error submitting request to HyperScience  for ${req.externalId}")
                return submission
            }
        }
        return req
    }

    private fun getSubmission(externalId: String): Submission? {
        val call = hyperScienceService.getSubmissionByExternalId(externalId)
        println("getSubmissionByExternalId Request body: " + call.request().bodyToString())
        println("getSubmissionByExternalId Request url: " + call.request().url())
        val response = call.execute()
        return if (response.isSuccessful) {
            val querySubmissionResponse = response.body()
            println("getSubmissionByExternalId Body after successful call is: $querySubmissionResponse")
            println("$externalId was created at ${querySubmissionResponse?.startTime}")
            querySubmissionResponse
        } else if (response.code() == HttpStatus.SC_NOT_FOUND) {
            println("$externalId does not exist")
            null
        } else {
            val rawErrorBody = response.errorBody()
            val code = response.code()
            System.err.println("getSubmissionByExternalId got error with code: $code")
            throw HyperScienceException(
                "getSubmissionByExternalId returned code $code with errorBody: $rawErrorBody"
            )
        }
    }

    protected fun moveSubmissionRequestFileToS3(req: SubmissionRequest): SubmissionRequest {
        val bucketName = environment.getSystemEnvironmentVariable("S3_BUCKET_NAME")
        val key = environment.getSystemEnvironmentVariable("S3_FOLDER_PATH")
        logBeforeFunction("Moving Appian Documents to S3")
        if (req.file.none { url: String -> url.startsWith("s3://") }) {
            println("Doc missing s3 url, downloading from Appian to put in s3")
            try {
                logBeforeFunction("Download doc from Appian")
                val responseInputStream = downloadDocFromAppian(req)
                logAfterFunction("Download doc from Appian")
                val omd = ObjectMetadata()
                omd.contentType = "application/octet-stream"
                val name = "${req.metadata.docType}-${req.parsedExternalId()}-${req.metadata.ecmDocId}.pdf"
                amazonS3.putObject(bucketName, key + name, responseInputStream, omd)
                //            responseInputStream.close();
                val s3Url = "s3://$bucketName/$key$name"
                println("s3 url = $s3Url")
                val updatedMetadata = req.metadata
                updatedMetadata.s3location = s3Url
                req.metadata = updatedMetadata
                req.file = setOf(s3Url)
                logAfterFunction("Moving Appian Documents to S3")
                return req
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
        } else {
            println("Doc has s3 url")
        }
        return req
    }

    private fun downloadDocFromAppian(req: SubmissionRequest): InputStream {
        return try {
            val call = appianService.downloadDocument(req.metadata.ecmDocId)
            println("Download Doc Request url: " + call.request().url())
            val response = call.execute()
            if (response.isSuccessful) {
                val body = response.body()
                println("Made call to download doc")
                body!!.byteStream()
            } else {
                val rawErrorBody = response.errorBody()
                System.err.println("Call to Download Doc unsuccessful with code: " + response.code())
                throw AppianException(
                    "Downloading doc from Appian for ready documents returned code "
                            + response.code()
                            + "with errorBody: "
                            + rawErrorBody
                )
            }
        } catch (ioException: IOException) {
            System.err.println(ioExceptionMessage(ioException))
            throw AppianException(ioException)
        }
    }

    protected fun putMessageOnHyperScienceQueue(submissionRequest: SubmissionRequest) {
        println("Putting submission request with id ${submissionRequest.externalId} on hyperscience queue")
        hyperScienceInputMessageService.putMessageOnQueue(submissionRequest)
    }

    protected fun putMessageOnOutputQueue(submission: Submission) {
        println("Putting existing submission with id ${submission.externalId} on output queue")
        outputMessageService.putMessageOnQueue(SubmissionNotification(submission))
    }

    protected fun updateDocumentToInProgress(submission: ExternalIdAble) {
        println("Updating document to in progress with docId ${submission.externalId}")
        AppianHelperService.callAppianToUpdateDocumentStatus(
            updateDocumentStatusRequest = UpdateDocumentStatusRequest(
                submission.parsedExternalId(),
                SubmissionConstants.OCR_IN_PROGRESS
            ),
            appianService = appianService
        )
    }

    protected fun Sequence<ExternalIdAble>.printSQSMessageCount(documentType: String? = "unknown"): String {
        return this.groupingBy { it.javaClass.kotlin }
            .eachCount()
            .onEach { println("Submission type count: ${it.key} has ${it.value}") }
            .mapKeys {
                (when (it.key.simpleName) {
                    "Submission" -> outputMessageService
                    else -> hyperScienceInputMessageService
                } as MessageServiceImpl).sqsUrl
            }.map {
                "| Successfully sent ${it.value} messages to ${it.key} for document type $documentType"
            }
            .onEach { println(it) }
            .joinToString(" ")
            .ifBlank {
                "| Sent 0 messages to ${(hyperScienceInputMessageService as MessageServiceImpl).sqsUrl} and " +
                        "${(outputMessageService as MessageServiceImpl).sqsUrl} for document type $documentType"
            }
    }

    protected fun logBeforeFunction(functionName: String) {
        println("Entering the method for $functionName")
    }

    protected fun logAfterFunction(functionName: String) {
        println("Exited the method for $functionName successfully")
    }

    protected fun ioExceptionMessage(ioException: IOException): String {
        return "| Caught ioException with message: ${ioException.message}"
    }

    protected fun SubmissionRequest.isFileSizeTooLarge(): Boolean {
        return metadata.fileSize ?: 0 > maxFileSize
    }

    protected val docStatus: String?
        get() = environment.getSystemEnvironmentVariable("DOC_STATUS")

    protected val batchSize: Int
        get() {
            val batchSize = environment.getSystemEnvironmentVariable("BATCH_SIZE")
            return batchSize?.toInt() ?: 2
        }

    protected val isLite: Boolean
        get() = java.lang.Boolean.parseBoolean(environment.getSystemEnvironmentVariable("IS_LITE"))

    protected val machineOnly: Boolean
        get() = java.lang.Boolean.parseBoolean(environment.getSystemEnvironmentVariable("MACHINE_ONLY"))

    protected val maxFileSize: Int
        get() = environment.getSystemEnvironmentVariable("MAX_FILE_SIZE")?.toInt() ?: 1850924
}
