package com.example.final_project.domain.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class BookingDraft(
    val destinationId: String,
    val tourDate: String = defaultTourDate(),
    val travelers: Int = 2
) {
    fun totalPrice(pricePerPerson: Double): Double = pricePerPerson * travelers

    companion object {
        fun defaultTourDate(): String {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, 7)
            return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
        }
    }
}

enum class PaymentMethod(val label: String, val apiValue: String) {
    VisaMasterCard("Visa / MasterCard", "CARD"),
    AbaPay("ABA Pay", "ABA"),
    WingPay("Wing Pay", "WING"),
    PayPal("PayPal", "PAYPAL")
}
