package nl.maas.wicket.framework.viewer

import com.jogamp.opengl.awt.GLCanvas
import me.friwi.jcefmaven.CefAppBuilder
import org.cef.CefApp
import org.cef.CefSettings
import org.cef.browser.CefBrowser
import org.cef.handler.CefDisplayHandlerAdapter
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GraphicsEnvironment
import java.awt.Toolkit
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.nio.file.Path
import javax.swing.JFrame
import javax.swing.SwingUtilities

object CEFViewer : Viewer() {

    private val app = createApp()
    private val client = app.createClient()
    private var browser: CefBrowser? = null
    private var frame: JFrame? = null

    override fun startBrowser(url: String) {
        clientDebugger()
        browser = client.createBrowser(url, true, false)
        browserdebugger()
        createFrame()
    }

    private fun clientDebugger() {
        client.addDisplayHandler(object : CefDisplayHandlerAdapter() {

            override fun onConsoleMessage(
                browser: CefBrowser,
                level: CefSettings.LogSeverity,
                message: String,
                source: String,
                line: Int
            ): Boolean {
                if (message.startsWith("VIEWPORT")) {
                    println(message)
                }

                return false
            }
        })
    }

    private fun browserdebugger() {
        var lastPaintWidth = -1
        var lastPaintHeight = -1

        browser!!.renderHandler?.addOnPaintListener { event ->
            if (!event.popup &&
                (event.width != lastPaintWidth || event.height != lastPaintHeight)
            ) {
                lastPaintWidth = event.width
                lastPaintHeight = event.height

                val dirty = event.dirtyRects.joinToString {
                    "x=${it.x},y=${it.y},${it.width}x${it.height}"
                }

                println(
                    "ONPAINT | " +
                            "buffer=${event.width}x${event.height} | " +
                            "dirty=[$dirty]"
                )
            }
        }
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
            val browserComponent = browser!!.uiComponent

            frame.addComponentListener(resizeAdapter)
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

    val resizeAdapter = object : ComponentAdapter() {

        override fun componentResized(e: ComponentEvent) {
            SwingUtilities.invokeLater {
                val currentBrowser = browser ?: return@invokeLater
                val component = currentBrowser.uiComponent

                val before = currentBrowser.renderHandler
                    ?.getViewRect(currentBrowser)

                println(
                    "BEFORE display | " +
                            "component=${component.width}x${component.height} | " +
                            "viewRect=x=${before?.x}, y=${before?.y}, " +
                            "${before?.width}x${before?.height}"
                )

                if (component is GLCanvas) {
                    component.display()
                    currentBrowser.executeJavaScript(
                        """
    console.log("VIEWPORT " + JSON.stringify({
        innerWidth: window.innerWidth,
        innerHeight: window.innerHeight,
        dpr: window.devicePixelRatio,

        scrollX: window.scrollX,
        scrollY: window.scrollY,

        clientWidth: document.documentElement.clientWidth,
        clientHeight: document.documentElement.clientHeight,

        bodyX: document.body.getBoundingClientRect().x,
        bodyY: document.body.getBoundingClientRect().y,
        bodyWidth: document.body.getBoundingClientRect().width,
        bodyHeight: document.body.getBoundingClientRect().height,

        visualWidth: window.visualViewport?.width,
        visualHeight: window.visualViewport?.height,
        visualOffsetX: window.visualViewport?.offsetLeft,
        visualOffsetY: window.visualViewport?.offsetTop
    }));
    """.trimIndent(),
                        currentBrowser.url,
                        0
                    )
                    val surfaceScale = FloatArray(2)
                    component.getCurrentSurfaceScale(surfaceScale)

                    println(
                        "GLCanvas | " +
                                "component=${component.width}x${component.height} | " +
                                "surface=${component.surfaceWidth}x${component.surfaceHeight} | " +
                                "scale=${surfaceScale[0]}x${surfaceScale[1]}"
                    )
                }

                val after = currentBrowser.renderHandler
                    ?.getViewRect(currentBrowser)

                println(
                    "AFTER display  | " +
                            "component=${component.width}x${component.height} | " +
                            "viewRect=x=${after?.x}, y=${after?.y}, " +
                            "${after?.width}x${after?.height}"
                )

                val frameLocation = frame!!.locationOnScreen
                val contentLocation = frame!!.contentPane.locationOnScreen
                val componentLocation = component.locationOnScreen

                val gc = component.graphicsConfiguration
                val bounds = gc.bounds
                val insets = Toolkit.getDefaultToolkit().getScreenInsets(gc)

                println(
                    "POSITIONS | " +
                            "frame=${frameLocation.x},${frameLocation.y} | " +
                            "content=${contentLocation.x},${contentLocation.y} | " +
                            "CEF=${componentLocation.x},${componentLocation.y} | " +
                            "screen=${bounds.x},${bounds.y},${bounds.width}x${bounds.height} | " +
                            "insets=${insets.top},${insets.left},${insets.bottom},${insets.right}"
                )
            }
        }
    }
}