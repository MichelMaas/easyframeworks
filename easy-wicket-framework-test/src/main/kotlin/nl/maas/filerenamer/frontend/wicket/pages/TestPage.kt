package nl.maas.filerenamer.frontend.wicket.pages

import com.giffing.wicket.spring.boot.context.scan.WicketHomePage
import jakarta.inject.Inject
import nl.maas.filerenamer.frontend.wicket.caches.ModelCache
import nl.maas.filerenamer.frontend.wicket.objects.enums.Options
import nl.maas.filerenamer.frontend.wicket.panels.Panel1
import nl.maas.filerenamer.frontend.wicket.panels.Panel2
import nl.maas.wicket.framework.components.base.CollapsablePanel
import nl.maas.wicket.framework.components.base.CollapsablePanel.Companion.CONTENT_ID
import nl.maas.wicket.framework.components.base.DynamicDataTable
import nl.maas.wicket.framework.components.base.DynamicFormComponent
import nl.maas.wicket.framework.components.base.DynamicPanel
import nl.maas.wicket.framework.components.base.DynamicPanel.Companion.ROW_CONTENT_ID
import nl.maas.wicket.framework.components.charts.BarChart
import nl.maas.wicket.framework.components.charts.LineChart
import nl.maas.wicket.framework.components.charts.PieChart
import nl.maas.wicket.framework.components.charts.data.BarchartData
import nl.maas.wicket.framework.components.charts.data.LineChartData
import nl.maas.wicket.framework.components.charts.data.PieChartData
import nl.maas.wicket.framework.objects.Tuple
import nl.maas.wicket.framework.objects.behavior.FormValueTransformer
import nl.maas.wicket.framework.objects.enums.NavbarOrientation
import nl.maas.wicket.framework.objects.enums.NavbarType
import nl.maas.wicket.framework.pages.RIAPage
import nl.maas.wicket.framework.services.TranslatorPlaceHolder
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.model.CompoundPropertyModel
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@WicketHomePage
class TestPage @Inject constructor(modelCache: ModelCache) :
    RIAPage<ModelCache>(
        "Test",
        Panel1(),
        modelCache,
        TranslatorPlaceHolder(),
        brandPath = "/lib/brand.png",
        navbarType = NavbarType.PRIMARY,
        navbarOrientation = NavbarOrientation.VERTICAL
    ) {

    constructor() : this(ModelCache())

    data class Hello(
        var name: String,
        var option: Options,
        var goodDay: Boolean = true,
        var time: LocalTime = LocalTime.now()
    ) :
        Serializable {
        override fun toString(): String {
            return "Hello $name!"
        }
    }

    class TimeTransformer : FormValueTransformer<LocalTime> {
        override fun fromString(value: String): LocalTime {
            return LocalTime.parse(value, DateTimeFormatter.ISO_TIME)
        }

        override fun toString(value: LocalTime): String {
            return value.format(DateTimeFormatter.ISO_TIME)
        }

    }

    init {
        registerPanels(
            "Panel 1" to Panel1(),
            "Panel 2" to Panel2()
        )
    }

    override fun onBeforeRender() {
        super.onBeforeRender()
        val format = "yyyy-MM-dd"
    }

    private fun createDynamicPanel() {
        val panel = DynamicPanel("panel").addRows(
            "Row" to intArrayOf(4, 4, 4),
            "form" to intArrayOf(12),
            "table" to intArrayOf(12)
        )
        panel
            .addOrReplaceComponentToColumn("Row", 0, createBarGraph())
            .addOrReplaceComponentToColumn("Row", 1, createPieGraph())
            .addOrReplaceComponentToColumn("Row", 2, createLineGraph(panel))
            .addOrReplaceComponentToColumn("form", 0, createForm())
            .addOrReplaceComponentToColumn("table", 0, createDataTable())
    }

    private fun createDataTable(): DynamicDataTable {
        return DynamicDataTable.get(
            ROW_CONTENT_ID,
            createTuples(),
            10,
            onTupleClick = { target, tuple -> println(tuple.columns.values.joinToString(" ")) }).light()
    }

    private fun createTuples(): List<Tuple> {
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

    private fun createForm(): DynamicFormComponent<Hello> {
        return object :
            DynamicFormComponent<Hello>(ROW_CONTENT_ID, "Hello", CompoundPropertyModel.of(Hello("", Options.optie3))) {
            override fun onSubmit(target: AjaxRequestTarget, typedModelObject: Hello) {
                super.onSubmit(target, typedModelObject)
                Thread.sleep(10000)
                println(typedModelObject.toString())
            }
        }.addTextBox("name", "Name")
            .addSelect("option", "Option", Options.values().toList(), Options.optie4)
    }

    private fun createBarGraph(): BarChart {
        return BarChart(ROW_CONTENT_ID, "bar", createBarChartParams())
    }

    private fun createPieGraph(): PieChart {
        return PieChart(ROW_CONTENT_ID, "Pie", createPieChartParams())
    }

    private fun createLineGraph(parentContainer: WebMarkupContainer): WebMarkupContainer {
        return CollapsablePanel(
            ROW_CONTENT_ID,
            "Line chart",
            LineChart(CONTENT_ID, "Line", createLineChartParams())
        )
    }


    private fun createBarChartParams(): BarchartData<BigDecimal> {
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

    private fun createLineChartParams(): LineChartData<BigDecimal, BigDecimal> {
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

    private fun createPieChartParams(): PieChartData<BigDecimal> {
        return PieChartData<BigDecimal>().addSlice("A", BigDecimal.valueOf(25))
            .addSlice("B", BigDecimal.valueOf(60))
            .addSlice("C", BigDecimal.valueOf(15))
    }

//    override fun createNavBarButtons(): Array<NavbarButton<*>> {
//        return ButtonTypes.values().map { BaseNavbarButton(it) }.toTypedArray()
//    }
//
//    override fun isButtonActive(button: BaseNavbarButton): Boolean {
//        return when (button.buttonType) {
//            ButtonTypes.TEST -> true
//            else -> false
//        }
//    }


}