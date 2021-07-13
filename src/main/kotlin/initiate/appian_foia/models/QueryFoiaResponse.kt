package initiate.appian_foia.models

data class QueryFoiaResponse(
    val packets: Set<Data> = setOf()
)
//{
//    val documentsReturned: Int by lazy { this.packets?.data?.size ?: 0 }
//}
