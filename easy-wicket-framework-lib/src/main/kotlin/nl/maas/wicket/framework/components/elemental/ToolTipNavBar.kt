package nl.maas.wicket.framework.components.elemental

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar
import org.apache.wicket.AttributeModifier
import org.apache.wicket.Component

open class ToolTipNavBar(id: String, val toolTip: String) : Navbar(id) {

    override fun newBrandNameLink(componentId: String): Component {
        return super.newBrandNameLink(componentId).add(*tooltipVersionProvider(toolTip))
    }

    fun tooltipVersionProvider(toolTip: String) = arrayOf(
        AttributeModifier("data-toggle", "tooltip"),
        AttributeModifier("data-placement", "bottom"),
        AttributeModifier("title", toolTip)
    )
}