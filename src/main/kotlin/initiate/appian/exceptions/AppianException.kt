package initiate.appian.exceptions

class AppianException : RuntimeException {
    constructor(err: Throwable) : super(err) {}
    constructor(message: String) : super(message) {}
}