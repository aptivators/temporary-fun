package initiate.appian.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import initiate.common.constants.SubmissionConstants

data class UpdateDocumentType(
    @JsonProperty("DOC_ID")
    var documentId: Int? = null,
    @JsonProperty("DOC_TYPE_CODE")
    var docType: String? = null,
    @JsonProperty("USER_ID")
    val userId: String = SubmissionConstants.HS_USER_ID,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("NUM_PAGES")
    var numPages: Int? = null
)
