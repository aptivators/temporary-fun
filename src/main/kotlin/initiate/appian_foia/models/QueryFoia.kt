package initiate.appian_foia.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class QueryFoiaRequest(
    @JsonProperty("docType")
    val docTypeCode: String? = null,
    @JsonProperty("docId")
    val docId: Int? = null,
    @JsonProperty("docStatus")
    val docStatus: String? = null,
    @JsonProperty("batchSize")
    val batchSize: Int? = null,
    @JsonProperty("startIndex")
    val startIndex: Int? = null,
    @JsonProperty("ocrEngine")
    val ocrEngine: String? = null,
    @JsonProperty("startDate") // MM-DD-YYYY
    val startDate: String? = null,
    @JsonProperty("endDate") // MM-DD-YYYY
    val endDate: String? = null,
    @JsonProperty("queryInfo")
    val queryInfo: Boolean = true,
    @JsonProperty("fetchFileSize")
    val fetchFileSize: Boolean = true
)
