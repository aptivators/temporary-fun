package initiate.appian.models

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class FindVetFileIdRequest(
    val lastNameStartsWith: String? = null,
    val firstNameStartsWith: String? = null,
    val lastName: String? = null,
    val firstName: String? = null,
    val ssnStartsWith: String? = null,
    val ssnEndsWith: String? = null,
    val addressZipcode: String? = null,
    val addressLineOne: String? = null,
    val addressLineTwo: String? = null,
    val addressCity: String? = null,
    val addressState: String? = null
)
