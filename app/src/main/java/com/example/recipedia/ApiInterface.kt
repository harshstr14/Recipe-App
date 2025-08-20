package com.example.recipedia

import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {
    @GET("recipes")
    fun getRecipeData(): Call<Data>
}