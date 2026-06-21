package com.example.final_project.domain.model

data class BookingDraft(
    val destinationId: String,
    val date: String = "25 May 2024",
    val travelers: Int = 2,
    val pickupLocation: String = "Your Hotel in Phnom Penh"
) {
    fun totalPrice(pricePerPerson: Double): Double = pricePerPerson * travelers
}

enum class PaymentMethod(val label: String) {
    VisaMasterCard("Visa / MasterCard"),
    AbaPay("ABA Pay"),
    WingPay("Wing Pay"),
    PayPal("PayPal")
}
