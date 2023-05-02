package jobs.hh

import jobs.personal.EmailPasswordHH
import jobs.tools.waitUntilClickable
import org.openqa.selenium.By
import org.openqa.selenium.remote.RemoteWebDriver


fun doLoginEmailAndPasswordHH(driver: RemoteWebDriver, loginDetails: EmailPasswordHH) {
    println("LOGIN :: RUN")
    driver.get(loginDetails.loginUrl)

    println("Expand password")
    driver.findElement(By.cssSelector("button[data-qa='expand-login-by-password']")).click()

    println("username & password & submit form -> invoke capture")
    driver.findElement(By.cssSelector("input[data-qa='login-input-username']")).sendKeys(loginDetails.email)
    driver.findElement(By.cssSelector("input[data-qa='login-input-password']")).sendKeys(loginDetails.password)
    driver.findElement(By.cssSelector("button[data-qa='account-login-submit']")).submit()

    println("Вам нужно нажать \"Войти\", пройти Captcha и нажать \"Войти\" снова - на это у вас ${loginDetails.captchaWaitingSec} секунд")
    driver.waitUntilClickable(loginDetails.captchaWaitingSec, By.cssSelector("button[data-qa='search-button']"))
    println("Captcha пройдена, ожидаем загрузки \"Базовая страница поиска\". Возможно ожидание")
    println("LOGIN :: END")
}


/* DEPRECATED */


//private fun loginWithCode(driver: ChromeDriver, loginDetails : EmailPasswordHH) {
//    val signupForm = driver.findElement(By.cssSelector("form[data-qa='account-signup']"))
//    driver.findElement(By.name("login")).sendKeys(loginDetails.email)
//    val codeFromConsole = Scanner(System.`in`).nextLine() // await
//    signupForm.submit()
//
//    driver
////        .findElement(By.className("bloko-input-text"))
//        .findElement(By.name("otp-code-input"))
//        .sendKeys(codeFromConsole)
//
//    Thread.sleep(1000)
//
//    driver
//        .findElement(By.cssSelector("form[data-qa='otp-code-form']"))
//        .submit()
//}