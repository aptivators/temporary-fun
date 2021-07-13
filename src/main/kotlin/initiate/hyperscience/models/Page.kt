package initiate.hyperscience.models

import com.fasterxml.jackson.annotation.JsonProperty

data class Page(

    @JsonProperty("id")
    val id: Int? = null,

    @JsonProperty("state")
    val state: String? = null,

    @JsonProperty("substate")
    val substate: String? = null,

    @JsonProperty("exceptions")
    val exceptions: Set<String>? = null,

    @JsonProperty("page_type")
    val pageType: String? = null,

    @JsonProperty("file_page_number")
    val filePageNumber: Int? = null,

    @JsonProperty("submission_page_number")
    val submissionPageNumber: Int? = null,

    @JsonProperty("layout_page_number")
    val layoutPageNumber: Int? = null,

    @JsonProperty("submitted_filename")
    val submittedFileName: String? = null,

    @JsonProperty("image_url")
    val imageUrl: String? = null,

    @JsonProperty("corrected_image_url")
    val correctedImageUrl: String? = null,

    @JsonProperty("identifiers")
    val identifiers: Set<Identifier>? = null
)