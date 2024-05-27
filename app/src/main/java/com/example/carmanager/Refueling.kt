package com.example.carmanager

import java.util.Date

data class Refueling(
    val id: Long = 0,
    val date: String,
    val fuelAmount: Double,
    val price: Double,
    val distance: Double,
    val averageFuelEconomy:Double?,
    val distanceFromLastFueling:Double?
)
