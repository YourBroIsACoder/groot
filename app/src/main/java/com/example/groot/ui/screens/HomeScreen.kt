package com.example.groot.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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

// Sample data - This remains the same.
val samplePlants = listOf(
    Plant(1, "Monstera", 25.99, "https://images.unsplash.com/photo-1614594975525-e45190c55d0b?w=500", "Loves indirect light."),
    Plant(2, "Snake Plant", 19.99, "https://images.unsplash.com/photo-1587334274328-64186a80aeee?w=500", "Almost impossible to kill."),
    Plant(3, "Pothos", 15.50, "https://images.unsplash.com/photo-1615591039343-96947511a283?w=500", "Great for hanging baskets."),
    Plant(4, "Fiddle Leaf", 45.00, "https://images.unsplash.com/photo-1581827479532-a5d6f1a307a6?w=500", "Can be a bit dramatic."),
    Plant(5, "ZZ Plant", 22.00, "https://images.unsplash.com/photo-1632207626027-0366e63c784d?w=500", "Thrives on neglect."),
    Plant(6, "Spider Plant", 12.99, "https://images.unsplash.com/photo-1594313542113-116554034371?w=500", "Produces baby spiderettes.")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun HomeScreen(onPlantClick: (Int) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🌱 Groot Nursery") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                placeholder = { Text("Search plants...") },
                singleLine = true
            )

            // Grid of plants (filtered)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(samplePlants.filter {
                    it.name.contains(searchQuery, ignoreCase = true)
                }) { plant ->
                    PlantCard(plant, onClick = { onPlantClick(plant.id) })
                }
            }
        }
    }
}

@Composable
fun PlantCard(plant: Plant, onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f, label = "scaleAnimation"
    )

    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
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
                    .height(150.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(plant.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = plant.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "₹${plant.price}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Button(
                        onClick = onClick,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("Buy")
                    }
                }
            }
        }
    }
}

// --- PREVIEW FUNCTION FIX ---
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    GardeningNurseryTheme {
        // We provide an empty lambda for the preview, as it doesn't need to navigate.
        HomeScreen(onPlantClick = {})
    }
}