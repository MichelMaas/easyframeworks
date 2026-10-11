package nl.maas.wicket.framework.viewer

import me.friwi.jcefmaven.CefAppBuilder
import nl.maas.framework.io.utils.FileUtils
import nl.maas.wicket.framework.objects.enums.RuntimeRequirement
import nl.maas.wicket.framework.tools.RuntimeManager
import nl.maas.wicket.framework.viewer.components.CEFFrame
import org.cef.CefApp
import org.cef.browser.CefBrowser
import java.nio.file.Path
import javax.swing.SwingUtilities
import kotlin.io.path.exists

internal class CEFViewer() : Viewer() {

    private var jcefDir = FileUtils.getPathFromCache("jcef", "easy-wicket-framework")

    private val builder = CefAppBuilder()
    private val app = createApp()
    private val client = app.createClient()
    private var browser: CefBrowser? = null
    private var frame: CEFFrame? = null

    override fun startBrowser(url: String, appName: String) {
        jcefDir?.let {
            RuntimeManager(appName).ensureAvailable(RuntimeRequirement.JCEF)
        }
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
                val appDir = System.getProperty(it)

                if (!appDir.isNullOrBlank()) {
                    FileUtils.pathFromResourceOrPath(location)
                } else {
                    FileUtils.getPathFromCache("jcef", "easy-wicket-framework")
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