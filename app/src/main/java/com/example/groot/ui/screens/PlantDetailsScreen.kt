package com.example.groot.ui.screens

// ADD these new imports for launching the map intent
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.platform.LocalContext

// --- Standard Imports (some are unchanged) ---
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.groot.data.Plant
import androidx.core.net.toUri

// REMOVE these old map imports as they are no longer needed
// import com.google.android.gms.maps.model.CameraPosition
// import com.google.android.gms.maps.model.LatLng
// import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailScreen(plant: Plant, onNavigateUp: () -> Unit) {
    // We need the application context to create and launch the map intent
    val context = LocalContext.current
    // Define the location using a standard navigation URI.
    // "google.navigation:q=latitude,longitude" will open navigation directly.
    val nurseryLocationUri = "google.navigation:q=37.4220,-122.0841" // Example: Googleplex coordinates

    Scaffold(
        topBar = {
            // This part remains the same
            TopAppBar(
                title = { Text(plant.name) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Column remains scrollable
        ) {
            // The image and plant details remain the same
            Image(
                painter = rememberAsyncImagePainter(plant.imageUrl),
                contentDescription = plant.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = plant.name,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$${plant.price}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = plant.description,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Nursery Location Section (Text part is the same)
                Text(
                    text = "Available At",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Groot Nursery, 123 Plant Street, Greenville",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(24.dp))

                // --- EDITED SECTION: Replaced SimulatedMapView with a Button ---
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, nurseryLocationUri.toUri())
                        // This line specifically tries to launch Google Maps.
                        intent.setPackage("com.google.android.apps.maps")

                        // Check if the user has Google Maps installed.
                        if (intent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(intent)
                        } else {
                            // If not, create a generic intent that allows the user
                            // to choose any installed map application (like Waze).
                            val genericIntent = Intent(Intent.ACTION_VIEW,
                                nurseryLocationUri.toUri())
                            context.startActivity(genericIntent)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    // Use the accent color from our theme for a nice visual pop.
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Icon(
                        Icons.Default.Place,
                        contentDescription = "Location Icon",
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Get Directions")
                }
            }
        }
    }
}

// --- DELETED SECTION ---
// The entire SimulatedMapView() composable function has been removed.
// It is no longer needed.