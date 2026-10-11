package nl.maas.wicket.framework.tools

import nl.maas.wicket.framework.services.RuntimeProgressHandler
import java.nio.file.Path

interface RuntimeInstaller {
    val installDir: Path
    fun isInstalled(): Boolean
    fun install(progressHandler: RuntimeProgressHandler? = null)
}