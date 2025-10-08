package nl.maas.wicket.framework.components.charts.data

open class LineChartData<X : Comparable<X>, Y : Number> : HashMap<String, ChartData<X, Y>>() {
    init {
        require(allYLabelsMatch())
    }

    private fun allYLabelsMatch(): Boolean {
        return values.map { it.values.map { vl -> vl.first } }.distinct().size <= 1
    }

    open fun addLines(vararg values: Pair<String, Array<Pair<X, Y>>>): LineChartData<X, Y> {
        require(allYLabelsMatch())
        putAll(values.sortedBy { it.first }.map { it.first to ChartData.create(*it.second) })
        return this
    }
}