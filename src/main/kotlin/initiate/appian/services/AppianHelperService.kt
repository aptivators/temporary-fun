package initiate.appian.services

import initiate.appian.exceptions.AppianException
import initiate.appian.exceptions.DocumentNotFoundException
import initiate.appian.models.DocumentExtractRequest
import initiate.appian.models.UpdateDocumentStatusRequest
import initiate.appian.models.UpdateDocumentType
import initiate.common.utils.HttpUtils.bodyToString
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException

object AppianHelperService {
    @JvmStatic
    fun callAppianToUpdateDocumentStatus(
        updateDocumentStatusRequest: UpdateDocumentStatusRequest,
        appianService: AppianService
    ) {
        try {
            println("update document status request $updateDocumentStatusRequest")
            val call = appianService.updateDocumentStatus(updateDocumentStatusRequest)
            println("Update Doc Status Request body: " + call.request().bodyToString())
            println("Update Doc Status Request url: " + call.request().url())
            val response = call.execute()
            println("$response")
            if (!response.isSuccessful) {
                handleUnsuccessfulAppianResponse(
                    response,
                    "Updating document status",
                    updateDocumentStatusRequest.documentId!!
                )
            }
        } catch (ioException: IOException) {
            System.err.println("Caught ioException with message: " + ioException.message)
            throw AppianException(ioException)
        }
        println("Updated document status successfully")
    }

    @JvmStatic
    fun callAppianToUpdateDocumentType(
        updateDocumentType: UpdateDocumentType, appianService: AppianService
    ) {
        val response = try {
            println("update document type $updateDocumentType")
            val call = appianService.updateDocumentType(updateDocumentType)
            println("Update Doc Type body: " + call.request().bodyToString())
            println("Update Doc Type url: " + call.request().url())
            val response = call.execute()
            println("$response")
            if (!response.isSuccessful) {
                handleUnsuccessfulAppianResponse(response, "Updating document type", updateDocumentType.documentId!!)
            }
            response
        } catch (ioException: IOException) {
            System.err.println("Caught ioException with message: " + ioException.message)
            throw AppianException(ioException)
        }
        println("Updated document type successfully with response: $response")
    }

    @JvmStatic
    fun callAppianToSaveDocumentExtract(
        documentExtractRequest: DocumentExtractRequest,
        appianService: AppianService
    ) {
        try {
            val call = appianService.insertExtractedData(documentExtractRequest)
            println("Save Doc Extract Request body: " + call.request().bodyToString())
            println("Save Doc Extract Request url: " + call.request().url())
            val response = call.execute()
            println("$response")
            if (!response.isSuccessful) {
                handleUnsuccessfulAppianResponse(
                    response,
                    "Insert Extracted Fields",
                    documentExtractRequest.documentId!!
                )
            }
        } catch (ioException: IOException) {
            System.err.println("Caught ioException with message: " + ioException.message)
            throw AppianException(ioException)
        }
        println("Saved Document Extract successfully")
    }

    fun handleUnsuccessfulAppianResponse(response: Response<ResponseBody?>, callType: String, documentId: Int) {
        val errorBody = if (response.errorBody() != null) response.errorBody()!!.string() else "no error body"
        val code = response.code()
        System.err.println("$callType got error with code: $code")
        if (response.code() == 404) {
            throw DocumentNotFoundException(
                "$callType in Appian returned code $code"
                        + " for document $documentId with errorBody: $errorBody. Stopping processing for this document"
            )
        } else {
            throw AppianException(
                "$callType in Appian returned code $code for document $documentId with errorBody: $errorBody"
            )
        }
    }
}
