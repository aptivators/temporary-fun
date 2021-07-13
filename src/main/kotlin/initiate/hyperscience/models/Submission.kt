package initiate.hyperscience.models

import com.fasterxml.jackson.annotation.JsonProperty
import initiate.hyperscience.traits.ExternalIdAble

data class Submission(
    @JsonProperty("id")
    val id: Int? = null,

    @JsonProperty("external_id")
    override val externalId: String? = null,
    @JsonProperty("state")
    val state: String? = null,
    @JsonProperty("substate")
    val substate: String? = null,
    @JsonProperty("halted")
    val halted: Boolean = false,
    @JsonProperty("exceptions")
    val exceptions: Set<String> = setOf(),

    @JsonProperty("start_time")
    val startTime: String? = null,

    @JsonProperty("complete_time")
    val completeTime: String? = null,

    @JsonProperty("supervision_url")
    val supervisionUrl: String? = null,
    @JsonProperty("metadata")
    val metadata: Metadata? = null,
    @JsonProperty("data_deleted")
    val dataDeleted: Boolean = false,

// By default, this endpoint will not return the following properties of the Submission object:
// documents, document_folders, unassigned_pages, and files_submitted.
    @JsonProperty("files_submitted")
    val filesSubmitted: Set<String>? = null,

    @JsonProperty("submission_files")
    val submissionFiles: Set<SubmissionFile>? = null,

//  @JsonProperty("document_folders")
//  private final Set<DocumentFolder> documentFolders;
    @JsonProperty("documents")
    val documents: Set<Document> = setOf(),

    @JsonProperty("unassigned_pages")
    val unassignedPages: Set<Page>? = emptySet()
) : ExternalIdAble(externalId) {

}
