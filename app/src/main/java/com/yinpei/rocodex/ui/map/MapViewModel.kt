package com.yinpei.rocodex.ui.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yinpei.rocodex.data.model.MapData
import com.yinpei.rocodex.data.model.PointDetail
import com.yinpei.rocodex.data.model.RegionPointFeature
import com.yinpei.rocodex.data.repository.MapRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MapRepository(application)

    private val _currentMapId = MutableStateFlow(61)
    val currentMapId: StateFlow<Int> = _currentMapId.asStateFlow()

    private val _currentMapData = MutableStateFlow<MapData?>(null)
    val currentMapData: StateFlow<MapData?> = _currentMapData.asStateFlow()

    private val _points = MutableStateFlow<List<PointDetail>>(emptyList())
    val points: StateFlow<List<PointDetail>> = _points.asStateFlow()

    private val _selectedPointTypes = MutableStateFlow<Set<Int>>(emptySet())
    val selectedPointTypes: StateFlow<Set<Int>> = _selectedPointTypes.asStateFlow()

    fun togglePointType(typeId: Int) {
        val current = _selectedPointTypes.value.toMutableSet()
        if (current.contains(typeId)) {
            current.remove(typeId)
        } else {
            current.add(typeId)
        }
        _selectedPointTypes.value = current
    }

    private val _regionPoints = MutableStateFlow<List<RegionPointFeature>>(emptyList())
    val regionPoints: StateFlow<List<RegionPointFeature>> = _regionPoints.asStateFlow()

    init {
        viewModelScope.launch {
            loadPointsForMap(61)
        }
    }

    fun setMapId(mapId: Int) {
        if (_currentMapId.value == mapId) return
        _currentMapId.value = mapId
        loadPointsForMap(mapId)
    }

    private fun loadPointsForMap(mapId: Int) {
        viewModelScope.launch {
            // clear points immediately for "instant sync clear"
            _points.value = emptyList()
            _regionPoints.value = emptyList()
            _currentMapData.value = null
            
            _currentMapData.value = repository.getMapDataById(mapId)
            _points.value = repository.getPointsByMapId(mapId)
            
            if (mapId == 61) {
                _regionPoints.value = repository.getAllRegionPoints()
            }
        }
    }
}