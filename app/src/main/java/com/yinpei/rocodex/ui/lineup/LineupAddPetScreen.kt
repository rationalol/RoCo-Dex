package com.yinpei.rocodex.ui.lineup

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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yinpei.rocodex.ui.components.FilterBar
import com.yinpei.rocodex.ui.components.PetCard
import com.yinpei.rocodex.ui.gallery.GalleryViewModel
import com.yinpei.rocodex.ui.theme.LocalWindowSizeClass
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3WindowSizeClassApi::class
)
@Composable
fun LineupAddPetScreen(
    lineupId: Int,
    onBack: () -> Unit,
    onPetSelected: (Int) -> Unit, // returns petId
    galleryViewModel: GalleryViewModel = viewModel(),
    lineupViewModel: LineupViewModel = viewModel()
) {
    val windowSize = LocalWindowSizeClass.current

    val cellsPerRow = when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 3
        WindowWidthSizeClass.Medium -> 5
        WindowWidthSizeClass.Expanded -> 6
        else -> 3
    }

    val pets by galleryViewModel.pets.collectAsState()
    val selectedElements by galleryViewModel.selectedElements.collectAsState()
    val selectedEggGroups by galleryViewModel.selectedEggGroups.collectAsState()
    val isShinyOnly by galleryViewModel.isShinyOnly.collectAsState()
    val searchQuery by galleryViewModel.searchQuery.collectAsState()
    val isLoading by galleryViewModel.isLoading.collectAsState()
    
    // Collect lineups so the StateFlow becomes active and fetches data from DB
    val lineups by lineupViewModel.lineups.collectAsState()

    val listState = remember {
        LazyListState()
    }
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
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item(key = "header") {
                Column {
                    TopAppBar(
                        title = {
                            Text(text = "选择精灵", fontWeight = FontWeight.Bold)
                        },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                            }
                        },
                        windowInsets = WindowInsets(0, 0, 0, 0),
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { galleryViewModel.onSearchQueryChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 2.dp),
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
            
            stickyHeader(key = "filter_bar") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    FilterBar(
                        selectedElements = selectedElements,
                        onToggleElement = { galleryViewModel.toggleElement(it) },
                        onClearFilters = { galleryViewModel.clearFilters() },
                        showShinyFilter = true,
                        isShinyOnly = isShinyOnly,
                        onToggleShiny = { galleryViewModel.toggleShiny() },
                        showEggGroupFilter = true,
                        selectedEggGroups = selectedEggGroups,
                        onToggleEggGroup = { galleryViewModel.toggleEggGroup(it) },
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
            
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
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (pet in rowPets) {
                            Box(modifier = Modifier.weight(1f)) {
                                PetCard(
                                    pet = pet,
                                    onClick = {
                                        android.util.Log.d("LineupDebug", "LineupAddPetScreen: Pet clicked, petId=${pet.id}")
                                        // Add to lineup and return
                                        val lineup = lineups.find { it.id == lineupId }
                                        if (lineup != null) {
                                            // The index of the new pet will be the current size of the list
                                            val newIndex = lineup.pets.size
                                            android.util.Log.d("LineupDebug", "LineupAddPetScreen: Lineup found, newIndex=$newIndex")
                                            lineupViewModel.addPetToLineup(lineup, pet.id) {
                                                android.util.Log.d("LineupDebug", "LineupAddPetScreen: addPetToLineup onComplete callback triggered")
                                                onPetSelected(newIndex) // Pass the index of the new pet
                                            }
                                        } else {
                                            android.util.Log.d("LineupDebug", "LineupAddPetScreen: Lineup NOT found for id=$lineupId")
                                        }
                                    }
                                )
                            }
                        }
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
    }
}
