package nl.maas.wicket.framework.test.wicket.panels

import nl.maas.wicket.framework.components.base.DynamicPanel
import nl.maas.wicket.framework.panels.RIAPanel
import nl.maas.wicket.framework.test.wicket.caches.ModelCache
import nl.maas.wicket.framework.viewer.ScrapeViewer
import nl.maas.wicket.framework.viewer.Viewers
import org.apache.wicket.Component
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.spring.injection.annot.SpringBean

class Panel2 : RIAPanel() {

    @SpringBean
    private lateinit var modelCache: ModelCache
    private lateinit var scraper: ScrapeViewer
    override fun onInitialize() {
        super.onInitialize()
        scraper = Viewers.scraper
    }

    override fun onBeforeRender() {
        super.onBeforeRender()
        scraper.startBrowser("https://www.nu.nl", "wicket-test")
        val items = scraper.findElementsByClass("app-nav-item").map { it.text }
        addOrReplace(createContent(items))
    }

    private fun createContent(items: List<String>): Component {
        return DynamicPanel("content").addRows(
            "Row" to intArrayOf(12),
        ).addOrReplaceComponentToColumn("Row", 0, Label(DynamicPanel.ROW_CONTENT_ID, items.joinToString(", ")))

    }


    override fun isAvailable(): Boolean {
        return true
    }
}