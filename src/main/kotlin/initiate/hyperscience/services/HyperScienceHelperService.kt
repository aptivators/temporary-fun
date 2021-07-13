package initiate.hyperscience.services

class HyperScienceHelperService {
    data class IdAndCoordinates(val path: String, val coordinates: Map<String, String>)

    fun getImagePathAndCoordinates(url: String): IdAndCoordinates {
        val imageInd = url.lastIndexOf("/") + 1
        val qIndex = url.indexOf("?")
        val id = url.slice(imageInd until qIndex)
        return IdAndCoordinates(id, coordinatesList.map(mapCoordinates(url)).toMap())
    }

    private val coordinatesList = setOf("start_x", "start_y", "end_x", "end_y")

    private fun mapCoordinates(url: String): (String) -> Pair<String, String> = { coordinate ->
        val startInd = url.indexOf(coordinate) + coordinate.length + 1
        val end = url.indexOf(string = "&", startIndex = startInd)
        val endInd = if (end < 0) url.length else end
        coordinate to url.slice(startInd until endInd)
    }
}