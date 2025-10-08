package nl.maas.wicket.framework.objects

import nl.maas.wicket.framework.services.Translator
import org.apache.wicket.markup.html.form.ChoiceRenderer
import org.apache.wicket.model.IModel

class EnumChoiceRenderer(val translator: Translator) : ChoiceRenderer<Enum<*>>() {
    override fun getDisplayValue(enum: Enum<*>): String {
        return translator.translate(enum.name)
    }

    override fun getIdValue(enum: Enum<*>, index: Int): String {
        return enum.name
    }

    override fun getObject(id: String, choices: IModel<out MutableList<out Enum<*>>>): Enum<*> {
        return choices.`object`.find { it.name.equals(id) }!!
    }
}