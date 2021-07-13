package initiate.aws_integration.services.implementations

import com.amazonaws.services.sns.AmazonSNS
import initiate.aws_integration.services.NotificationService

class NotificationServiceImpl(private val amazonSns: AmazonSNS, private val topicArn: String) : NotificationService {
    override fun publishMessage(message: String, subject: String): String {
        println("Putting message $message on SNS topic $topicArn")
        return amazonSns.publish(topicArn, message).messageId
    }
}
