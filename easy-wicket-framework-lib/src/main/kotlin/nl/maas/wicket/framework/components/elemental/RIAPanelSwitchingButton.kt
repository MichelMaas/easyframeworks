package nl.maas.wicket.framework.components.elemental

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons
import nl.maas.wicket.framework.pages.RIAPage
import nl.maas.wicket.framework.panels.RIAPanel
import nl.maas.wicket.framework.services.Translator
import nl.maas.wicket.framework.services.TranslatorPlaceHolder
import org.apache.wicket.ajax.AjaxRequestTarget

class RIAPanelSwitchingButton(
    id: String,
    label: String,
    translator: Translator = TranslatorPlaceHolder(),
    private val riaPage: RIAPage<*>,
    private val panel: RIAPanel
) : SimpleAjaxButton(id, label, Buttons.Type.NavLink, Size.SMALL, translator, false) {

    override fun onClick(target: AjaxRequestTarget) {
        if (!riaPage.isPanelCurrent(panel)) {
            riaPage.updatePanel(panel, target)
        }
    }

    override fun isEnabled(): Boolean {
        return !isActive()
    }

    open fun isActive(): Boolean {
        return riaPage.isPanelCurrent(panel)
    }
}