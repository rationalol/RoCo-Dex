package com.yinpei.rocodex.ui.map

import android.graphics.BitmapFactory
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yinpei.rocodex.data.model.MapData
import com.yinpei.rocodex.data.model.PointDetail
import com.yinpei.rocodex.data.model.RegionPointFeature
import com.yinpei.rocodex.ui.theme.RoCoFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.ln
import kotlin.math.tan
import kotlin.math.PI

data class PointType(val id: Int, val name: String, val iconPath: String)
data class PointCategory(val name: String, val items: List<PointType>)

val pointCategories = listOf(
    PointCategory("收集", listOf(
        PointType(5483, "果实", "map/icons/collection/果实.png"),
        PointType(5481, "蓝色眠枭之星", "map/icons/collection/蓝色眠枭之星.png"),
        PointType(5480, "黄色眠枭之星", "map/icons/collection/黄色眠枭之星.png")
    )),
    PointCategory("地点", listOf(
        PointType(5454, "副本", "map/icons/locations/副本.png")
    )),
    PointCategory("矿石", listOf(
        PointType(5511, "黄石榴石", "map/icons/mineral/黄石榴石.png"),
        PointType(5512, "紫莲刚玉", "map/icons/mineral/紫莲刚玉.png"),
        PointType(5514, "蓝晶碧玺", "map/icons/mineral/蓝晶碧玺.png"),
        PointType(5513, "黑晶琉璃", "map/icons/mineral/黑晶琉璃.png")
    )),
    PointCategory("植物", listOf(
        PointType(5597, "紫雀花", "map/icons/plant/紫雀花.png"),
        PointType(5585, "喵喵草", "map/icons/plant/喵喵草.png"),
        PointType(5587, "伞伞菌", "map/icons/plant/伞伞菌.png"),
        PointType(5582, "蓝掌", "map/icons/plant/蓝掌.png"),
        PointType(5577, "凤眼莲", "map/icons/plant/凤眼莲.png"),
        PointType(5584, "蜜黄菌", "map/icons/plant/蜜黄菌.png"),
        PointType(5589, "睡铃", "map/icons/plant/睡铃.png"),
        PointType(5593, "雪菇", "map/icons/plant/雪菇.png"),
        PointType(5592, "星霜花", "map/icons/plant/星霜花.png"),
        PointType(5588, "石耳", "map/icons/plant/石耳.png"),
        PointType(5600, "恶魔雪茄", "map/icons/plant/恶魔雪茄.png"),
        PointType(5601, "骨片", "map/icons/plant/骨片.png"),
        PointType(5594, "荧光兰", "map/icons/plant/荧光兰.png"),
        PointType(5602, "象牙花", "map/icons/plant/象牙花.png"),
        PointType(5598, "藻羽花", "map/icons/plant/藻羽花.png"),
        PointType(5578, "海桑花", "map/icons/plant/海桑花.png"),
        PointType(5596, "紫晶菇", "map/icons/plant/紫晶菇.png"),
        PointType(5580, "花星角", "map/icons/plant/花星角.png"),
        PointType(5591, "向阳花", "map/icons/plant/向阳花.png"),
        PointType(5603, "杏黄贝", "map/icons/plant/杏黄贝.png"),
        PointType(5576, "大嘴花", "map/icons/plant/大嘴花.png"),
        PointType(5586, "喷气菇", "map/icons/plant/喷气菇.png"),
        PointType(5590, "天使草", "map/icons/plant/天使草.png"),
        PointType(5595, "幽幽草", "map/icons/plant/幽幽草.png"),
        PointType(5609, "海珊瑚", "map/icons/plant/海珊瑚.png"),
        PointType(5579, "海神花", "map/icons/plant/海神花.png"),
        PointType(5581, "火焰花", "map/icons/plant/火焰花.png"),
        PointType(5599, "短木莲", "map/icons/plant/短木莲.png")
    ))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onBack: () -> Unit,
    viewModel: MapViewModel = viewModel()
) {
    val regionPoints by viewModel.regionPoints.collectAsState()
    val points by viewModel.points.collectAsState()
    val currentMapId by viewModel.currentMapId.collectAsState()
    val currentMapData by viewModel.currentMapData.collectAsState()
    val selectedPointTypes by viewModel.selectedPointTypes.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        RocoMapView(
            regionPoints = regionPoints,
            points = points,
            selectedPointTypes = selectedPointTypes,
            currentMapId = currentMapId,
            currentMapData = currentMapData
        )

        // 悬浮返回键 (左上角)
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "返回",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        // 悬浮多地图切换与点位筛选 UI 组件 (左下角)
        MapSwitcherCard(
            currentMapId = currentMapId,
            onMapSelected = { mapId ->
                viewModel.setMapId(mapId)
            },
            selectedPointTypes = selectedPointTypes,
            onTogglePointType = { typeId ->
                viewModel.togglePointType(typeId)
            },
            modifier = Modifier
                .navigationBarsPadding()
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        )
    }
}

@Composable
fun MapSwitcherCard(
    currentMapId: Int,
    onMapSelected: (Int) -> Unit,
    selectedPointTypes: Set<Int>,
    onTogglePointType: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    
    val mapList = listOf(
        Pair(61, "卡洛西亚大陆"),
        Pair(91, "魔法学院")
    )
    val currentMapName = mapList.find { it.first == currentMapId }?.second ?: "未知地图"

    Surface(
        modifier = modifier
            .widthIn(max = 240.dp)
            .heightIn(max = 400.dp)
            .animateContentSize()
            .clip(RoundedCornerShape(24.dp)),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        tonalElevation = 8.dp,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
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
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            if (expanded) {
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(scrollState)
                ) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp))
                    
                    // 地图列表
                    mapList.forEach { (id, name) ->
                        val isSelected = id == currentMapId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
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
                                        else MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp))

                    // 点位筛选
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pointCategories.forEach { category ->
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                            
                            category.items.forEach { item ->
                                val isSelected = selectedPointTypes.contains(item.id)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable { onTogglePointType(item.id) }
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.secondaryContainer 
                                            else Color.Transparent
                                        )
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = "file:///android_asset/${item.iconPath}",
                                        contentDescription = item.name,
                                        modifier = Modifier.size(24.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer 
                                                else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RocoMapView(
    regionPoints: List<RegionPointFeature>,
    points: List<PointDetail>,
    selectedPointTypes: Set<Int>,
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
    
    // Load icons
    val icons = remember { mutableStateMapOf<Int, ImageBitmap>() }
    
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            pointCategories.flatMap { it.items }.forEach { item ->
                try {
                    val stream = context.assets.open(item.iconPath)
                    val bitmap = BitmapFactory.decodeStream(stream)
                    icons[item.id] = bitmap.asImageBitmap()
                    stream.close()
                } catch (e: Exception) {
                    // ignore missing icons
                }
            }
        }
    }
    
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

            // Draw Point Icons
            points.forEach { point ->
                if (selectedPointTypes.contains(point.type)) {
                    val icon = icons[point.type]
                    if (icon != null) {
                        val (worldPx, worldPy) = mapLibreLngLatToPixel(point.bx, point.by, 10, tileSize)
                        val px = worldPx - (minX * tileSize)
                        val py = worldPy - (minY * tileSize)
                        
                        // Draw centered icon
                        val iconWidth = icon.width.toFloat()
                        val iconHeight = icon.height.toFloat()
                        drawImage(
                            image = icon,
                            topLeft = Offset(px - iconWidth / 2f, py - iconHeight / 2f)
                        )
                    }
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
