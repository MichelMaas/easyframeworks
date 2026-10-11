package nl.maas.framework.io.utils

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.function.BiPredicate
import java.util.stream.Collectors
import kotlin.io.path.exists

object FileUtils {
    fun findFileOrFolder(fileName: String, appFolder: String = System.getProperty("appFolder")): String {
        val files = runBlocking {
            if (Files.exists(Path.of(fileName))) listOf(Path.of(fileName)) else async {
                Files.find(
                    Paths.get("").toRealPath(), 5,
                    BiPredicate({ path, basicFileAttributes ->
                        val file: File = path.toFile()
                        !file.isDirectory() &&
                                file.absolutePath.endsWith(fileName, true)
                    })
                )
            }.await().collect(Collectors.toList())
        }
        return runBlocking {
            files.firstOrNull() { async { it.toRealPath().toString().contains(appFolder) }.await() }?.toRealPath()
                .toString() ?: StringUtils.EMPTY
        }
    }

    fun getPathFromCache(fileName: String, appName: String): Path? {
        return AppDirectories.cacheDir(appName).resolve(fileName)
    }

    fun pathFromResourceOrPath(path: String): Path? {
        return this::class.java.getResource(path)?.let { Path.of(it.file) } ?: if (Path.of(path)
                .exists()
        ) Path.of(path) else null
    }

    fun pathFromResourceOrPath(path: Path): Path? {
        return pathFromResourceOrPath(path.toString())
    }

}