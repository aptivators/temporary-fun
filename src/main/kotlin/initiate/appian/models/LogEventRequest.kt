package initiate.appian.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import initiate.appian.enums.EventTypeId
import initiate.common.constants.SubmissionConstants
import initiate.hyperscience.models.Metadata

const val HYPERSCIENCE_MAS_COMPONENT_ID = 22
const val HYPERSCIENCE_FOIA_COMPONENT_ID = 34

data class LogEventRequest(
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("PACKET_ID")
    var packetId: Int? = null,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("REQUEST_ID")
    var requestId: Int? = null,
    @JsonProperty("BATCH_ID")
    var batchId: Int? = null,
    @JsonProperty("EVENT_TYPE_ID")
    var eventTypeId: Int? = null,
    @JsonProperty("USER_ID")
    val userId: String? = null,
    @JsonProperty("BOT_FIELD_UPDATE_ID")
    val botFieldUpdateId: String? = null,
    @JsonProperty("DOC_ID")
    var documentId: Int? = null,
    @JsonProperty("EVENT_NOTES")
    var eventNotes: String? = null,
    @JsonProperty("CATEGORY_ID")
    var categoryId: Int? = null,
    @JsonProperty("COMPONENT_ID")
    val componentId: Int? = null
) {

    constructor(eventTypeId: EventTypeId, metadata: Metadata) : this(
        eventTypeId = eventTypeId.id,
        eventNotes = eventTypeId.eventNote,
        userId = SubmissionConstants.HS_USER_ID,
        categoryId = eventTypeId.categoryId.id
    ) {
        packetId = metadata.packetId
        requestId = metadata.requestId
        batchId = metadata.batchId
        documentId = metadata.documentId
    }

    constructor(addressPrefix: String, metadata: Metadata) : this(
        packetId = metadata.packetId,
        batchId = metadata.batchId,
        documentId = metadata.documentId,
        userId = SubmissionConstants.HS_USER_ID
    ) {
        prefixEventTypeMap[addressPrefix]?.let {
            eventTypeId = it.id
            eventNotes = it.eventNote
            categoryId = it.categoryId.id
        }
    }

    companion object {
        val prefixEventTypeMap = mapOf(
            SubmissionConstants.CLAIMANT_ADDRESS_PREFIX to EventTypeId.CLAIMANT_ADDRESS_VALIDATED,
            SubmissionConstants.VETERAN_ADDRESS_PREFIX to EventTypeId.VETERAN_ADDRESS_VALIDATED,
            SubmissionConstants.NEW_ADDRESS_PREFIX to EventTypeId.NEW_ADDRESS_VALIDATED
        )
    }
}
