package initiate.hyperscience.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import initiate.common.constants.SubmissionConstants
import initiate.hyperscience.traits.ExternalIdAble

data class SubmissionRequest(
    @JsonProperty("file")
    var file: Set<String> = setOf(),
    @JsonProperty("metadata")
    var metadata: Metadata,

    @JsonProperty("external_id")
    override val externalId: String,

    @JsonProperty("machine_only")
    val machineOnly: Boolean = false
) : ExternalIdAble(externalId) {

    @JsonIgnore
    fun hasEcmDocIdOfZero(): Boolean {
        return metadata.ecmDocId?.equals("0") ?: false
    }

    @JsonIgnore
    fun getFailureStatus(): String {
        return if (!metadata.isFoia) SubmissionConstants.OCR_ERROR else
            if (shouldBeReadyForClassifier()) SubmissionConstants.READY_FOR_CLASSIFIER
            else SubmissionConstants.OCR_SUCCESS
    }

    @JsonIgnore
    private fun shouldBeReadyForClassifier(): Boolean {
        val masReadyForClassifier = !SubmissionConstants.DOC_TYPES_OCR_SUCCESS_MAS.contains(metadata.docType)
        val foiaReadyForClassifier = !SubmissionConstants.DOC_TYPES_OCR_SUCCESS_FOIA.contains(metadata.docType)

        return if (metadata.isFoia) foiaReadyForClassifier else masReadyForClassifier
    }
}