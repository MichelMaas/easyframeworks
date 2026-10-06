package nl.maas.wicket.framework.domain.scrapers

import org.openqa.selenium.By
import org.openqa.selenium.WebElement

class SeleniumScrapeElement(private val webElement: WebElement) : ScrapeElement {
    override val text: String
        get() = TODO("Not yet implemented")

    override fun getAttribute(name: String): String? {
        return webElement.getDomAttribute(name)
    }

    override fun findElementByClass(className: String): List<ScrapeElement> {
        return webElement.findElements(By.className(className)).filterNotNull().map { SeleniumScrapeElement(it) }
    }

    override fun findElementById(id: String): ScrapeElement? {
        return webElement.findElement(By.id(id))?.let { SeleniumScrapeElement(it) }
    }

    override fun findElementsByTagName(tagName: String): List<ScrapeElement> {
        return webElement.findElements(By.tagName(tagName)).filterNotNull().map { SeleniumScrapeElement(it) }
    }
}