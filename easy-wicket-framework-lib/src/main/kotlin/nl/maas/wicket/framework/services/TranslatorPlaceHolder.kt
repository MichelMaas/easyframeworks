package nl.maas.wicket.framework.services

import java.util.*

class TranslatorPlaceHolder:Translator {
    override val language = Locale.getDefault().language

    override fun translate(word: String): String {
        return word.lowercase().replaceFirstChar { it.uppercase() }
    }

    override fun unTranslate(word: String): String {
        return word.uppercase()
    }
}