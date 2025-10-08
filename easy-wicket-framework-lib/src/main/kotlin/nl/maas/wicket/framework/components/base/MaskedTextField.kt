package nl.maas.wicket.framework.components.base

import nl.maas.wicket.framework.components.base.MaskedTextField.Companion.MASKS.*
import nl.maas.wicket.framework.objects.behavior.JSLoader
import nl.maas.wicket.framework.objects.behavior.JSScriptsLoader
import org.apache.commons.lang3.StringUtils
import org.apache.wicket.markup.head.IHeaderResponse
import org.apache.wicket.markup.html.form.TextField
import org.apache.wicket.model.CompoundPropertyModel
import org.apache.wicket.protocol.http.WebApplication
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.reflect.KClass

/**
 * Textfield with the possibility of applying a text mask.
 *
 * Dates and DateTimes can be formatted in the ISO way (d/M/y/h/H/m/s)
 * Numbers will be formatted as standard BigDecimal with a scale of 2 if mask is left empty
 *
 * Reserved symbols are:
 * 0 -> Any digit (0-9)
 * a -> Any Alphabetical character (a-z, A-Z)
 * * -> Any Character
 *
 * @param id the wicket id
 * @param model the modelvalue
 * @param clazz the class of the model for masking purpose
 * @param mask the mask formatted with symbols mentioned above (n)
 */
abstract class MaskedTextField<T : Serializable>(
    id: String,
    model: T,
    val mask: MASKS = MASKS.NONE,
    val customMask: String = StringUtils.EMPTY
) :
    TextField<T>(id, CompoundPropertyModel.of(model)) {

    companion object {
        enum class MASKS {
            DECIMAL,
            NUMBER,
            DATE,
            AUTO,
            CUSTOM,
            NONE
        }

    }

    init {
        require(
            (!CUSTOM.equals(mask) || (CUSTOM.equals(mask) && customMask.isNullOrBlank())),
            { "A custom mask is always required in combination with CUSTOM mask type!" })
    }

    val reservedCharacters = listOf("0", "a", "*")

    fun getClassFor(value: T): KClass<out T> {
        return value::class
    }

    override fun renderHead(response: IHeaderResponse) {
        super.renderHead(response)
        listOf("/static/js/masks.js", "/static/js/moment.min.js", "/static/js/imask.js").forEach {
            JSLoader.load(findPage()::class, application as WebApplication, response, it)
        }
        when (mask) {
            DATE -> JSScriptsLoader.load(
                response,
                "applyDateMask(document.getElementById('${this.getMarkupId(true)}'))"
            )

            DECIMAL -> JSScriptsLoader.load(
                response,
                "applyNumberMask(document.getElementById('${this.getMarkupId(true)}'),'2')"
            )

            NUMBER -> JSScriptsLoader.load(
                response,
                "applyNumberMask(document.getElementById('${this.getMarkupId(true)}'),'0')"
            )

            CUSTOM -> JSScriptsLoader.load(
                response,
                "applyMask(document.getElementById('${this.getMarkupId(true)}','${customMask}'))"
            )

            AUTO -> autoDetermineMask(response)

            NONE -> return

        }
    }

    private fun autoDetermineMask(response: IHeaderResponse) {
        when (getClassFor(modelObject) as KClass<T>) {
            LocalDate::class -> JSScriptsLoader.load(
                response,
                "applyDateMask(document.getElementById('${this.getMarkupId(true)}'))"
            )

            BigDecimal::class -> JSScriptsLoader.load(
                response,
                "applyNumberMask(document.getElementById('${this.getMarkupId(true)}'),'2')"
            )

            Int::class -> JSScriptsLoader.load(
                response,
                "applyNumberMask(document.getElementById('${this.getMarkupId(true)}'),'0')"
            )

            else -> return

        }
    }

    override fun getModelValue(): String {
        return typedModelObject.toString()
    }

    abstract val typedModelObject: T

    abstract override fun convertInput()

}