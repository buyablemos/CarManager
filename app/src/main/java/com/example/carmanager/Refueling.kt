package com.example.carmanager

data class Refueling(
    val id: Long = 0,
    val date: String,
    val fuelAmount: Double,
    val price: Double,
    val distance: Double,
)
