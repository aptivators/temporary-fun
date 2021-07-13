package initiate.mappers

import com.amazonaws.util.CollectionUtils
import initiate.appian.models.Document
import initiate.appian.models.Packet
import initiate.appian.models.QueryDocumentResponse
import initiate.hyperscience.models.Metadata
import initiate.hyperscience.models.SubmissionRequest
import java.util.*

class DocumentSubmissionRequestMapper(
    private val queryDocumentResponse: QueryDocumentResponse?,
    private val machineOnly: Boolean
) {

    fun toDomain(): Sequence<SubmissionRequest> {
        if (queryDocumentResponse == null ||
            CollectionUtils.isNullOrEmpty(queryDocumentResponse.packets)
        ) {
            println("Query to Appian returned 0 results")
            return emptySequence()
        }
        println("Mapping Appian Query Document Response to Hyperscience input queue message")
        return queryDocumentResponse.packets.asSequence().flatMap {
            it.documents.asSequence().map(mapDocumentToSubmissionRequest(it))
        }
    }

    private fun mapDocumentToSubmissionRequest(packet: Packet): (Document?) -> SubmissionRequest {
        return { document: Document? ->
            println("Document fileSize is ${document?.fileSize}")
            SubmissionRequest(
                hashSetOf(document?.docUrl ?: "missing"),
                getMetaData(packet, document),
                document?.docId.toString(),
                machineOnly
            )
        }
    }

    private fun getMetaData(packet: Packet, document: Document?): Metadata {
        return Metadata(
            packetId = packet.packetId,
            batchId = packet.batchId,
            vetFileId = packet.vetFileId,
            vetSsn = packet.vetSsn,
            documentId = document?.docId,
            ecmDocId = document?.ecmDocId,
            docType = document?.docTypeCode,
            s3location = document?.docUrl,
            machineOnly = machineOnly,
            vetFirstName = packet.vetFirstName?.uppercase(Locale.getDefault()),
            vetLastName = packet.vetLastName?.uppercase(Locale.getDefault()),
            requestId = null,
            isFoia = false
        )
    }
}
