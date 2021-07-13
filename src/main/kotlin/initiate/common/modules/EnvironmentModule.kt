package initiate.common.modules

class EnvironmentModule {
    fun getSystemEnvironmentVariable(key: String): String? {
        return System.getenv(key)
    }
}