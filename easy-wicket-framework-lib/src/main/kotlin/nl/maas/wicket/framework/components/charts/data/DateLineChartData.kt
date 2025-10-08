package nl.maas.wicket.framework.components.charts.data

import java.io.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateLineChartData<N : Number>(val format: String) : LineChartData<DateLineChartData.LocalDateWrapper, N>() {
    fun addLines(vararg values: Pair<String, Array<Pair<LocalDate, N>>>): DateLineChartData<N> {
        val parsedValues =
            values.map {
                it.first to it.second.sortedBy { it.first }.map { LocalDateWrapper(it.first, format) to it.second }
                    .toTypedArray()
            }
                .toTypedArray()
        super.addLines(*parsedValues)
        return this
    }

    class LocalDateWrapper(val date: LocalDate, val format: String) : Comparable<LocalDateWrapper>, Serializable {
        override fun compareTo(other: LocalDateWrapper): Int {
            return this.date.compareTo(other.date)
        }

        override fun toString(): String {
            return DateTimeFormatter.ofPattern(format).format(date)
        }
    }
}