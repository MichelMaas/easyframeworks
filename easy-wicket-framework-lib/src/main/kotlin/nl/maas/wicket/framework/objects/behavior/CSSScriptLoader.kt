package nl.maas.wicket.framework.objects.behavior

import org.apache.wicket.markup.head.CssContentHeaderItem
import org.apache.wicket.markup.head.IHeaderResponse

class CSSScriptLoader(
    private val cssName: String,
    private val response: IHeaderResponse,
    private val css: String,
    private vararg val replacers: Pair<String, String>
) {
    companion object {
        fun load(cssName: String, response: IHeaderResponse, css: String, vararg replacers: Pair<String, String>) {
            CSSScriptLoader(cssName, response, css, *replacers).load()
        }
    }

    protected fun load() {
        var newCss = css
        replacers.forEach { newCss = newCss.replace(it.first, it.second) }
        val cssContentHeaderItem = CssContentHeaderItem(css, "css/${cssName}.css")
        response.render(cssContentHeaderItem)
    }
}