package initiate.appian.models

import com.fasterxml.jackson.annotation.JsonIgnore
import initiate.common.constants.SubmissionConstants
import initiate.common.utils.PrintUtils

class ExtractedAddress(
    @JsonIgnore
    var fullBlock: String? = null,
    var streetAddress: String? = null,
    var line3: String? = null,
    var city: String? = null,
    var state: String? = null,
    @JsonIgnore
    var zipCodeString: String? = null,
    var country: String? = null,
    var confidence: Float? = null
) {
    fun getZipCode(): Int? {
        return zipCodeString?.toInt()
    }

    constructor(extractedStructuredData: Map<String, String?>, prefix: String) : this() {
        println("Constructing Extracted Address for address normalization")

        val getAddressFieldForPrefixBySuffix: (String) -> String = { suffix ->
            extractedStructuredData.getAddressField(prefix, suffix)
        }

        fullBlock = getAddressFieldForPrefixBySuffix(SubmissionConstants.ADDRESS_FULL_BLOCK)

        streetAddress = PrintUtils.combineStringsWithSpace(
            getAddressFieldForPrefixBySuffix(SubmissionConstants.ADDRESS_LINE1),
            getAddressFieldForPrefixBySuffix(SubmissionConstants.ADDRESS_LINE2)
        )
        line3 = getAddressFieldForPrefixBySuffix(SubmissionConstants.ADDRESS_LINE3)
        city = getAddressFieldForPrefixBySuffix(SubmissionConstants.ADDRESS_CITY)
        state = getAddressFieldForPrefixBySuffix(SubmissionConstants.ADDRESS_STATE)
        zipCodeString = getAddressFieldForPrefixBySuffix(SubmissionConstants.ADDRESS_ZIP5)
        country = getAddressFieldForPrefixBySuffix(SubmissionConstants.ADDRESS_COUNTRY)
    }


    private fun Map<String, String?>.getAddressField(prefix: String, suffix: String): String {
        return this[PrintUtils.combineStringsWithUnderscore(prefix, suffix)].orEmpty()
    }

    @JsonIgnore
    fun toFullAddressStringWithoutCountry(): String {
        val allFields = listOf(streetAddress, city, state, zipCodeString)
            .filter { value: String? -> !PrintUtils.isNullOrEmpty(value) }
        return java.lang.String.join(" ", allFields)
    }

    @JsonIgnore
    fun toFullAddressStringFromLine3(): String {
        val allFields = listOf(streetAddress, line3)
            .filter { value: String? -> !PrintUtils.isNullOrEmpty(value) }
        return java.lang.String.join(" ", allFields)
    }

    @JsonIgnore
    fun hasStreetAddressAndLine3(): Boolean {
        return hasStreetAddress() && hasLine3()
    }

    @JsonIgnore
    fun hasStreetAddressAndZipOrCity(): Boolean {
        return hasStreetAddress() && (hasCityAndState() || hasZipCode())
    }

    @JsonIgnore
    fun hasZipCode(): Boolean {
        return !PrintUtils.isNullOrEmpty(zipCodeString)
    }

    @JsonIgnore
    fun hasFullBlock(): Boolean {
        return !PrintUtils.isNullOrEmpty(fullBlock)
    }

    private fun hasStreetAddress(): Boolean {
        return !PrintUtils.isNullOrEmpty(streetAddress)
    }

    @JsonIgnore
    fun hasCityAndState(): Boolean {
        return !(PrintUtils.isNullOrEmpty(city) || PrintUtils.isNullOrEmpty(state))
    }

    @JsonIgnore
    fun hasLine3(): Boolean {
        return !PrintUtils.isNullOrEmpty(line3)
    }
}
