package nl.maas.wicket.framework.components.base

import nl.maas.wicket.framework.objects.behavior.CSSLoader
import nl.maas.wicket.framework.objects.behavior.JSScriptsLoader
import org.apache.wicket.AttributeModifier
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.markup.head.IHeaderResponse
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.protocol.http.WebApplication

class Loader(id: String, hiddenOnStart: Boolean = true) : Panel(id) {

    private var hidden = hiddenOnStart

    private val container = object : WebMarkupContainer("container") {
        init {
            outputMarkupId = true
        }

        override fun onBeforeRender() {
            super.onBeforeRender()
            addOrReplace(object : WebMarkupContainer("overlay") {
                init {
                    add(AttributeModifier.remove("hidden"))
                    if (hidden) {
                        add(AttributeModifier.replace("hidden", ""))
                    }
                }

                override fun renderHead(response: IHeaderResponse) {
                    super.renderHead(response)
                    when (hidden) {
                        false -> JSScriptsLoader.load(response, "$('#${markupId}').removeAttr('hidden')")
                        else -> JSScriptsLoader.load(response, "$('#${markupId}').attr('hidden','')")
                    }
                }
            })
        }
    }

    override fun renderHead(response: IHeaderResponse) {
        super.renderHead(response)
        CSSLoader.load(application as WebApplication, response, "/static/css/loader.css")
    }

    fun activate(target: AjaxRequestTarget) {
        hidden = false
        target.add(container)
    }

    fun deactivate(target: AjaxRequestTarget) {
        hidden = true
        target.add(container)
    }

    override fun onInitialize() {
        super.onInitialize()
        addOrReplace(container)
    }
}