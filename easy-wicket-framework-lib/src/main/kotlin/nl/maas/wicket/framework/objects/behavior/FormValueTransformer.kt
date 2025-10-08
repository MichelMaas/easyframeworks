package nl.maas.wicket.framework.objects.behavior

import java.io.Serializable

interface FormValueTransformer<T : Serializable> : Serializable {

    fun fromString(value: String): T

    fun toString(value: T): String
}