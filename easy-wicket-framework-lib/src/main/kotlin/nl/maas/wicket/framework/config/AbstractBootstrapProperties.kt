package nl.maas.filerenamer.frontend.wicket.config

import de.agilecoders.wicket.core.settings.BootstrapSettings
import de.agilecoders.wicket.themes.markup.html.bootswatch.BootswatchTheme


abstract class AbstractBootstrapProperties : BootstrapSettings() {
    var isEnabled = true
    var theme: BootswatchTheme = BootswatchTheme.Litera

    companion object {
        const val PROPERTY_PREFIX = "nl.maas.filerenamer.frontend.wicket.config"
    }
}