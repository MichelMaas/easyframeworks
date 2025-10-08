package nl.maas.wicket.framework.components.base

import org.apache.wicket.Component
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.list.ListItem
import org.apache.wicket.markup.html.list.ListView
import org.apache.wicket.markup.html.panel.Panel

abstract class ComponentListView(id: String, vararg component: Component) : Panel(id) {

    protected var components = component.toList()

    companion object {
        val CONTENT_ID = "item"
    }

    protected val container = object : WebMarkupContainer("container") {
        init {
            outputMarkupId = true
        }

        override fun onBeforeRender() {
            super.onBeforeRender()
            addOrReplace(Components())
        }
    }


    override fun onInitialize() {
        super.onInitialize()
        add(container)
    }

    fun update(target: AjaxRequestTarget, vararg component: Component) {
        components = component.toList()
        target.add(container)
    }

    inner class Components : ListView<Component>("list", components) {

        override fun populateItem(item: ListItem<Component>) {
            item.addOrReplace(item.modelObject)
        }
    }
}