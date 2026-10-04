package nl.maas.wicket.framework.viewer

import me.friwi.jcefmaven.CefAppBuilder
import org.cef.CefApp
import java.nio.file.Path

object CEFViewer : Viewer() {

    val app = createApp()

    override fun startBrowser(url: String) {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }



    private fun createApp(): CefApp {
        val builder = CefAppBuilder()
        builder.setInstallDir(
            Path.of(
                System.getProperty("user.home"),
                ".cache",
                "easy-wicket-framework",
                "jcef"
            ).toFile()
        )
        builder.cefSettings.windowless_rendering_enabled = false
        return builder.build()
    }
}