package nl.maas.filerenamer.frontend.wicket.caches

import nl.maas.wicket.framework.components.charts.data.BarchartData
import nl.maas.wicket.framework.components.charts.data.LineChartData
import nl.maas.wicket.framework.components.charts.data.PieChartData
import nl.maas.wicket.framework.objects.Tuple
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDate

@Component
class ModelCache : nl.maas.wicket.framework.services.ModelCache {

    var localDate: LocalDate = LocalDate.now()

    override fun refresh() {
        TODO("Not yet implemented")
    }

    override fun isEmpty(): Boolean {
        return false
    }

    fun createTuples(): List<Tuple> {
        return listOf(
            Tuple("Col1" to "Val1", "Col2" to "Val2", "Col3" to "Val3"),
            Tuple("Col1" to "Val4", "Col2" to "Val5", "Col3" to "Val6"),
            Tuple("Col1" to "Val7", "Col2" to "Val8", "Col3" to "Val9"),
            Tuple("Col1" to "Val10", "Col2" to "Val11", "Col3" to "Val12"),
            Tuple("Col1" to "Val13", "Col2" to "Val14", "Col3" to "Val15"),
            Tuple("Col1" to "Val16", "Col2" to "Val17", "Col3" to "Val18"),
            Tuple("Col1" to "Val19", "Col2" to "Val20", "Col3" to "Val21"),
            Tuple("Col1" to "Val22", "Col2" to "Val23", "Col3" to "Val24"),
            Tuple("Col1" to "Val25", "Col2" to "Val26", "Col3" to "Val27"),
            Tuple("Col1" to "Val28", "Col2" to "Val29", "Col3" to "Val30"),
            Tuple("Col1" to "Val31", "Col2" to "Val32", "Col3" to "Val33"),
            Tuple("Col1" to "Val34", "Col2" to "Val35", "Col3" to "Val36"),
            Tuple("Col1" to "Val37", "Col2" to "Val38", "Col3" to "Val39"),
            Tuple("Col1" to "Val40", "Col2" to "Val41", "Col3" to "Val42"),
            Tuple("Col1" to "Val43", "Col2" to "Val44", "Col3" to "Val45"),
            Tuple("Col1" to "Val46", "Col2" to "Val47", "Col3" to "Val48"),
            Tuple("Col1" to "Val49", "Col2" to "Val50", "Col3" to "Val51"),
            Tuple("Col1" to "Val52", "Col2" to "Val53", "Col3" to "Val54"),
            Tuple("Col1" to "Val55", "Col2" to "Val56", "Col3" to "Val57"),
            Tuple("Col1" to "Val58", "Col2" to "Val59", "Col3" to "Val60"),
            Tuple("Col1" to "Val61", "Col2" to "Val62", "Col3" to "Val63"),
            Tuple("Col1" to "Val64", "Col2" to "Val65", "Col3" to "Val66"),

            )
    }

    fun createBarChartParams(): BarchartData<BigDecimal> {
        return BarchartData<BigDecimal>().addBar(
            "Bar 1", *arrayOf(
                BarchartData.BarHistory<BigDecimal>("Val 1", BigDecimal.valueOf(1)),
                BarchartData.BarHistory<BigDecimal>("Val 2", BigDecimal.valueOf(2)),
                BarchartData.BarHistory<BigDecimal>("Val 3", BigDecimal.valueOf(3)),
                BarchartData.BarHistory<BigDecimal>("Val 4", BigDecimal.valueOf(4)),
                BarchartData.BarHistory<BigDecimal>("Val 5", BigDecimal.valueOf(5))
            )
        ).addBar(
            "Bar 2", *arrayOf(
                BarchartData.BarHistory<BigDecimal>("Val 1", BigDecimal.valueOf(3)),
                BarchartData.BarHistory<BigDecimal>("Val 2", BigDecimal.valueOf(6)),
                BarchartData.BarHistory<BigDecimal>("Val 3", BigDecimal.valueOf(4)),
                BarchartData.BarHistory<BigDecimal>("Val 4", BigDecimal.valueOf(2)),
                BarchartData.BarHistory<BigDecimal>("Val 5", BigDecimal.valueOf(5))
            )
        )
    }

    fun createLineChartParams(): LineChartData<BigDecimal, BigDecimal> {
        return LineChartData<BigDecimal, BigDecimal>().addLines(
            "Line 1" to arrayOf(
                BigDecimal.valueOf(0) to BigDecimal.valueOf(1),
                BigDecimal.valueOf(1) to BigDecimal.valueOf(5),
                BigDecimal.valueOf(3) to BigDecimal.valueOf(4),
                BigDecimal.valueOf(4) to BigDecimal.valueOf(1),
                BigDecimal.valueOf(5) to BigDecimal.valueOf(8)
            ),
            "Line 2" to
                    arrayOf(
                        BigDecimal.valueOf(0) to BigDecimal.valueOf(1),
                        BigDecimal.valueOf(1) to BigDecimal.valueOf(7),
                        BigDecimal.valueOf(3) to BigDecimal.valueOf(4),
                        BigDecimal.valueOf(4) to BigDecimal.valueOf(3),
                        BigDecimal.valueOf(5) to BigDecimal.valueOf(5)
                    )
        )
    }

    fun createPieChartParams(): PieChartData<BigDecimal> {
        return PieChartData<BigDecimal>().addSlice("A", BigDecimal.valueOf(25))
            .addSlice("B", BigDecimal.valueOf(60))
            .addSlice("C", BigDecimal.valueOf(15))
    }
}