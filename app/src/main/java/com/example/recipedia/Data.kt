package com.example.recipedia

data class Data(
    val limit: Int,
    val recipes: List<Recipe>,
    val skip: Int,
    val total: Int
)