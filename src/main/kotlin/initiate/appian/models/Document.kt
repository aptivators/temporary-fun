package initiate.appian.models

import com.fasterxml.jackson.annotation.JsonProperty

data class Document(
    @JsonProperty("docId")
    val docId: Int? = null,
    @JsonProperty("packetId")
    val packetId: Int? = null,
    @JsonProperty("ecmDocId")
    val ecmDocId: String? = null,
    @JsonProperty("docStatus")
    val docStatus: String? = null,
    @JsonProperty("docTypeCode")
    val docTypeCode: String? = null,
    @JsonProperty("docName")
    val docName: String? = null,
    @JsonProperty("docURL")
    val docUrl: String? = null,
    @JsonProperty("createdDate")
    val createdDate: String? = null,
    @JsonProperty("updatedDate")
    val updatedDate: String? = null,
    @JsonProperty("fileSize")
    val fileSize: Int? = null
)
