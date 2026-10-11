package nl.maas.wicket.framework.tools

import nl.maas.wicket.framework.objects.enums.RuntimeRequirement
import nl.maas.wicket.framework.services.RuntimeProgressHandler
import java.nio.file.Files
import java.nio.file.Path

class PlaywrightRuntimeInstaller(override val installDir: Path) : RuntimeInstaller {

    override fun isInstalled(): Boolean {
        if (!Files.isDirectory(installDir)) {
            return false
        }

        return Files.list(installDir).use {
            it.anyMatch { path ->
                path.fileName.toString().startsWith("chromium")
            }
        }
    }

    override fun install(progressHandler: RuntimeProgressHandler?) {
        Files.createDirectories(installDir)

        progressHandler?.started(
            RuntimeRequirement.PLAYWRIGHT,
            "Playwright Chromium voorbereiden"
        )

        // hier komt straks de echte Playwright install logic

        progressHandler?.completed(
            RuntimeRequirement.PLAYWRIGHT
        )
    }
}