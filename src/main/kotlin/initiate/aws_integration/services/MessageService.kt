package initiate.aws_integration.services

interface MessageService {
    fun <T> putMessageOnQueue(queueObject: T): String?
    fun <T> readMessagesFromQueue(numberOfMessages: Int, tClass: Class<T>?): Sequence<T>?
}