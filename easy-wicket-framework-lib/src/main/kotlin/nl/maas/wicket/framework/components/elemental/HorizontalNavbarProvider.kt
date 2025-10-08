package nl.maas.wicket.framework.components.elemental

import nl.maas.wicket.framework.objects.enums.NavbarOrientation
import nl.maas.wicket.framework.objects.enums.NavbarType
import org.apache.wicket.AttributeModifier

internal class HorizontalNavbarProvider(navbarType: NavbarType = NavbarType.LIGHT) : AttributeModifier(
    "class",
    "navbar sticky-top navbar-expand-lg shadow-lg ${navbarType.makeup} ${NavbarOrientation.HORIZONTAL.makeUp}"
) {
    val childClass = AttributeModifier("class", "nav-item")
}