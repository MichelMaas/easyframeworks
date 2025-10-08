package nl.maas.wicket.framework.panels

import nl.maas.wicket.framework.pages.RIAPage
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.markup.html.panel.Panel


abstract class RIAPanel(private var _id: String = "placeholder") : Panel(_id) {

    init {
        outputMarkupId = true
    }

    override fun onBeforeRender() {
        super.onBeforeRender()
        while (holdRenderWhile()) {
            Thread.sleep(1000)
        }
    }

    protected open fun holdRenderWhile(): Boolean {
        return false
    }

    override fun getId(): String {
        return _id
    }

    internal fun changeId(id: String): RIAPanel {
        _id = id
        return this
    }

    protected fun switchToPanel(riaPanel: RIAPanel, target: AjaxRequestTarget) {
        require(
            RIAPage::class.isInstance(target.page),
            { "switchToPanel() can only be called from a RIAPage or child" })
        val riaPage = target.page as RIAPage<*>
        riaPage.updatePanel(riaPanel, target)
    }

    open fun isAvailable(): Boolean {
        return true
    }

    fun reload(target: AjaxRequestTarget) {
        (target.page as RIAPage<*>).updatePanel(this, target)
    }

    fun activateLoader(target: AjaxRequestTarget) {
        (target.page as RIAPage<*>).activateLoader(target)
    }

}