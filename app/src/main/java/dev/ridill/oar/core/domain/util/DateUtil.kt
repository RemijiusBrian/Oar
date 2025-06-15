package dev.ridill.oar.core.domain.util

import androidx.annotation.StringRes
import dev.ridill.oar.R
import dev.ridill.oar.core.ui.util.UiText
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

object DateUtil {
    fun now(zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime = LocalDateTime.now(zoneId)

    fun dateNow(zoneId: ZoneId = ZoneId.systemDefault()): LocalDate = LocalDate.now(zoneId)
    fun timeNow(zoneId: ZoneId = ZoneId.systemDefault()): LocalTime = LocalTime.now(zoneId)

    fun parseDateTime(
        value: String,
        formatter: DateTimeFormatter = Formatters.isoLocalDateTime
    ): LocalDateTime = LocalDateTime.parse(value, formatter)

    fun parseDateTimeOrNull(
        value: String,
        formatter: DateTimeFormatter = Formatters.isoLocalDateTime
    ): LocalDateTime? = tryOrNull { LocalDateTime.parse(value, formatter) }

    fun getPartOfDay(): PartOfDay = when (now().hour) {
        in (0..11) -> PartOfDay.MORNING
        12 -> PartOfDay.NOON
        in (13..15) -> PartOfDay.AFTERNOON
        else -> PartOfDay.EVENING
    }

    fun toMillis(
        dateTime: LocalDateTime,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Long = dateTime
        .atZone(zoneId)
        .toInstant()
        .toEpochMilli()

    fun toMillis(
        zonedDateTime: ZonedDateTime,
    ): Long = zonedDateTime
        .toInstant()
        .toEpochMilli()

    fun toMillis(
        date: LocalDate,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Long = date
        .atStartOfDay(zoneId)
        .toInstant()
        .toEpochMilli()

    fun dateFromMillisWithTime(
        millis: Long,
        time: LocalTime = timeNow(),
        zoneId: ZoneId = ZoneId.systemDefault()
    ): LocalDateTime = Instant.ofEpochMilli(millis)
        .atZone(zoneId)
        .withHour(time.hour)
        .withMinute(time.minute)
        .withSecond(time.second)
        .withNano(time.nano)
        .toLocalDateTime()

    fun prettyDateRange(
        start: LocalDate,
        end: LocalDate
    ): String = if (start.month == end.month) end.format(Formatters.MMM_yy_spaceSep)
    else buildString {
        append(start.format(Formatters.localizedDateMedium))
        append(" - ")
        append(end.format(Formatters.localizedDateMedium))

    }

    object Formatters {
        val isoLocalDateTime: DateTimeFormatter
            get() = DateTimeFormatter.ISO_LOCAL_DATE_TIME

        val localizedDateMedium: DateTimeFormatter
            get() = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

        val localizedDateLong: DateTimeFormatter
            get() = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)

        val localizedDateMediumTimeShort: DateTimeFormatter
            get() = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)

        /**
         * Eg. MAR 2025
         */
        val MMMM_yyyy_spaceSep: DateTimeFormatter
            get() = DateTimeFormatter.ofPattern("MMMM yyyy")

        /**
         * Eg. MAR 99
         */
        val MMM_yy_spaceSep: DateTimeFormatter
            get() = DateTimeFormatter.ofPattern("MMM yy")

        /**
         * Eg. 1st
         */
        val ddth: DateTimeFormatter
            get() = DateTimeFormatterBuilder()
                .appendText(ChronoField.DAY_OF_MONTH, ordinalsMap)
                .toFormatter()

        /**
         * Eg. Mon
         */
        val EEE: DateTimeFormatter
            get() = DateTimeFormatter.ofPattern("EEE")

        /**
         * Eg. Jan
         */
        val MMM: DateTimeFormatter
            get() = DateTimeFormatter.ofPattern("MMM")

        /**
         * Eg. MAR 27th
         */
        val MMM_ddth_spaceSep: DateTimeFormatter
            get() = DateTimeFormatterBuilder()
                .appendPattern("MMM ")
                .appendText(ChronoField.DAY_OF_MONTH, ordinalsMap)
                .toFormatter()

        /**
         * Eg. 1st / 2nd / 3rd
         */
        val dayOfMonthOrdinal: DateTimeFormatter
            get() = DateTimeFormatterBuilder()
                .appendText(ChronoField.DAY_OF_MONTH, ordinalsMap)
                .toFormatter()

        /**
         * Eg. Today / Tomorrow / 5 days ago
         */
        fun prettyDateAgo(
            date: LocalDate
        ): UiText {
            val currentDate = now()
            if (date.isAfter(currentDate.toLocalDate())) return UiText.DynamicString(String.Empty)

            val daysDiff = ChronoUnit.DAYS.between(date, currentDate)
                .coerceAtLeast(Long.Zero)
                .toInt()

            if (daysDiff < 1)
                return UiText.StringResource(R.string.today)

            if (daysDiff <= 3)
                return UiText.PluralResource(R.plurals.days_past, daysDiff, daysDiff.toString())

            return UiText.DynamicString(date.format(localizedDateLong))
        }

        fun formatterWithDefault(
            pattern: DateTimeFormatter,
            dateTime: LocalDateTime = now()
        ): DateTimeFormatter = DateTimeFormatterBuilder()
            .append(pattern)
            .parseDefaulting(ChronoField.YEAR, dateTime.year.toLong())
            .parseDefaulting(ChronoField.MONTH_OF_YEAR, dateTime.monthValue.toLong())
            .parseDefaulting(ChronoField.DAY_OF_MONTH, dateTime.dayOfMonth.toLong())
            .parseDefaulting(ChronoField.HOUR_OF_DAY, dateTime.hour.toLong())
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, dateTime.minute.toLong())
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, dateTime.second.toLong())
            .toFormatter()

        private val ordinalsMap: Map<Long, String>
            get() {
                val mutableMap = mutableMapOf(
                    1L to "1st",
                    2L to "2nd",
                    3L to "3rd",
                    21L to "21st",
                    22L to "22nd",
                    23L to "23rd",
                    31L to "31st",
                ).also { map ->
                    (1L..31L).forEach { map.putIfAbsent(it, "${it}th") }
                }

                return mutableMap.toMap()
            }
    }
}

fun LocalDateTime.isSameMonthAs(other: LocalDateTime?): Boolean =
    this.year == other?.year && this.month == other.month

fun LocalDateTime.isSameMonthAs(other: LocalDate?): Boolean =
    this.year == other?.year && this.month == other.month

enum class PartOfDay(
    @StringRes val labelRes: Int
) {
    MORNING(R.string.part_of_day_morning),
    NOON(R.string.part_of_day_noon),
    AFTERNOON(R.string.part_of_day_afternoon),
    EVENING(R.string.part_of_day_evening)
}