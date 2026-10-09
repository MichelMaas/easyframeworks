package nl.maas.wicket.framework.domain.scrapers

import com.microsoft.playwright.Locator

internal class PlaywrightScrapeElement(private val locator: Locator) : ScrapeElement {
    override val text: String
        get() = locator.innerText()

    override fun getAttribute(
        name: String
    ): String? =
        locator.getAttribute(name)

    override fun findElementsByClass(
        className: String
    ): List<ScrapeElement> =
        locator.locator(".$className")
            .all()
            .map { PlaywrightScrapeElement(it) }

    override fun findElementById(
        id: String
    ): ScrapeElement? {
        val result =
            locator.locator("#$id")

        return if (result.count() > 0) {
            PlaywrightScrapeElement(result.first())
        } else {
            null
        }
    }

    override fun findElementsByTagName(
        tagName: String
    ): List<ScrapeElement> =
        locator.locator(tagName)
            .all()
            .map { PlaywrightScrapeElement(it) }
}