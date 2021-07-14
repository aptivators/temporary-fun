package initiate.lambda

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import initiate.appian.models.QueryDocumentRequest
import initiate.appian.models.UpdateDocumentStatusRequest
import initiate.appian.services.AppianService
import initiate.aws_integration.services.implementations.MessageServiceImpl
import initiate.common.modules.EnvironmentModule
import initiate.fixtures.InitiateFixtures
import initiate.fixtures.InitiateFixtures.makeDocType
import initiate.fixtures.InitiateFixtures.makeDocument
import initiate.fixtures.InitiateFixtures.makeDocumentWithNoUpload
import initiate.fixtures.InitiateFixtures.makeQueryDocumentResponse
import initiate.fixtures.LambdaFixtures.context
import initiate.helpers.WebClientHelper
import initiate.hyperscience.models.Submission
import initiate.hyperscience.models.SubmissionNotification
import initiate.hyperscience.models.SubmissionRequest
import initiate.hyperscience.services.HyperScienceService
import java.io.InputStream
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

class InitiateServiceMasTest {

  @Test
  fun initiateShouldPutMessagesOnTheQueueForASingleDocument() {
    val mockAmazonS3 = Mockito.mock(AmazonS3::class.java)
    val mockAppianService = Mockito.mock(AppianService::class.java)
    val mockEnvironmentModule = Mockito.mock(EnvironmentModule::class.java)
    val mockHyperScienceService = Mockito.mock(HyperScienceService::class.java)
    val mockInputMessageService = Mockito.mock(MessageServiceImpl::class.java)
    val mockOutputMessageService = Mockito.mock(MessageServiceImpl::class.java)
    val initiateService =
        InitiateServiceMas(
            mockAmazonS3,
            mockAppianService,
            mockEnvironmentModule,
            mockHyperScienceService,
            mockInputMessageService,
            mockOutputMessageService)
    Mockito.`when`(mockEnvironmentModule.getSystemEnvironmentVariable("S3_BUCKET_NAME"))
        .thenReturn("SAMPLE_BUCKET")
    Mockito.`when`(mockEnvironmentModule.getSystemEnvironmentVariable("INPUT_SQS_URL"))
        .thenReturn("INPUT_SQS_URL")
    Mockito.`when`(
            mockAppianService.queryForDocuments(
                ArgumentMatchers.any(QueryDocumentRequest::class.java)))
        .thenReturn(
            WebClientHelper.makeCallResponseJson(
                makeQueryDocumentResponse(setOf(makeDocument(111)))))
    Mockito.`when`(
            mockHyperScienceService.getSubmissionByExternalId(
                any(String::class.java), any(Boolean::class.java)))
        .thenReturn(WebClientHelper.makeCallResponseSubmission(null))
    Mockito.`when`(
            mockAppianService.updateDocumentStatus(
                ArgumentMatchers.any(UpdateDocumentStatusRequest::class.java)))
        .thenReturn(WebClientHelper.makeCallResponseJson(WebClientHelper.EMPTY_JSON_OBJECT))
    Mockito.`when`(mockAppianService.downloadDocument(ArgumentMatchers.anyString()))
        .thenReturn(WebClientHelper.makeCallResponseByteStream())
    Mockito.`when`(mockInputMessageService.sqsUrl).thenReturn("HyperScience Input Queue")
    val result =
        initiateService.initiateHyperScience(makeDocType(), context, InitiateFixtures.DOC_TYPE)
    Assertions.assertEquals(
        "| Successfully sent 1 messages to HyperScience Input Queue for document type 21-22",
        result,
        "initiateHyperScience should pass with a single document")
    Mockito.verify(mockAmazonS3)
        .putObject(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString(),
            ArgumentMatchers.any(InputStream::class.java),
            ArgumentMatchers.any(ObjectMetadata::class.java))
    Mockito.verify(mockInputMessageService)
        .putMessageOnQueue(ArgumentMatchers.any(SubmissionRequest::class.java))
    Mockito.verify(mockAppianService)
        .updateDocumentStatus(ArgumentMatchers.any(UpdateDocumentStatusRequest::class.java))
  }

  @Test
  fun initiateShouldPutMessagesOnTheQueueForMultipleDocuments() {
    val mockAmazonS3 = Mockito.mock(AmazonS3::class.java)
    val mockAppianService = Mockito.mock(AppianService::class.java)
    val mockEnvironmentModule = Mockito.mock(EnvironmentModule::class.java)
    val mockHyperScienceService = Mockito.mock(HyperScienceService::class.java)
    val mockInputMessageService = Mockito.mock(MessageServiceImpl::class.java)
    val mockOutputMessageService = Mockito.mock(MessageServiceImpl::class.java)
    val initiateService =
        InitiateServiceMas(
            mockAmazonS3,
            mockAppianService,
            mockEnvironmentModule,
            mockHyperScienceService,
            mockInputMessageService,
            mockOutputMessageService)

    Mockito.`when`(mockEnvironmentModule.getSystemEnvironmentVariable("SHOULD_SEND_DUPS_TO_UPDATE"))
        .thenReturn("true")
    Mockito.`when`(mockEnvironmentModule.getSystemEnvironmentVariable("S3_BUCKET_NAME"))
        .thenReturn("SAMPLE_BUCKET")
    Mockito.`when`(mockEnvironmentModule.getSystemEnvironmentVariable("INPUT_SQS_URL"))
        .thenReturn("INPUT_SQS_URL")
    Mockito.`when`(
            mockAppianService.queryForDocuments(
                ArgumentMatchers.any(QueryDocumentRequest::class.java)))
        .thenReturn(
            WebClientHelper.makeCallResponseJson(
                makeQueryDocumentResponse(
                    setOf(
                        makeDocument(331),
                        makeDocumentWithNoUpload(332),
                        makeDocument(333),
                        makeDocument(334)))))
    Mockito.`when`(
            mockHyperScienceService.getSubmissionByExternalId(
                any(String::class.java), any(Boolean::class.java)))
        .thenReturn(WebClientHelper.makeCallResponseSubmission(null))
        //                .thenReturn(WebClientHelper.makeCallResponseSubmission(null))
        .thenReturn(WebClientHelper.makeCallResponseSubmission(null))
        .thenReturn(WebClientHelper.makeCallResponseSubmission(Submission(externalId = "334")))
    Mockito.`when`(
            mockAppianService.updateDocumentStatus(
                ArgumentMatchers.any(UpdateDocumentStatusRequest::class.java)))
        .thenReturn(WebClientHelper.makeCallResponseJson(WebClientHelper.EMPTY_JSON_OBJECT))
    Mockito.`when`(mockAppianService.downloadDocument(ArgumentMatchers.anyString()))
        .thenReturn(WebClientHelper.makeCallResponseByteStream())
    Mockito.`when`(mockInputMessageService.sqsUrl).thenReturn("HyperScience Input Queue")
    Mockito.`when`(mockOutputMessageService.sqsUrl).thenReturn("HyperScience Output Queue")
    val result =
        initiateService.initiateHyperScience(makeDocType(), context, InitiateFixtures.DOC_TYPE)
    Assertions.assertEquals(
        "| Successfully sent 2 messages to HyperScience Input Queue for document type 21-22 " +
            "| Successfully sent 1 messages to HyperScience Output Queue for document type 21-22",
        result,
        "initiateHyperScience should pass with multiple documents")
    Mockito.verify(mockAmazonS3, Mockito.times(2))
        .putObject(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString(),
            ArgumentMatchers.any(InputStream::class.java),
            ArgumentMatchers.any(ObjectMetadata::class.java))
    Mockito.verify(mockInputMessageService, Mockito.times(2))
        .putMessageOnQueue(ArgumentMatchers.any(SubmissionRequest::class.java))
    Mockito.verify(mockOutputMessageService, Mockito.times(1))
        .putMessageOnQueue(ArgumentMatchers.any(SubmissionNotification::class.java))
    Mockito.verify(mockAppianService, Mockito.times(4))
        .updateDocumentStatus(ArgumentMatchers.any(UpdateDocumentStatusRequest::class.java))
  }

  @Test
  fun initiateShouldNotPutMessagesOnTheQueueIfNoDocumentsReturn() {
    val mockAmazonS3 = Mockito.mock(AmazonS3::class.java)
    val mockAppianService = Mockito.mock(AppianService::class.java)
    val mockEnvironmentModule = Mockito.mock(EnvironmentModule::class.java)
    val mockHyperScienceService = Mockito.mock(HyperScienceService::class.java)
    val mockInputMessageService = Mockito.mock(MessageServiceImpl::class.java)
    val mockOutputMessageService = Mockito.mock(MessageServiceImpl::class.java)
    val initiateService =
        InitiateServiceMas(
            mockAmazonS3,
            mockAppianService,
            mockEnvironmentModule,
            mockHyperScienceService,
            mockInputMessageService,
            mockOutputMessageService)
    Mockito.`when`(mockEnvironmentModule.getSystemEnvironmentVariable("S3_BUCKET_NAME"))
        .thenReturn("SAMPLE_BUCKET")
    Mockito.`when`(
            mockAppianService.queryForDocuments(
                ArgumentMatchers.any(QueryDocumentRequest::class.java)))
        .thenReturn(WebClientHelper.makeCallResponseJson(makeQueryDocumentResponse(setOf())))
    Mockito.`when`(mockInputMessageService.sqsUrl).thenReturn("HyperScience Input Queue")
    val result =
        initiateService.initiateHyperScience(makeDocType(), context, InitiateFixtures.DOC_TYPE)
    Assertions.assertEquals(
        "| Sent 0 messages to HyperScience Input Queue and null for document type 21-22",
        result,
        "initiateHyperScience should pass with no documents returned")
    Mockito.verify(mockAmazonS3, Mockito.times(0))
        .putObject(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString(),
            ArgumentMatchers.any(InputStream::class.java),
            ArgumentMatchers.any(ObjectMetadata::class.java))
    Mockito.verify(mockInputMessageService, Mockito.times(0))
        .putMessageOnQueue(ArgumentMatchers.any(SubmissionRequest::class.java))
    Mockito.verify(mockAppianService, Mockito.times(0))
        .updateDocumentStatus(ArgumentMatchers.any(UpdateDocumentStatusRequest::class.java))
  }

  @Test
  fun initiateShouldEndIfAppianQueryFails() {
    val mockAmazonS3 = Mockito.mock(AmazonS3::class.java)
    val mockAppianService = Mockito.mock(AppianService::class.java)
    val mockEnvironmentModule = Mockito.mock(EnvironmentModule::class.java)
    val mockHyperScienceService = Mockito.mock(HyperScienceService::class.java)
    val mockInputMessageService = Mockito.mock(MessageServiceImpl::class.java)
    val mockOutputMessageService = Mockito.mock(MessageServiceImpl::class.java)
    val initiateService =
        InitiateServiceMas(
            mockAmazonS3,
            mockAppianService,
            mockEnvironmentModule,
            mockHyperScienceService,
            mockInputMessageService,
            mockOutputMessageService)
    Mockito.`when`(mockEnvironmentModule.getSystemEnvironmentVariable("S3_BUCKET_NAME"))
        .thenReturn("SAMPLE_BUCKET")
    Mockito.`when`(
            mockAppianService.queryForDocuments(
                ArgumentMatchers.any(QueryDocumentRequest::class.java)))
        .thenThrow(RuntimeException("Query for documents failed"))
    val exceptionResult: Exception =
        Assertions.assertThrows(RuntimeException::class.java) {
          initiateService.initiateHyperScience(makeDocType(), context, InitiateFixtures.DOC_TYPE)
        }
    Assertions.assertEquals(
        "Query for documents failed",
        exceptionResult.message,
        "initiateHyperScience should fail if query fails")
    Mockito.verify(mockAmazonS3, Mockito.times(0))
        .putObject(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString(),
            ArgumentMatchers.any(InputStream::class.java),
            ArgumentMatchers.any(ObjectMetadata::class.java))
    Mockito.verify(mockInputMessageService, Mockito.times(0))
        .putMessageOnQueue(ArgumentMatchers.any(SubmissionRequest::class.java))
    Mockito.verify(mockAppianService, Mockito.times(0))
        .updateDocumentStatus(ArgumentMatchers.any(UpdateDocumentStatusRequest::class.java))
  }

  @Test
  fun initiateShouldEndIfAppianDownloadDocFails() {
    val mockAmazonS3 = Mockito.mock(AmazonS3::class.java)
    val mockAppianService = Mockito.mock(AppianService::class.java)
    val mockEnvironmentModule = Mockito.mock(EnvironmentModule::class.java)
    val mockHyperScienceService = Mockito.mock(HyperScienceService::class.java)
    val mockInputMessageService = Mockito.mock(MessageServiceImpl::class.java)
    val mockOutputMessageService = Mockito.mock(MessageServiceImpl::class.java)
    val initiateService =
        InitiateServiceMas(
            mockAmazonS3,
            mockAppianService,
            mockEnvironmentModule,
            mockHyperScienceService,
            mockInputMessageService,
            mockOutputMessageService)
    Mockito.`when`(mockEnvironmentModule.getSystemEnvironmentVariable("S3_BUCKET_NAME"))
        .thenReturn("SAMPLE_BUCKET")
    Mockito.`when`(
            mockAppianService.queryForDocuments(
                ArgumentMatchers.any(QueryDocumentRequest::class.java)))
        .thenReturn(
            WebClientHelper.makeCallResponseJson(
                makeQueryDocumentResponse(
                    setOf(makeDocument(331), makeDocument(332), makeDocument(333)))))
    Mockito.`when`(
            mockHyperScienceService.getSubmissionByExternalId(
                any(String::class.java), any(Boolean::class.java)))
        .thenReturn(WebClientHelper.makeCallResponseSubmission(null))
        .thenReturn(WebClientHelper.makeCallResponseSubmission(null))
    Mockito.`when`(
            mockAppianService.updateDocumentStatus(
                ArgumentMatchers.any(UpdateDocumentStatusRequest::class.java)))
        .thenReturn(WebClientHelper.makeCallResponseJson(WebClientHelper.EMPTY_JSON_OBJECT))
    Mockito.`when`(mockAppianService.downloadDocument(ArgumentMatchers.anyString()))
        .thenReturn(WebClientHelper.makeCallResponseByteStream())
        .thenThrow(RuntimeException("Download document failed"))
    val exceptionResult: Exception =
        Assertions.assertThrows(RuntimeException::class.java) {
          initiateService.initiateHyperScience(makeDocType(), context, InitiateFixtures.DOC_TYPE)
        }
    Assertions.assertEquals(
        "Download document failed",
        exceptionResult.message,
        "initiateHyperScience should fail if query fails")
    Mockito.verify(mockAmazonS3, Mockito.times(1))
        .putObject(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString(),
            ArgumentMatchers.any(InputStream::class.java),
            ArgumentMatchers.any(ObjectMetadata::class.java))
    Mockito.verify(mockInputMessageService, Mockito.times(1))
        .putMessageOnQueue(ArgumentMatchers.any(SubmissionRequest::class.java))
    Mockito.verify(mockAppianService, Mockito.times(1))
        .updateDocumentStatus(ArgumentMatchers.any(UpdateDocumentStatusRequest::class.java))
  }

  private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)
}
