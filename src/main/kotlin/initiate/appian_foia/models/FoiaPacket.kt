package initiate.appian_foia.models

import com.fasterxml.jackson.annotation.JsonProperty

data class FoiaPacket(
    @JsonProperty("startIndex")
    val startIndex: Int? = null,
    @JsonProperty("batchSize")
    val batchSize: Int? = null,
    @JsonProperty("data")
    val data: Set<Data> = setOf()
)