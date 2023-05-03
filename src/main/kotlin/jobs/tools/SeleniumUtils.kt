package jobs.tools

import jobs.personal.BrowserType
import org.openqa.selenium.*
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

val WebElement.nodes: List<WebElement> get() = this.findElements(By.xpath("./child::*")) ?: listOf()

val WebElement.asStr: String
    get() {
        val rsl = StringBuilder(this.asStrRoot)
        this.nodes.forEach { rsl.append(System.lineSeparator()).append("   ${it.asStrRoot}") }
        return rsl.toString()
    }

val WebElement.asStrRoot
    get() = "<${tagName} class=${getAttribute("class")} data-qa=${getAttribute("data-qa")} text=${text} " +
            "name=${getAttribute("name")} href=${getAttribute("href")} ...>"

fun WebElement.logIt() = println(this.asStr)
fun WebElement.logItRoot() = println(this.asStrRoot)


val WebDriverException?.toStringClassAndElem: String
    get() =
        try {
            if (this == null) "null"
            else if (this is TimeoutException) this.toString().split("\n")[0].substring(38) // обрезаем Класс ошибки
            else this.javaClass.simpleName + this.toString().apply { this.substring(this.indexOf("->")) }
        } catch (exception: Exception) {
            println(exception.javaClass.simpleName)
            exception.toString()
        }


/* Driver extensions */


fun openNewBrowserWindow(browser: BrowserType): RemoteWebDriver = when (browser) {
    BrowserType.GOOGLE_CHROME -> ChromeDriver()
    BrowserType.MOZILLA_FIREFOX -> FirefoxDriver()
    BrowserType.MICROSOFT_EDGE -> EdgeDriver()
    BrowserType.SAFARI -> SafariDriver()
}.also { it.manage().window().maximize() }

fun RemoteWebDriver.openNewTab(): Unit = this.run { this.switchTo().newWindow(WindowType.TAB) }

fun RemoteWebDriver.waitUntilClickable(seconds: Long, selector: By) {
    WebDriverWait(this, Duration.ofSeconds(seconds))
        .until(ExpectedConditions.elementToBeClickable(selector))
}

fun RemoteWebDriver.tryWaitUntilClickableThenClick(seconds: Long, selector: By) {
    try {
        this.waitUntilClickable(seconds, selector)
        this.findElement(selector).click()
    } catch (exception: WebDriverException) {
        System.err.println(exception.toStringClassAndElem)
    }
}

