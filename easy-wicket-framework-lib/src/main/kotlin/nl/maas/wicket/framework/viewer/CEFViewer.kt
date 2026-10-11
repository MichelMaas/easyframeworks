package nl.maas.wicket.framework.viewer

import me.friwi.jcefmaven.CefAppBuilder
import nl.maas.framework.io.utils.FileUtils
import nl.maas.wicket.framework.objects.enums.RuntimeRequirement
import nl.maas.wicket.framework.tools.JcefRuntimeInstaller
import nl.maas.wicket.framework.tools.RuntimeManager
import nl.maas.wicket.framework.viewer.components.CEFFrame
import nl.maas.wicket.framework.viewer.components.SwingRuntimePreparationUi
import org.cef.CefApp
import org.cef.CefClient
import org.cef.browser.CefBrowser
import java.nio.file.Path
import javax.swing.SwingUtilities
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists

internal class CEFViewer() : Viewer() {

    private lateinit var jcefDir: Path

    private lateinit var builder: CefAppBuilder
    private lateinit var app: CefApp
    private lateinit var client: CefClient
    private lateinit var browser: CefBrowser
    private lateinit var frame: CEFFrame


    override fun startBrowser(url: String, appName: String) {
        val runtimeManager = RuntimeManager(appName)
        setBrowserLocation(runtimeManager.pathFor(RuntimeRequirement.JCEF).absolutePathString())

        if (!runtimeManager.isAvailable(RuntimeRequirement.JCEF)) {
            JcefRuntimeInstaller(jcefDir)
            runtimeManager.ensureAvailable(RuntimeRequirement.JCEF, SwingRuntimePreparationUi(appName))
        }
        builder = CefAppBuilder()
        app = createApp()
        client = app.createClient()
        browser = client.createBrowser(url, true, false)
        SwingUtilities.invokeLater {
            frame = CEFFrame(browser, appName, icon, onCloseRequest = {
                fireCloseRequest()
            }).also { it.open() }
        }
    }


    override fun close() {
        SwingUtilities.invokeLater {
            frame.dispose()
        }

        browser.doClose()
    }

    override fun setBrowserLocation(location: String?) {
        jcefDir = location?.let {
            if (Path.of(it).exists()) {
                Path.of(it)
            } else {
                val appDir = System.getProperty(it)

                if (!appDir.isNullOrBlank()) {
                    FileUtils.pathFromResourceOrPath(it)!!
                } else {
                    null
                }
            }
        } ?: FileUtils.getPathFromCache("jcef", "easy-wicket-framework")!!
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