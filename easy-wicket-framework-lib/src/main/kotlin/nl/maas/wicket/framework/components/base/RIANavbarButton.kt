package nl.maas.wicket.framework.components.base

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar
import nl.maas.wicket.framework.components.elemental.RIAPanelSwitchingButton
import nl.maas.wicket.framework.pages.RIAPage
import nl.maas.wicket.framework.panels.RIAPanel
import nl.maas.wicket.framework.services.Translator
import nl.maas.wicket.framework.services.TranslatorPlaceHolder
import org.apache.wicket.AttributeModifier

class RIANavbarButton(
    private val label: String,
    private val translator: Translator = TranslatorPlaceHolder(),
    private val riaPage: RIAPage<*>,
    private val panel: RIAPanel
) : RIAPanel(Navbar.componentId()) {

    private val riaPanelSwitchingButton = RIAPanelSwitchingButton("button", label, translator, riaPage, panel)

    override fun onBeforeRender() {
        super.onBeforeRender()
        if (isActive()) {
            add(AttributeModifier.replace("class", "nav-link active"))
        } else if (!isEnabled) {
            add(AttributeModifier.replace("class", "nav-link disabled"))
        } else {
            add(AttributeModifier.replace("class", "nav-link"))
        }
        addOrReplace(riaPanelSwitchingButton)
    }

    override fun isEnabled(): Boolean {
        return riaPanelSwitchingButton.isEnabled && panel.isAvailable()
    }

    fun isActive(): Boolean {
        return riaPanelSwitchingButton.isActive()
    }

}