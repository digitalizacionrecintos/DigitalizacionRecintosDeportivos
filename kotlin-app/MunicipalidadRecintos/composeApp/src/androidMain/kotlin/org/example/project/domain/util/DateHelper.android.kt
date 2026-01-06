package org.example.project.domain.util

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

actual class DateHelper {
    actual fun getCurrentDateTimeIso(): String {
        val currentMoment = Clock.System.now()
        val datetimeInSystemZone = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())

        val year = datetimeInSystemZone.year
        val month = datetimeInSystemZone.monthNumber.toString().padStart(2, '0')
        val day = datetimeInSystemZone.dayOfMonth.toString().padStart(2, '0')
        val hour = datetimeInSystemZone.hour.toString().padStart(2, '0')
        val minute = datetimeInSystemZone.minute.toString().padStart(2, '0')

        return "$year-$month-$day $hour:$minute"
    }
}
