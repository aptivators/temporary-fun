package initiate.fixtures

import com.amazonaws.services.lambda.runtime.ClientContext
import com.amazonaws.services.lambda.runtime.CognitoIdentity
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger

object LambdaFixtures {
    @JvmStatic
    val context: Context
        get() = object : Context {
            override fun getAwsRequestId(): String? {
                return null
            }

            override fun getLogGroupName(): String? {
                return null
            }

            override fun getLogStreamName(): String? {
                return null
            }

            override fun getFunctionName(): String? {
                return null
            }

            override fun getFunctionVersion(): String? {
                return null
            }

            override fun getInvokedFunctionArn(): String? {
                return null
            }

            override fun getIdentity(): CognitoIdentity? {
                return null
            }

            override fun getClientContext(): ClientContext? {
                return null
            }

            override fun getRemainingTimeInMillis(): Int {
                return 0
            }

            override fun getMemoryLimitInMB(): Int {
                return 0
            }

            override fun getLogger(): LambdaLogger {
                return object : LambdaLogger {
                    override fun log(message: String) {
                        println(message)
                    }

                    override fun log(message: ByteArray) {}
                }
            }
        }
}