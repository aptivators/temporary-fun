package initiate.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.util.json.Jackson
import initiate.appian.modules.WebClientModule
import initiate.aws_integration.modules.AWSModule
import initiate.aws_integration.services.MessageService
import initiate.common.constants.SubmissionConstants
import initiate.common.modules.EnvironmentModule
import initiate.hyperscience.modules.HyperScienceModule
import initiate.models.ExtractConfigurations

class Dispatcher {
    fun initiateHyperScience(input: Any, context: Context): String {
        println("Entering the Initiate function with Input: $input")
        val extractConfigurations = getExtractConfigurations(input)
        val initiateService = getInitiateServiceMas(extractConfigurations)
        val initiateOcrProcessingResponse = extractConfigurations.docTypeList!!.joinToString {
            if (!sequenceOf(
                    SubmissionConstants.DOC_TYPE_TPC,
                    SubmissionConstants.DOC_TYPE_CORRESPONDENCE,
                    SubmissionConstants.READY_FOR_RECORD_MATCHING
                ).contains(it)
            ) {
                initiateService.initiateHyperScience(input, context, it)
            } else " Skipping HyperScience for docType of $it "
        }
        println("initiateOcrProcessingResponse is $initiateOcrProcessingResponse")
        return setOf(initiateOcrProcessingResponse).joinToString("\n")
    }

    fun initiateHyperScienceFoia(input: Any, context: Context): String {
        println("Entering the Foia Initiate function with Input: $input")
        val extractConfigurations = getExtractConfigurations(input)
        val unknownFoiaForms = if (extractConfigurations.docTypeList!!.contains(SubmissionConstants.READY_FOR_INDEXING))
            initiateServiceFoiaUnknown.initiateHyperScience(input, context)
        else "Not checking unknown FOIA forms"

        return extractConfigurations.docTypeList.joinToString {
            if (SubmissionConstants.READY_FOR_INDEXING != it) {
                initiateServiceFoia.initiateHyperScience(input, context, it)
            } else " Skipping HyperScience for docType of $it "
        }.plus(", $unknownFoiaForms")
    }

    private val initiateServiceFoia: InitiateServiceFoia
        get() = InitiateServiceFoia(
            AWSModule.amazonS3,
            initiate.appian_foia.modules.WebClientModule.getAppianFoiaService(AWSModule.secretsMap),
            EnvironmentModule(),
            HyperScienceModule.hyperScienceService,
            AWSModule.foiaRequestInputMessageService,
            AWSModule.foiaOutputMessageService
        )

    private val initiateServiceFoiaUnknown: InitiateServiceFoiaUnknown
        get() = InitiateServiceFoiaUnknown(
            AWSModule.amazonS3,
            initiate.appian_foia.modules.WebClientModule.getAppianFoiaService(AWSModule.secretsMap),
            EnvironmentModule(),
            HyperScienceModule.hyperScienceService,
            AWSModule.foiaRequestInputMessageService,
            AWSModule.foiaOutputMessageService
        )

    private fun getExtractConfigurations(input: Any): ExtractConfigurations {
        return try {
            val extractConfigurations =
                Jackson.fromJsonString(Jackson.toJsonString(input), ExtractConfigurations::class.java)
            when {
                extractConfigurations.docType?.isNotBlank() == true ->
                    ExtractConfigurations(docTypeList = listOf(extractConfigurations.docType))
                extractConfigurations.docTypeList?.isNotEmpty() == true ->
                    extractConfigurations
                else ->
                    ExtractConfigurations(docTypeList = listOf(System.getenv("DOC_TYPE")))
            }
        } catch (exception: Exception) {
            println("Could not parse extractConfigurations from input with exception of type ${exception.javaClass} and message ${exception.message}")
            ExtractConfigurations(docTypeList = listOf(System.getenv("DOC_TYPE")))
        }
    }

    private fun getInitiateServiceMas(extractConfigurations: ExtractConfigurations): InitiateService {
        return InitiateServiceMas(
            AWSModule.amazonS3,
            WebClientModule.getAppianService(AWSModule.secretsMap),
            EnvironmentModule(),
            HyperScienceModule.hyperScienceService,
            getInputMessageService(extractConfigurations),
            AWSModule.masOutputMessageService
        )
    }

    private fun getInputMessageService(extractConfigurations: ExtractConfigurations): MessageService {
        return if (extractConfigurations.docType == SubmissionConstants.DOC_TYPE_EVD_DC ||
            extractConfigurations.docTypeList?.contains(SubmissionConstants.DOC_TYPE_EVD_DC) == true
        ) {
            AWSModule.deathCertificateMessageService
        } else
            AWSModule.messageService
    }

}
