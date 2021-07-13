package initiate.aws_integration.modules

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import initiate.aws_integration.services.MessageService
import initiate.aws_integration.services.implementations.MessageServiceImpl

object AWSModule {
    private val awsRegion: String
        get() = System.getenv("AWS_REGION")

    private val amazonSqs: AmazonSQS
        get() = AmazonSQSClientBuilder.standard().withRegion(awsRegion).build()

    private val sqsUrl: String
        get() =
            sequenceOf(
                System.getenv("INPUT_SQS_URL"),
                System.getenv("RECORD_MATCHING_SQS_URL"),
                System.getenv("EXTRACTED_FIELD_SQS_URL"),
                System.getenv("PAGED_HS_RESPONSE_SQS_URL")
            )
                .filterNotNull()
                .first()

    private val deathCertificateQueueUrl: String
        get() = System.getenv("DEATH_CERTIFICATE_SQS_URL")

    private val foiaRequestInputQueueUrl: String
        get() = System.getenv("FOIA_REQUEST_INPUT_SQS_URL")

    private val foiaOutputSqsUrl: String
        get() = System.getenv("FOIA_REQUEST_OUTPUT_SQS_URL")

    private val masOutputSqsUrl: String
        get() = System.getenv("MAS_OUTPUT_SQS_URL")

    private val foiaRecordMatchingQueueUrl: String
        get() = System.getenv("FOIA_RECORD_MATCHING_SQS_URL")

    private val masRecordMatchingQueueUrl: String
        get() = System.getenv("RECORD_MATCHING_SQS_URL")

    @JvmStatic
    val amazonS3: AmazonS3
        get() = AmazonS3ClientBuilder.standard().withRegion(awsRegion).build()

    @JvmStatic
    val messageService: MessageService
        get() = MessageServiceImpl(amazonSqs, sqsUrl)

    @JvmStatic
    val foiaRecordMatchingMessageService: MessageService
        get() = MessageServiceImpl(amazonSqs, foiaRecordMatchingQueueUrl)

    @JvmStatic
    val masRecordMatchingMessageService: MessageService
        get() = MessageServiceImpl(amazonSqs, masRecordMatchingQueueUrl)

    @JvmStatic
    val deathCertificateMessageService: MessageService
        get() = MessageServiceImpl(amazonSqs, deathCertificateQueueUrl)

    @JvmStatic
    val foiaRequestInputMessageService: MessageService
        get() = MessageServiceImpl(amazonSqs, foiaRequestInputQueueUrl)

    @JvmStatic
    val foiaOutputMessageService: MessageService
        get() = MessageServiceImpl(amazonSqs, foiaOutputSqsUrl)

    @JvmStatic
    val masOutputMessageService: MessageService
        get() = MessageServiceImpl(amazonSqs, masOutputSqsUrl)

    @JvmStatic
    val objectMapper: ObjectMapper =
        ObjectMapper()
            .registerKotlinModule()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    private val secretName: String
        get() = System.getenv("SECRETS") ?: "secrets"
    private val secretEndpoint: String
        get() = System.getenv("ENDPOINT") ?: "secretsmanager.us-gov-west-1.amazonaws.com"

    val secretsMap: Map<String, String>
        get() {
            return emptyMap()
        }
}
