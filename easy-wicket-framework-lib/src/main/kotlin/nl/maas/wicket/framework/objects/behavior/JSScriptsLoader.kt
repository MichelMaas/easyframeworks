package nl.maas.wicket.framework.objects.behavior

import org.apache.wicket.markup.head.IHeaderResponse
import org.apache.wicket.markup.head.OnDomReadyHeaderItem

class JSScriptsLoader private constructor(
    val response: IHeaderResponse,
    val scripts: Array<out String>
) {

    companion object {
        fun load(
            response: IHeaderResponse,
            vararg scripts: String
        ) {
            JSScriptsLoader(response, scripts).load()
        }
    }

    protected fun load() {
        scripts.forEach {
            response.render(
                OnDomReadyHeaderItem.forScript(it)
            )
        }
    }

}