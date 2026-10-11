package nl.maas.wicket.framework.tools

import nl.maas.wicket.framework.objects.enums.RuntimeRequirement
import javax.swing.SwingUtilities

class RequirementsInstaller {
    private lateinit var runtimeManager: RuntimeManager

    fun install(requirement: RuntimeRequirement,runtimeManager: RuntimeManager) {
        this.runtimeManager = runtimeManager
    }

    private fun installJcef() {
        val dialog = RuntimePreparationDialog(
            configuration.applicationName
        )

        dialog.isVisible = true

        Thread {
            try {
                runtimeManager.ensureAvailable(
                    RuntimeRequirement.JCEF,
                    SwingRuntimeProgressHandler(dialog)
                )

                SwingUtilities.invokeLater {
                    dialog.dispose()
                    startBrowserInternal(url)
                }
            } catch (e: Exception) {
                // fout tonen
            }
        }.start()

        return
    }
}