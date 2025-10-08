package nl.maas.wicket.framework.components.charts.data

class PieChartData<Y : Number> : ChartData<String, Y>() {
    fun addSlice(name: String, value: Y): PieChartData<Y> {
        return addValues((name to value) as Pair<String, Y>)
    }

    fun getSlices(): List<Pair<String, Y>> {
        return values
    }
}