package nl.maas.wicket.framework.tools

import java.io.File
import java.nio.file.Path
import kotlin.io.path.exists

object FileUtil {

    fun fileFromResourceOrPath(path: String): File? {
        return this::class.java.getResource(path)?.let { File(it.file) } ?: if (Path.of(path)
                .exists()
        ) File(path) else null
    }
}