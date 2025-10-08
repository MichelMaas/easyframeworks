package nl.maas.wicket.framework.services

import nl.maas.wicket.framework.services.Translator

abstract class TranslatorProvider:Service {
    abstract fun getTranslatorFor(language:String):Translator
}