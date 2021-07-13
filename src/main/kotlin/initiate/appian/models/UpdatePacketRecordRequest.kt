package initiate.appian.models

import com.fasterxml.jackson.annotation.JsonProperty
import initiate.common.constants.SubmissionConstants

data class UpdatePacketRecordRequest(
    @JsonProperty("vtrnFileId")
    var veteranFileId: String? = null,
    @JsonProperty("vtrnSsn")
    var veteranSSN: String? = null,
    @JsonProperty("vtrnFirstName")
    var veteranFirstName: String? = null,
    @JsonProperty("vtrnLastName")
    var veteranLastName: String? = null
) {

    constructor(documentExtractRequest: DocumentExtractRequest) : this() {
        veteranFileId = documentExtractRequest.getFieldValue(SubmissionConstants.VA_FILE_NUMBER)
        veteranSSN = documentExtractRequest.getFieldValue(SubmissionConstants.VETERAN_SSN)
        veteranFirstName = documentExtractRequest.getFieldValue(SubmissionConstants.VETERAN_FIRST_NAME)
        veteranLastName = documentExtractRequest.getFieldValue(SubmissionConstants.VETERAN_LAST_NAME)
    }
}
