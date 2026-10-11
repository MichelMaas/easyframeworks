package nl.maas.framework.io.utils

import com.google.gson.Gson
import java.io.BufferedReader
import java.io.FileReader


object JsonUtils {
    fun <T> load(path: String, clazz: Class<T>): T? {
        val jsonFile = FileUtils.pathFromResourceOrPath(path)
        return jsonFile?.let {
            Gson().fromJson(
                BufferedReader(FileReader(it.toAbsolutePath().toString(), Charsets.UTF_8)),
                clazz
            ) as T
        }
    }
}
