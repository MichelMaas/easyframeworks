package nl.maas.wicket.framework.tools

import nl.maas.framework.io.utils.AppDirectories
import nl.maas.wicket.framework.objects.enums.RuntimeRequirement
import nl.maas.wicket.framework.viewer.components.RuntimePreparationUi
import java.nio.file.Path

class RuntimeManager(appName: String) {
    private val cacheDir = AppDirectories.cacheDir(appName)

    private val installers = mapOf(
        RuntimeRequirement.JCEF to
                JcefRuntimeInstaller(cacheDir.resolve("jcef")),

        RuntimeRequirement.PLAYWRIGHT to
                PlaywrightRuntimeInstaller(cacheDir.resolve("playwright"))
    )

    fun isAvailable(requirement: RuntimeRequirement): Boolean =
        installers.getValue(requirement).isInstalled()

    fun ensureAvailable(
        requirement: RuntimeRequirement,
        ui: RuntimePreparationUi? = null
    ) {
        val installer = installers.getValue(requirement)

        if (installer.isInstalled()) {
            return
        }

        ui?.show()

        try {
            installer.install(
                ui?.progressHandler
            )

            ui?.close()
        } catch (e: Exception) {
            ui?.failed(e)
            throw e
        }
    }

    fun pathFor(requirement: RuntimeRequirement): Path =
        installers.getValue(requirement).installDir
}
