package initiate.appian.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import initiate.common.utils.RedactionUtils.isPiiField
import java.util.*

data class ExtractedStructuredData(
    @JsonProperty("fieldName") var fieldName: String,
    @JsonProperty("fieldValue") var fieldValue: String? = null,
    @JsonProperty("fieldValueNum") var fieldValueNum: Int? = null,
    @JsonProperty("pageNum") var pageNum: String? = null,
    @JsonProperty("isFinalValue") var finalValue: String? = null,
    @JsonProperty("confidence") var confidence: Float? = null,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("extractedAddress") var extractedAddress: ExtractedAddress? = null,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("formPageNum") var formPageNum: Int? = null
) {

    constructor(entry: Map.Entry<String, String?>, isFinalValue: String?) :
            this(
                fieldName = entry.key.uppercase(Locale.getDefault()),
                fieldValue = entry.value?.uppercase(Locale.getDefault()),
                finalValue = isFinalValue
            )

    override fun toString(): String {
        return if (isPiiField(fieldName)) {
            "ExtractedStructuredData{\"fieldName\": \"$fieldName\", \"fieldValue\": \"$REDACTED\", \"fieldValueNum\":\"$REDACTED\", " +
                    "\"pageNum\":\"$pageNum\", \"isFinalValue\":\"$finalValue\", \"confidence\":\"$confidence\", " +
                    "\"extractedAddress\":\"$extractedAddress\", \"formPageNum\":\"$formPageNum\"}"
        } else
            (return "ExtractedStructuredData{\"fieldName\": \"$fieldName\", \"fieldValue\": \"$fieldValue\", " +
                    "\"fieldValueNum\":\"$fieldValueNum\", \"pageNum\":\"$pageNum\", \"isFinalValue\":\"$finalValue\", " +
                    "\"confidence\":\"$confidence\", \"extractedAddress\":\"$extractedAddress\", \"formPageNum\":\"$formPageNum\"}")
    }

    companion object {
        private const val REDACTED = "REDACTED"
    }
}