package nl.maas.wicket.framework.tools

import me.friwi.jcefmaven.CefAppBuilder
import nl.maas.wicket.framework.objects.enums.RuntimeRequirement
import nl.maas.wicket.framework.services.JcefProgressHandler
import nl.maas.wicket.framework.services.RuntimeProgressHandler
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isDirectory

class JcefRuntimeInstaller(override val installDir: Path) : RuntimeInstaller {

    override fun isInstalled(): Boolean {
        return installDir.isDirectory() && Files.list(installDir).use {
            it.findAny().isPresent
        }
    }

    override fun install(
        progressHandler: RuntimeProgressHandler?
    ) {
        Files.createDirectories(installDir)

        progressHandler?.started(
            RuntimeRequirement.JCEF,
            "JCEF browser voorbereiden..."
        )

        try {
            val builder = CefAppBuilder().apply {
                setInstallDir(installDir.toFile())

                progressHandler?.let {
                    setProgressHandler(
                        JcefProgressHandler(it)
                    )
                }
            }

            builder.install()

            progressHandler?.completed(
                RuntimeRequirement.JCEF
            )

        } catch (e: Exception) {
            progressHandler?.failed(
                RuntimeRequirement.JCEF,
                e
            )
            throw e
        }
    }
}