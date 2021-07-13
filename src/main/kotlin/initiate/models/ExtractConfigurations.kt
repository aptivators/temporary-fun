package initiate.models

import com.fasterxml.jackson.annotation.JsonProperty

data class ExtractConfigurations(
    @JsonProperty("docType")
    val docType: String? = null,
    @JsonProperty("docTypeList")
    val docTypeList: List<String>? = null
)
