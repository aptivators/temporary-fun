package initiate.hyperscience.models

import com.fasterxml.jackson.annotation.JsonProperty

data class Identifier(
    @JsonProperty("transcription")
    private val transcription: Transcription? = null,

    @JsonProperty("image_url")
    private val imageUrl: String? = null
)