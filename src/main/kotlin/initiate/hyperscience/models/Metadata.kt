package initiate.hyperscience.models

data class Metadata(
    val packetId: Int? = null,
    val batchId: Int? = null,
    val vetFileId: String? = null,
    val vetSsn: String? = null,
    val documentId: Int? = null,
    val ecmDocId: String? = null,
    val docType: String? = null,
    var s3location: String? = null,
    val machineOnly: Boolean = false,
    val vetFirstName: String? = null,
    val vetLastName: String? = null,
    val requestId: Int? = null,
    val isFoia: Boolean = false,
    val signatureFieldImageUrl: String? = null,
    val repSignatureFieldImageUrl: String? = null,
    val signaturePageImageUrls: List<String> = listOf(),
    val originalDocType: String? = null,
    val fileSize: Int? = null
)
