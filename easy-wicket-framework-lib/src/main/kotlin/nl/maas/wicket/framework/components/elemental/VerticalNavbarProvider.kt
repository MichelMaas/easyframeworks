package nl.maas.wicket.framework.components.elemental

import nl.maas.wicket.framework.objects.behavior.JSScriptsLoader
import nl.maas.wicket.framework.objects.enums.NavbarOrientation
import nl.maas.wicket.framework.objects.enums.NavbarType
import org.apache.wicket.AttributeModifier
import org.apache.wicket.Component
import org.apache.wicket.markup.head.IHeaderResponse

internal class VerticalNavbarProvider(navbarType: NavbarType = NavbarType.LIGHT) : AttributeModifier(
    "class",
    "navbar collapse d-lg-block sidebar collapse position-fixed float-left shadow-lg ${navbarType.makeup} ${NavbarOrientation.VERTICAL.makeUp}"
) {
    val childClass = AttributeModifier("class", "nav-item")

    override fun renderHead(component: Component, response: IHeaderResponse) {
        super.renderHead(component, response)
        val renderscripts =
            arrayOf(
                "$('#${component.markupId}').find('div').removeClass('container-fluid navbar-collapse collapse show')",
                "$('#${component.markupId}').find('ul').removeClass().addClass('navbar-nav flex-column')",
                "$('#${component.markupId}').find('button.navbar-toggler').attr('hidden','')",
                "$('#${component.markupId}').find('.navbar-brand img').addClass('rounded mx-auto d-block')"
            )

        JSScriptsLoader.load(response, *renderscripts)
    }
}