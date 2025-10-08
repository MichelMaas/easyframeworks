package nl.maas.wicket.framework.components.base

import nl.maas.wicket.framework.components.elemental.TooltipLabel
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.model.IModel

open class Switch(id: String, val boolean: IModel<Boolean>, val label: String) : Panel(id) {
    private val checkbox = object : AjaxCheckBox("switch", boolean) {
        override fun onUpdate(target: AjaxRequestTarget) {
            onUpdate(target, this.modelObject)
        }

    }

    override fun onBeforeRender() {
        super.onBeforeRender()
        val label = TooltipLabel("label", label, 12)
        addOrReplace(checkbox, label)
    }

    open fun onUpdate(target: AjaxRequestTarget, modelObject: Boolean) {

    }
}