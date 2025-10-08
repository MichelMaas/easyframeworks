package nl.maas.wicket.framework.components.charts

import de.agilecoders.wicket.themes.markup.html.bootswatch.BootswatchTheme
import de.martinspielmann.wicket.chartjs.chart.impl.Bar
import de.martinspielmann.wicket.chartjs.core.internal.IndexableOption
import de.martinspielmann.wicket.chartjs.data.dataset.BarDataset
import de.martinspielmann.wicket.chartjs.data.dataset.property.TextLabel
import de.martinspielmann.wicket.chartjs.data.dataset.property.data.Data
import de.martinspielmann.wicket.chartjs.panel.BarChartPanel
import nl.maas.wicket.framework.components.charts.data.BarchartData
import nl.maas.wicket.framework.services.Translator
import nl.maas.wicket.framework.services.TranslatorPlaceHolder
import org.apache.wicket.model.IModel
import org.apache.wicket.model.LoadableDetachableModel
import java.math.BigDecimal

class BarChart(
    id: String,
    label: String,
    val data: BarchartData<BigDecimal>,
    translator: Translator = TranslatorPlaceHolder(),
    bootswatchTheme: BootswatchTheme? = null
) : AbstractChart(id, label, translator, bootswatchTheme) {

    override fun onBeforeRender() {
        super.onBeforeRender()
        addOrReplace(BarChartPanel("graph", createBarData()))
    }

    private fun createBarData(): IModel<out Bar> {
        val bar = Bar()
        val labels = mutableSetOf<String>()
        data.keys.forEach {
            labels.addAll(data[it]!!.values.map { translator.translate(it.first) })
            val barDataSet = BarDataset()
            barDataSet.label = translator.translate(it)
            barDataSet.data = Data(data[it]!!.dataValues)
            bar.data.datasets.add(barDataSet)
        }
        bar.data.labels.addAll(TextLabel.of(labels.toList()))
        bar.data.datasets.forEachIndexed { index, ds ->
            ds.backgroundColor = IndexableOption(colors[index])
        }
        return object : LoadableDetachableModel<Bar>(bar) {
            override fun load(): Bar {
                return defaultModelObject as Bar
            }
        }

    }
}