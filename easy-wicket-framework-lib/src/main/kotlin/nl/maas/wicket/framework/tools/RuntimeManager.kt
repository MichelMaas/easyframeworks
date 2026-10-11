package nl.maas.wicket.framework.tools

import nl.maas.framework.io.utils.AppDirectories
import nl.maas.wicket.framework.objects.enums.RuntimeRequirement
import nl.maas.wicket.framework.services.RuntimeProgressHandler
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
        progressHandler: RuntimeProgressHandler? = null
    ) {
        val installer = installers.getValue(requirement)

        if (installer.isInstalled()) {
            return
        }

        installer.install(progressHandler)
    }

    fun pathFor(requirement: RuntimeRequirement): Path =
        installers.getValue(requirement).installDir
}
