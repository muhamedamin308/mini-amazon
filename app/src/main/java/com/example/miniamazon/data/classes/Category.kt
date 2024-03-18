package com.example.miniamazon.data.classes

sealed class Category(
    val category: String
) {
    data object Electronics : Category("Electronics")
    data object Appliances : Category("Appliances")
    data object Fashion : Category("Fashion")
    data object Grocery : Category("Grocery")
    data object VideoGame : Category("Video Games")
    data object Perfumes : Category("Perfumes")
}