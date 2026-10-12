package nl.maas.wicket.framework.tools

import com.microsoft.playwright.Playwright
import nl.maas.wicket.framework.objects.enums.RuntimeRequirement
import nl.maas.wicket.framework.services.RuntimeProgressHandler
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile

class PlaywrightRuntimeInstaller(
    override val installDir: Path
) : RuntimeInstaller {

    companion object {
        private const val PLAYWRIGHT_BROWSERS_PATH =
            "PLAYWRIGHT_BROWSERS_PATH"

        private const val PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD =
            "PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD"

        private const val INSTALL_MARKER =
            ".easy-wicket-installed"
    }

    private val installMarker: Path
        get() = installDir.resolve(INSTALL_MARKER)

    override fun isInstalled(): Boolean {
        if (!installDir.isDirectory()) {
            return false
        }

        if (!installMarker.isRegularFile()) {
            return false
        }

        // Als we de Playwright-versie kunnen bepalen,
        // moet de geïnstalleerde versie daarmee overeenkomen.
        currentVersion()?.let { currentVersion ->
            val installedVersion =
                Files.readString(installMarker).trim()

            if (installedVersion != currentVersion) {
                return false
            }
        }

        // Marker én een echte Chromium-installatie moeten bestaan.
        return Files.list(installDir).use { paths ->
            paths.anyMatch { path ->
                Files.isDirectory(path) &&
                        path.fileName
                            .toString()
                            .startsWith("chromium-")
            }
        }
    }

    override fun install(
        progressHandler: RuntimeProgressHandler?
    ) {
        Files.createDirectories(installDir)

        // Een vorige mislukte poging mag nooit als compleet gelden.
        Files.deleteIfExists(installMarker)

        progressHandler?.started(
            RuntimeRequirement.PLAYWRIGHT,
            "Playwright Chromium voorbereiden..."
        )

        try {
            installChromium()

            Files.writeString(
                installMarker,
                currentVersion() ?: "installed"
            )

            progressHandler?.completed(
                RuntimeRequirement.PLAYWRIGHT
            )
        } catch (e: Exception) {
            progressHandler?.failed(
                RuntimeRequirement.PLAYWRIGHT,
                e
            )

            throw e
        }
    }

    private fun installChromium() {
        val javaExecutable =
            Path.of(
                System.getProperty("java.home"),
                "bin",
                if (isWindows()) "java.exe" else "java"
            )

        val process =
            ProcessBuilder(
                javaExecutable.toString(),
                "-cp",
                System.getProperty("java.class.path"),
                "com.microsoft.playwright.CLI",
                "install",
                "chromium"
            ).apply {
                environment()[PLAYWRIGHT_BROWSERS_PATH] =
                    installDir
                        .toAbsolutePath()
                        .normalize()
                        .toString()

                // De installatie moet juist wél mogen downloaden,
                // ongeacht wat het hoofdproces gebruikt.
                environment()
                    .remove(PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD)

                // Voor nu zien we Playwrights eigen voortgang
                // gewoon in de console.
                inheritIO()
            }
                .start()

        val finished =
            process.waitFor(
                10,
                TimeUnit.MINUTES
            )

        if (!finished) {
            process.destroyForcibly()

            throw IllegalStateException(
                "Timeout tijdens installatie van Playwright Chromium"
            )
        }

        if (process.exitValue() != 0) {
            throw IllegalStateException(
                "Installatie van Playwright Chromium is mislukt " +
                        "met exitcode ${process.exitValue()}"
            )
        }
    }

    private fun currentVersion(): String? =
        Playwright::class.java
            .`package`
            .implementationVersion

    private fun isWindows(): Boolean =
        System.getProperty("os.name")
            .lowercase()
            .contains("win")
}