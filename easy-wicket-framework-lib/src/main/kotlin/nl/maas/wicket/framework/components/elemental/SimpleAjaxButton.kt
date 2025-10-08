package nl.maas.wicket.framework.components.elemental

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons
import de.agilecoders.wicket.extensions.markup.html.bootstrap.spinner.SpinnerAjaxLink
import nl.maas.wicket.framework.objects.behavior.JSScriptsLoader
import nl.maas.wicket.framework.services.Translator
import nl.maas.wicket.framework.services.TranslatorPlaceHolder
import org.apache.commons.text.StringEscapeUtils
import org.apache.wicket.AttributeModifier
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.markup.head.IHeaderResponse
import java.util.*

abstract class SimpleAjaxButton(
    id: String,
    val label: String,
    type: Buttons.Type = Buttons.Type.Primary,
    val size: Size = Size.NORMAL,
    val translator: Translator = TranslatorPlaceHolder(),
    block: Boolean = false
) : SpinnerAjaxLink<String>(id, type) {


    enum class Size {
        SMALL,
        NORMAL,
        LARGE;
    }

    init {
        markupId = StringEscapeUtils.escapeHtml4(UUID.randomUUID().toString())
        when (size) {
            Size.LARGE -> add(AttributeModifier.append("class", "btn-lg"))
            Size.SMALL -> add(AttributeModifier.append("class", "btn-sm"))
            Size.NORMAL -> add(AttributeModifier.append("class", ""))
        }
        if (block) {
            add(AttributeModifier.append("class", "btn-block"))
        }
    }

    override fun renderHead(response: IHeaderResponse) {
        super.renderHead(response)
        JSScriptsLoader.load(
            response,
            "\$(\"#${markupId}\").text('${StringEscapeUtils.escapeEcmaScript(translator.translate(label))}');"
        )
    }

    abstract override fun onClick(target: AjaxRequestTarget)
}
