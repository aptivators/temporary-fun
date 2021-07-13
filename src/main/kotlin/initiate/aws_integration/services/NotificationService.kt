package initiate.aws_integration.services

interface NotificationService {
    fun publishMessage(message: String, subject: String): String
}
