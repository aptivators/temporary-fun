package initiate.common.utils

import okhttp3.Request
import okio.Buffer

object HttpUtils {
    fun Request.bodyToString(): String? {
        val copy = this.newBuilder().build()
        if (copy.body() == null) return null
        val buffer = Buffer()
        copy.body()!!.writeTo(buffer)
        return buffer.readUtf8()
    }
}
