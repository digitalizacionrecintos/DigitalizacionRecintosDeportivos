package org.example.project.domain.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

object DateTimeUtils {

        private val monthNames =
                listOf(
                        "Ene",
                        "Feb",
                        "Mar",
                        "Abr",
                        "May",
                        "Jun",
                        "Jul",
                        "Ago",
                        "Sep",
                        "Oct",
                        "Nov",
                        "Dic"
                )

        fun formatEventDate(dateStr: String, timeStr: String): String {
                try {
                        val cleanDateStr = dateStr.trim()
                        var cleanTimeStr = timeStr.trim().replace("null", "", ignoreCase = true)

                        val datePart =
                                when {
                                        cleanDateStr.contains("T") ->
                                                cleanDateStr.substringBefore("T")
                                        cleanDateStr.contains(" ") ->
                                                cleanDateStr.substringBefore(" ")
                                        else -> cleanDateStr
                                }

                        if (datePart.isBlank()) return "$dateStr $timeStr"
                        val date = LocalDate.parse(datePart)

                        val time = parseTime(cleanDateStr, cleanTimeStr)

                        val day = date.dayOfMonth.toString().padStart(2, '0')
                        val month = date.monthNumber.toString().padStart(2, '0')
                        val year = date.year

                        val hourStr = time.hour.toString().padStart(2, '0')
                        val minuteStr = time.minute.toString().padStart(2, '0')

                        return "$day/$month/$year $hourStr:$minuteStr"
                } catch (e: Exception) {
                        return "$dateStr $timeStr"
                }
        }

        fun formatTime(timeStr: String): String {
                try {
                        val time = parseTime("", timeStr)
                        val hourStr = time.hour.toString().padStart(2, '0')
                        val minuteStr = time.minute.toString().padStart(2, '0')
                        return "$hourStr:$minuteStr"
                } catch (e: Exception) {
                        return timeStr
                }
        }

        private fun parseTime(dateStr: String, timeStr: String): LocalTime {
                var cleanTimeStr = timeStr.trim().replace("null", "", ignoreCase = true)
                val cleanDateStr = dateStr.trim()

                val timePart =
                        when {

                                cleanTimeStr.contains("T") ->
                                        cleanTimeStr.substringAfter("T").substringBefore(".")
                                cleanTimeStr.isNotBlank() &&
                                        cleanTimeStr.contains(":") &&
                                        cleanTimeStr != "00:00" -> cleanTimeStr
                                cleanTimeStr.isNotBlank() &&
                                        cleanTimeStr.contains(":") &&
                                        !cleanDateStr.contains("T") &&
                                        !cleanDateStr.contains(" ") -> cleanTimeStr
                                cleanDateStr.contains("T") ->
                                        cleanDateStr.substringAfter("T").substringBefore(".")
                                cleanDateStr.contains(" ") ->
                                        cleanDateStr.substringAfter(" ").trim()
                                cleanTimeStr.isNotBlank() -> cleanTimeStr
                                else -> "00:00"
                        }

                val simpleTime = timePart.substringBefore("+").substringBefore("Z").trim()
                val timeParts = simpleTime.split(":")

                val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 0
                val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0
                return LocalTime(hour, minute)
        }
}
