package nl.maas.wicket.framework.viewer

import com.vaadin.open.App
import com.vaadin.open.Open
import com.vaadin.open.Options
import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.edge.EdgeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration
import java.util.*


object ScrapeViewer : Viewer() {
    private const val DEFAULT_TIME_OUT: Long = 1

    private lateinit var driver: WebDriver
    private var windowHandle: String = ""
    var app = App.CHROME
    val os =
        if (System.getProperty("os.name").lowercase(Locale.getDefault()).contains("win")) "win" else System.getProperty(
            "os.name"
        ).lowercase(Locale.getDefault())
    val arch = if (System.getProperty("os.arch").lowercase(Locale.getDefault()).contains("64")) "64" else "32"
    val pathDelimiter = if (os.equals("win")) "\\" else "/"

    override fun startBrowser(url: String) {
        startWebDriver(url)
    }

    fun navigateTo(url: String): Boolean {
        try {
            driver.get(url)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun findElementsByClass(className: String, timeOut: Long = DEFAULT_TIME_OUT): List<WebElement> {
        val wait = WebDriverWait(driver, Duration.ofSeconds(timeOut))
        return try {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className(className))).filterNotNull()
        } catch (e: Exception) {
            listOf<WebElement>()
        }
    }

    fun findElementsByTagName(tagName: String, timeOut: Long = DEFAULT_TIME_OUT): List<WebElement> {
        val wait = WebDriverWait(driver, Duration.ofSeconds(timeOut))
        return try {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName(tagName))).filterNotNull()
        } catch (e: Exception) {
            listOf<WebElement>()
        }
    }

    fun findElementById(id: String, timeOut: Long = DEFAULT_TIME_OUT): WebElement? {
        val wait = WebDriverWait(driver, Duration.ofSeconds(timeOut))
        return try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)))
        } catch (e: Exception) {
            null
        }
    }

    fun findElementsByCSSSelector(cssSelector: String, timeOut: Long = DEFAULT_TIME_OUT): List<WebElement> {
        val wait = WebDriverWait(driver, Duration.ofSeconds(timeOut))
        return try {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(cssSelector))).filterNotNull()
        } catch (e: Exception) {
            listOf<WebElement>()
        }
    }

    private fun openBrowser(url: String) {
        val options = Options()
        options.isNewInstance = true
        Open.open("--app=$url", app, options)
    }

    override fun close() {
        driver.switchTo().window(windowHandle).close()
    }

    private fun startWebDriver(url: String) {
        System.setProperty("webdriver.http.factory", "jdk-http-client");
        app =
            if (WebDriverManager.chromedriver().browserPath.isPresent) App.CHROME else if (WebDriverManager.edgedriver().browserPath.isPresent) App.EDGE else App.FIREFOX
        when (app) {
            App.CHROME -> setUpChromeDriver(url)
            App.EDGE -> setUpEdgeDriver(url)
            else -> setUpGeckoDriver(url)
        }
        windowHandle = driver.windowHandle
    }

    private fun setUpGeckoDriver(url: String) {
        WebDriverManager.firefoxdriver().clearDriverCache().setup()
        val options = FirefoxOptions()
        if (headless) {
            options.addArguments("--headless")
        }
        driver = FirefoxDriver(options)
        driver[url]
//        driver.manage().window().maximize()

    }

    private fun setUpEdgeDriver(url: String) {
        WebDriverManager.edgedriver().clearDriverCache().setup()
        val options = EdgeOptions()
        options.addArguments("--app=$url")
            .setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"))
            .addArguments("--remote-allow-origins=*")
            .addArguments("-inprivate")
        if (headless) {
            options.addArguments("--headless")
        }

        driver = EdgeDriver(options)
        driver[url]
        driver.manage().window().maximize()
    }

    private fun setUpChromeDriver(url: String) {
        try {
            WebDriverManager.chromedriver().clearDriverCache().setup()

            val options = ChromeOptions()
            options.addArguments("--app=$url")
            options.addArguments("-incognito")
            options.addArguments("disable-infobars")
            options.setExperimentalOption("useAutomationExtension", false)
            options.setExperimentalOption("excludeSwitches", arrayOf("enable-automation"))
            if (headless) {
                options.addArguments("--headless=new")
            }
            driver = ChromeDriver(options)
            driver[url]
            driver.manage().window().maximize()
        } catch (ex: WebDriverException) {
            println(ex.message)
            if (ex.message?.contains("ERR_NAME_NOT_RESOLVED") ?: false) {
                handleURLResolveError(ex, driver)
            } else {
                throw ex
            }
        }
    }

    fun handleURLResolveError(ex: WebDriverException, driver: WebDriver) {
        println(ex.message)
    }

}