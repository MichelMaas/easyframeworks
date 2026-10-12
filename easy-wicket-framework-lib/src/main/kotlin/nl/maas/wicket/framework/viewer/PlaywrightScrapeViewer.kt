package nl.maas.wicket.framework.viewer

import com.microsoft.playwright.*
import com.microsoft.playwright.options.WaitForSelectorState
import nl.maas.framework.io.utils.FileUtils
import nl.maas.wicket.framework.domain.scrapers.PlaywrightScrapeElement
import nl.maas.wicket.framework.domain.scrapers.ScrapeElement
import nl.maas.wicket.framework.objects.enums.RuntimeRequirement
import nl.maas.wicket.framework.tools.PlaywrightRuntimeInstaller
import nl.maas.wicket.framework.tools.RuntimeManager
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists

internal class PlaywrightScrapeViewer() : ScrapeViewer() {
    private val PLAYWRIGHT_BROWSERS_PATH = "PLAYWRIGHT_BROWSERS_PATH"
    private var playWrightPath: Path? = null
    private lateinit var options: Playwright.CreateOptions
    private var playwright: Playwright? = null
    private var browser: Browser? = null
    private var context: BrowserContext? = null
    private var page: Page? = null

    init {
        setBrowserLocation()
    }

    override fun startBrowser(url: String, appName: String) {
        val runtimeManager = RuntimeManager(appName)
        options.env?.getValue(PLAYWRIGHT_BROWSERS_PATH) ?: run {
            setBrowserLocation(
                runtimeManager.pathFor(
                    RuntimeRequirement.PLAYWRIGHT
                ).absolutePathString()
            )
        }
        if (!runtimeManager.isAvailable(RuntimeRequirement.PLAYWRIGHT)) {
            options.env?.get(PLAYWRIGHT_BROWSERS_PATH)?.let {
                PlaywrightRuntimeInstaller(Path.of(it))
                runtimeManager.ensureAvailable(RuntimeRequirement.PLAYWRIGHT)
            }
        }

        playwright = Playwright.create(options)
        browser = playwright!!.chromium().launch(BrowserType.LaunchOptions().setHeadless(headless))
        context = browser!!.newContext()
        page = context!!.newPage()
        page!!.navigate(url)
    }

    override fun setBrowserLocation(location: String?) {

        val path = location?.let {
            if (Path.of(it, "playwright").exists()) {
                Path.of(it)
            } else {
                Path.of(System.getProperty(it))
            }
        } ?: FileUtils.getPathFromCache("playwright", "easy-wicket-framework")!!
        options = Playwright.CreateOptions()
        val env = System.getenv().toMutableMap()

        env[PLAYWRIGHT_BROWSERS_PATH] =
            path.toAbsolutePath().toString()
        options.setEnv(env)
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