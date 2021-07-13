package initiate.appian.models

import com.fasterxml.jackson.annotation.JsonProperty

data class Packet(
    @JsonProperty("packetId")
    val packetId: Int? = null,
    @JsonProperty("batchId")
    val batchId: Int? = null,
    @JsonProperty("notes")
    val notes: String? = null,
    @JsonProperty("status")
    val status: String? = null,
    @JsonProperty("vetFileId")
    val vetFileId: String? = null,
    @JsonProperty("vetSSN")
    val vetSsn: String? = null,
    @JsonProperty("vetFirstName")
    val vetFirstName: String? = null,
    @JsonProperty("vetMiddleName")
    val vetMiddleName: String? = null,
    @JsonProperty("vetLastName")
    val vetLastName: String? = null,
    @JsonProperty("documents")
    val documents: Set<Document> = setOf(),
    @JsonProperty("veteranInfo")
    val veteranInfo: VeteranInfo? = null,
    @JsonProperty("claims")
    val claims: Set<Claim>? = null
) {

    override fun toString(): String {
        return "Packet{" +
                "packetId=" + packetId +
                ", batchId=" + batchId +
                ", notes='" + notes + '\'' +
                ", status='" + status + '\'' +
                ", vetFileId='" + vetFileId + '\'' +
                ", vetSsn='" + REDACTED + '\'' +
                ", vetFirstName='" + REDACTED + '\'' +
                ", vetMiddleName='" + REDACTED + '\'' +
                ", vetLastName='" + vetLastName + '\'' +
                ", documents=" + documents +
                ", veteranInfo=" + veteranInfo +
                ", claims=" + claims +
                '}'
    }

    companion object {
        private const val REDACTED = "REDACTED"
    }
}