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


// org.openqa.selenium.NoSuchElementException: no such element: Unable to locate element: {"method":"tag name","selector":"textarea"}
// org.openqa.selenium.NoSuchElementException: no such element: Unable to locate element: {"method":"tag name","selector":"textarea"}
// java.util.NoSuchElementException: Sequence contains no element matching the predicate.
val WebDriverException?.toStringCompact: String
    get() = if (this == null) "null" else this.toString().split("\n")[0]


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
        System.err.println(exception.toStringCompact)
    }
}

