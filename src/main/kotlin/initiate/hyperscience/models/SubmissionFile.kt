package initiate.hyperscience.models

import com.fasterxml.jackson.annotation.JsonProperty

data class SubmissionFile(
    @JsonProperty("name")
    val name: String? = null,

    @JsonProperty("upload_type")
    val uploadType: String? = null,
    @JsonProperty("url")
    val url: String? = null
)