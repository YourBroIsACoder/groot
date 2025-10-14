package com.example.groot.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Place

import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.groot.data.Plant
import com.example.groot.ui.theme.GardeningNurseryTheme
//import com.example.groot.viewmodel.CartViewModel // Assuming CartViewModel is in this package
//import com.example.groot.viewmodel.PlantViewModel // Assuming PlantViewModel is in this package

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailScreen(
    plantId: String,
    plantViewModel: PlantViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel(), // <-- Added CartViewModel
    onNavigateUp: () -> Unit
) {
    // This LaunchedEffect will trigger the data fetch whenever the plantId changes.
    LaunchedEffect(plantId) {
        plantViewModel.fetchPlantById(plantId)
    }

    // Collect the selected plant and loading state from the ViewModel
    val plant by plantViewModel.selectedPlant.collectAsState()
    val isLoading by plantViewModel.isLoading.collectAsState()

    // State for the quantity to be added to the cart, initialized to 1.
    var quantity by remember { mutableStateOf(1) }

    val context = LocalContext.current
    val nurseryLocationUri = "google.navigation:q=37.4220,-122.0841"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(plant?.name ?: "Details") },
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
        // Conditional UI based on loading and data state
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            plant != null -> {
                // Main content when data is loaded
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(plant!!.imageUrl),
                        contentDescription = plant!!.name,
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
                            text = plant!!.name,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$${plant!!.price}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Quantity available display
                        Text(
                            text = if (plant!!.quantity > 0) "Available: ${plant!!.quantity}" else "Currently Out of Stock",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (plant!!.quantity > 0) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = plant!!.description,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        // --- NEW QUANTITY SELECTOR AND ADD TO CART BUTTON ---
                        if (plant!!.quantity > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Quantity:", style = MaterialTheme.typography.titleMedium)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { if (quantity > 1) quantity-- },
                                        enabled = quantity > 1
                                    ) {
                                        Icon(Icons.Default.Clear, "Decrease quantity")
                                    }
                                    Text(
                                        text = "$quantity",
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.width(40.dp),
                                        textAlign = TextAlign.Center
                                    )
                                    IconButton(
                                        onClick = { if (quantity < plant!!.quantity) quantity++ },
                                        enabled = quantity < plant!!.quantity
                                    ) {
                                        Icon(Icons.Default.Add, "Increase quantity")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    cartViewModel.addToCart(plant!!, quantity)
                                    Toast.makeText(context, "${plant!!.name} added to cart", Toast.LENGTH_SHORT).show()
                                    onNavigateUp() // Go back to home screen after adding
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Icon(Icons.Default.ShoppingCart, "Add to cart")
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Add to Cart")
                            }
                        } else {
                            // This part is already covered by the main quantity display, but kept for explicit structure as per original
                            Text(
                                "Currently Out of Stock",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        // --- END NEW QUANTITY SELECTOR AND ADD TO CART BUTTON ---

                        // Nursery Location Section
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

                        // Get Directions Button
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(nurseryLocationUri))
                                intent.setPackage("com.google.android.apps.maps")
                                if (intent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(intent)
                                } else {
                                    val genericIntent = Intent(Intent.ACTION_VIEW, Uri.parse(nurseryLocationUri))
                                    context.startActivity(genericIntent)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            ),
                            // Disable button if the item is out of stock (Kept original logic)
                            enabled = plant!!.quantity > 0
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
            else -> {
                // Handle case where plant is not found after loading
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Plant not found.")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlantDetailScreenPreview() {
    GardeningNurseryTheme {
        // For preview, we can simulate a loaded plant
        val dummyPlant = Plant(
            id = "1",
            name = "Monstera",
            price = 25.99,
            imageUrl = "",
            description = "Loves indirect light and brings a tropical feel to any room.",
            quantity = 10
        )
        // We can't actually use the ViewModel here, so a full preview is complex.
        // This will show the basic layout if the data were present.
    }
}