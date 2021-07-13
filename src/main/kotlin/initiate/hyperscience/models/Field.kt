package initiate.hyperscience.models

import com.amazonaws.util.StringUtils
import com.fasterxml.jackson.annotation.JsonProperty
import initiate.common.constants.SubmissionConstants
import initiate.common.utils.RedactionUtils.isPiiField
import java.util.*

data class Field(
    @JsonProperty("id")
    val id: Int? = null,
    @JsonProperty("state")
    val state: String? = null,
    @JsonProperty("substate")
    val substate: String? = null,
    @JsonProperty("exceptions")
    val exceptions: Set<String>? = null,
    @JsonProperty("name")
    val name: String? = null,
    @JsonProperty("output_name")
    val outputName: String,
    @JsonProperty("field_definition_attributes")
    val fieldDefinitionAttributes: FieldDefinitionAttributes? = null,
    @JsonProperty("transcription")
    val transcription: Transcription? = null,
    @JsonProperty("field_image_url")
    val fieldImageUrl: String? = null,
    @JsonProperty("page_id")
    val pageId: Int? = null
) {

    constructor(fieldName: String, fieldValue: String) :
            this(
                name = fieldName,
                outputName = fieldName,
                transcription = Transcription(
                    raw = fieldValue,
                    normalized = fieldValue
                )
            )

    fun getFieldValueNum(pageIdsForMutableFields: SortedSet<Int> = mutableSetOf<Int>().toSortedSet()): Int? {
        return if (!StringUtils.isNullOrEmpty(name)
            && SubmissionConstants.ITERATED_FIELDS.contains(outputName)
        ) {
            pageIdsForMutableFields.plusAssign(pageId ?: Int.MAX_VALUE)
            val initialValue = stripNonDigits(name!!).toInt()
            return initialValue + (15 * pageIdsForMutableFields.indexOf(pageId))

        } else null
    }

    private fun stripNonDigits(input: CharSequence): String {
        val sb = StringBuilder(input.length)
        for (element in input) {
            val c = element
            if (c.code in 48..57) {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    fun hasNormalizationErrors(): Boolean {
        return this.exceptions?.contains("normalization_error") == true
    }

    override fun toString(): String {
        return if (isPiiField(outputName)) {
            ("Field{"
                    + "id="
                    + id
                    + ", state='"
                    + state
                    + '\''
                    + ", substate='"
                    + substate
                    + '\''
                    + ", exceptions="
                    + exceptions
                    + ", name='"
                    + name
                    + '\''
                    + ", outputName='"
                    + outputName
                    + '\''
                    + ", fieldDefinitionAttributes="
                    + fieldDefinitionAttributes
                    + ", transcription="
                    + "REDACTED"
                    + ", fieldImageUrl='"
                    + fieldImageUrl
                    + '\''
                    + ", pageId="
                    + pageId
                    + '}')
        } else ("Field{"
                + "id="
                + id
                + ", state='"
                + state
                + '\''
                + ", substate='"
                + substate
                + '\''
                + ", exceptions="
                + exceptions
                + ", name='"
                + name
                + '\''
                + ", outputName='"
                + outputName
                + '\''
                + ", fieldDefinitionAttributes="
                + fieldDefinitionAttributes
                + ", transcription="
                + transcription
                + ", fieldImageUrl='"
                + fieldImageUrl
                + '\''
                + ", pageId="
                + pageId
                + '}')
    }
}