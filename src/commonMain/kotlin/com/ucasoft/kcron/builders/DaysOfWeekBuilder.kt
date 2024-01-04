package com.ucasoft.kcron.builders

import com.ucasoft.kcron.common.DayOfWeekGroups
import com.ucasoft.kcron.common.WeekDays
import com.ucasoft.kcron.exceptions.UnknownCronPart

class DaysOfWeekBuilder(private val firstWeekDay: WeekDays = WeekDays.Monday) : PartBuilder<DayOfWeekGroups>() {

    lateinit var daysOfWeek: List<Int>

    private val dayWeekNames = WeekDays.entries.map { v -> v.shortName }

    override fun build(type: DayOfWeekGroups, value: String) {
        val firstWeekDayIndex = WeekDays.entries.indexOf(firstWeekDay)
        when (type) {
            DayOfWeekGroups.Any -> {
                daysOfWeek = listOf(1..7).flatten()
            }
            DayOfWeekGroups.Specific -> {
                val data = value.split(',')
                daysOfWeek = if (data.all { d -> dayWeekNames.contains(d) }) {
                    data.map { d -> dayShift(firstWeekDayIndex, dayWeekNames.indexOf(d)) }.sorted()
                } else {
                    data.map { d -> d.toInt() }.sorted()
                }
            }
            DayOfWeekGroups.EveryStartingAt -> {
                val data = value.split('/')
                daysOfWeek = listOf(
                    if (dayWeekNames.contains(data[0])) {
                        dayShift(firstWeekDayIndex, dayWeekNames.indexOf(data[0]))
                    } else {
                        data[0].toInt()
                    }..7 step data[1].toInt()
                ).flatten()
            }
            DayOfWeekGroups.Last -> {
                daysOfWeek = listOf(value.removeSuffix("L").toInt() * -1)
            }
            DayOfWeekGroups.OfMonth -> {
                val data = value.split('#')
                daysOfWeek = listOf(
                    if (dayWeekNames.contains(data[0])) {
                        dayShift(firstWeekDayIndex, dayWeekNames.indexOf(data[0]))
                    } else {
                        data[0].toInt()
                    } * 10, data[1].toInt() * 10
                )
            }
            DayOfWeekGroups.Range -> {
                val (start, end) = value.split('-', limit = 2).map {
                    it.toInt()
                }.apply {
                    // Check all values are in valid bounds.
                    // This is required for unverified string input before transforming range into list.
                    require(all { (1..7).contains(it) })
                }
                daysOfWeek = (start .. end).toList()
            }
            DayOfWeekGroups.Unknown -> throw UnknownCronPart("daysOfWeek")
        }
    }

    private fun dayShift(firstWeekDayIndex: Int, currentDayIndex: Int) : Int
    {
        var index = currentDayIndex - firstWeekDayIndex
        if (index < 0)
        {
            index += 7
        }

        return index + 1
    }
}