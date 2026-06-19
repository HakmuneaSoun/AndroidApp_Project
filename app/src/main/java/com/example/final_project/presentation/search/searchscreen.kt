package com.example.final_project.presentation.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.final_project.domain.model.Destination
import com.example.final_project.domain.model.mockDestinations
import com.example.final_project.navigation.AppBottomBar
import com.example.final_project.navigation.BottomNavItem

// ── Tokens ────────────────────────────────────────────────────────────────────
private val Accent      = Color(0xFF667eea)
private val AccentDark  = Color(0xFF764ba2)
private val ScreenBg    = Color(0xFFF5F7FF)
private val CardBg      = Color.White
private val TextPri     = Color(0xFF1A1A2E)
private val TextSec     = Color(0xFF6B7280)

private enum class SortOption(val label: String) {
    Recommended("Recommended"),
    RatingHigh("Rating: High → Low"),
    PriceLow("Price: Low → High"),
    PriceHigh("Price: High → Low"),
    NameAZ("Name: A → Z")
}

private data class CategoryMeta(val label: String, val icon: ImageVector)

private val categories = listOf(
    CategoryMeta("All",        Icons.Default.Apps),
    CategoryMeta("Temples",    Icons.Default.AccountBalance),
    CategoryMeta("Beaches",    Icons.Default.BeachAccess),
    CategoryMeta("Historical", Icons.Default.Museum),
    CategoryMeta("Nature",     Icons.Default.Park),
    CategoryMeta("Adventure",  Icons.Default.Hiking)
)

private val trending = listOf("Angkor Wat", "Koh Rong", "Phnom Penh", "Kampot", "Battambang")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onDestinationClick: (String) -> Unit,
    onNavigateBack: () -> Unit,
    selectedTab: BottomNavItem = BottomNavItem.Explore,
    onTabSelected: (BottomNavItem) -> Unit = {}
) {
    var searchQuery     by remember { mutableStateOf("") }
    var selectedFilter  by remember { mutableStateOf("All") }
    var showFilterPanel by remember { mutableStateOf(false) }
    var showSortMenu    by remember { mutableStateOf(false) }
    var selectedSort    by remember { mutableStateOf(SortOption.Recommended) }
    var minRating       by remember { mutableStateOf(0f) }
    var maxPrice        by remember { mutableStateOf(500f) }

    val baseResults = mockDestinations.filter {
        (searchQuery.isEmpty() ||
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.province.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)) &&
                (selectedFilter == "All" || it.category == selectedFilter) &&
                it.rating >= minRating &&
                it.price <= maxPrice
    }

    val searchResults = when (selectedSort) {
        SortOption.RatingHigh -> baseResults.sortedByDescending { it.rating }
        SortOption.PriceLow   -> baseResults.sortedBy { it.price }
        SortOption.PriceHigh  -> baseResults.sortedByDescending { it.price }
        SortOption.NameAZ     -> baseResults.sortedBy { it.name }
        else                  -> baseResults
    }

    val activeFilterCount = listOf(
        minRating > 0f,
        maxPrice < 500f,
        selectedFilter != "All"
    ).count { it }

    Scaffold(
        modifier = Modifier.background(ScreenBg),
        topBar = {
            // Gradient search bar header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(colors = listOf(AccentDark, Accent))
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Row: back + search field + clear
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(16.dp), clip = false),
                    shape = RoundedCornerShape(16.dp),
                    color = CardBg
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back", tint = TextSec)
                        }
                        Icon(Icons.Default.Search, contentDescription = null,
                            tint = Accent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 14.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = TextPri, fontSize = 14.sp
                            ),
                            decorationBox = { inner ->
                                if (searchQuery.isEmpty()) {
                                    Text("Search temples, beaches, cities…",
                                        color = TextSec, fontSize = 14.sp)
                                }
                                inner()
                            },
                            singleLine = true
                        )
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSec)
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            AppBottomBar(selectedItem = selectedTab, onItemSelected = onTabSelected)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBg)
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // ── Trending searches (only when query is empty) ───────────────
            if (searchQuery.isEmpty()) {
                item {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        SectionLabel("Trending Searches", Icons.Default.TrendingUp)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            items(trending) { term ->
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = CardBg,
                                    shadowElevation = 3.dp,
                                    modifier = Modifier.clickable { searchQuery = term }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                                    ) {
                                        Icon(Icons.Default.TrendingUp, contentDescription = null,
                                            tint = Accent, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(term, fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium, color = TextPri)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Category chips ────────────────────────────────────────────
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    items(categories) { cat ->
                        val selected = selectedFilter == cat.label
                        FilterChip(
                            selected = selected,
                            onClick = { selectedFilter = cat.label },
                            label = { Text(cat.label) },
                            leadingIcon = {
                                Icon(cat.icon, contentDescription = null,
                                    modifier = Modifier.size(15.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Accent,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White,
                                containerColor = CardBg,
                                labelColor = TextPri,
                                iconColor = Accent
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true, selected = selected,
                                borderColor = Color.LightGray, borderWidth = 1.dp
                            )
                        )
                    }
                }
            }

            // ── Results header: count + sort + filter ─────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("${searchResults.size} destination${if (searchResults.size != 1) "s" else ""} found",
                            fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPri)
                        if (searchQuery.isNotEmpty()) {
                            Text("for \"$searchQuery\"", fontSize = 12.sp, color = TextSec)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Sort
                        Box {
                            Surface(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { showSortMenu = true },
                                shape = CircleShape,
                                color = CardBg,
                                shadowElevation = 3.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort",
                                        tint = if (selectedSort != SortOption.Recommended) Accent else TextSec,
                                        modifier = Modifier.size(18.dp))
                                }
                            }
                            DropdownMenu(expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false }) {
                                SortOption.entries.forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(option.label,
                                                color = if (option == selectedSort) Accent else TextPri,
                                                fontWeight = if (option == selectedSort) FontWeight.Bold else FontWeight.Normal)
                                        },
                                        onClick = { selectedSort = option; showSortMenu = false },
                                        leadingIcon = {
                                            if (option == selectedSort)
                                                Icon(Icons.Default.Check, null, tint = Accent)
                                        }
                                    )
                                }
                            }
                        }

                        // Filter panel toggle
                        Box {
                            Surface(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { showFilterPanel = !showFilterPanel },
                                shape = CircleShape,
                                color = if (showFilterPanel || activeFilterCount > 0)
                                    Accent.copy(alpha = 0.12f) else CardBg,
                                shadowElevation = 3.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Tune, contentDescription = "Filters",
                                        tint = if (showFilterPanel || activeFilterCount > 0) Accent else TextSec,
                                        modifier = Modifier.size(18.dp))
                                }
                            }
                            if (activeFilterCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(Accent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("$activeFilterCount", fontSize = 8.sp,
                                        color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // ── Collapsible filter panel ───────────────────────────────────
            item {
                AnimatedVisibility(
                    visible = showFilterPanel,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .shadow(5.dp, RoundedCornerShape(20.dp), clip = false),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBg)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text("Filters", fontSize = 16.sp,
                                fontWeight = FontWeight.Bold, color = TextPri)

                            Spacer(modifier = Modifier.height(16.dp))

                            // Min rating
                            Text("Minimum Rating: ${String.format("%.1f", minRating)} ★",
                                fontSize = 13.sp, color = TextSec)
                            Slider(
                                value = minRating, onValueChange = { minRating = it },
                                valueRange = 0f..5f, steps = 9,
                                colors = SliderDefaults.colors(
                                    thumbColor = Accent, activeTrackColor = Accent,
                                    inactiveTrackColor = Accent.copy(alpha = 0.2f)
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Max price
                            Text("Max Price: $${maxPrice.toInt()}",
                                fontSize = 13.sp, color = TextSec)
                            Slider(
                                value = maxPrice, onValueChange = { maxPrice = it },
                                valueRange = 10f..500f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Accent, activeTrackColor = Accent,
                                    inactiveTrackColor = Accent.copy(alpha = 0.2f)
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { minRating = 0f; maxPrice = 500f },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) { Text("Reset") }
                                Button(
                                    onClick = { showFilterPanel = false },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Accent),
                                    modifier = Modifier.weight(1f)
                                ) { Text("Apply") }
                            }
                        }
                    }
                }
            }

            // ── Active filter pills ───────────────────────────────────────
            if (activeFilterCount > 0) {
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        if (minRating > 0f) item {
                            ActiveFilterPill("Rating ≥ ${String.format("%.1f", minRating)}") {
                                minRating = 0f
                            }
                        }
                        if (maxPrice < 500f) item {
                            ActiveFilterPill("Max $${maxPrice.toInt()}") { maxPrice = 500f }
                        }
                        if (selectedFilter != "All") item {
                            ActiveFilterPill(selectedFilter) { selectedFilter = "All" }
                        }
                    }
                }
            }

            // ── Results or empty state ────────────────────────────────────
            if (searchResults.isEmpty()) {
                item { EmptySearchState(searchQuery) }
            } else {
                items(searchResults, key = { it.id }) { destination ->
                    SearchResultCard(
                        destination = destination,
                        searchQuery = searchQuery,
                        onClick = { onDestinationClick(destination.id) }
                    )
                }
            }
        }
    }
}

// ── Section label ─────────────────────────────────────────────────────────────
@Composable
fun SectionLabel(title: String, icon: ImageVector? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier
            .width(4.dp).height(18.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Accent))
        Spacer(modifier = Modifier.width(8.dp))
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = Accent, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPri)
    }
}

// ── Active filter pill ────────────────────────────────────────────────────────
@Composable
fun ActiveFilterPill(label: String, onRemove: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Accent.copy(alpha = 0.12f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 10.dp, end = 6.dp, top = 6.dp, bottom = 6.dp)
        ) {
            Text(label, fontSize = 12.sp, color = Accent, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Accent)
                    .clickable(onClick = onRemove),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Close, contentDescription = "Remove",
                    tint = Color.White, modifier = Modifier.size(10.dp))
            }
        }
    }
}

// ── Rich search result card ───────────────────────────────────────────────────
@Composable
fun SearchResultCard(
    destination: Destination,
    searchQuery: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick)
            .shadow(6.dp, RoundedCornerShape(20.dp),
                spotColor = Accent.copy(alpha = 0.15f), clip = false),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .clip(RoundedCornerShape(14.dp))
            ) {
                Image(
                    painter = painterResource(id = destination.imageRes),
                    contentDescription = destination.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Category overlay at bottom of thumb
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f))
                            )
                        )
                        .padding(bottom = 5.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Text(destination.category, fontSize = 9.sp,
                        color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(destination.name, fontSize = 15.sp,
                    fontWeight = FontWeight.Bold, color = TextPri,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)

                Spacer(modifier = Modifier.height(3.dp))

                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null,
                        tint = TextSec, modifier = Modifier.size(12.dp))
                    Text(destination.province, fontSize = 11.sp, color = TextSec)
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(destination.description, fontSize = 11.sp, color = TextSec,
                    maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 15.sp)

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Rating pill
                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFFFFBE6)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null,
                                tint = Color(0xFFFFD700), modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("${destination.rating}", fontSize = 11.sp,
                                fontWeight = FontWeight.Bold, color = TextPri)
                        }
                    }
                    // Opening hours teaser
                    Surface(shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFE8F5E9)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null,
                                tint = Color(0xFF4CAF50), modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("8AM–6PM", fontSize = 10.sp, color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Price + action
            Column(horizontalAlignment = Alignment.End) {
                Text("from", fontSize = 9.sp, color = TextSec)
                Text("$${destination.price.toInt()}", fontSize = 18.sp,
                    fontWeight = FontWeight.Bold, color = Accent)
                Text("/ person", fontSize = 9.sp, color = TextSec)

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable(onClick = onClick),
                    shape = CircleShape,
                    color = Accent
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "View",
                            tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────
@Composable
fun EmptySearchState(query: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Accent.copy(alpha = 0.15f), Color.Transparent)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Surface(modifier = Modifier.size(80.dp), shape = CircleShape,
                color = Accent.copy(alpha = 0.1f)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.SearchOff, contentDescription = null,
                        tint = Accent, modifier = Modifier.size(38.dp))
                }
            }
        }

        Text(
            if (query.isNotEmpty()) "No results for \"$query\""
            else "No destinations found",
            fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPri
        )
        Text("Try a different name, province, or remove some filters.",
            fontSize = 13.sp, color = TextSec,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center)

        Spacer(modifier = Modifier.height(4.dp))

        Text("Popular searches:", fontSize = 12.sp, color = TextSec,
            fontWeight = FontWeight.Medium)

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(trending.take(4)) { term ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Accent.copy(alpha = 0.08f),
                    modifier = Modifier.clickable { /* apply term */ }
                ) {
                    Text(term, fontSize = 12.sp, color = Accent, fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp))
                }
            }
        }
    }
}