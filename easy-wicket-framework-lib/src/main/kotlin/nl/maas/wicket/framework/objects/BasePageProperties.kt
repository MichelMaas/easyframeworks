package nl.maas.wicket.framework.objects

import nl.maas.wicket.framework.services.ModelCache
import nl.maas.wicket.framework.services.Translator

open class BasePageProperties<T : ModelCache>(
    val modelCache: T,
    val translator: Translator,
    val iconPath: String = "icon.png",
    val brandPath: String = "brand.png",
    val brandName: String = "Brand",
    val applicationVersion: String = ""
)
