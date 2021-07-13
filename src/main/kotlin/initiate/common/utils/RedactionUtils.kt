package initiate.common.utils


object RedactionUtils {
    private val PII_FIELD_SUBSTRINGS = setOf("SSN", "SOCIAL", "DOB", "NAME", "ADDRESS", "SECURITY_ANSWER")

    @JvmStatic
    fun isPiiField(fieldName: String?): Boolean {
        return fieldName?.let { name: String ->
            PII_FIELD_SUBSTRINGS.any { name.contains(it) }
        } ?: false
    }
}