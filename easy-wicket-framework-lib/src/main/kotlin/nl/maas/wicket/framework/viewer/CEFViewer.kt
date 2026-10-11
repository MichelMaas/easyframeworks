package nl.maas.wicket.framework.viewer

import me.friwi.jcefmaven.CefAppBuilder
import nl.maas.wicket.framework.viewer.components.CEFFrame
import org.cef.CefApp
import org.cef.browser.CefBrowser
import java.nio.file.Path
import javax.swing.SwingUtilities
import kotlin.io.path.exists

internal class CEFViewer() : Viewer() {

    private var jcefDir = Path.of(
        System.getProperty("user.home"),
        ".cache",
        "easy-wicket-framework",
        "jcef"
    )
    private val builder = CefAppBuilder()
    private val app = createApp()
    private val client = app.createClient()
    private var browser: CefBrowser? = null
    private var frame: CEFFrame? = null

    override fun startBrowser(url: String, appName: String) {
        browser = client.createBrowser(url, true, false)
        SwingUtilities.invokeLater {
            frame = CEFFrame(browser!!, appName, icon, onCloseRequest = {
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

    override fun setBrowserLocation(location: String?) {
        location?.let {
            jcefDir = if (Path.of(it).exists()) {
                Path.of(it)
            } else {
                val appDir = System.getProperty("filerenamer.appdir")

                if (!appDir.isNullOrBlank()) {
                    Path.of(appDir, "jcef")
                } else {
                    Path.of(
                        System.getProperty("user.home"),
                        ".cache",
                        "easy-wicket-framework",
                        "jcef"
                    )
                }
            }
        }
    }


    private fun createApp(): CefApp {
        builder.setInstallDir(jcefDir.toFile())
        builder.cefSettings.root_cache_path =
            jcefDir.resolve("cache")
                .toAbsolutePath()
                .toString()
        builder.cefSettings.windowless_rendering_enabled = true
        return builder.build()
    }


}