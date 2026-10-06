package nl.maas.wicket.framework.viewer.components

import nl.maas.wicket.framework.viewer.component.CEFComponent
import org.cef.browser.CefBrowser
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GraphicsEnvironment
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame

class CEFFrame(
    browser: CefBrowser,
    title: String = "Easy Wicket",
    private val onCloseRequest: () -> Unit
) : JFrame(title) {

    private val cefComponent: CEFComponent

    init {

        cefComponent = CEFComponent(browser, this)
        layout = BorderLayout()

        add(
            cefComponent,
            BorderLayout.CENTER
        )

        val bounds =
            GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .maximumWindowBounds

        val width =
            (bounds.width * 0.8).toInt()

        val height =
            (bounds.height * 0.8).toInt()

        minimumSize =
            Dimension(
                width,
                height
            )

        setSize(
            width,
            height
        )

        setLocationRelativeTo(null)

        defaultCloseOperation =
            DO_NOTHING_ON_CLOSE

        addWindowListener(
            object : WindowAdapter() {
                override fun windowClosing(
                    e: WindowEvent
                ) {
                    onCloseRequest()
                }
            }
        )

        extendedState =
            MAXIMIZED_BOTH
    }


    fun open() {
        isVisible = true
    }

    override fun dispose() {
        cefComponent.dispose()
        super.dispose()
    }
}