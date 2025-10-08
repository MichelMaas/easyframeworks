package nl.maas.filerenamer.frontend.wicket.config

import de.agilecoders.wicket.core.settings.BootstrapSettings
import de.agilecoders.wicket.themes.markup.html.bootswatch.BootswatchTheme
import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = BootstrapProperties.PROPERTY_PREFIX)
class BootstrapProperties : BootstrapSettings() {
    var isEnabled = true
    var theme: BootswatchTheme = BootswatchTheme.Simplex

    companion object {
        const val PROPERTY_PREFIX = "nl.maas.filerenamer.frontend.wicket.config"
    }
}