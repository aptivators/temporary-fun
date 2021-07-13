package initiate.appian_foia.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import initiate.common.constants.SubmissionConstants


data class UpdateDocumentClassification(
    @JsonProperty("DOC_ID")
    var documentId: Int? = null,
    @JsonProperty("DOC_STATUS")
    var documentStatus: String? = null,
    @JsonProperty("DOC_CLASSIFICATION")
    var documentClassification: String? = null,
    @JsonProperty("USER_ID")
    var userId: String? = SubmissionConstants.HS_USER_ID
)

data class UpdateDocumentTypeFoia(
    @JsonProperty("DOC_ID")
    var documentId: Int? = null,
    @JsonProperty("DOC_TYPE_CODE")
    var docType: String? = null,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("NUM_PAGES")
    var numPages: Int? = null
)
