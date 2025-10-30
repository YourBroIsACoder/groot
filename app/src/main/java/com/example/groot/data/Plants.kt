package com.example.groot.data


data class Plant(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val description: String = "",
    val quantity: Long = 0
)