package initiate.common.utils

object PrintUtils {
    @JvmStatic
    fun combineStringsWithSpace(string1: String, string2: String): String {
        return combineStringsWithDelimiter(string1, string2, " ")
    }

    @JvmStatic
    fun combineStringsWithUnderscore(string1: String, string2: String): String {
        return combineStringsWithDelimiter(string1, string2, "_")
    }

    @JvmStatic
    private fun combineStringsWithDelimiter(
        string1: String, string2: String, delimiter: String
    ): String {
        return String.format("%s%s%s", string1, delimiter, string2)
    }

    @JvmStatic
    fun isNullOrEmpty(str: String?): Boolean {
        return str == null || str.trim { it <= ' ' }.isEmpty()
    }
}
