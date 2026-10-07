package nl.maas.wicket.framework.viewer

import com.microsoft.playwright.*
import com.microsoft.playwright.options.WaitForSelectorState
import nl.maas.wicket.framework.domain.scrapers.PlaywrightScrapeElement
import nl.maas.wicket.framework.domain.scrapers.ScrapeElement

internal class PlaywrightScrapeViewer : ScrapeViewer() {

    private var playwright: Playwright? = null
    private var browser: Browser? = null
    private var context: BrowserContext? = null
    private var page: Page? = null

    override fun startBrowser(url: String, appName: String) {
        playwright = Playwright.create()
        browser = playwright!!.chromium().launch(BrowserType.LaunchOptions().setHeadless(headless))
        context = browser!!.newContext()
        page = context!!.newPage()
        page!!.navigate(url)
    }

    override fun navigateTo(url: String): Boolean {
        return try {
            page?.navigate(url)
            true
        } catch (e: Exception) {
            println(e.message)
            false
        }
    }

    override fun findElementsByClass(className: String, timeOut: Long): List<ScrapeElement> {
        val locator = page!!.locator(".$className")
        return try {
            locator.first()
                .waitFor(Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(timeOut * 1000.0))
            locator.all().map { PlaywrightScrapeElement(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun findElementsByTagName(tagName: String, timeOut: Long): List<ScrapeElement> {
        val locator = page!!.locator(tagName)
        return try {
            locator.first()
                .waitFor(Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(timeOut * 1000.0))
            locator.all().map { PlaywrightScrapeElement(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun findElementById(id: String, timeOut: Long): ScrapeElement? {
        val locator = page!!.locator("#$id")
        return try {
            locator.first()
                .waitFor(Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(timeOut * 1000.0))
            PlaywrightScrapeElement(locator.first())
        } catch (e: Exception) {
            null
        }
    }

    override fun findElementsByCSSSelector(cssSelector: String, timeOut: Long): List<ScrapeElement> {
        val locator = page!!.locator(cssSelector)
        return try {
            locator.first()
                .waitFor(Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(timeOut * 1000.0))
            locator.all().map { PlaywrightScrapeElement(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun close() {
        page?.close()
        page = null
        context?.close()
        context = null
        browser?.close()
        browser = null
        playwright?.close()
        playwright = null
    }

}