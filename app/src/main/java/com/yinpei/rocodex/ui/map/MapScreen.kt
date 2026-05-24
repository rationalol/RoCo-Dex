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
import com.yinpei.rocodex.data.model.MapData
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
    val currentMapData by viewModel.currentMapData.collectAsState()

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
                currentMapId = currentMapId,
                currentMapData = currentMapData
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
    currentMapId: Int,
    currentMapData: MapData?
) {
    val context = LocalContext.current
    val tileSize = 256
    
    val bounds = currentMapData?.getBounds() ?: emptyList()
    var minX = 508
    var maxX = 515
    var minY = 508
    var maxY = 515

    if (bounds.size == 4) {
        val minLng = bounds[0]
        val minLat = bounds[1]
        val maxLng = bounds[2]
        val maxLat = bounds[3]
        
        // Use Zoom 10 for coordinate system
        val (topLeftX, topLeftY) = mapLibreLngLatToPixel(minLng, maxLat, 10, tileSize)
        val (bottomRightX, bottomRightY) = mapLibreLngLatToPixel(maxLng, minLat, 10, tileSize)
        
        minX = (topLeftX / tileSize).toInt()
        maxX = (bottomRightX / tileSize).toInt()
        minY = (topLeftY / tileSize).toInt()
        maxY = (bottomRightY / tileSize).toInt()
    }

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val textMeasurer = rememberTextMeasurer()
    
    val renderZoom = if (scale <= 1.5f) 9 else 10

    val tiles9 = remember { mutableStateMapOf<String, ImageBitmap>() }
    val tiles10 = remember { mutableStateMapOf<String, ImageBitmap>() }
    
    LaunchedEffect(currentMapId) {
        scale = 1f
        offset = Offset.Zero
    }

    // Load tiles when mapId, renderZoom, or mapData changes
    LaunchedEffect(currentMapId, renderZoom, currentMapData) {
        if (currentMapData == null) return@LaunchedEffect
        tiles9.clear()
        tiles10.clear()
        
        val folderBase = if (currentMapId == 61) "scripts/map/roco_tiles_dalu" else "scripts/map/roco_tiles_mofaxueyuan"
        val folder9 = "${folderBase}_9"
        val folder10 = "${folderBase}_10"

        withContext(Dispatchers.IO) {
            val minX9 = minX / 2
            val maxX9 = maxX / 2
            val minY9 = minY / 2
            val maxY9 = maxY / 2
            
            // 始终加载 9 层级瓦片作为 fallback 或是缩小状态下的底图
            for (x9 in minX9..maxX9) {
                for (y9 in minY9..maxY9) {
                    val filename = "$folder9/${x9}_${y9}.jpg"
                    try {
                        val stream = context.assets.open(filename)
                        val bitmap = BitmapFactory.decodeStream(stream)
                        tiles9["${x9}_${y9}"] = bitmap.asImageBitmap()
                        stream.close()
                    } catch (e: Exception) {
                        // ignore missing tiles
                    }
                }
            }

            // 如果当前在 10 层级，再尝试加载 10 级瓦片
            if (renderZoom == 10) {
                for (x in minX..maxX) {
                    for (y in minY..maxY) {
                        val filename = "$folder10/${x}_${y}.jpg"
                        try {
                            val stream = context.assets.open(filename)
                            val bitmap = BitmapFactory.decodeStream(stream)
                            tiles10["${x}_${y}"] = bitmap.asImageBitmap()
                            stream.close()
                        } catch (e: Exception) {
                            // ignore missing tiles
                        }
                    }
                }
            }
        }
    }

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
            // Draw Tiles - 统一在 Zoom 10 网格下遍历
            for (x in minX..maxX) {
                for (y in minY..maxY) {
                    val dstOffset = IntOffset((x - minX) * tileSize, (y - minY) * tileSize)
                    val tile10 = if (renderZoom == 10) tiles10["${x}_${y}"] else null
                    
                    if (tile10 != null) {
                        // 如果有 10 级瓦片，直接绘制
                        drawImage(
                            image = tile10,
                            dstOffset = dstOffset,
                            dstSize = IntSize(tileSize, tileSize)
                        )
                    } else {
                        // Fallback 降级：如果对应的 _10 瓦片不存在，或处于 renderZoom == 9 时，寻找父级 _9 瓦片
                        val x9 = x / 2
                        val y9 = y / 2
                        val tile9 = tiles9["${x9}_${y9}"]
                        
                        if (tile9 != null) {
                            // 裁剪对应的四分之一区域
                            val halfSize = tileSize / 2
                            val srcOffsetX = (x % 2) * halfSize
                            val srcOffsetY = (y % 2) * halfSize
                            
                            drawImage(
                                image = tile9,
                                srcOffset = IntOffset(srcOffsetX, srcOffsetY),
                                srcSize = IntSize(halfSize, halfSize),
                                dstOffset = dstOffset,
                                dstSize = IntSize(tileSize, tileSize)
                            )
                        }
                    }
                }
            }

            // Draw Region Texts (地名)
            val textStyleRegion = TextStyle(
                color = Color.Black,
                fontSize = 8.sp,
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

            regionPoints.forEach { feature ->
                if (feature.geometry.coordinates.size >= 2) {
                    val lng = feature.geometry.coordinates[0]
                    val lat = feature.geometry.coordinates[1]

                    val (worldPx, worldPy) = mapLibreLngLatToPixel(lng, lat, 10, tileSize)
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
fun mapLibreLngLatToPixel(lng: Double, lat: Double, zoom: Int = 10, tileSize: Int = 256): Pair<Float, Float> {
    val totalWidth = tileSize * (1 shl zoom)
    val x = ((lng + 180.0) / 360.0) * totalWidth
    val latRad = Math.toRadians(lat)
    val clampedLatRad = latRad.coerceIn(-1.48442222, 1.48442222)
    val mercator = ln(tan(PI / 4.0 + clampedLatRad / 2.0))
    val y = (0.5 - mercator / (2.0 * PI)) * totalWidth
    return Pair(x.toFloat(), y.toFloat())
}
