package nl.maas.wicket.framework.test

import jakarta.inject.Inject
import nl.maas.framework.io.utils.AppDirectories
import nl.maas.wicket.framework.viewer.Viewers
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component


@Component
class BrowserManager private constructor() : ApplicationListener<ApplicationReadyEvent> {

    @Inject
    private lateinit var appContext: ApplicationContext
    val viewer = Viewers.viewer
    private val appName = "wicket-test"

    private fun startBrowser() {
        AppDirectories.appDir(appName)
        viewer.onCloseRequest {
            System.exit(0)
        }
        viewer.startBrowser("http://localhost:8080", appName)
    }

    fun close() {
        viewer.close()
    }

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        startBrowser()
    }


}