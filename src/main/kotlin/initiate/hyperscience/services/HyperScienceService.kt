package initiate.hyperscience.services

import initiate.hyperscience.models.Submission
import initiate.hyperscience.models.SubmissionsResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface HyperScienceService {
    @GET("api/v5/submissions")
    fun listSubmissions(
        @Query("state") state: String, @Query("start_time__lt") startTimeLt: String
    ): Call<SubmissionsResponse>

    @GET("api/v5/submissions")
    fun listSubmissions(
        @Query("state") state: String
    ): Call<SubmissionsResponse>

    @GET("api/v5/submissions")
    fun listSubmissions(
        @Query(value = "limit") limit: Int,
        @Query(value = "offset") offset: Int,
        @Query(value = "state") state: String,
        @Query(value = "start_time__gte") startTimeGte: String
    ): Call<SubmissionsResponse>

    @GET("api/v5/submissions/{id}")
    fun getSubmission(@Path(value = "id") id: Int, @Query("flat") flat: Boolean? = false): Call<Submission>

    @GET("api/v5/submissions/external/{external_id}")
    fun getSubmissionByExternalId(
        @Path(value = "external_id") id: String,
        @Query("flat") flat: Boolean? = false
    ): Call<Submission?>

    @GET("api/v5/image/{id}")
    fun getImage(@Path(value = "id", encoded = true) id: String): Call<ResponseBody>

    @GET("api/v5/image/{id}")
    fun getImage(
        @Path(value = "id", encoded = true) id: String,
        @QueryMap coordinates: Map<String, String>
    ): Call<ResponseBody>
}
