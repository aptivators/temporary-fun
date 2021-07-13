package initiate.appian.models

data class QueryDocumentResponse(
    val packets: Set<Packet> = setOf()
) {
    val documentsReturned: Int by lazy { this.packets.map { it.documents.size }.sum() }
}
