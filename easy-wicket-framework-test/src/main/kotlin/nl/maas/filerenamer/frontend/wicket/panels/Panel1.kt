package nl.maas.filerenamer.frontend.wicket.panels

import nl.maas.filerenamer.frontend.wicket.caches.ModelCache
import nl.maas.filerenamer.frontend.wicket.objects.enums.Options
import nl.maas.filerenamer.frontend.wicket.pages.TestPage
import nl.maas.wicket.framework.components.base.CollapsablePanel
import nl.maas.wicket.framework.components.base.DynamicDataTable
import nl.maas.wicket.framework.components.base.DynamicFormComponent
import nl.maas.wicket.framework.components.base.DynamicPanel
import nl.maas.wicket.framework.components.charts.BarChart
import nl.maas.wicket.framework.components.charts.LineChart
import nl.maas.wicket.framework.components.charts.PieChart
import nl.maas.wicket.framework.components.elemental.DatePickerButton
import nl.maas.wicket.framework.panels.RIAPanel
import org.apache.wicket.Component
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.model.CompoundPropertyModel
import org.apache.wicket.model.Model
import org.apache.wicket.spring.injection.annot.SpringBean
import java.math.BigDecimal

class Panel1 : RIAPanel() {

    @SpringBean
    private lateinit var modelCache: ModelCache

//    @SpringBean
//    private lateinit var repository: EntityRepository

    private var name = ""

    override fun onBeforeRender() {
        super.onBeforeRender()
//        val entity = repository.fetch(1)
//        name = entity.name
        addOrReplace(createContent())
    }

    private fun createContent(): Component {
        return DynamicPanel("content").addRows(
            "Row" to intArrayOf(4, 4, 4),
            "Row2" to intArrayOf(12),
            "form" to intArrayOf(12),
            "table" to intArrayOf(12)
        )
            .addOrReplaceComponentToColumn("Row", 0, createBarGraph())
            .addOrReplaceComponentToColumn("Row", 1, createPieGraph())
            .addOrReplaceComponentToColumn("Row", 2, createLineGraph())
            .addOrReplaceComponentToColumn("Row2", 0, createDatePicker())
            .addOrReplaceComponentToColumn("form", 0, createForm())
            .addOrReplaceComponentToColumn("table", 0, createCollapsable())
    }

    private fun createDatePicker(): Component {
        val datePickerButton = DatePickerButton(DynamicPanel.ROW_CONTENT_ID)
        datePickerButton.label = Model.of("Date")
        return datePickerButton
    }

    private fun createCollapsable(): CollapsablePanel {
        return CollapsablePanel(DynamicPanel.ROW_CONTENT_ID, "Table", createDataTable())
    }

    private fun createDataTable(): DynamicDataTable {
        return DynamicDataTable.get(
            CollapsablePanel.CONTENT_ID,
            modelCache.createTuples(),
            10,
            onTupleClick = { target, tuple -> println(tuple.columns.values.joinToString(" ")) }).invertHeader()
    }

    private fun createForm(): DynamicFormComponent<TestPage.Hello> {
        return object :
            DynamicFormComponent<TestPage.Hello>(
                DynamicPanel.ROW_CONTENT_ID, "Hello", CompoundPropertyModel.of(
                    TestPage.Hello(
                        name,
                        Options.optie3
                    )
                )
            ) {
            override fun onSubmit(target: AjaxRequestTarget, typedModelObject: TestPage.Hello) {
                super.onSubmit(target, typedModelObject)
                Thread.sleep(10000)
                println(typedModelObject.toString())
            }

            override fun onAfterSubmit(target: AjaxRequestTarget, typedModelObject: TestPage.Hello) {
                super.onAfterSubmit(target, typedModelObject)
                switchToPanel(Panel2(), target)
            }

            override fun onSwitchToggled(propertyName: String, switch: Boolean, target: AjaxRequestTarget) {
                super.onSwitchToggled(propertyName, switch, target)
                println("$propertyName: $switch")
                toggleEnabledFor("option" to switch)
                reload(target)
            }

            override fun <M> onSelectChanged(propertyName: String, value: M, target: AjaxRequestTarget) {
                super.onSelectChanged(propertyName, value, target)
                println("$propertyName: $value")
            }
        }.addSelect("name", "Name", listOf("World", "Test", "Michel", "All"), "All")
            .addSelect("option", "Option", Options.values().toList(), Options.optie4)
            .addSwitch("goodDay", "Is it a good day?")
            .addTextBox("time", "Time", TestPage.TimeTransformer())
    }


    private fun createBarGraph(): BarChart {
        return BarChart(DynamicPanel.ROW_CONTENT_ID, "bar", modelCache.createBarChartParams())
    }

    private fun createPieGraph(): PieChart {
        return PieChart(DynamicPanel.ROW_CONTENT_ID, "Pie", modelCache.createPieChartParams())
    }

    private fun createLineGraph(): LineChart<BigDecimal, BigDecimal> {
        return LineChart(DynamicPanel.ROW_CONTENT_ID, "Line", modelCache.createLineChartParams())
    }

    override fun isAvailable(): Boolean {
        return true
    }


}