package com.horgaring.dateapp.ui.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class DateFormatterTest {

    // ── formatMessageTime ──────────────────────────────────────────

    @Test
    fun formatMessageTime_returnsHHmmFormat() {
        val millis = 0L // 1970-01-01 00:00 UTC
        val result = DateFormatter.formatMessageTime(millis)
        assertTrue("Expected HH:mm format, got: $result", result.matches(Regex("\\d{2}:\\d{2}")))
    }

    @Test
    fun formatMessageTime_midnight_returnsHHmm() {
        val millis = 0L
        val result = DateFormatter.formatMessageTime(millis)
        assertTrue("Expected HH:mm format, got: $result", result.matches(Regex("\\d{2}:\\d{2}")))
    }

    // ── formatRelativeTime ─────────────────────────────────────────

    @Test
    fun formatRelativeTime_lessThanOneMinute_returnsNow() {
        val now = 100_000L
        val result = DateFormatter.formatRelativeTime(now - 30_000, now)
        assertEquals("now", result)
    }

    @Test
    fun formatRelativeTime_oneMinute_returns1m() {
        val now = 100_000L
        val result = DateFormatter.formatRelativeTime(now - 60_000, now)
        assertEquals("1m", result)
    }

    @Test
    fun formatRelativeTime_fiveMinutes_returns5m() {
        val now = 100_000L
        val result = DateFormatter.formatRelativeTime(now - 300_000, now)
        assertEquals("5m", result)
    }

    @Test
    fun formatRelativeTime_oneHour_returns1h() {
        val now = 100_000L
        val result = DateFormatter.formatRelativeTime(now - 3_600_000, now)
        assertEquals("1h", result)
    }

    @Test
    fun formatRelativeTime_threeHours_returns3h() {
        val now = 100_000L
        val result = DateFormatter.formatRelativeTime(now - 10_800_000, now)
        assertEquals("3h", result)
    }

    @Test
    fun formatRelativeTime_oneDay_returns1d() {
        val now = 100_000L
        val result = DateFormatter.formatRelativeTime(now - 86_400_000, now)
        assertEquals("1d", result)
    }

    @Test
    fun formatRelativeTime_sevenDays_returns7d() {
        val now = 100_000L
        val result = DateFormatter.formatRelativeTime(now - 604_800_000, now)
        assertEquals("7d", result)
    }

    @Test
    fun formatRelativeTime_exactBoundary_59seconds_returnsNow() {
        val now = 100_000L
        val result = DateFormatter.formatRelativeTime(now - 59_999, now)
        assertEquals("now", result)
    }

    @Test
    fun formatRelativeTime_exactBoundary_60seconds_returns1m() {
        val now = 100_000L
        val result = DateFormatter.formatRelativeTime(now - 60_000, now)
        assertEquals("1m", result)
    }

    @Test
    fun formatRelativeTime_exactBoundary_3599seconds_returns59m() {
        val now = 100_000L
        val result = DateFormatter.formatRelativeTime(now - 3_599_000, now)
        assertEquals("59m", result)
    }

    @Test
    fun formatRelativeTime_exactBoundary_3600seconds_returns1h() {
        val now = 100_000L
        val result = DateFormatter.formatRelativeTime(now - 3_600_000, now)
        assertEquals("1h", result)
    }

    // ── formatBirthDate ────────────────────────────────────────────

    @Test
    fun formatBirthDate_validDate_returnsFormatted() {
        val result = DateFormatter.formatBirthDate("2000-01-15")
        assertEquals("15.01.2000", result)
    }

    @Test
    fun formatBirthDate_anotherValidDate_returnsFormatted() {
        val result = DateFormatter.formatBirthDate("1995-12-25")
        assertEquals("25.12.1995", result)
    }

    @Test
    fun formatBirthDate_invalidInput_returnsOriginal() {
        val result = DateFormatter.formatBirthDate("not-a-date")
        assertEquals("not-a-date", result)
    }

    @Test
    fun formatBirthDate_emptyString_returnsEmpty() {
        val result = DateFormatter.formatBirthDate("")
        assertEquals("", result)
    }

    // ── calculateAge ───────────────────────────────────────────────

    @Test
    fun calculateAge_bornToday_returnsZero() {
        val today = LocalDate.now()
        val result = DateFormatter.calculateAge(today.toString(), today)
        assertEquals(0, result)
    }

    @Test
    fun calculateAge_bornTwentyYearsAgo_returnsTwenty() {
        val now = LocalDate.of(2025, 6, 15)
        val result = DateFormatter.calculateAge("2005-06-15", now)
        assertEquals(20, result)
    }

    @Test
    fun calculateAge_birthdayTomorrow_stillPreviousAge() {
        val now = LocalDate.of(2025, 6, 14)
        val result = DateFormatter.calculateAge("2005-06-15", now)
        assertEquals(19, result)
    }

    @Test
    fun calculateAge_birthdayToday_exactAge() {
        val now = LocalDate.of(2025, 6, 15)
        val result = DateFormatter.calculateAge("2005-06-15", now)
        assertEquals(20, result)
    }

    @Test
    fun calculateAge_leapYearBirthday_beforeFeb28_previousAge() {
        val now = LocalDate.of(2025, 2, 28)
        val result = DateFormatter.calculateAge("2000-02-29", now)
        assertEquals(24, result)
    }

    @Test
    fun calculateAge_leapYearBirthday_onMar1_nextAge() {
        val now = LocalDate.of(2025, 3, 1)
        val result = DateFormatter.calculateAge("2000-02-29", now)
        assertEquals(25, result)
    }

    @Test
    fun calculateAge_invalidBirthDate_returnsZero() {
        val result = DateFormatter.calculateAge("bad-date")
        assertEquals(0, result)
    }
}
