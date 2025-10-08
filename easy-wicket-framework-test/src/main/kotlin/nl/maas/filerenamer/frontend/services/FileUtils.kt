package nl.maas.filerenamer.frontend.services

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.function.BiPredicate
import java.util.stream.Collectors

class FileUtils {
    companion object {
        fun findFile(fileName: String): String {
            val files = if (Files.exists(Path.of(fileName))) listOf(Path.of(fileName)) else Files.find(
                Paths.get("../../").toRealPath(), 8,
                BiPredicate({ path, basicFileAttributes ->
                    val file: File = path.toFile()
                    !file.isDirectory &&
                            file.absolutePath.endsWith(fileName, true)
                })
            ).collect(Collectors.toList())
            return files.firstOrNull { it.toRealPath().toString().contains("easy-wicket-framework-test") }?.toRealPath()
                .toString()
        }
    }
}