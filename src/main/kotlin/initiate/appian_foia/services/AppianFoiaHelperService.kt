package initiate.appian_foia.services

import initiate.appian.exceptions.AppianException
import initiate.appian.services.AppianHelperService
import initiate.appian_foia.models.UpdateDocumentClassification
import initiate.appian_foia.models.UpdateDocumentTypeFoia
import initiate.common.utils.HttpUtils.bodyToString
import java.io.IOException

object AppianFoiaHelperService {
    @JvmStatic
    fun callAppianToUpdateDocumentClassification(
        updateDocumentClassification: UpdateDocumentClassification, appianService: AppianFoiaService
    ) {
        try {
            println("update document classification $updateDocumentClassification")
            val call = appianService.updateDocumentClassification(updateDocumentClassification)
            println("Update Doc Status Classification body: " + call.request().bodyToString())
            println("Update Doc Status Classification url: " + call.request().url())
            val response = call.execute()
            println("$response")
            if (!response.isSuccessful) {
                val errorBody = if (response.errorBody() != null) response.errorBody()!!.string() else "no error body"
                throw AppianException(
                    "Updating document classification in Appian returned code "
                            + response.code()
                            + " for document "
                            + updateDocumentClassification.documentId
                            + " with errorBody: "
                            + errorBody
                )
            }
        } catch (ioException: IOException) {
            System.err.println("Caught ioException with message: " + ioException.message)
            throw AppianException(ioException)
        }
        println("Updated document classification successfully")
    }

    @JvmStatic
    fun callAppianToUpdateDocumentType(
        updateDocumentTypeFoia: UpdateDocumentTypeFoia, appianService: AppianFoiaService
    ) {
        val response = try {
            println("update document type $updateDocumentTypeFoia")
            val call = appianService.updateDocumentType(updateDocumentTypeFoia)
            println("Update Doc Type body: " + call.request().bodyToString())
            println("Update Doc Type url: " + call.request().url())
            val response = call.execute()
            println("$response")
            if (!response.isSuccessful) {
                AppianHelperService.handleUnsuccessfulAppianResponse(
                    response,
                    "Updating document type",
                    updateDocumentTypeFoia.documentId!!
                )
            }
            response
        } catch (ioException: IOException) {
            System.err.println("Caught ioException with message: " + ioException.message)
            throw AppianException(ioException)
        }
        println("Updated document type successfully with response: $response")
    }
}
