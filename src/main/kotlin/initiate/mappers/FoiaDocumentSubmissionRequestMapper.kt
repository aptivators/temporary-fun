package initiate.mappers

import com.amazonaws.util.CollectionUtils
import initiate.appian_foia.models.Data
import initiate.appian_foia.models.QueryFoiaResponse
import initiate.common.constants.SubmissionConstants
import initiate.hyperscience.models.Metadata
import initiate.hyperscience.models.SubmissionRequest

class FoiaDocumentSubmissionRequestMapper(
    private val queryFoiaResponse: QueryFoiaResponse?,
    private val machineOnly: Boolean,
    private val isDocTypeKnown: Boolean = true
) {

    fun toDomain(): List<SubmissionRequest> {
        if (queryFoiaResponse == null || CollectionUtils.isNullOrEmpty(queryFoiaResponse.packets)) {
            println("Query to Foia Appian returned 0 results")
            return emptyList()
        }
        println(
            "Mapping Appian Query Document Response to Hyperscience foia request input queue message"
        )
        return queryFoiaResponse.packets.map { mapDocumentToSubmissionRequest(it) }
    }

    private fun mapDocumentToSubmissionRequest(data: Data): SubmissionRequest {
        println("Foia Document fileSize is ${data.fileSize}")
        return SubmissionRequest(
            hashSetOf(data.docUrl ?: "missing"),
            getMetaData(data),
            makeFoiaExternalId(data.docId),
            machineOnly
        )
    }

    private fun makeFoiaExternalId(docId: Int?): String {
        return "${SubmissionConstants.FOIA_STRING}$docId"
    }

    private fun getMetaData(data: Data): Metadata {
        return Metadata(
            batchId = data.batchId,
            vetFileId = data.vetFileId,
            vetSsn = data.vetSsn,
            documentId = data.docId,
            ecmDocId = data.ecmDocId,
            docType = if (isDocTypeKnown) data.docTypeCode else null,
            s3location = data.docUrl,
            machineOnly = machineOnly,
            vetFirstName = data.vetFirstName,
            vetLastName = data.vetLastName,
            requestId = data.requestId,
            isFoia = true,
            fileSize = data.fileSize
        )
    }
}
