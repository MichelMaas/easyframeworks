package nl.maas.wicket.framework.tools

import nl.maas.wicket.framework.services.Translator
import nl.maas.wicket.framework.services.TranslatorPlaceHolder
import java.time.DayOfWeek
import kotlin.reflect.KClass

class TranslatedDayOfWeek private constructor(val translator: Translator) {

    companion object {
        fun translatedDays(translator: Translator) =
            TranslatedDayOfWeek(translator).getDays()
    }

    fun getDays(): Array<String> {
        return DayOfWeek.values().map { translator.translate(it.name) }.toTypedArray()
    }
}