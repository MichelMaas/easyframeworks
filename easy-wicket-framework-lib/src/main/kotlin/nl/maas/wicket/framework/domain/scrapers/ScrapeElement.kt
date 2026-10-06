package nl.maas.wicket.framework.domain.scrapers

interface ScrapeElement {
    val text: String

    fun getAttribute(
        name: String
    ): String?

    fun findElementByClass(
        className: String
    ): List<ScrapeElement>

    fun findElementById(id: String): ScrapeElement?
    
    fun findElementsByTagName(
        tagName: String
    ): List<ScrapeElement>
}