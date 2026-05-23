package com.yinpei.rocodex.ui.map

import android.graphics.BitmapFactory
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yinpei.rocodex.data.model.RegionPointFeature
import com.yinpei.rocodex.ui.theme.RoCoFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.ln
import kotlin.math.tan
import kotlin.math.PI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onBack: () -> Unit,
    viewModel: MapViewModel = viewModel()
) {
    val regionPoints by viewModel.regionPoints.collectAsState()
    val currentMapId by viewModel.currentMapId.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("地图探索") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.Black)
        ) {
            RocoMapView(
                regionPoints = regionPoints,
                currentMapId = currentMapId
            )

            // 悬浮多地图切换 UI 组件 (MapSwitcherCard)
            MapSwitcherCard(
                currentMapId = currentMapId,
                onMapSelected = { mapId ->
                    viewModel.setMapId(mapId)
                },
                modifier = Modifier
                    .statusBarsPadding()
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun MapSwitcherCard(
    currentMapId: Int,
    onMapSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    val mapList = listOf(
        Pair(61, "卡洛西亚大陆"),
        Pair(91, "魔法学院")
    )
    val currentMapName = mapList.find { it.first == currentMapId }?.second ?: "未知地图"

    Surface(
        modifier = modifier
            .animateContentSize()
            .clip(RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        tonalElevation = 8.dp,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .clickable { expanded = !expanded }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Layers,
                    contentDescription = "Switch Map",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = currentMapName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            if (expanded) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                mapList.forEach { (id, name) ->
                    val isSelected = id == currentMapId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(if (expanded) 0.5f else 1f) // Ensure some width constraint
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                onMapSelected(id)
                                expanded = false
                            }
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer 
                                else Color.Transparent
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer 
                                    else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RocoMapView(
    regionPoints: List<RegionPointFeature>,
    currentMapId: Int
) {
    val context = LocalContext.current
    val minX = 254
    val maxX = 257
    val minY = 254
    val maxY = 257
    val tileSize = 256

    val tiles = remember { mutableStateMapOf<String, ImageBitmap>() }
    
    // Clear tiles when mapId changes
    LaunchedEffect(currentMapId) {
        tiles.clear()
        
        val tileFolder = if (currentMapId == 61) {
            "scripts/map/roco_tiles_dalu_9"
        } else {
            "scripts/map/roco_tiles_mofaxueyuan_9"
        }

        withContext(Dispatchers.IO) {
            for (x in minX..maxX) {
                for (y in minY..maxY) {
                    val filename = "$tileFolder/${x}_${y}.jpg"
                    try {
                        val stream = context.assets.open(filename)
                        val bitmap = BitmapFactory.decodeStream(stream)
                        tiles["${x}_${y}"] = bitmap.asImageBitmap()
                        stream.close()
                    } catch (e: Exception) {
                        // ignore missing tiles
                    }
                }
            }
        }
    }

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val textMeasurer = rememberTextMeasurer()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { centroid, pan, zoom, _ ->
                    val oldScale = scale
                    val newScale = (scale * zoom).coerceIn(0.2f, 5f)
                    val actualZoom = newScale / oldScale
                    offset = (offset + pan - centroid) * actualZoom + centroid
                    scale = newScale
                }
            }
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y,
                transformOrigin = TransformOrigin(0f, 0f)
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw Tiles
            for (x in minX..maxX) {
                for (y in minY..maxY) {
                    val tile = tiles["${x}_${y}"]
                    if (tile != null) {
                        val dstOffset = IntOffset((x - minX) * tileSize, (y - minY) * tileSize)
                        drawImage(
                            image = tile,
                            dstOffset = dstOffset,
                            dstSize = IntSize(tileSize, tileSize)
                        )
                    }
                }
            }

            val zoomLevel = 9

            // Draw Region Texts (地名)
            val textStyleRegion = TextStyle(
                color = Color.Black,
                fontSize = 2.sp,
                fontFamily = RoCoFamily,
                shadow = Shadow(color = Color.White, blurRadius = 4f)
            )

            // Helper to measure and draw text centered
            fun drawCenteredText(text: String, px: Float, py: Float, style: TextStyle) {
                val textLayoutResult = textMeasurer.measure(
                    text = text,
                    style = style
                )
                // Fix Text Anchor Alignment: Center text around (px, py)
                val textOffset = Offset(
                    x = px - textLayoutResult.size.width / 2f,
                    y = py - textLayoutResult.size.height / 2f
                )
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = textOffset
                )
            }

            // Optional: Filter regionPoints conceptually. Since region_points.json doesn't have mapId,
            // we'll draw them if they fall into typical bounds, but we just draw them all for now.
            // If they are specific to a map, the original code had them drawn. 
            regionPoints.forEach { feature ->
                if (feature.geometry.coordinates.size >= 2) {
                    val lng = feature.geometry.coordinates[0]
                    val lat = feature.geometry.coordinates[1]

                    val (worldPx, worldPy) = mapLibreLngLatToPixel(lng, lat, zoomLevel, tileSize)
                    val px = worldPx - (minX * tileSize)
                    val py = worldPy - (minY * tileSize)
                    
                    // Draw centered text
                    drawCenteredText(feature.properties.name, px, py, textStyleRegion)
                }
            }
        }
    }
}

/**
 * 完美还原 MapLibre GL 的经纬度到绝对世界像素的转换
 */
fun mapLibreLngLatToPixel(lng: Double, lat: Double, zoom: Int = 9, tileSize: Int = 256): Pair<Float, Float> {
    val totalWidth = tileSize * (1 shl zoom)
    val x = ((lng + 180.0) / 360.0) * totalWidth
    val latRad = Math.toRadians(lat)
    val clampedLatRad = latRad.coerceIn(-1.48442222, 1.48442222)
    val mercator = ln(tan(PI / 4.0 + clampedLatRad / 2.0))
    val y = (0.5 - mercator / (2.0 * PI)) * totalWidth
    return Pair(x.toFloat(), y.toFloat())
}
