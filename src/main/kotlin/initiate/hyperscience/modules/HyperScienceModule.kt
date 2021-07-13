package initiate.hyperscience.modules

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import initiate.hyperscience.services.HyperScienceService
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.time.Duration
import java.util.concurrent.TimeUnit

object HyperScienceModule {
    private val hyperScienceUrl: String
        get() = System.getenv("HYPERSCIENCE_URL")

    private val hyperScienceApiKey: String
        get() = System.getenv("HYPERSCIENCE_API_KEY")

    private val connections: String
        get() = System.getenv("CONNECTIONS")

    private val keepAliveDuration: String
        get() = System.getenv("KEEP_ALIVE_IN_SECONDS")

    private val httpClient: OkHttpClient
        get() {
            val authTokenInterceptor = Interceptor { chain ->
                val originalRequest = chain.request()
                val builder = originalRequest
                    .newBuilder()
                    .header("Authorization", "Token $hyperScienceApiKey")
                val newRequest = builder.build()
                chain.proceed(newRequest)
            }
            val connectionPool = ConnectionPool(
                connections.toInt(), keepAliveDuration.toInt().toLong(),
                TimeUnit.SECONDS
            )
            return OkHttpClient()
                .newBuilder()
                .addInterceptor(authTokenInterceptor)
                .connectTimeout(Duration.ofSeconds(60L))
                .readTimeout(Duration.ofSeconds(60L))
                .connectionPool(connectionPool)
                .build()
        }

    @JvmStatic
    val objectMapper: ObjectMapper = ObjectMapper()
        .registerKotlinModule()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    val hyperScienceService: HyperScienceService
        get() {
            val jacksonConverterFactory = JacksonConverterFactory.create(objectMapper)
            return Retrofit.Builder()
                .baseUrl(hyperScienceUrl)
                .addConverterFactory(jacksonConverterFactory)
                .client(httpClient)
                .build()
                .create(HyperScienceService::class.java)
        }
}