package nl.maas.wicket.framework.viewer

object Viewers {

    fun getScrapeViewer(): ScrapeViewer {
        return PlaywrightScrapeViewer
    }

    fun getViewer(): Viewer {
        return CEFViewer
    }
}