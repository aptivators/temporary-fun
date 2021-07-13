package initiate.hyperscience.models

import com.fasterxml.jackson.annotation.JsonProperty

data class FieldDefinitionAttributes(
    @JsonProperty("required")
    val required: Boolean? = null,

    @JsonProperty("data_type")
    val dataType: String? = null,
    @JsonProperty("multiline")
    val multiline: Boolean? = null,

    @JsonProperty("consensus_required")
    val consensusRequired: Boolean? = null,

    @JsonProperty("supervision_override")
    val supervisionOverride: String? = null
)