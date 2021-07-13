package initiate.appian.enums

enum class EventTypeId(val id: Int, val eventNote: String, val categoryId: CategoryId) {
    PROCESSING(20, "OCR Process Started", CategoryId.CONTROL_POINT),
    COMPLETE(22, "OCR Process Completed", CategoryId.CONTROL_POINT),
    READY_FOR_CLASSIFIER(23, "Classification Needed", CategoryId.CONTROL_POINT),
    MISSING_REQUIRED_PAGE(38, "Required Page Not Found", CategoryId.CONTROL_POINT),
    OUTDATED(40, "Outdated Form", CategoryId.EXCEPTION),
    REQUIRED_FIELD_MISSING(45, "Required Field Not Extracted", CategoryId.EXCEPTION),
    VETERAN_MATCHED(46, "Veteran Matched", CategoryId.CONTROL_POINT),
    VETERAN_NOT_MATCHED(71, "Veteran Not Matched", CategoryId.CONTROL_POINT),
    DEPENDENT_MATCHED(47, "Dependent Matched", CategoryId.DEBUG),
    DEPENDENT_NOT_MATCHED(72, "Dependent Not Matched", CategoryId.DEBUG),
    SPOUSE_MATCHED(48, "Spouse Matched", CategoryId.DEBUG),
    SPOUSE_NOT_MATCHED(73, "Spouse Not Matched", CategoryId.DEBUG),
    OUT_OF_SCOPE_21_22(101, "Business Offramp: 21-22a Contains More Than Two Pages", CategoryId.EXCEPTION),
    FOIA_UNKNOWN_FORM(
        38,
        "Extract Data OCR (EDO) some required page(s) were not identified and the document has some unidentified pages",
        CategoryId.EXCEPTION
    ),
    OCR_REQUIRED_FIELD_NOT_EXTRACTED(45, "Required Field Not Extracted", CategoryId.EXCEPTION),
    CLAIMANT_ADDRESS_VALIDATED(100, "Address on form validated (Claimant Address)", CategoryId.DEBUG),
    VETERAN_ADDRESS_VALIDATED(195, "Address on form validated (Veteran Address)", CategoryId.CONTROL_POINT),
    NEW_ADDRESS_VALIDATED(196, "Address on form validated (New Address)", CategoryId.DEBUG),
    OCR_PROCESS_HALTED(147, "OCR Process Halted", CategoryId.EXCEPTION)
}

enum class CategoryId(val id: Int, val description: String) {
    CONTROL_POINT(1, "Control Point"),
    DEBUG(3, "Debug"),
    EXCEPTION(4, "Exception")
}
