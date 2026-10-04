package nl.maas.wicket.framework.viewer

import me.friwi.jcefmaven.CefAppBuilder
import org.cef.CefApp
import org.cef.browser.CefBrowser
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GraphicsEnvironment
import java.nio.file.Path
import javax.swing.JFrame
import javax.swing.SwingUtilities

object CEFViewer : Viewer() {

    private val app = createApp()
    private val client = app.createClient()
    private var browser: CefBrowser? = null
    private var frame: JFrame? = null

    override fun startBrowser(url: String) {
        browser = client.createBrowser(url, true, false)
        createFrame()
    }

    private fun createFrame() {
        SwingUtilities.invokeLater {
            val frame = JFrame("Easy Wicket")
            frame.layout = BorderLayout()
            frame.add(browser!!.uiComponent, BorderLayout.CENTER)
            val bounds = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .maximumWindowBounds
            val width = (bounds.width * 0.8).toInt()
            val height = (bounds.height * 0.8).toInt()
            frame.minimumSize = Dimension(width, height)
            frame.setSize(
                width, height
            )
            frame.setLocationRelativeTo(null)
            frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
            frame.extendedState = JFrame.MAXIMIZED_BOTH
            frame.isVisible = true
            this.frame = frame
        }
    }

    override fun close() {
        browser?.doClose()
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