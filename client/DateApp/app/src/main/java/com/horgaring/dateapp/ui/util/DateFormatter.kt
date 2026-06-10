package com.horgaring.dateapp.ui.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object DateFormatter {

    fun formatMessageTime(timestampMillis: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestampMillis))
    }

    fun formatRelativeTime(timestampMillis: Long, nowMillis: Long = System.currentTimeMillis()): String {
        val diff = nowMillis - timestampMillis
        return when {
            diff < 60_000 -> "now"
            diff < 3_600_000 -> "${diff / 60_000}m"
            diff < 86_400_000 -> "${diff / 3_600_000}h"
            else -> "${diff / 86_400_000}d"
        }
    }

    fun formatBirthDate(birthDate: String): String {
        return try {
            val date = LocalDate.parse(birthDate)
            date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        } catch (_: Exception) {
            birthDate
        }
    }

    fun calculateAge(birthDate: String, now: LocalDate = LocalDate.now()): Int {
        return try {
            Period.between(LocalDate.parse(birthDate), now).years
        } catch (_: Exception) {
            0
        }
    }
}
