package nl.maas.framework.io.utils

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.function.BiPredicate
import java.util.stream.Collectors
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists

object FileUtils {
    fun findFileOrFolder(fileName: String, appFolder: String = System.getProperty("appFolder")): String? {
        val files = runBlocking {
            if (Files.exists(Path.of(appFolder, fileName))) listOf(Path.of(appFolder, fileName)) else if (Files.exists(
                    Path.of(appFolder)
                )
            ) async {
                Files.find(
                    Paths.get(appFolder).toAbsolutePath(), 5,
                    BiPredicate({ path, basicFileAttributes ->
                        path.resolve(fileName).exists()
                    })
                )
            }.await().collect(Collectors.toList())
            else listOf()
        }
        return runBlocking {
            files.firstOrNull() { async { it.toRealPath().toString().contains(appFolder) }.await() }
                ?.absolutePathString()
        }
    }

    fun findFileOrFolderInAnyOf(fileName: String, vararg appFolder: Path): Path? {
        val found =
            appFolder.map { findFileOrFolder(fileName, it.absolutePathString()) }.filterNotNull().firstOrNull()
        return found?.let { Path.of(it) }
    }

    fun findFileOrFolderInAnyAppFolder(fileName: String, appName: String): Path? {
        return findFileOrFolderInAnyOf(
            fileName, AppDirectories.appDir(appName), AppDirectories.dataDir(appName),
            AppDirectories.cacheDir(appName)
        )
    }

    fun getPathFromCache(fileName: String, appName: String): Path? {
        return AppDirectories.cacheDir(appName).resolve(fileName)
    }

    fun getPathFromData(fileName: String, appName: String): Path? {
        return AppDirectories.dataDir(appName).resolve(fileName)
    }

    fun pathFromResourceOrPath(path: String): Path? {
        return this::class.java.getResource(path)?.let { Path.of(it.file) } ?: if (Path.of(path)
                .exists()
        ) Path.of(path) else null
    }

    fun pathFromResourceOrPath(path: Path): Path? {
        return pathFromResourceOrPath(path.absolutePathString())
    }

}