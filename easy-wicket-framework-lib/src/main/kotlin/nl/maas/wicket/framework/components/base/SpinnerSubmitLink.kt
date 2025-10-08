package nl.maas.wicket.framework.components.base

import de.agilecoders.wicket.extensions.markup.html.bootstrap.spinner.SpinnerBehavior
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink
import org.apache.wicket.markup.html.form.Form

open class SpinnerSubmitLink(id: String, form: Form<*>) : AjaxSubmitLink(id, form) {
    private val laddaBehavior = SpinnerBehavior()

    override fun onInitialize() {
        super.onInitialize()
        add(laddaBehavior)
    }

    /**
     * Sets the effect to use
     *
     * @param effect The effect to use
     * @return `this`, for chaining
     */
    fun setEffect(effect: SpinnerBehavior.Effect?): SpinnerSubmitLink {
        laddaBehavior.withEffect(effect)
        return this
    }

    /**
     * Sets the color for the spinner
     *
     * @param color The color for the spinner
     * @return `this`, for chaining
     */
    fun setSpinnerColor(color: SpinnerBehavior.Color?): SpinnerSubmitLink {
        laddaBehavior.withSpinnerColor(color)
        return this
    }
}