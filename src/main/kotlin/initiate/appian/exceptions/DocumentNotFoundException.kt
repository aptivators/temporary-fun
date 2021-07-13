package initiate.appian.exceptions

class DocumentNotFoundException : RuntimeException {
    constructor(err: Throwable?) : super(err) {}
    constructor(message: String?) : super(message) {}
}
