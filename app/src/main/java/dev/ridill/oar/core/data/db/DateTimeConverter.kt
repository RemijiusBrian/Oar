package dev.ridill.oar.core.data.db

import androidx.room.TypeConverter
import dev.ridill.oar.core.domain.util.DateUtil
import dev.ridill.oar.core.domain.util.tryOrNull
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateTimeConverter {

    @TypeConverter
    fun fromDateTimeString(value: String?): LocalDateTime? = tryOrNull {
        value?.let {
            DateUtil.parseDateTime(
                it, DateUtil.Formatters
                    .formatterWithDefault(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
            LocalDateTime.parse(it)
        }
    }

    @TypeConverter
    fun toDateTimeString(dateTime: LocalDateTime?): String? = tryOrNull {
        dateTime?.toString()
    }

    @TypeConverter
    fun fromDateString(value: String?): LocalDate? = tryOrNull {
        value?.let { LocalDate.parse(value) }
    }

    @TypeConverter
    fun toDateString(date: LocalDate?): String? = tryOrNull {
        date?.toString()
    }
}