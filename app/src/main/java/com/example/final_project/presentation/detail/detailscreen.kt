package com.example.final_project.presentation.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

private val AccentColor = Color(0xFF667eea)
private val AccentColorDark = Color(0xFF764ba2)
private val ScreenBackground = Color(0xFFFAFAFC)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    destinationId: String,
    onNavigateBack: () -> Unit,
    onNavigateToDestination: (String) -> Unit = {},
    onBookNow: () -> Unit = {},
    selectedTab: BottomNavItem = BottomNavItem.Home,
    onTabSelected: (BottomNavItem) -> Unit = {}
) {
    var isFavorite by remember { mutableStateOf(false) }

    // Mock destination data based on ID
    val destination = mockDestinations.find { it.id == destinationId } ?: mockDestinations[0]

    val highlights = remember(destination) { highlightsFor(destination.category) }
    val tips = remember(destination) { tipsFor(destination.category) }
    val nearbyDestinations = remember(destination) {
        val sameProvince = mockDestinations.filter { it.id != destination.id && it.province == destination.province }
        if (sameProvince.isNotEmpty()) sameProvince.take(6)
        else mockDestinations.filter { it.id != destination.id }.sortedByDescending { it.rating }.take(6)
    }

    Scaffold(
        modifier = Modifier.background(ScreenBackground),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        destination.name,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isFavorite = !isFavorite }) {
                        Icon(
                            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color(0xFFFF4759) else Color.Black
                        )
                    }
                    IconButton(onClick = { /* Share */ }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ScreenBackground,
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            AppBottomBar(
                selectedItem = selectedTab,
                onItemSelected = onTabSelected
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBackground)
                .padding(paddingValues)
        ) {
            // Hero Image
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                ) {
                    Image(
                        painter = painterResource(id = destination.imageRes),
                        contentDescription = destination.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient overlay for readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.25f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.78f)
                                    )
                                )
                            )
                    )

                    // Quick badges
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(shape = RoundedCornerShape(20.dp), color = Color.White.copy(alpha = 0.9f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Category, contentDescription = null, tint = AccentColor, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(destination.category, fontSize = 11.sp, color = AccentColor, fontWeight = FontWeight.Medium)
                            }
                        }
                        if (destination.rating >= 4.7) {
                            Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFFFFD700)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Top Rated", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Title overlay
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(24.dp)
                    ) {
                        Text(
                            text = destination.name,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White,
                                contentDescription = "Location"
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = destination.province,
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            // Highlights
            item {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    SectionLabel("Highlights")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(highlights) { (icon, label) ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color.White,
                                shadowElevation = 2.dp
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Icon(icon, contentDescription = null, tint = AccentColor, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(label, fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }

            // Rating and Price Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(20.dp),
                            clip = false
                        ),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Rating", fontSize = 12.sp, color = Color.Gray)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                repeat(5) { index ->
                                    Icon(
                                        if (index < destination.rating.toInt()) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = null,
                                        tint = Color(0xFFFFD700),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    destination.rating.toString(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }

                        VerticalDivider(
                            modifier = Modifier
                                .height(40.dp)
                                .width(1.dp),
                            color = Color.LightGray
                        )

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Price", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                text = "$${destination.price}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentColor
                            )
                            Text("per person", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }

            // About This Place
            item {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    SectionLabel("About This Place")
                    Text(
                        text = destination.description,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            // Quick Facts grid
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        InfoCard(
                            icon = Icons.Default.Schedule,
                            title = "Opening Hours",
                            value = "8:00 AM - 6:00 PM",
                            modifier = Modifier.weight(1f)
                        )
                        InfoCard(
                            icon = Icons.Default.Category,
                            title = "Category",
                            value = destination.category,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        InfoCard(
                            icon = Icons.Default.WbSunny,
                            title = "Best Time",
                            value = bestTimeFor(destination.category),
                            modifier = Modifier.weight(1f)
                        )
                        InfoCard(
                            icon = Icons.Default.Timelapse,
                            title = "Duration",
                            value = durationFor(destination.category),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Traveler Tips
            item {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    SectionLabel("Traveler Tips")
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .shadow(
                                elevation = 3.dp,
                                shape = RoundedCornerShape(16.dp),
                                clip = false
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            tips.forEachIndexed { index, tip ->
                                Row(verticalAlignment = Alignment.Top) {
                                    Surface(
                                        modifier = Modifier.size(20.dp),
                                        shape = CircleShape,
                                        color = AccentColor.copy(alpha = 0.12f)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = null,
                                                tint = AccentColor,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(tip, fontSize = 13.sp, color = Color.Black, lineHeight = 18.sp)
                                }
                                if (index != tips.lastIndex) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Map Location
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(16.dp),
                            clip = false
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Location",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { /* Navigate to map */ }
                            ) {
                                Icon(Icons.Default.Directions, contentDescription = null, tint = AccentColor, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Get Directions",
                                    fontSize = 12.sp,
                                    color = AccentColor,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF4FACFE),
                                            Color(0xFF00F2FE)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = Color.White
                                )
                                Text(
                                    "Map View",
                                    fontSize = 14.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Nearby Attractions
            if (nearbyDestinations.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        SectionLabel("Nearby Attractions")
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            items(nearbyDestinations) { nearby ->
                                NearbyDestinationCard(
                                    destination = nearby,
                                    onClick = { onNavigateToDestination(nearby.id) }
                                )
                            }
                        }
                    }
                }
            }

            // Reviews Section
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionLabel("Reviews")
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 3.dp,
                                shape = RoundedCornerShape(16.dp),
                                clip = false
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "${destination.rating}",
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Column(modifier = Modifier.padding(start = 12.dp)) {
                                    Row {
                                        repeat(5) { i ->
                                            Icon(
                                                if (i < destination.rating.toInt()) Icons.Default.Star else Icons.Default.StarBorder,
                                                contentDescription = null,
                                                tint = Color(0xFFFFD700),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Text("Based on traveler reviews", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            val breakdown = ratingBreakdown(destination.rating)
                            for (starIndex in 0..4) {
                                val starLabel = 5 - starIndex
                                val fraction = breakdown[starIndex]
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Text("$starLabel", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.width(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(Color.LightGray.copy(alpha = 0.3f))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(fraction)
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(Color(0xFFFFD700))
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "${(fraction * 100).toInt()}%",
                                        fontSize = 10.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.width(32.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Recent Reviews",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            "See All",
                            fontSize = 12.sp,
                            color = AccentColor,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable { /* Navigate to all reviews */ }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    ReviewCard(
                        userName = "John Doe",
                        rating = 5,
                        comment = "Amazing place! Highly recommended for anyone visiting Cambodia.",
                        date = "2 days ago"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ReviewCard(
                        userName = "Jane Smith",
                        rating = 4,
                        comment = "Beautiful temple complex. Best to come early morning to avoid crowds.",
                        date = "1 week ago"
                    )
                }
            }

            // Book Now Button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { isFavorite = !isFavorite },
                        modifier = Modifier.height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (isFavorite) Color(0xFFFF4759) else Color.LightGray)
                    ) {
                        Icon(
                            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isFavorite) Color(0xFFFF4759) else Color.Gray
                        )
                    }
                    Button(
                        onClick = onBookNow,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(12.dp),
                                clip = false
                            ),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentColor,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Book Now", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SectionLabel(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(18.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(AccentColor)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

@Composable
fun InfoCard(icon: ImageVector, title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = AccentColor.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = AccentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontSize = 10.sp, color = Color.Gray)
            Text(
                value,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        }
    }
}

@Composable
fun NearbyDestinationCard(destination: Destination, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .clickable(onClick = onClick)
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(14.dp),
                clip = false
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            ) {
                Image(
                    painter = painterResource(id = destination.imageRes),
                    contentDescription = destination.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    destination.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(11.dp), tint = Color(0xFFFFD700))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("${destination.rating}", fontSize = 11.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun ReviewCard(userName: String, rating: Int, comment: String, date: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        color = AccentColor.copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                userName.first().toString(),
                                fontWeight = FontWeight.Bold,
                                color = AccentColor
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(userName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Text(date, fontSize = 10.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                repeat(5) { index ->
                    Icon(
                        if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFFFFD700)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(comment, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

private fun highlightsFor(category: String): List<Pair<ImageVector, String>> = when (category.lowercase()) {
    "temples" -> listOf(
        Icons.Default.AccountBalance to "Sightseeing",
        Icons.Default.PhotoCamera to "Photography",
        Icons.Default.Groups to "Guided Tours",
        Icons.Default.WbSunny to "Sunrise Views"
    )
    "beaches" -> listOf(
        Icons.Default.Pool to "Swimming",
        Icons.Default.WbSunny to "Sunbathing",
        Icons.Default.Restaurant to "Seafood",
        Icons.Default.PhotoCamera to "Photography"
    )
    "historical" -> listOf(
        Icons.Default.Museum to "Museum Tour",
        Icons.Default.AccountBalance to "Sightseeing",
        Icons.Default.PhotoCamera to "Photography",
        Icons.Default.Groups to "Guided Tours"
    )
    "nature" -> listOf(
        Icons.Default.Park to "Wildlife",
        Icons.Default.LocalFlorist to "Scenic Views",
        Icons.Default.DirectionsWalk to "Nature Walk",
        Icons.Default.PhotoCamera to "Photography"
    )
    "adventure" -> listOf(
        Icons.Default.Hiking to "Hiking",
        Icons.Default.Terrain to "Trekking",
        Icons.Default.DirectionsWalk to "Exploring",
        Icons.Default.PhotoCamera to "Photography"
    )
    else -> listOf(
        Icons.Default.PhotoCamera to "Photography",
        Icons.Default.Restaurant to "Local Cuisine",
        Icons.Default.ShoppingBag to "Shopping",
        Icons.Default.Groups to "Guided Tours"
    )
}

private fun tipsFor(category: String): List<String> = when (category.lowercase()) {
    "temples" -> listOf(
        "Arrive early morning to avoid crowds and the midday heat.",
        "Dress modestly — shoulders and knees should be covered.",
        "Hire a local guide to learn the history behind each structure."
    )
    "beaches" -> listOf(
        "Bring sun protection — the sun can be intense around midday.",
        "Check tide times if you plan to walk along the shoreline.",
        "Try the fresh seafood at the nearby beachside stalls."
    )
    "nature" -> listOf(
        "Wear comfortable shoes — trails can be uneven in places.",
        "Bring water and insect repellent for longer walks.",
        "Visit during the dry season for the clearest views."
    )
    "adventure" -> listOf(
        "Wear sturdy footwear suited for uneven terrain.",
        "Start early to avoid the midday heat on longer routes.",
        "Travel with a guide if you're unfamiliar with the trail."
    )
    else -> listOf(
        "Arrive early morning to avoid crowds and capture better photos.",
        "Carry water and sun protection, especially in the dry season.",
        "Check opening hours in advance, as they can vary by season."
    )
}

private fun bestTimeFor(category: String): String = when (category.lowercase()) {
    "beaches" -> "Nov - Apr"
    "temples" -> "Early Morning"
    "historical" -> "Year-round"
    "nature" -> "Nov - Mar"
    "adventure" -> "Dry Season"
    else -> "Nov - Apr"
}

private fun durationFor(category: String): String = when (category.lowercase()) {
    "beaches" -> "Half Day"
    "temples" -> "2-3 Hours"
    "historical" -> "2-4 Hours"
    "nature" -> "3-5 Hours"
    "adventure" -> "Full Day"
    else -> "2-3 Hours"
}

private fun ratingBreakdown(rating: Double): List<Float> = when {
    rating >= 4.7 -> listOf(0.72f, 0.20f, 0.05f, 0.02f, 0.01f)
    rating >= 4.3 -> listOf(0.55f, 0.30f, 0.10f, 0.03f, 0.02f)
    rating >= 4.0 -> listOf(0.40f, 0.33f, 0.16f, 0.07f, 0.04f)
    else -> listOf(0.30f, 0.28f, 0.20f, 0.12f, 0.10f)
}