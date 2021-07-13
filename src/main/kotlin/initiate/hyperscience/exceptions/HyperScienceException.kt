package initiate.hyperscience.exceptions

class HyperScienceException : RuntimeException {
    constructor(err: Throwable?) : super(err)
    constructor(message: String?) : super(message)
}