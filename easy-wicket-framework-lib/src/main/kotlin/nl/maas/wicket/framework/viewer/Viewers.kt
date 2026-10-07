package nl.maas.wicket.framework.viewer

object Viewers {

    val scraper: ScrapeViewer get() = PlaywrightScrapeViewer()
    val viewer: Viewer get() = CEFViewer()

}