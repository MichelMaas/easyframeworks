package nl.maas.framework.io.utils

import java.io.File
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

    fun dataDir(appName: String): Path {
        val userHome = Path.of(
            System.getProperty("user.home")
        )

        val osName = System.getProperty("os.name")
            .lowercase()

        return when {
            osName.contains("win") -> {
                val localAppData =
                    System.getenv("LOCALAPPDATA")
                        ?.takeIf { it.isNotBlank() }

                if (localAppData != null) {
                    Path.of(localAppData, appName)
                } else {
                    userHome
                        .resolve("AppData")
                        .resolve("Local")
                        .resolve(appName.lowercase())
                }
            }

            osName.contains("mac") -> {
                userHome
                    .resolve("Library")
                    .resolve("Application Support")
                    .resolve(appName.lowercase())
            }

            else -> {
                userHome.resolve(
                    ".local/share/${appName.lowercase()}"
                )
            }
        }
    }

    fun appDir(appName: String): Path {
        val candidates = buildList {
            System.getProperty("jpackage.app-path")
                ?.let { add(Path.of(it)) }

            System.getProperty("java.class.path")
                ?.split(File.pathSeparator)
                ?.mapNotNull {
                    runCatching { Path.of(it) }.getOrNull()
                }
                ?.let { addAll(it) }

            System.getProperty("user.dir")
                ?.let { add(Path.of(it)) }
        }

        return candidates
            .asSequence()
            .mapNotNull { findAncestor(it, appName) }
            .firstOrNull() ?: dataDir(appName)
    }

    private fun findAncestor(
        start: Path,
        name: String
    ): Path? {
        var current: Path? =
            start.toAbsolutePath().normalize()

        while (current != null) {
            if (current.fileName
                    ?.toString()
                    ?.equals(name, ignoreCase = true) == true
            ) {
                return current
            }

            current = current.parent
        }

        return null
    }

    fun userDir(): Path {
        return Path.of(System.getProperty("user.home"))
    }


}