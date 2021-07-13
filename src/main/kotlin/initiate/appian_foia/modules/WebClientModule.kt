package initiate.appian_foia.modules

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import initiate.appian_foia.services.AppianFoiaService
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.time.Duration
import java.util.concurrent.TimeUnit

object WebClientModule {
    private val appianFoiaUrl: String
        get() = System.getenv("FOIA_APPIAN_URL")

    private val appianFoiaApiKey: String
        get() = System.getenv("FOIA_APPIAN_API_KEY_NAME") ?: "Appian-API-Key"

    private fun getAppianFoiaApiKeyValue(secretsMap: Map<String, String>): String? {
        return System.getenv("FOIA_APPIAN_API_KEY") ?: secretsMap["APPIAN_FOIA_API_KEY_VALUE"]
    }

    private val connections: String
        get() = System.getenv("CONNECTIONS")

    private val keepAliveDuration: String
        get() = System.getenv("KEEP_ALIVE_IN_SECONDS")

    private fun getHttpClient(secretsMap: Map<String, String>): OkHttpClient {
        val appianFoiaApiKeyValue = getAppianFoiaApiKeyValue(secretsMap)!!
        val basicAuthInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val builder = originalRequest
                .newBuilder()
                .header(
                    "Authorization", "Token $appianFoiaApiKeyValue"
                )
                .header(appianFoiaApiKey, appianFoiaApiKeyValue)
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

    fun getAppianFoiaService(secretsMap: Map<String, String>): AppianFoiaService {
        val jacksonConverterFactory = JacksonConverterFactory.create(objectMapper)
        return Retrofit.Builder()
            .baseUrl(appianFoiaUrl)
            .addConverterFactory(jacksonConverterFactory)
            .client(getHttpClient(secretsMap))
            .build()
            .create(AppianFoiaService::class.java)
    }
}
