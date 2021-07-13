package initiate.hyperscience.models

import com.fasterxml.jackson.annotation.JsonProperty

data class Transcription(
    @JsonProperty("raw")
    val raw: String? = null,
    @JsonProperty("normalized")
    val normalized: String? = null,
    @JsonProperty("source")
    val source: String? = null,
    @JsonProperty("data_deleted")
    val dataDeleted: String? = null,
    @JsonProperty("user_transcribed")
    val userTranscribed: String? = null,
    @JsonProperty("row_index")
    val rowIndex: String? = null
) {
    override fun toString(): String {
        return "Transcription(raw=${
            raw?.replace(
                '\n',
                ' '
            )
        }, normalized=$normalized, source=$source, dataDeleted=$dataDeleted, userTranscribed=$userTranscribed, rowIndex=$rowIndex)"
    }
}
