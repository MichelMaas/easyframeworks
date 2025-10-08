package nl.maas.wicket.framework.services

import java.io.Serializable

interface Translator : Serializable {
    val language: String
    fun translate(word: String): String
    fun unTranslate(word: String): String
}