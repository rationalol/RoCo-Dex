package com.yinpei.rocodex.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PointDetail(
    val id: Int,
    val title: String,
    @SerialName("mapId") val map_id: Int,
    @SerialName("lng") val bx: Double,
    @SerialName("lat") val by: Double,
    @SerialName("typeId") val type: Int = 0
)

@Serializable
data class PointFeature(
    val properties: PointDetail
)

@Serializable
data class PointFeatureCollection(
    val features: List<PointFeature>
)

@Serializable
data class RegionPointProperties(
    val id: Int,
    val name: String,
    val level: Int = 1
)

@Serializable
data class RegionPointGeometry(
    val type: String,
    val coordinates: List<Double> // [lng, lat]
)

@Serializable
data class RegionPointFeature(
    val id: Int,
    val properties: RegionPointProperties,
    val geometry: RegionPointGeometry
)

@Serializable
data class RegionPointFeatureCollection(
    val features: List<RegionPointFeature>
)

@Serializable
data class MapTileSet(
    val bounds: List<Double> = emptyList()
)

@Serializable
data class MapData(
    val id: Int,
    val title: String,
    val tile_sets: String
) {
    fun getBounds(): List<Double> {
        return try {
            val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
            val sets = json.decodeFromString<List<MapTileSet>>(tile_sets)
            sets.firstOrNull()?.bounds ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}