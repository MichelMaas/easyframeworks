package nl.maas.wicket.framework.viewer.component

import com.jogamp.opengl.awt.GLCanvas
import org.cef.browser.CefBrowser
import org.cef.browser.CefPaintEvent
import java.awt.BorderLayout
import java.awt.Cursor
import java.awt.Graphics
import java.awt.Window
import java.awt.event.*
import java.awt.image.BufferedImage
import java.beans.PropertyChangeListener
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.function.Consumer
import javax.swing.JPanel
import javax.swing.JWindow
import javax.swing.SwingUtilities

internal class CEFComponent(
    private val browser: CefBrowser,
    private val owner: Window
) : JPanel() {

    @Volatile
    private var image: BufferedImage? = null

    private val browserComponent = browser.uiComponent

    private var driverWindow: JWindow? = null

    private val cursorChangeListener =
        PropertyChangeListener { event ->
            val newCursor =
                event.newValue as? Cursor
                    ?: Cursor.getDefaultCursor()

            if (SwingUtilities.isEventDispatchThread()) {
                cursor = newCursor
            } else {
                SwingUtilities.invokeLater {
                    cursor = newCursor
                }
            }
        }

    private val paintListener =
        Consumer<CefPaintEvent> { event ->
            if (!event.popup) {
                updateImage(
                    event.renderedFrame,
                    event.width,
                    event.height
                )
            }
        }

    init {
        isFocusable = true

        browser.renderHandler
            ?.addOnPaintListener(paintListener)

        setCursorHandling()

        addComponentListener(
            object : ComponentAdapter() {
                override fun componentResized(
                    e: ComponentEvent
                ) {
//                    resizeBrowser()
                    syncDriverWindowBounds()
                }
            }
        )


        installInputHandlers()

        createDriverWindow()
    }

    private fun setCursorHandling() {
        browserComponent.addPropertyChangeListener(
            "cursor",
            cursorChangeListener
        )

        cursor =
            browserComponent.cursor
                ?: Cursor.getDefaultCursor()
    }

    private fun createDriverWindow() {
        SwingUtilities.invokeLater {
            driverWindow =
                JWindow(owner).apply {
                    setFocusableWindowState(false)
                    setAutoRequestFocus(false)

                    layout = BorderLayout()

                    add(
                        browserComponent,
                        BorderLayout.CENTER
                    )

                    opacity = 0.0f

                    isVisible = true
                }

            syncDriverWindowBounds()
        }
    }

    private fun syncDriverWindowBounds() {
        SwingUtilities.invokeLater {
            val currentDriverWindow =
                driverWindow
                    ?: return@invokeLater

            if (!isShowing) {
                return@invokeLater
            }

            val location =
                locationOnScreen

            currentDriverWindow.setBounds(
                location.x,
                location.y,
                width.coerceAtLeast(1),
                height.coerceAtLeast(1)
            )

            currentDriverWindow.validate()

            if (browserComponent is GLCanvas) {
                browserComponent.display()
            }
        }
    }

    private fun resizeBrowser() {
        SwingUtilities.invokeLater {
            val currentDriverFrame =
                driverWindow
                    ?: return@invokeLater

            val width =
                width.coerceAtLeast(1)

            val height =
                height.coerceAtLeast(1)

            currentDriverFrame.setSize(
                width,
                height
            )

            currentDriverFrame.validate()

            if (browserComponent is GLCanvas) {
                browserComponent.display()
            }
        }
    }


    private fun installInputHandlers() {

        addMouseListener(
            object : MouseAdapter() {

                override fun mousePressed(e: MouseEvent) {
                    requestFocusInWindow()
                    forwardMouseEvent(e)
                }

                override fun mouseReleased(e: MouseEvent) {
                    forwardMouseEvent(e)
                }

                override fun mouseClicked(e: MouseEvent) {
                    forwardMouseEvent(e)
                }

                override fun mouseEntered(e: MouseEvent) {
                    forwardMouseEvent(e)
                }

                override fun mouseExited(e: MouseEvent) {
                    forwardMouseEvent(e)
                }
            }
        )

        addMouseMotionListener(
            object : MouseMotionAdapter() {

                override fun mouseMoved(e: MouseEvent) {
                    forwardMouseEvent(e)
                }

                override fun mouseDragged(e: MouseEvent) {
                    forwardMouseEvent(e)
                }
            }
        )

        addMouseWheelListener { event ->
            forwardMouseWheelEvent(event)
        }

        addKeyListener(
            object : KeyAdapter() {

                override fun keyPressed(e: KeyEvent) {
                    forwardKeyEvent(e)
                }

                override fun keyReleased(e: KeyEvent) {
                    forwardKeyEvent(e)
                }

                override fun keyTyped(e: KeyEvent) {
                    forwardKeyEvent(e)
                }
            }
        )

//        addFocusListener(
//            object : FocusAdapter() {
//
//                override fun focusGained(e: FocusEvent) {
//                    browserComponent.dispatchEvent(
//                        FocusEvent(
//                            browserComponent,
//                            FocusEvent.FOCUS_GAINED
//                        )
//                    )
//                }
//
//                override fun focusLost(e: FocusEvent) {
//                    browserComponent.dispatchEvent(
//                        FocusEvent(
//                            browserComponent,
//                            FocusEvent.FOCUS_LOST
//                        )
//                    )
//                }
//            }
//        )
    }

    private fun forwardMouseEvent(
        event: MouseEvent
    ) {
        browserComponent.dispatchEvent(
            MouseEvent(
                browserComponent,
                event.id,
                event.`when`,
                event.modifiersEx,
                event.x,
                event.y,
                event.clickCount,
                event.isPopupTrigger,
                event.button
            )
        )
    }

    private fun forwardMouseWheelEvent(
        event: MouseWheelEvent
    ) {
        browserComponent.dispatchEvent(
            MouseWheelEvent(
                browserComponent,
                event.id,
                event.`when`,
                event.modifiersEx,
                event.x,
                event.y,
                event.clickCount,
                event.isPopupTrigger,
                event.scrollType,
                event.scrollAmount,
                event.wheelRotation
            )
        )
    }

    private fun forwardKeyEvent(
        event: KeyEvent
    ) {
        browserComponent.dispatchEvent(
            KeyEvent(
                browserComponent,
                event.id,
                event.`when`,
                event.modifiersEx,
                event.keyCode,
                event.keyChar,
                event.keyLocation
            )
        )
    }

    private fun updateImage(
        buffer: ByteBuffer,
        width: Int,
        height: Int
    ) {
        /*
         * CEF OSR levert BGRA.
         *
         * Op little-endian x86 wordt dat via IntBuffer
         * 0xAARRGGBB, wat BufferedImage.TYPE_INT_ARGB
         * direct kan gebruiken.
         */
        val pixels =
            IntArray(width * height)

        buffer
            .duplicate()
            .order(ByteOrder.LITTLE_ENDIAN)
            .asIntBuffer()
            .get(pixels)

        val newImage =
            BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_ARGB
            )

        newImage.setRGB(
            0,
            0,
            width,
            height,
            pixels,
            0,
            width
        )

        SwingUtilities.invokeLater {
            image = newImage
            repaint()
        }
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        image?.let {
            g.drawImage(
                it,
                0,
                0,
                width,
                height,
                null
            )
        }
    }

    fun dispose() {
        browser.renderHandler
            ?.removeOnPaintListener(paintListener)

        browserComponent.removePropertyChangeListener(
            "cursor",
            cursorChangeListener
        )

        SwingUtilities.invokeLater {
            driverWindow?.dispose()
            driverWindow = null
        }

        image = null
    }
}