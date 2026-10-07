package nl.maas.wicket.framework.viewer

import nl.maas.wicket.framework.domain.scrapers.ScrapeElement

abstract class ScrapeViewer() : Viewer(headless = true, icon = "") {

    private val DEFAULT_TIME_OUT: Long = 1

    abstract fun navigateTo(url: String): Boolean

    abstract fun findElementsByClass(className: String, timeOut: Long = DEFAULT_TIME_OUT): List<ScrapeElement>

    abstract fun findElementsByTagName(tagName: String, timeOut: Long = DEFAULT_TIME_OUT): List<ScrapeElement>

    abstract fun findElementById(id: String, timeOut: Long = DEFAULT_TIME_OUT): ScrapeElement?

    abstract fun findElementsByCSSSelector(cssSelector: String, timeOut: Long = DEFAULT_TIME_OUT): List<ScrapeElement>
}