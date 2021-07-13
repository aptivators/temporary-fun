package initiate.aws_integration.services.implementations

import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.model.Message
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import initiate.aws_integration.modules.AWSModule
import initiate.aws_integration.services.MessageService

class MessageServiceImpl(private val amazonSqs: AmazonSQS, val sqsUrl: String?) : MessageService {
    override fun <T> putMessageOnQueue(queueObject: T): String {
        println("Putting message $queueObject on queue $sqsUrl")
        val jsonRequest = AWSModule.objectMapper.writeValueAsString(queueObject)
        return amazonSqs.sendMessage(sqsUrl!!, jsonRequest).messageId
    }

    override fun <T> readMessagesFromQueue(numberOfMessages: Int, tClass: Class<T>?): Sequence<T>? {
        println("Reading $numberOfMessages messages from queue")
        val receiveMessageRequest = ReceiveMessageRequest(outputSqsUrl).withMaxNumberOfMessages(numberOfMessages)
        val messages = amazonSqs.receiveMessage(receiveMessageRequest).messages
        println("Found ${messages.size} messages")
        return messages.asSequence()
            .map { obj: Message -> obj.body }
            .onEach { println("Body is: $it") }
            .map { messageBody: String? -> AWSModule.objectMapper.readValue(messageBody, tClass) }
    }

    companion object {
        private val outputSqsUrl: String
            get() = System.getenv("OUTPUT_SQS_URL")
    }

}