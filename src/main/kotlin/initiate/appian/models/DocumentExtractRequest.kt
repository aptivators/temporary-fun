package initiate.appian.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

data class DocumentExtractRequest(
    @JsonProperty("docId")
    var documentId: Int? = null,
    @JsonProperty("userId")
    var userId: String? = null,
    @JsonProperty("docStatus")
    var docStatus: String? = null,
    @JsonProperty("extractedStructuredData")
    var extractedStructuredData: Set<ExtractedStructuredData> = setOf(),
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("numPages")
    var numPages: Int? = null
) {

    @JsonIgnore
    var distinctExtractedFieldsMap: Map<String, String?> = mapOf()

    init {
        distinctExtractedFieldsMap = this.extractedStructuredData
            .groupBy { it.fieldName }
            .mapValues { (_, valueList) -> valueList.maxByOrNull { it.fieldValue?.length ?: 0 }?.fieldValue }
    }

    fun anyFieldHasValue(): Boolean {
        return extractedStructuredData.any { data: ExtractedStructuredData -> !data.fieldValue.isNullOrBlank() }
    }

    fun fieldExistsWithNameContained(name: CharSequence): Boolean {
        return extractedStructuredData.any { data: ExtractedStructuredData -> data.fieldName.contains(name) }
    }

    @JsonIgnore
    fun getIsFinalValue(): String? {
        return extractedStructuredData.first().finalValue
    }

    fun getFieldValue(fieldName: String): String? {
        return this.distinctExtractedFieldsMap[fieldName]
    }
}