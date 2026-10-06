package nl.maas.wicket.framework.viewer

import org.openqa.selenium.WebElement

abstract class ScrapeViewer : Viewer() {

    private val DEFAULT_TIME_OUT: Long = 1


    abstract fun navigateTo(url: String): Boolean

    abstract fun findElementsByClass(className: String, timeOut: Long = DEFAULT_TIME_OUT): List<WebElement>

    abstract fun findElementsByTagName(tagName: String, timeOut: Long = DEFAULT_TIME_OUT): List<WebElement>

    abstract fun findElementById(id: String, timeOut: Long = DEFAULT_TIME_OUT): WebElement?

    abstract fun findElementsByCSSSelector(cssSelector: String, timeOut: Long = DEFAULT_TIME_OUT): List<WebElement>
}