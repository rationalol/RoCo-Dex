package com.yinpei.rocodex.data.repository

import android.content.Context
import com.yinpei.rocodex.data.model.PointDetail
import com.yinpei.rocodex.data.model.PointFeatureCollection
import com.yinpei.rocodex.data.model.RegionPointFeature
import com.yinpei.rocodex.data.model.RegionPointFeatureCollection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStreamReader

class MapRepository(private val context: Context) {

    private var cachedPoints: List<PointDetail>? = null
    private var cachedRegions: List<RegionPointFeature>? = null
    
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getAllPoints(): List<PointDetail> = withContext(Dispatchers.IO) {
        if (cachedPoints == null) {
            context.assets.open("map/all_points.json").use { inputStream ->
                val jsonString = InputStreamReader(inputStream).readText()
                val collection = json.decodeFromString<PointFeatureCollection>(jsonString)
                cachedPoints = collection.features.map { it.properties }
            }
        }
        return@withContext cachedPoints ?: emptyList()
    }

    suspend fun getPointsByMapId(mapId: Int): List<PointDetail> = withContext(Dispatchers.IO) {
        return@withContext getAllPoints().filter { it.map_id == mapId }
    }

    suspend fun getAllRegionPoints(): List<RegionPointFeature> = withContext(Dispatchers.IO) {
        if (cachedRegions == null) {
            context.assets.open("map/region_points.json").use { inputStream ->
                val jsonString = InputStreamReader(inputStream).readText()
                val collection = json.decodeFromString<RegionPointFeatureCollection>(jsonString)
                cachedRegions = collection.features
            }
        }
        return@withContext cachedRegions ?: emptyList()
    }
}