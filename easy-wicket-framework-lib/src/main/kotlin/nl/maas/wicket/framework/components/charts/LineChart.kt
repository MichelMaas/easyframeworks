package nl.maas.wicket.framework.components.charts

import de.agilecoders.wicket.themes.markup.html.bootswatch.BootswatchTheme
import de.martinspielmann.wicket.chartjs.chart.impl.Line
import de.martinspielmann.wicket.chartjs.data.dataset.LineDataset
import de.martinspielmann.wicket.chartjs.data.dataset.property.TextLabel
import de.martinspielmann.wicket.chartjs.data.dataset.property.data.Data
import de.martinspielmann.wicket.chartjs.panel.LineChartPanel
import nl.maas.wicket.framework.components.charts.data.LineChartData
import nl.maas.wicket.framework.services.Translator
import nl.maas.wicket.framework.services.TranslatorPlaceHolder
import org.apache.wicket.model.IModel
import org.apache.wicket.model.LoadableDetachableModel

class LineChart<T : Comparable<T>, N : Number>(
    id: String,
    label: String,
    val lines: LineChartData<T, N>,
    translator: Translator = TranslatorPlaceHolder(),
    bootswatchTheme: BootswatchTheme? = null
) : AbstractChart(id, label, translator, bootswatchTheme) {

    override fun onBeforeRender() {
        super.onBeforeRender()
        addOrReplace(LineChartPanel("graph", createData()))
    }

    private fun createData(): IModel<out Line> {
        val line = Line()
        lines.forEach { lineData ->
            val lineDataset = LineDataset()
            lineDataset.label = lineData.key
            lineDataset.data = Data(lineData.value.dataValues)
            lineDataset.borderColor = colors[lines.keys.indexOf(lineData.key)]
            line.data.datasets.add(lineDataset)
        }
        line.data.labels.addAll(TextLabel.of(lines.flatMap {
            it.value.values.sortedBy { it.first }.map { vl -> vl.first.toString() }
        }.distinct()))
        return object : LoadableDetachableModel<Line>(line) {
            override fun load(): Line {
                return defaultModelObject as Line
            }
        }
    }
}