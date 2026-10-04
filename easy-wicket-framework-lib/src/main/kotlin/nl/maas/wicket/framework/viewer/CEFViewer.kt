package nl.maas.wicket.framework.viewer

import com.jogamp.opengl.GL
import com.jogamp.opengl.GL2
import com.jogamp.opengl.GLAutoDrawable
import com.jogamp.opengl.GLEventListener
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
            val browserComponent = browser!!.uiComponent

            if (browserComponent is GLCanvas) {
                browserComponent.setShallUseOffscreenLayer(true)

                println(
                    "Requested JOGL offscreen layer: " +
                            browserComponent.shallUseOffscreenLayer
                )
            }
            frame.add(browserComponent, BorderLayout.CENTER)
            val bounds = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .maximumWindowBounds
            val width = (bounds.width * 0.8).toInt()
            val height = (bounds.height * 0.8).toInt()
            frame.minimumSize = Dimension(width, height)
            frame.setSize(
                width, height
            )
            frame.addComponentListener(resizeAdapter)
            frame.setLocationRelativeTo(null)
            frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
            frame.extendedState = JFrame.MAXIMIZED_BOTH
            frame.isVisible = true

            SwingUtilities.invokeLater {
                if (browserComponent is GLCanvas) {
                    println(
                        "JOGL offscreen layer active: " +
                                browserComponent.isOffscreenLayerSurfaceEnabled
                    )
                }
            }

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

    private fun forceCefInvalidate(browser: CefBrowser) {
        var type: Class<*>? = browser.javaClass

        while (type != null) {
            val method = type.declaredMethods.firstOrNull {
                it.name == "invalidate" && it.parameterCount == 0
            }

            if (method != null) {
                method.isAccessible = true
                method.invoke(browser)

                println("Forced CEF invalidate")
                return
            }

            type = type.superclass
        }

        println("CEF invalidate method not found")
    }

    val resizeAdapter = object : ComponentAdapter() {

        override fun componentResized(e: ComponentEvent) {
            SwingUtilities.invokeLater {
                val currentBrowser = browser ?: return@invokeLater
                val component = currentBrowser.uiComponent
                if (component is GLCanvas) {

                    component.addGLEventListener(object : GLEventListener {

                        private var lastState = ""

                        override fun init(drawable: GLAutoDrawable) {
                        }

                        override fun dispose(drawable: GLAutoDrawable) {
                        }

                        override fun reshape(
                            drawable: GLAutoDrawable,
                            x: Int,
                            y: Int,
                            width: Int,
                            height: Int
                        ) {
                        }

                        override fun display(drawable: GLAutoDrawable) {
                            val gl = drawable.gl.gL2

                            val viewport = IntArray(4)
                            gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0)

                            val scissor = IntArray(4)
                            gl.glGetIntegerv(GL.GL_SCISSOR_BOX, scissor, 0)

                            val scissorEnabled = gl.glIsEnabled(GL.GL_SCISSOR_TEST)

                            val texture = IntArray(1)
                            gl.glGetIntegerv(GL2.GL_TEXTURE_BINDING_2D, texture, 0)

                            val textureWidth = IntArray(1)
                            val textureHeight = IntArray(1)

                            if (texture[0] != 0) {
                                gl.glGetTexLevelParameteriv(
                                    GL.GL_TEXTURE_2D,
                                    0,
                                    GL2.GL_TEXTURE_WIDTH,
                                    textureWidth,
                                    0
                                )

                                gl.glGetTexLevelParameteriv(
                                    GL.GL_TEXTURE_2D,
                                    0,
                                    GL2.GL_TEXTURE_HEIGHT,
                                    textureHeight,
                                    0
                                )
                            }

                            val state =
                                "GL STATE | " +
                                        "drawable=${drawable.surfaceWidth}x${drawable.surfaceHeight} | " +
                                        "viewport=${viewport[0]},${viewport[1]}," +
                                        "${viewport[2]}x${viewport[3]} | " +
                                        "scissorEnabled=$scissorEnabled | " +
                                        "scissor=${scissor[0]},${scissor[1]}," +
                                        "${scissor[2]}x${scissor[3]} | " +
                                        "texture=${texture[0]} " +
                                        "${textureWidth[0]}x${textureHeight[0]}"

                            if (state != lastState) {
                                println(state)
                                lastState = state
                            }
                        }
                    })
                }
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
                    forceCefInvalidate(browser!!)
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