package nl.maas.wicket.framework.viewer.components

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import javax.swing.*

class RuntimePreparationDialog(
    applicationName: String
) : JDialog() {

    private val statusLabel = JLabel("Voorbereiden...")

    private val progressBar = JProgressBar(0, 100).apply {
        isStringPainted = true
        value = 0
        string = "0%"
    }

    init {
        title = "$applicationName voorbereiden"
        defaultCloseOperation = DO_NOTHING_ON_CLOSE
        isModal = false
        isResizable = false

        val titleLabel = JLabel("$applicationName voorbereiden").apply {
            font = font.deriveFont(Font.BOLD, 20f)
        }

        val explanationLabel = JLabel(
            """
            <html>
            De applicatie wordt voorbereid voor gebruik.<br>
            Benodigde onderdelen worden automatisch geïnstalleerd.
            </html>
            """.trimIndent()
        )

        val contentPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = BorderFactory.createEmptyBorder(24, 28, 24, 28)

            add(titleLabel)
            add(Box.createVerticalStrut(12))
            add(explanationLabel)
            add(Box.createVerticalStrut(24))
            add(statusLabel)
            add(Box.createVerticalStrut(12))
            add(progressBar)
            add(Box.createVerticalStrut(16))

            add(
                JLabel(
                    """
                    <html>
                    De applicatie start automatisch zodra de voorbereiding klaar is.
                    </html>
                    """.trimIndent()
                )
            )
        }

        layout = BorderLayout()
        add(contentPanel, BorderLayout.CENTER)

        preferredSize = Dimension(500, 260)

        pack()
        setLocationRelativeTo(null)
    }

    fun setStatus(message: String) {
        onEdt {
            statusLabel.text = message
        }
    }

    fun setProgress(percent: Int) {
        onEdt {
            progressBar.isIndeterminate = false
            progressBar.value = percent.coerceIn(0, 100)
            progressBar.string = "${progressBar.value}%"
        }
    }

    fun setIndeterminate() {
        onEdt {
            progressBar.isIndeterminate = true
            progressBar.string = "Bezig..."
        }
    }

    fun completed() {
        onEdt {
            progressBar.isIndeterminate = false
            progressBar.value = 100
            progressBar.string = "Gereed"
            statusLabel.text = "Voorbereiding voltooid"
        }
    }

    fun failed(message: String) {
        onEdt {
            progressBar.isIndeterminate = false
            progressBar.string = "Mislukt"
            statusLabel.text = message
        }
    }

    private fun onEdt(action: () -> Unit) {
        if (SwingUtilities.isEventDispatchThread()) {
            action()
        } else {
            SwingUtilities.invokeLater(action)
        }
    }
}