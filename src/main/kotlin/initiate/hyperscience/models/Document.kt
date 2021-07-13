package initiate.hyperscience.models

import com.fasterxml.jackson.annotation.JsonProperty
import initiate.common.constants.SubmissionConstants

data class Document(
    @JsonProperty("id")
    val id: Int? = null,
    @JsonProperty("state")
    val state: String? = null,
    @JsonProperty("substate")
    val substate: String? = null,
    @JsonProperty("exceptions")
    val exceptions: Set<String>? = null,

    @JsonProperty("start_time")
    val startTime: String? = null,

    @JsonProperty("complete_time")
    val completeTime: String? = null,
    @JsonProperty("priority")
    val priority: Int? = null,

    @JsonProperty("layout_uuid")
    val layoutUuid: String? = null,

    @JsonProperty("layout_name")
    val layoutName: String? = null,

    @JsonProperty("layout_tags")
    val layoutTags: Set<String> = setOf(),

    @JsonProperty("layout_version_uuid")
    val layoutVersionUuid: String? = null,

    @JsonProperty("layout_version_name")
    val layoutVersionName: String? = null,

    @JsonProperty("document_folders")
    val documentFolders: Set<String>? = null,

    @JsonProperty("supervision_url")
    val supervisionUrl: String? = null,

    @JsonProperty("document_type")
    val documentType: String? = null,
    @JsonProperty("type")
    val type: String? = null,
    @JsonProperty("metadata")
    val metadata: Metadata? = null,
    @JsonProperty("pages")
    val pages: Set<Page>? = setOf(),

    @JsonProperty("document_fields")
    val documentFields: Set<Field?>? = setOf()

    // unassignedPages
) {
    fun is05382020(): Boolean {
        return this.layoutTags.distinct()
            .filter { it == SubmissionConstants.LAYOUT_TAG_0538 || it == SubmissionConstants.YEAR_2020 }
            .count() == 2
    }
}
