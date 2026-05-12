package com.yinpei.rocodex.ui.gallery

import android.app.Activity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yinpei.rocodex.ui.components.FilterBar
import com.yinpei.rocodex.ui.components.PetCard
import kotlinx.coroutines.launch
// 确保所有相关的类都来自 material3 包
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import com.yinpei.rocodex.ui.theme.LocalWindowSizeClass


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3WindowSizeClassApi::class
)
@Composable
fun GalleryScreen(
    onPetClick: (Int) -> Unit,
    viewModel: GalleryViewModel = viewModel(),

) {
    val windowSize = LocalWindowSizeClass.current

    val cellsPerRow = when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 3
        WindowWidthSizeClass.Medium -> 5
        WindowWidthSizeClass.Expanded -> 6
        else -> 3
    }

    val pets by viewModel.pets.collectAsState()
    val selectedElements by viewModel.selectedElements.collectAsState()
    val isShinyOnly by viewModel.isShinyOnly.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // 将滑动状态从 gridState 换成 listState
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val showBackToTop = remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            // 给底部留一点空间，防止被可能存在的导航栏或 FAB 遮挡
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item(key = "header") {
                Column {
                    // 原来的 TopAppBar 变成了普通的 Composable
                    @OptIn(ExperimentalMaterial3Api::class)
                    TopAppBar(
                        title = {
                            Text(text = "精灵图鉴", fontWeight = FontWeight.Bold)
                        },
                        windowInsets = WindowInsets(0, 0, 0, 0),
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    // 搜索栏
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        placeholder = { Text("搜索精灵名称或编号", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        shape = CircleShape,
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                }


            }
// --- 第 2 部分：始终吸顶的筛选栏 ---
            stickyHeader(key = "filter_bar") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        // 【关键改动 2】吸顶元素必须设置纯色背景，否则上滑时底下的列表内容会透视过来，导致重叠混乱
                        .background(MaterialTheme.colorScheme.surface)
                    // 可选：添加一点底部的阴影或边框，让吸顶效果更具立体感
                ) {
                    FilterBar(
                        selectedElements = selectedElements,
                        onToggleElement = { viewModel.toggleElement(it) },
                        onClearFilters = { viewModel.clearFilters() },
                        showShinyFilter = true,
                        isShinyOnly = isShinyOnly,
                        onToggleShiny = { viewModel.toggleShiny() },
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
// --- 第 3 部分：精灵网格（用 Row 模拟 Grid） ---
            if (isLoading) {
                item(key = "loading") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {

                val chunkedPets = pets.chunked(cellsPerRow)

                items(
                    items = chunkedPets,
                    key = { row -> row.firstOrNull()?.id ?: -1 }
                ) { rowPets ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 12.dp,
                                vertical = 4.dp
                            ), // 模拟 Vertical/Horizontal Arrangement
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (pet in rowPets) {

                            Box(modifier = Modifier.weight(1f)) {
                                PetCard(
                                    pet = pet,
                                    onClick = { onPetClick(pet.id) }
                                )
                            }
                        }
                        // 兜底逻辑：如果最后一行不足 cellsPerRow 个，用空的 Spacer 占位
                        repeat(cellsPerRow - rowPets.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        if (showBackToTop.value) {
            Box(modifier = Modifier.fillMaxSize()) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "回到顶部"
                    )
                }
            }
        }
//            if (false){
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(paddingValues)
//                ) {
//                    // 搜索栏
//                    OutlinedTextField(
//                        value = searchQuery,
//                        onValueChange = { viewModel.onSearchQueryChange(it) },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 16.dp, vertical = 8.dp),
//                        placeholder = { Text("搜索精灵名称或编号") },
//                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
//                        shape = MaterialTheme.shapes.medium,
//                        singleLine = true,
//                        colors = TextFieldDefaults.colors(
//                            focusedContainerColor = MaterialTheme.colorScheme.surface,
//                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
//                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
//                            unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant
//                        )
//                    )
//
//                    // 筛选栏
//                    FilterBar(
//                        selectedElement = selectedElement,
//                        onElementSelected = { viewModel.selectElement(it) }
//                    )
//
//                    // 精灵网格
//                    if (isLoading) {
//                        Box(
//                            modifier = Modifier.fillMaxSize(),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            CircularProgressIndicator()
//                        }
//                    } else {
//                        LazyVerticalGrid(
//                            columns = GridCells.Fixed(3),
//                            contentPadding = PaddingValues(12.dp),
//                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
//                            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
//                            modifier = Modifier.fillMaxSize()
//                        ) {
//                            items(
//                                items = pets,
//                                key = { it.id }
//                            ) { pet ->
//                                PetCard(
//                                    pet = pet,
//                                    onClick = { onPetClick(pet.id) }
//                                )
//                            }
//                        }
//                    }
//                }
//            }
    }


}

