package nl.maas.wicket.framework.components.charts.data

class BarchartData<T : Number> : HashMap<String, ChartData<String, T>>() {

    fun addBar(barName: String, vararg barHistory: BarHistory<T>): BarchartData<T> {
        require(phasesAreEqual(*barHistory), { "Phases of the barchart do not match between bars!" })
        put(barName, ChartData.create(*barHistory.map { Pair(it.phase, it.value) }.toTypedArray()))
        return this
    }

    private fun phasesAreEqual(vararg barHistory: BarHistory<T>): Boolean {
        return keys.all { get(it)!!.values.all { vl -> barHistory.map { bh -> bh.phase }.contains(vl.first) } }
    }

    data class BarHistory<B : Number>(val phase: String, val value: B)
}
