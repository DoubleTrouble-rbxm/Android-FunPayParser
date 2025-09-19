package com.example.funpayparser

data class Lot(
    val link: String,
    val price: Double,
    val quantity: Long,
    val merchant: String,
    val reviewsCount: Int,
)