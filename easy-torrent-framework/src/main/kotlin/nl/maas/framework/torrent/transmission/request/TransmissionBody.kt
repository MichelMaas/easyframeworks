package nl.maas.framework.torrent.transmission.request

import com.google.gson.Gson
import nl.maas.framework.torrent.transmission.METHODS
import kotlin.random.Random

abstract class TransmissionBody(val sessioID: String = "") {
    protected val arguments: MutableMap<String, Any> = mutableMapOf()
    protected lateinit var method: String
    val tag: Long


    init {
        tag = Random.nextLong()
    }

    protected fun setArgument(argument: String, value: Any) {
        arguments.put(argument, value)
    }

    protected fun <T : Any> appendArguments(argument: String, vararg value: T) {
        var values: MutableList<T> = mutableListOf()
        if (arguments.contains(argument)) {
            values.addAll((arguments.get(argument) as Collection<T>))
        }
        values.addAll(value)
        arguments.put(argument, values)
    }

    protected fun getArgument(argument: String): Any {
        return arguments.get(argument) ?: "NOT FOUND"
    }

    protected fun setMethod(methods: METHODS) {
        method = methods.toMethodString()
    }

    fun toJSon(): String {
        return Gson().toJson(this)
    }
}