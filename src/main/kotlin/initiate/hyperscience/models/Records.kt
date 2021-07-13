package initiate.hyperscience.models

import com.amazonaws.services.sqs.model.Message
import com.fasterxml.jackson.annotation.JsonProperty

data class Records(
    @JsonProperty("Records")
    val records: Set<Message> = setOf()
)