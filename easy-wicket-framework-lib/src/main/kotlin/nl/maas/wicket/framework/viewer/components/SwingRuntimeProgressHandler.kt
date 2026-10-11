package nl.maas.wicket.framework.viewer.components

import nl.maas.wicket.framework.objects.enums.RuntimeRequirement
import nl.maas.wicket.framework.services.RuntimeProgressHandler
import org.apache.commons.lang3.StringUtils
import javax.swing.SwingUtilities

class SwingRuntimeProgressHandler(
    private val dialog: RuntimePreparationDialog
) : RuntimeProgressHandler {

    override fun started(
        requirement: RuntimeRequirement,
        message: String
    ) {
        SwingUtilities.invokeLater {
            dialog.setStatus(message)
            dialog.setIndeterminate()
        }
    }

    override fun progress(requirement: RuntimeRequirement, percent: Int, message: String?) {
        SwingUtilities.invokeLater {
            dialog.setStatus(message ?: StringUtils.EMPTY)

            if (percent == null) {
                dialog.setIndeterminate()
            } else {
                dialog.setProgress(percent)
            }
        }
    }

    override fun completed(
        requirement: RuntimeRequirement
    ) {
        SwingUtilities.invokeLater {
            dialog.setProgress(100)
            dialog.setStatus("Gereed")
        }
    }

    override fun failed(
        requirement: RuntimeRequirement,
        throwable: Throwable
    ) {
        SwingUtilities.invokeLater {
            dialog.setStatus(
                "Installatie mislukt: ${throwable.message}"
            )
        }
    }
}