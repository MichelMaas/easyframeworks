package nl.maas.filerenamer.frontend

import jakarta.inject.Inject
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
    private fun startBrowser() {
        viewer.startBrowser("http://localhost:8080")
    }

    fun close() {
        viewer.close()
    }

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        startBrowser()
    }


}