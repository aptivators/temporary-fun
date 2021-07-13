package initiate.appian.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class QueryDocumentRequest(
    @JsonProperty("packetID")
    val packetId: Int? = null,
    val docId: Int? = null,
    val docType: String? = null,
    val docStatus: String? = null,
    val source: String? = null,
    val isLite: Boolean? = false,
    val batchSize: Int? = null,
    val startIndex: Int? = null,
    // MM-DD-YYYY
    val startDate: String? = null,
    // MM-DD-YYYY
    val endDate: String? = null
)
