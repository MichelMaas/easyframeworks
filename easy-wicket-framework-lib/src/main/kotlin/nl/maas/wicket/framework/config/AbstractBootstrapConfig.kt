package nl.maas.filerenamer.frontend.wicket.config

import com.giffing.wicket.spring.boot.context.extensions.WicketApplicationInitConfiguration
import de.agilecoders.wicket.core.Bootstrap
import de.agilecoders.wicket.core.settings.ThemeProvider
import de.agilecoders.wicket.themes.markup.html.bootswatch.BootswatchThemeProvider
import org.apache.wicket.protocol.http.WebApplication

abstract class AbstractBootstrapConfig(val prop: AbstractBootstrapProperties) : WicketApplicationInitConfiguration {

    protected lateinit var webApplication: WebApplication

    override fun init(webApplication: WebApplication) {
        this.webApplication = webApplication
        webApplication.cspSettings.blocking().disabled()
        println(
            "BOOTSTRAP INIT: " +
                    "app=${System.identityHashCode(webApplication)}, " +
                    "class=${webApplication.javaClass.name}, " +
                    "loader=${webApplication.javaClass.classLoader}"
        )
        configure()
    }

    fun configure() {
        val themeProvider: ThemeProvider = BootswatchThemeProvider(prop.theme)
        prop.themeProvider = themeProvider
        Bootstrap.install(webApplication, prop)
    }
}