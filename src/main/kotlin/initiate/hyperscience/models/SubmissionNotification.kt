package initiate.hyperscience.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class SubmissionNotification(
    @JsonProperty("id")
    val id: Int? = null,
    @JsonProperty("external_id")
    val externalId: String? = null,
    @JsonProperty("state")
    val state: String? = null,
    @JsonProperty("substate")
    val substate: String? = null,
    @JsonProperty("halted")
    val halted: Boolean = false,
    @JsonProperty("exceptions")
    val exceptions: Set<String>? = null,
    @JsonProperty("start_time")
    val startTime: String? = null,
    @JsonProperty("complete_time")
    val completeTime: String? = null,
    @JsonProperty("metadata")
    val metadata: Metadata? = null,
    @JsonProperty("output")
    val output: Submission? = null
) {
    constructor(submission: Submission) :
            this(
                id = submission.id,
                externalId = submission.externalId,
                state = submission.state,
                substate = submission.substate,
                halted = submission.halted,
                exceptions = submission.exceptions,
                startTime = submission.startTime,
                completeTime = submission.startTime,
                metadata = submission.metadata,
                output = submission
            )

    val parsedExternalId: Int?
        get() = externalId?.replace("\\D".toRegex(), "")?.toInt()

    @JsonIgnore
    fun hasExternalId(): Boolean {
        return externalId != null
    }

    @JsonIgnore
    fun printHelpfulInfo() {
        println("Submission id: $id . External/Doc id: $parsedExternalId . EcmDocId: ${metadata?.ecmDocId} . RequestId: ${metadata?.requestId}")
    }
}