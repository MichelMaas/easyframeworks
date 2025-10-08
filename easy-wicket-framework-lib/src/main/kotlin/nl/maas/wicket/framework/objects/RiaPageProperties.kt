package nl.maas.wicket.framework.objects

import nl.maas.wicket.framework.objects.enums.NavbarOrientation
import nl.maas.wicket.framework.objects.enums.NavbarType
import nl.maas.wicket.framework.panels.RIAPanel
import nl.maas.wicket.framework.services.ModelCache
import nl.maas.wicket.framework.services.Translator

open class RiaPageProperties<T : ModelCache>(
    val appName: String,
    val startPanel: RIAPanel,
    modelCache: T,
    translator: Translator,
    iconPath: String = "icon.png",
    brandPath: String = "brand.png",
    brandName: String = "Brand",
    applicationVersion: String = "",
    val navbarType: NavbarType = NavbarType.PRIMARY,
    val navbarOrientation: NavbarOrientation = NavbarOrientation.HORIZONTAL
) : BasePageProperties<T>(modelCache, translator, iconPath, brandPath, brandName, applicationVersion)
