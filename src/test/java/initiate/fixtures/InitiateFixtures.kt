package initiate.fixtures

import initiate.appian.models.Claim
import initiate.appian.models.Document
import initiate.appian.models.Packet
import initiate.appian.models.QueryDocumentResponse
import initiate.appian.models.TrackedItem
import initiate.appian.models.VeteranInfo
import initiate.common.constants.SubmissionConstants
import initiate.models.ExtractConfigurations

object InitiateFixtures {
    const val DOC_TYPE = "21-22"

    @JvmStatic
    fun makeDocType(): ExtractConfigurations {
        return ExtractConfigurations(DOC_TYPE)
    }

    private const val ecmDocId = "567"
    const val documentUrl = "www.fakesite.us.gov"

    @JvmStatic
    fun makeDocumentWithNoUpload(documentId: Int?): Document {
        return Document(
                documentId,
                334,
                "0",
                SubmissionConstants.READY_FOR_OCR,
                "0966",
                "0966",
                documentUrl,
                "01-05-2020",
                "01-05-2020")
    }

    @JvmStatic
    fun makeDocument(documentId: Int?): Document {
        return Document(
                documentId,
                334,
                ecmDocId,
                SubmissionConstants.READY_FOR_OCR,
                "0966",
                "0966",
                documentUrl,
                "01-05-2020",
                "01-05-2020")
    }

    private fun makePacket(documents: Set<Document>): Packet {
        val veteranInfo = VeteranInfo(
                "023001234", "first", "middle", "last", "street", "city", "state", "02345", "country",
                "VBMS")
        val trackedItem = TrackedItem("id", "description")
        val claims = setOf(Claim(1234, setOf(trackedItem)))
        return Packet(
                334,
                765,
                "notes",
                "status",
                "vetFileId",
                "vetSSN",
                "vetFirst",
                "vetMiddle",
                "vetLast",
                documents,
                veteranInfo,
                claims)
    }

    @JvmStatic
    fun makeQueryDocumentResponse(documents: Set<Document>): QueryDocumentResponse {
        return QueryDocumentResponse(setOf(makePacket(documents)))
    }
}