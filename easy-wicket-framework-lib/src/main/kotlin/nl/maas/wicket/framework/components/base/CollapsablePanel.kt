package nl.maas.wicket.framework.components.base

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons
import nl.maas.wicket.framework.components.elemental.SimpleAjaxButton
import nl.maas.wicket.framework.objects.behavior.JSScriptsLoader
import nl.maas.wicket.framework.services.Translator
import nl.maas.wicket.framework.services.TranslatorPlaceHolder
import org.apache.wicket.Component
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.markup.head.IHeaderResponse
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.panel.Panel

open class CollapsablePanel(
    id: String,
    val label: String,
    val content: Component,
    val translator: Translator = TranslatorPlaceHolder(),
    val collapseButtonType: Buttons.Type = Buttons.Type.Primary,
    _visible: Boolean = false
) : Panel(id) {

    internal var onOpenFunction = { target: AjaxRequestTarget -> }
    internal var onCollapseFunction = { target: AjaxRequestTarget -> }
    internal var visible = _visible

    companion object {
        val CONTENT_ID = "content"
    }

    private val container = object : WebMarkupContainer("panelToHide") {

        init {
            outputMarkupId = true
        }

        override fun onInitialize() {
            super.onInitialize()
            add(content)
        }

        override fun renderHead(response: IHeaderResponse) {
            super.renderHead(response)
            if (visible) {
                JSScriptsLoader.load(
                    response,
                    "$('.accordion').find('#${this.markupId}').addClass('show')"
                )
            } else {
                JSScriptsLoader.load(
                    response,
                    "$('.accordion').find('#${this.markupId}').removeClass('show')"
                )
            }
        }

    }

    fun createButton(type: Buttons.Type = Buttons.Type.Primary): SimpleAjaxButton {
        val button =
            object : SimpleAjaxButton("button", label, type, Size.SMALL, translator, true) {

                override fun onClick(target: AjaxRequestTarget) {
                    toggleVisible(target)
                    if (visible) {
                        onOpenFunction(target)
                        onOpen(target)
                    } else {
                        onCollapseFunction(target)
                        onCollapse(target)
                    }
                }
            }
        return button
    }


    override fun onInitialize() {
        super.onInitialize()
        addOrReplace(container, createButton(collapseButtonType))
    }


    fun toggleVisible(target: AjaxRequestTarget): CollapsablePanel {
        visible = !visible
        target.add(container)
        return this
    }

    open fun onCollapse(target: AjaxRequestTarget) {
    }

    open fun onOpen(target: AjaxRequestTarget) {

    }
}