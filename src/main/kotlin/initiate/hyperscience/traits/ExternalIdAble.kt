package initiate.hyperscience.traits

import com.fasterxml.jackson.annotation.JsonIgnore

open class ExternalIdAble(open val externalId: String?) {
    @JsonIgnore
    fun parsedExternalId(): Int? = externalId?.replace("\\D".toRegex(), "")?.toInt()
}
