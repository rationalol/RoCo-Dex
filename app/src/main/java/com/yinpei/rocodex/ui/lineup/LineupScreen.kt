package com.yinpei.rocodex.ui.lineup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.yinpei.rocodex.data.getElementColor
import com.yinpei.rocodex.data.mapAvatarPath
import com.yinpei.rocodex.data.model.Lineup
import com.yinpei.rocodex.ui.icons.rebase_edit
import com.yinpei.rocodex.ui.theme.RoCoFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LineupScreen(
    onBack: () -> Unit,
    onLineupClick: (Int) -> Unit,
    viewModel: LineupViewModel = viewModel()
) {
    val lineups by viewModel.lineups.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = { Text("阵容搭配", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onBack) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "新建队伍")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (lineups.isEmpty()) {
                Text(
                    text = "暂无队伍，点击右下角新建",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = lineups,
                        key = { it.id }
                    ) { lineup ->
                        LineupItem(
                            lineup = lineup,
                            viewModel = viewModel,
                            onClick = { onLineupClick(lineup.id) },
                            onRemove = { viewModel.deleteLineup(lineup) }
                        )
                    }
                }
            }
        }

        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("新建队伍", fontFamily = RoCoFamily) },
                text = {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("队伍名称", fontFamily = RoCoFamily) },
                        singleLine = true,
                        shape = CircleShape,
                        leadingIcon = { Icon(rebase_edit, contentDescription = null) },
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newName.isNotBlank()) {
                                viewModel.createLineup(newName)
                                showCreateDialog = false
                                newName = ""
                            }
                        }
                    ) {
                        Text("确定")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

@Composable
fun LineupItem(
    lineup: Lineup,
    viewModel: LineupViewModel,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    val firstPetId = lineup.pets.firstOrNull()?.petId
    val firstPet = firstPetId?.let { viewModel.getPetById(it) }
    val primaryColor = firstPet?.element?.firstOrNull()?.let { getElementColor(it) } 
        ?: MaterialTheme.colorScheme.surfaceVariant

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            primaryColor.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.background
        )
    )

    val hasLeaderBloodline = lineup.pets.any { it.bloodline == "首领" }
    val teamImagePath = if (hasLeaderBloodline) "file:///android_asset/team/1.png" else "file:///android_asset/team/0.png"

    SwipeToRevealCard(
        onRemove = onRemove,
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(16.dp),
        ) {
            Box(
                modifier = Modifier.background(gradientBrush)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = lineup.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = RoCoFamily
                        )
                        AsyncImage(
                            model = teamImagePath,
                            contentDescription = "Team Config",
                            modifier = Modifier.height(24.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 0 until 6) {
                            val petId = lineup.pets.getOrNull(i)?.petId
                            val pet = petId?.let { viewModel.getPetById(it) }
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (pet != null) {
                                    AsyncImage(
                                        model = mapAvatarPath(pet.avatar),
                                        contentDescription = pet.name,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(4.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Empty",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.size(20.dp)
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
