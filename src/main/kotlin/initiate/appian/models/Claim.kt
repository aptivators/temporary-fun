package initiate.appian.models

data class Claim(
    val claimId: Int? = null,
    val trackedItems: Set<TrackedItem>? = null
)

