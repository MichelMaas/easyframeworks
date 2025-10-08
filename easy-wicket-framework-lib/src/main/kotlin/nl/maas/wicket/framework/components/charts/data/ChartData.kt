package nl.maas.wicket.framework.components.charts.data

import nl.maas.wicket.framework.tools.BigNumberSafeNumberDataValue

abstract class ChartData<X, Y : Number> : java.io.Serializable {

    companion object {
        @JvmStatic
        fun <X, Y : Number> create(vararg values: Pair<X, Y>): ChartData<X, Y> {
            return object : ChartData<X, Y>() {}.addValues(*values)
        }
    }

    private val valuePairs = mutableListOf<Pair<X, Y>>()

    val values get() = valuePairs.toList()

    val dataValues get() = BigNumberSafeNumberDataValue.of(valuePairs.map { it.second })

    fun <T : ChartData<X, Y>> addValues(vararg values: Pair<X, Y>): T {
        valuePairs.addAll(values)
        return this as T
    }

}