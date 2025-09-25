package com.example.groot.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.groot.data.Plant
import com.example.groot.ui.theme.GardeningNurseryTheme

// Sample data is defined here for simplicity
val samplePlants = listOf(
    Plant(1, "Monstera", 25.99, "https://images.unsplash.com/photo-1614594975525-e45190c55d0b?w=500", "Loves indirect light."),
    Plant(2, "Snake Plant", 19.99, "https://images.unsplash.com/photo-1587334274328-64186a80aeee?w=500", "Almost impossible to kill."),
    Plant(3, "Pothos", 15.50, "https://m.media-amazon.com/images/I/51OkJOxkE7L._UF1000,1000_QL80_.jpg", "Great for hanging baskets."),
    Plant(4, "Fiddle Leaf", 45.00, "https://m.media-amazon.com/images/I/51fehIGF48L._UF1000,1000_QL80_.jpg", "Can be a bit dramatic."),
    Plant(5, "ZZ Plant", 22.00, "https://rukminim2.flixcart.com/image/704/844/xif0q/plant-sapling/v/h/a/perennial-yes-no-money-plant-sn11m1a-1-pot-sayantika-nursery-original-imagc8zhybfmbz2g.jpeg?q=90&crop=false", "Thrives on neglect."),
    Plant(6, "Spider Plant", 12.99, "https://m.media-amazon.com/images/I/51CCyxsXSPL._UF1000,1000_QL80_.jpg", "Produces baby spiderettes.")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// NOTE: The onChatClick parameter is removed as requested
fun HomeScreen(onPlantClick: (Int) -> Unit) {
    // State for the search query and the filtered list
    var searchQuery by remember { mutableStateOf("") }
    val filteredPlants = remember(searchQuery, samplePlants) {
        if (searchQuery.isBlank()) {
            samplePlants
        } else {
            samplePlants.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Welcome to Groot") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
        // NOTE: The floatingActionButton property has been removed.
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // The Search Bar UI
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search for plants...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search Icon")
                },
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            )

            // The grid of plants, now using the filtered list
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredPlants) { plant ->
                    PlantCard(plant, onClick = { onPlantClick(plant.id) })
                }
            }
        }
    }
}

// The PlantCard composable is unchanged.
@Composable
fun PlantCard(plant: Plant, onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.95f else 1f, label = "scaleAnimation")

    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            }
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(plant.imageUrl),
                contentDescription = plant.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = plant.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${plant.price}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// The Preview function is updated to reflect the removed parameter.
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    GardeningNurseryTheme {
        HomeScreen(onPlantClick = {})
    }
}