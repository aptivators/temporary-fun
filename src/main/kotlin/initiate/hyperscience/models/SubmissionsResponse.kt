package initiate.hyperscience.models

data class SubmissionsResponse(
    val count: Int? = null,
    val next: String? = null,
    val previous: String? = null,
    val results: Set<Submission> = setOf()
)
