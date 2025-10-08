package nl.maas.wicket.framework.tools

import de.martinspielmann.wicket.chartjs.data.dataset.property.data.NumberDataValue
import java.math.BigDecimal
import java.math.BigInteger

class BigNumberSafeNumberDataValue(number: Number) : NumberDataValue(number) {
    constructor(number: BigDecimal) : this(number.toDouble())

    companion object {
        fun <T : Number> of(vararg number: T): List<BigNumberSafeNumberDataValue> {
            return number.map {
                when (it) {
                    is BigDecimal -> BigNumberSafeNumberDataValue(it.toDouble())
                    is BigInteger -> BigNumberSafeNumberDataValue(it.toInt())
                    else -> BigNumberSafeNumberDataValue(it)
                }
            }
        }

        fun <T : Number> of(numbers: Collection<T>): List<BigNumberSafeNumberDataValue> {
            return numbers.map { BigNumberSafeNumberDataValue(if (it is BigDecimal) it.toDouble() else it) }
        }
    }
}