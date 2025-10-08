package nl.maas.wicket.framework.components.base

import java.time.LocalDate
import java.time.format.DateTimeFormatter

open class DateTextField(id: String, model: LocalDate, val format: String) : MaskedTextField<LocalDate>(
    id, model, Companion.MASKS.CUSTOM, format.toString()
) {
    override val typedModelObject: LocalDate
        get() =
            LocalDate.parse(modelObject.toString(), DateTimeFormatter.ofPattern(format))

    override fun convertInput() {
        convertedInput = LocalDate.parse(input, DateTimeFormatter.ofPattern(format))
    }
}