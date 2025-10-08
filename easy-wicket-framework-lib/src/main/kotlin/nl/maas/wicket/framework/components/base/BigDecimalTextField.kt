package nl.maas.wicket.framework.components.base

import java.math.BigDecimal

class BigDecimalTextField(id: String, model: BigDecimal) :
    MaskedTextField<BigDecimal>(
        id, model,
        Companion.MASKS.DECIMAL
    ) {
    override val typedModelObject: BigDecimal
        get() = BigDecimal(modelObject.toString().replace(" ", ""))

    override fun convertInput() {
        convertedInput = BigDecimal(input.replace(" ", ""))
    }
}