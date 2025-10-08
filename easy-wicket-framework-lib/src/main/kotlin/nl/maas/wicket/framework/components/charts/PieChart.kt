package nl.maas.wicket.framework.components.charts

import de.agilecoders.wicket.themes.markup.html.bootswatch.BootswatchTheme
import de.martinspielmann.wicket.chartjs.chart.impl.Pie
import de.martinspielmann.wicket.chartjs.core.internal.IndexableOption
import de.martinspielmann.wicket.chartjs.data.dataset.PieDataset
import de.martinspielmann.wicket.chartjs.data.dataset.property.TextLabel
import de.martinspielmann.wicket.chartjs.data.dataset.property.data.Data
import de.martinspielmann.wicket.chartjs.panel.PieChartPanel
import nl.maas.wicket.framework.components.charts.data.PieChartData
import nl.maas.wicket.framework.services.Translator
import nl.maas.wicket.framework.services.TranslatorPlaceHolder
import org.apache.wicket.model.IModel
import org.apache.wicket.model.LoadableDetachableModel

class PieChart(
    id: String,
    label: String,
    val chartData: PieChartData<out Number>,
    translator: Translator = TranslatorPlaceHolder(),
    bootswatchTheme: BootswatchTheme? = null
) : AbstractChart(id, label, translator, bootswatchTheme) {

    override fun onBeforeRender() {
        super.onBeforeRender()
        addOrReplace(PieChartPanel("graph", createPieData()))
    }

    private fun createPieData(): IModel<out Pie> {
        val pie = Pie()
        pie.data.labels
            .addAll(TextLabel.of(chartData.getSlices().map { translator.translate(it.first) }))
        val pieDataset = PieDataset()
        pieDataset.label = translator.translate(this.label)
        pieDataset.data = Data(chartData.dataValues)
        pieDataset.backgroundColor = IndexableOption(colors)
        pie.data.datasets.add(pieDataset)
        return object : LoadableDetachableModel<Pie>(pie) {
            override fun load(): Pie {
                return defaultModelObject as Pie
            }
        }
    }
}