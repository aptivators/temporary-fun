package initiate.appian.modules

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import initiate.appian.services.AppianService
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.time.Duration
import java.util.concurrent.TimeUnit

object WebClientModule {
    private val appianUrl: String
        get() = System.getenv("APPIAN_URL")

    private val appianApiKey: String
        get() = System.getenv("APPIAN_API_KEY_NAME") ?: "Appian-API-Key"

    private fun getAppianApiKeyValue(secretsMap: Map<String, String>): String? {
        return System.getenv("APPIAN_API_KEY") ?: secretsMap["APPIAN_API_KEY_VALUE"]
    }

    private val connections: String
        get() = System.getenv("CONNECTIONS")

    private val keepAliveDuration: String
        get() = System.getenv("KEEP_ALIVE_IN_SECONDS")

    private fun getHttpClient(secretsMap: Map<String, String>): OkHttpClient {
        val appianApiKeyValue = getAppianApiKeyValue(secretsMap)!!
        val basicAuthInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val builder = originalRequest
                .newBuilder()
                .header(
                    "Authorization", "Token $appianApiKeyValue"
                )
                .header(appianApiKey, appianApiKeyValue)
            val newRequest = builder.build()
            chain.proceed(newRequest)
        }
        val connectionPool = ConnectionPool(
            connections.toInt(), keepAliveDuration.toInt().toLong(),
            TimeUnit.SECONDS
        )
        return OkHttpClient()
            .newBuilder()
            .addInterceptor(basicAuthInterceptor)
            .connectTimeout(Duration.ofSeconds(60L))
            .readTimeout(Duration.ofSeconds(60L))
            .connectionPool(connectionPool)
            .build()
    }

    @JvmStatic
    val objectMapper: ObjectMapper = ObjectMapper()
        .registerKotlinModule()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun getAppianService(secretsMap: Map<String, String>): AppianService {
        val jacksonConverterFactory = JacksonConverterFactory.create(objectMapper)
        return Retrofit.Builder()
            .baseUrl(appianUrl)
            .addConverterFactory(jacksonConverterFactory)
            .client(getHttpClient(secretsMap))
            .build()
            .create(AppianService::class.java)
    }
}