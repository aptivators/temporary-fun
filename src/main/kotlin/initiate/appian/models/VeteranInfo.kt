package initiate.appian.models

import com.fasterxml.jackson.annotation.JsonIgnore

data class VeteranInfo(
    val ssn: String? = null,
    val firstName: String? = null,
    val middleName: String? = null,
    val lastName: String? = null,
    val streetAddress: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zip: String? = null,
    val country: String? = null,
    val source: String? = null
) {


    companion object {
        @JsonIgnore
        private val REDACTED = "REDACTED"
    }

    override fun toString(): String {
        return "VeteranInfo(ssn=$REDACTED, firstName=$REDACTED, middleName=$REDACTED, lastName=$lastName, streetAddress=$REDACTED, city=$city, state=$state, zip=$zip, country=$country, source=$source)"
    }
}