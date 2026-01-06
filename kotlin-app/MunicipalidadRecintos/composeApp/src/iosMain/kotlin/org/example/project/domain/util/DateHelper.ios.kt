package org.example.project.domain.util

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

actual class DateHelper {
    actual fun getCurrentDateTimeIso(): String {
        val date = NSDate()
        val formatter = NSDateFormatter()
        formatter.dateFormat = "yyyy-MM-dd HH:mm"
        formatter.locale = NSLocale.currentLocale
        return formatter.stringFromDate(date)
    }
}
