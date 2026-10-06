package nl.maas.wicket.framework.viewer

import com.vaadin.open.App
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement
import java.util.*

object PlayWrightScrapeViewer : ScrapeViewer() {
    var app = App.CHROME
    val os =
        if (System.getProperty("os.name").lowercase(Locale.getDefault()).contains("win")) "win" else System.getProperty(
            "os.name"
        ).lowercase(Locale.getDefault())
    val arch = if (System.getProperty("os.arch").lowercase(Locale.getDefault()).contains("64")) "64" else "32"
    val pathDelimiter = if (os.equals("win")) "\\" else "/"
    override fun startBrowser(url: String, appName: String) {
    }

    override fun navigateTo(url: String): Boolean {
        return false
    }

    override fun findElementsByClass(className: String, timeOut: Long): List<WebElement> {
        return emptyList()
    }

    override fun findElementsByTagName(tagName: String, timeOut: Long): List<WebElement> {
        return emptyList()
    }

    override fun findElementById(id: String, timeOut: Long): WebElement? {
        return null
    }

    override fun findElementsByCSSSelector(cssSelector: String, timeOut: Long): List<WebElement> {
        return emptyList()
    }

    override fun close() {
    }

    fun handleURLResolveError(ex: WebDriverException, driver: WebDriver) {
        println(ex.message)
    }

}