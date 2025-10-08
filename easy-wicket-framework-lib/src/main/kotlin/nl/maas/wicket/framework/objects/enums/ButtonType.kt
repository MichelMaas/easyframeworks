package nl.maas.wicket.framework.objects.enums

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType
import nl.maas.wicket.framework.pages.BasePage
import kotlin.reflect.KClass

interface ButtonType {
    val iconType: IconType
    val pageClass: KClass<out BasePage<*>>
    val label: String
}