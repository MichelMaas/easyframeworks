package nl.maas.framework.io.utils

import java.nio.file.Path

object AppDirectories {

    fun cacheDir(appName: String? = null): Path {
        val os = System.getProperty("os.name").lowercase()

        val base = when {
            os.contains("win") -> {
                System.getenv("LOCALAPPDATA")
                    ?.takeIf { it.isNotBlank() }
                    ?.let(Path::of)
                    ?: Path.of(System.getProperty("user.home"), "AppData", "Local")
            }

            os.contains("mac") -> {
                Path.of(
                    System.getProperty("user.home"),
                    "Library",
                    "Caches"
                )
            }

            else -> {
                System.getenv("XDG_CACHE_HOME")
                    ?.takeIf { it.isNotBlank() }
                    ?.let(Path::of)
                    ?: Path.of(System.getProperty("user.home"), ".cache")
            }
        }

        return appName?.let { base.resolve(appName) } ?: base
    }

    fun userDir(): Path {
        return Path.of(System.getProperty("user.home"))
    }


}