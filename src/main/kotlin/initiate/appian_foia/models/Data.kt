package initiate.appian_foia.models

import com.fasterxml.jackson.annotation.JsonProperty

data class Data(
    @JsonProperty("ocrEngine")
    val ocrEngine: String? = null,
    @JsonProperty("redactionStatus")
    val redactionStatus: String? = null,
    @JsonProperty("redactionOpsURL")
    val redactionOpsURL: String? = null,
    @JsonProperty("redactionEditURL")
    val redactionEditURL: String? = null,
    @JsonProperty("originalRedactedIndicator")
    val originalRedactedIndicator: String? = null,
    @JsonProperty("inboundOutboundIndicator")
    val inboundOutboundIndicator: String? = null,
    @JsonProperty("docUrl")
    val docUrl: String? = null,
    @JsonProperty("redactionDocId")
    val redactionDocId: String? = null,
    @JsonProperty("ecmDocId")
    val ecmDocId: String? = null,
    @JsonProperty("docTypeDescription")
    val docTypeDescription: String? = null,
    @JsonProperty("docTypeCode")
    val docTypeCode: String? = null,
    @JsonProperty("docTypeId")
    val docTypeId: String? = null,
    @JsonProperty("docStatus")
    val docStatus: String? = null,
    @JsonProperty("docName")
    val docName: String? = null,
    @JsonProperty("docId")
    val docId: Int? = null,
    @JsonProperty("vaDateOfReceipt")
    val vaDateOfReceipt: String? = null,
    @JsonProperty("requestorType")
    val requestorType: String? = null,
    @JsonProperty("requestorName")
    val requestorName: String? = null,
    @JsonProperty("vtrnLstNm")
    val vetLastName: String? = null,
    @JsonProperty("vtrnFrstNm")
    val vetFirstName: String? = null,
    @JsonProperty("vtrnSsn")
    val vetSsn: String? = null,
    @JsonProperty("vtrnFileId")
    val vetFileId: String? = null,
    @JsonProperty("foiaCaseNum")
    val foiaCaseNum: String? = null,
    @JsonProperty("requestStatus")
    val requestStatus: String? = null,
    @JsonProperty("requestId")
    val requestId: Int? = null,
    @JsonProperty("batchStatus")
    val batchStatus: String? = null,
    @JsonProperty("batchId")
    val batchId: Int?,
    @JsonProperty("fileSize")
    val fileSize: Int? = null
)
