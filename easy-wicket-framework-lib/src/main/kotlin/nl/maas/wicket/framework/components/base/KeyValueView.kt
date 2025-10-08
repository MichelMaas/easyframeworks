package nl.maas.wicket.framework.components.base

import nl.maas.wicket.framework.services.Translator
import nl.maas.wicket.framework.services.TranslatorPlaceHolder
import org.apache.wicket.ajax.AjaxEventBehavior
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.markup.html.list.ListItem
import org.apache.wicket.markup.html.list.ListView
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.model.Model

open class KeyValueView(id: String, val translator: Translator, vararg val pairs: Pair<*, *>) :
    Panel(id) {

    constructor(id: String, vararg pairs: Pair<*, *>) : this(id, TranslatorPlaceHolder(), *pairs)

    override fun onBeforeRender() {
        super.onBeforeRender()
        addOrReplace(createList())
    }

    private fun createList(): ListView<Pair<*, *>> {
        val list = pairs.map { it.first.toString() to it.second.toString() }
        return object : ListView<Pair<*, *>>("list", list) {
            override fun populateItem(item: ListItem<Pair<*, *>>) {
                item.addOrReplace(
                    SingleDataViewPanel(
                        "item",
                        Model.of(translator.translate(item.modelObject.first.toString()) to translator.translate(item.modelObject.second.toString()))
                    )
                )
                item.add(object : AjaxEventBehavior("click") {
                    override fun onEvent(target: AjaxRequestTarget) {
                        onRowClicked(target, item.modelObject)
                    }
                })
            }
        }
    }

    open fun onRowClicked(target: AjaxRequestTarget, rowValue: Pair<*, *>) {}
}