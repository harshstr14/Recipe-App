package com.example.recipedia

data class FavRecipe(
    val image: String? = null,
    val name: String? = null,
    val cuisine: String? = null,
    val difficult: String? = null,
    val cookTimeMinutes: Int? = null,
    val rating: Double? = null,
    val caloriesPerServing: Int? = null,
    val ingredients: String? = null,
    val instructions: String? = null
)
