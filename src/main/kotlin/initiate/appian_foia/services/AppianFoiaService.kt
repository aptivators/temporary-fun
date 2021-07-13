package initiate.appian_foia.services

import initiate.appian.services.AppianService
import initiate.appian_foia.models.QueryFoiaRequest
import initiate.appian_foia.models.UpdateDocumentClassification
import initiate.appian_foia.models.UpdateDocumentTypeFoia
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface AppianFoiaService : AppianService {
    @POST("queryDocuments")
    fun queryForDocuments(@Body queryDocumentRequest: QueryFoiaRequest): Call<ResponseBody?>

    @POST("updateDocumentClassification")
    fun updateDocumentClassification(
        @Body updateDocumentClassification: UpdateDocumentClassification
    ): Call<ResponseBody?>

    @PUT("updateDocumentDocTypeId")
    fun updateDocumentType(
        @Body updateDocumentTypeFoia: UpdateDocumentTypeFoia
    ): Call<ResponseBody?>
}
