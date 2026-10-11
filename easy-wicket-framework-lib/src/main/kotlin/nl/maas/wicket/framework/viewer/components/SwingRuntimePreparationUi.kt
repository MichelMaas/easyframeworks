package nl.maas.wicket.framework.viewer.components

import nl.maas.wicket.framework.services.SwingRuntimeProgressHandler
import javax.swing.SwingUtilities

class SwingRuntimePreparationUi(
    applicationName: String
) : RuntimePreparationUi {

    private val dialog =
        RuntimePreparationDialog(applicationName)

    override val progressHandler =
        SwingRuntimeProgressHandler(dialog)

    override fun show() {
        SwingUtilities.invokeLater {
            dialog.isVisible = true
        }
    }

    override fun close() {
        SwingUtilities.invokeLater {
            dialog.dispose()
        }
    }

    override fun failed(throwable: Throwable) {
        dialog.failed(
            throwable.message ?: "Installatie mislukt"
        )
    }
}