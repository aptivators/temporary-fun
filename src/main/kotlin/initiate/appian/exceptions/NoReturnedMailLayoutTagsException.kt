package initiate.appian.exceptions

class NoReturnedMailLayoutTagsException : RuntimeException {
    constructor(err: Throwable) : super(err) {}
    constructor(message: String) : super(message) {}
}
