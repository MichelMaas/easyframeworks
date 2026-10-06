package nl.maas.wicket.framework.viewer

import nl.maas.wicket.framework.domain.scrapers.ScrapeElement

object PlaywrightScrapeViewer : ScrapeViewer() {

    override fun startBrowser(url: String, appName: String) {
    }

    override fun navigateTo(url: String): Boolean {
        return false
    }

    override fun findElementsByClass(className: String, timeOut: Long): List<ScrapeElement> {
        return emptyList()
    }

    override fun findElementsByTagName(tagName: String, timeOut: Long): List<ScrapeElement> {
        return emptyList()
    }

    override fun findElementById(id: String, timeOut: Long): ScrapeElement? {
        return null
    }

    override fun findElementsByCSSSelector(cssSelector: String, timeOut: Long): List<ScrapeElement> {
        return emptyList()
    }

    override fun close() {
    }

}