package initiate.appian.services

import com.fasterxml.jackson.annotation.JsonProperty
import initiate.appian.models.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface AppianService {
    @POST("queryDocuments")
    fun queryForDocuments(@Body queryDocumentRequest: QueryDocumentRequest?): Call<ResponseBody?>

    @GET("E-eYEg/{ecmDocId}")
    fun downloadDocument(@Path("ecmDocId") ecmDocId: String?): Call<ResponseBody?>

    @POST("insertExtractedData")
    fun insertExtractedData(@Body documentExtractRequest: DocumentExtractRequest?): Call<ResponseBody?>

    data class QueryExtractedFieldsRequest(val docId: Int)

    @POST("queryExtractedFields")
    fun getExtractedField(@Body queryExtractedFieldsRequest: QueryExtractedFieldsRequest?): Call<ResponseBody?>

    @POST("updatePacketRecord")
    fun updatePacketRecord(
        @Query("PACKET_ID") packetId: Int,
        @Query("UPDATED_BY") updatedBy: String = "RcdMtch",
        @Body updatePacketRecordRequest: UpdatePacketRecordRequest?
    ): Call<ResponseBody?>

    @POST("updateDocumentStatus")
    fun updateDocumentStatus(
        @Body updateDocumentStatusRequest: UpdateDocumentStatusRequest?
    ): Call<ResponseBody?>

    @PUT("updateDocumentDocTypeId")
    fun updateDocumentType(
        @Body updateDocumentType: UpdateDocumentType
    ): Call<ResponseBody?>

    data class FindVeteranRequest(val veteranFileId: String)

    @POST("findVeteranByFileNumber")
    fun findVeteranByFileNumber(@Body findVeteranRequest: FindVeteranRequest): Call<ResponseBody>

    @POST(value = "findVetFileId")
    fun findVetFileId(@Body request: FindVetFileIdRequest): Call<ResponseBody>

    data class FindDependentsRequest(val fileNumber: String)

    @POST("findDependents")
    fun findDependents(@Body findDependentsRequest: FindDependentsRequest): Call<ResponseBody>

    data class FindRelationshipsRequest(val ptcpntId: String)

    @POST("findAllRelationships")
    fun findRelationships(@Body findRelationshipsRequest: FindRelationshipsRequest): Call<ResponseBody>

    @POST("logEvent")
    fun logEvent(@Body logEventRequest: LogEventRequest): Call<ResponseBody>

    data class CompareNameResult(
        @JsonProperty("name1") val name1: String? = null,
        @JsonProperty("name2") val name2: String? = null,
        @JsonProperty("thresh") val thresh: Int,
        @JsonProperty("is_same") val isSame: Boolean,
        val similarity: Float
    )

    @POST("compareName")
    fun compareNames(@Body veteranBody: Any?): Call<ResponseBody>

    data class FindPoaRequest(val fileNumber: String)

    @POST(value = "findPOA")
    fun findPoa(@Body request: FindPoaRequest): Call<ResponseBody>

    @POST(value = "findFiduciary")
    fun findFiduciary(@Body request: FindPoaRequest): Call<ResponseBody>
}
