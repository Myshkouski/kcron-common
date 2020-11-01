package com.ucasoft.kcron.builders

import com.ucasoft.kcron.common.DayOfWeekGroups
import com.ucasoft.kcron.extensions.*
import kotlinx.datetime.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BuilderTests {

    private lateinit var builder : Builder

    @BeforeTest
    fun setupOnce() {
        builder = Builder()
    }

    @Test
    fun buildSeconds() {
        builder.years(2099)
        val expected = LocalDateTime(2099, 1, 1, 0, 0)
        assertEquals(expected, builder.nextRun)
        builder.hours(12).minutes(0).seconds(0)
        var result = builder.nextRunList(2)
        assertEquals(expected.plusHours(12), result[0])
        assertEquals(expected.plusHours(36), result[1])
        builder.anyHours().anyMonths().anySeconds().lastDay()
        assertEquals(expected.plusDays(30), builder.nextRun)
        builder.months(2).lastWorkDay()
        assertEquals(expected.plusDays(57), builder.nextRun)
        builder.daysOfWeek(DayOfWeekGroups.EveryStartingAt, "6/1")
        assertEquals(expected.plusDays(36), builder.nextRun)
        builder.hours(0).minutes(0).seconds(0).daysOfWeek(2, 4, 6)
        result = builder.nextRunList(3)
        assertEquals(expected.plusDays(32), result[0])
        assertEquals(expected.plusDays(34), result[1])
        assertEquals(expected.plusDays(36), result[2])
        builder.nearestWorkDay(30)
        assertNull(builder.nextRun)
        builder.months(5).nearestWorkDay(31)
        assertEquals(expected.plusDays(148), builder.nextRun)
        builder.months(10)
        assertEquals(expected.plusDays(302), builder.nextRun)
    }
}