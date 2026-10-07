package nl.maas.wicket.framework.viewer

import me.friwi.jcefmaven.CefAppBuilder
import nl.maas.wicket.framework.viewer.components.CEFFrame
import org.cef.CefApp
import org.cef.browser.CefBrowser
import java.nio.file.Path
import javax.swing.SwingUtilities

internal class CEFViewer : Viewer() {

    private val app = createApp()
    private val client = app.createClient()
    private var browser: CefBrowser? = null
    private var frame: CEFFrame? = null

    override fun startBrowser(url: String, appName: String) {
        browser = client.createBrowser(url, true, false)
        SwingUtilities.invokeLater {
            frame = CEFFrame(browser!!, appName, onCloseRequest = {
                fireCloseRequest()
            }).also { it.open() }
        }
    }


    override fun close() {
        SwingUtilities.invokeLater {
            frame?.dispose()
            frame = null
        }

        browser?.doClose()
        browser = null
    }


    private fun createApp(): CefApp {
        val builder = CefAppBuilder()
        val cefPath = Path.of(
            System.getProperty("user.home"),
            ".cache",
            "easy-wicket-framework",
            "jcef"
        )
        builder.setInstallDir(
            cefPath.toFile()
        )
        builder.cefSettings.root_cache_path =
            cefPath.resolve("cache")
                .toAbsolutePath()
                .toString()
        builder.cefSettings.windowless_rendering_enabled = true
        return builder.build()
    }


}