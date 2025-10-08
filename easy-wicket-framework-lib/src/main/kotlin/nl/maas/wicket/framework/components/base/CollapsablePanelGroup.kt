package nl.maas.wicket.framework.components.base

import nl.maas.wicket.framework.objects.behavior.CSSScriptLoader
import nl.maas.wicket.framework.objects.behavior.JSScriptsLoader
import org.apache.wicket.markup.head.IHeaderResponse

class CollapsablePanelGroup(id: String, val height: Int = 50, vararg components: CollapsablePanel) :
    ComponentListView(id, component = *components) {

    companion object {
        val CONTENT_ID = ComponentListView.CONTENT_ID
    }

    override fun renderHead(response: IHeaderResponse) {
        super.renderHead(response)
        if (height != 0) {
            CSSScriptLoader.load(
                "CollapsablePanelGroup${this.markupId}",
                response,
                "#${markupId} .show{max-height: ${height}em;overflow: auto;}"
            )
        }
        components.map { it as CollapsablePanel }.forEach {
            if (it.visible) {
                JSScriptsLoader.load(
                    response,
                    "$('#${it.markupId}').addClass('show')"
                )
            } else {
                JSScriptsLoader.load(
                    response,
                    "$('#${it.markupId}').removeClass('show')"
                )
            }
        }
    }

    override fun onInitialize() {
        super.onInitialize()
        outputMarkupId = true
        components.map { it as CollapsablePanel }.forEach {
            it.onOpenFunction = { target ->
                components.map { other -> other as CollapsablePanel }
                    .filter { other -> !it.markupId.equals(other.markupId) && other.visible }
                    .forEach { other -> other.toggleVisible(target) }

            }
        }
    }
}