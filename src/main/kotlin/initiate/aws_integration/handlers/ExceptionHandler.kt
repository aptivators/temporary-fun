package initiate.aws_integration.handlers

import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException

object ExceptionHandler {
    @JvmStatic
    fun logAmazonServiceException(ase: AmazonServiceException) {
        println(
            "Caught an AmazonServiceException, which means "
                    + "your request made it to the Amazon service, but was "
                    + "rejected with an error response for some reason."
        )
        println("Error Message:    " + ase.message)
        println("HTTP Status Code: " + ase.statusCode)
        println("AWS Error Code:   " + ase.errorCode)
        println("Error Type:       " + ase.errorType)
        println("Request ID:       " + ase.requestId)
    }

    @JvmStatic
    fun logAmazonClientException(ace: AmazonClientException) {
        println(
            "Caught an AmazonClientException, which means "
                    + "the client encountered a serious internal problem while "
                    + "trying to communicate with the Amazon service, such as not "
                    + "being able to access the network."
        )
        println("Error Message: " + ace.message)
    }
}