package com.example.final_project.presentation.favorite

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_project.domain.model.Destination
import com.example.final_project.navigation.AppBottomBar
import com.example.final_project.navigation.BottomNavItem
import com.example.final_project.presentation.common.DestinationImage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val AccentColor   = Color(0xFF667eea)
private val AccentDark    = Color(0xFF764ba2)
private val ScreenBg      = Color(0xFFF5F7FF)
private val TextPrimary   = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)

// Sort options
private enum class SortOption(val label: String) {
    Default("Default"),
    RatingHigh("Rating ↑"),
    PriceLow("Price ↑"),
    PriceHigh("Price ↓"),
    Name("A–Z")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    onDestinationClick: (String) -> Unit,
    onNavigateBack: () -> Unit,
    selectedTab: BottomNavItem = BottomNavItem.Favorites,
    onTabSelected: (BottomNavItem) -> Unit = {},
    viewModel: FavoritesViewModel = viewModel(
        factory = FavoritesViewModelFactory(
            LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    val uiState = viewModel.uiState
    val favorites = uiState.favorites

    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedSort by remember { mutableStateOf(SortOption.Default) }
    var isGridView by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    // Distinct categories present in the saved list
    val categories = remember(favorites) {
        listOf("All") + favorites.map { it.category }.distinct()
    }

    // Filtered + sorted list
    val displayList = remember(favorites, selectedCategory, selectedSort) {
        val filtered = if (selectedCategory == null || selectedCategory == "All")
            favorites else favorites.filter { it.category == selectedCategory }
        when (selectedSort) {
            SortOption.RatingHigh -> filtered.sortedByDescending { it.rating }
            SortOption.PriceLow   -> filtered.sortedBy { it.price }
            SortOption.PriceHigh  -> filtered.sortedByDescending { it.price }
            SortOption.Name       -> filtered.sortedBy { it.name }
            else                  -> filtered
        }
    }

    // Quick stats
    val avgRating = if (favorites.isEmpty()) 0.0
    else favorites.sumOf { it.rating } / favorites.size

    Scaffold(
        modifier = Modifier.background(ScreenBg),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "My Favorites",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = TextPrimary
                        )
                        Text(
                            "${favorites.size} destinations saved",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    // Grid / List toggle
                    IconButton(onClick = { isGridView = !isGridView }) {
                        Icon(
                            if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                            contentDescription = "Toggle view",
                            tint = AccentColor
                        )
                    }
                    // Sort
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.Default.Sort, contentDescription = "Sort", tint = AccentColor)
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            SortOption.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            option.label,
                                            fontWeight = if (option == selectedSort) FontWeight.Bold else FontWeight.Normal,
                                            color = if (option == selectedSort) AccentColor else TextPrimary
                                        )
                                    },
                                    onClick = {
                                        selectedSort = option
                                        showSortMenu = false
                                    },
                                    leadingIcon = {
                                        if (option == selectedSort) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = AccentColor)
                                        }
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ScreenBg,
                    titleContentColor = TextPrimary
                )
            )
        },
        bottomBar = {
            AppBottomBar(selectedItem = selectedTab, onItemSelected = onTabSelected)
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AccentColor)
            }
        } else if (favorites.isEmpty()) {
            EmptyFavoritesView(modifier = Modifier.padding(paddingValues))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ScreenBg)
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Stats banner
                item {
                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage,
                            fontSize = 12.sp,
                            color = Color(0xFFE53935),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                    FavoritesStatsBanner(
                        count = favorites.size,
                        avgRating = avgRating,
                        categories = favorites.map { it.category }.distinct().size
                    )
                }

                // Category filter chips
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        items(categories) { cat ->
                            val isSelected = (selectedCategory ?: "All") == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedCategory = if (cat == "All") null else cat
                                },
                                label = { Text(cat) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AccentColor,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White,
                                    labelColor = TextPrimary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = Color.LightGray,
                                    borderWidth = 1.dp
                                )
                            )
                        }
                    }
                }

                // Results label
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${displayList.size} result${if (displayList.size != 1) "s" else ""}",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Text(
                            "Sorted by: ${selectedSort.label}",
                            fontSize = 12.sp,
                            color = AccentColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (isGridView) {
                    // Grid — wrapped in a fixed-height box so LazyVerticalGrid works inside LazyColumn
                    item {
                        val itemHeight = 220.dp
                        val rows = (displayList.size + 1) / 2
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(itemHeight * rows + 12.dp * (rows - 1))
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            userScrollEnabled = false
                        ) {
                            items(displayList, key = { it.id }) { destination ->
                                GridFavoriteItem(
                                    destination = destination,
                                    onRemove = {
                                        destination.id.toLongOrNull()?.let { viewModel.removeFavorite(it) }
                                    },
                                    onClick = { onDestinationClick(destination.id) }
                                )
                            }
                        }
                    }
                } else {
                    items(displayList, key = { it.id }) { destination ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically(),
                            exit = fadeOut()
                        ) {
                            FavoriteItem(
                                destination = destination,
                                onRemove = {
                                    destination.id.toLongOrNull()?.let { viewModel.removeFavorite(it) }
                                },
                                onClick = { onDestinationClick(destination.id) },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoritesStatsBanner(count: Int, avgRating: Double, categories: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .shadow(6.dp, RoundedCornerShape(20.dp), clip = false),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(colors = listOf(AccentColor, AccentDark)),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(value = "$count", label = "Saved", icon = Icons.Default.Favorite)
                StatDivider()
                StatItem(value = String.format("%.1f", avgRating), label = "Avg Rating", icon = Icons.Default.Star)
                StatDivider()
                StatItem(value = "$categories", label = "Categories", icon = Icons.Default.Category)
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = Color.White.copy(alpha = 0.85f), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
    }
}

@Composable
fun StatDivider() {
    Box(
        modifier = Modifier
            .height(36.dp)
            .width(1.dp)
            .background(Color.White.copy(alpha = 0.3f))
    )
}

@Composable
fun FavoriteItem(
    destination: Destination,
    onRemove: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isRemoving by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isRemoving) 0.95f else 1f,
        animationSpec = tween(200)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable(onClick = onClick)
            .shadow(8.dp, RoundedCornerShape(20.dp),
                spotColor = AccentColor.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            ) {
                DestinationImage(
                    destination = destination,
                    contentDescription = destination.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                                startY = 0.5f
                            )
                        )
                )

                // Category badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = AccentColor
                ) {
                    Text(
                        destination.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Remove button
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(38.dp)
                        .clickable {
                            isRemoving = true
                            MainScope().launch { delay(180); onRemove() }
                        },
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Favorite, contentDescription = "Remove",
                            tint = Color(0xFFFF4759),
                            modifier = Modifier.size(20.dp))
                    }
                }

                // Name + location overlaid at the bottom of the image
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Text(destination.name, fontSize = 18.sp,
                        fontWeight = FontWeight.Bold, color = Color.White,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null,
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(destination.province, fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f))
                    }
                }
            }

            // Info row below image
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rating pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFFBE6)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("${destination.rating}", fontSize = 12.sp,
                            fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Description preview
                Text(destination.description,
                    fontSize = 12.sp, color = TextSecondary,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f))

                Spacer(modifier = Modifier.width(8.dp))

                // Price
                Column(horizontalAlignment = Alignment.End) {
                    Text("from", fontSize = 10.sp, color = TextSecondary)
                    Text("$${destination.price}", fontSize = 16.sp,
                        fontWeight = FontWeight.Bold, color = AccentColor)
                }
            }

            // Action row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { /* Share */ },
                    modifier = Modifier.height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null,
                        modifier = Modifier.size(14.dp), tint = TextSecondary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share", fontSize = 12.sp, color = TextSecondary)
                }

                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentColor,
                        contentColor = Color.White
                    )
                ) {
                    Text("View Details", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null,
                        modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
fun GridFavoriteItem(
    destination: Destination,
    onRemove: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clickable(onClick = onClick)
            .shadow(6.dp, RoundedCornerShape(16.dp),
                spotColor = AccentColor.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            DestinationImage(
                destination = destination,
                contentDescription = destination.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 0.4f
                        )
                    )
            )

            // Remove heart
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(30.dp)
                    .clickable { onRemove() },
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.9f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Favorite, contentDescription = "Remove",
                        tint = Color(0xFFFF4759), modifier = Modifier.size(16.dp))
                }
            }

            // Text at bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
            ) {
                Text(destination.name, fontSize = 13.sp,
                    fontWeight = FontWeight.Bold, color = Color.White,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null,
                        tint = Color(0xFFFFD700), modifier = Modifier.size(11.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("${destination.rating}", fontSize = 11.sp, color = Color.White)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("$${destination.price}", fontSize = 12.sp,
                        fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun EmptyFavoritesView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            // Animated glowing icon circle
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                AccentColor.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(90.dp),
                    shape = CircleShape,
                    color = AccentColor.copy(alpha = 0.1f),
                    shadowElevation = 0.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(44.dp),
                            tint = AccentColor
                        )
                    }
                }
            }

            Text(
                "No favorites yet",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                "Tap the heart icon on any destination\nto save it here for later",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            // Tips
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp), clip = false),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("What you can do with favorites:",
                        fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))
                    listOf(
                        Icons.Default.BookmarkBorder to "Save places to visit later",
                        Icons.Default.Sort to "Sort & filter your saved spots",
                        Icons.Default.Share to "Share picks with friends",
                        Icons.Default.GridView to "Switch between list & grid views"
                    ).forEach { (icon, text) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 5.dp)
                        ) {
                            Surface(modifier = Modifier.size(26.dp), shape = CircleShape,
                                color = AccentColor.copy(alpha = 0.1f)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(icon, contentDescription = null,
                                        tint = AccentColor, modifier = Modifier.size(14.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text, fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }
            }

            Button(
                onClick = { /* Navigate to explore */ },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentColor,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(8.dp, RoundedCornerShape(14.dp),
                        ambientColor = AccentColor.copy(alpha = 0.4f),
                        spotColor = AccentColor.copy(alpha = 0.4f))
            ) {
                Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Discover Destinations", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}