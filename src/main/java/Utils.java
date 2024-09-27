/**
 * @file Utils.java
 * @author wasym.atieh
 * @copyright Copyright (C) 2023, 2024 CTTI
 * @brief Main library's interface for tests creation.
 *
 * This file is part of mat-selenium.
 */


package cat.gencat.mat;

import java.lang.Math;
import java.time.Duration;
import org.openqa.selenium.By;
import java.lang.reflect.Method;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.JavascriptExecutor;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * @class Utils
 * @brief Main public utilities for tests creation and interaction.
 */
public final class Utils {
  /**
   * @brief Set of logging levels.
   */
  public enum LogLevel {
    WARNING,
    PASS,
    SKIP,
    INFO,
  }

  /**
   * @brief Access to the app's URL provided via its configuration parameter.
   */
  public static void gotoApp() {
    BaseTest.getDriver().navigate().to(ConfigParameters.app_url);
  }

  /**
   * @brief Maximize the browser's window via the web driver.
   */
  public static void maximize() {
    BaseTest.getDriver().manage().window().maximize();
  }

  /**
   * @brief Obtain the web element from a selector.
   * @param driver WebDriver object to use.
   * @param selector By object.
   * @return WebElement object.
   */
  public static WebElement getElement(WebDriver driver, By selector) {
    return driver.findElement(selector);
  }

  /**
   * @brief Obtain the web element from a selector.
   * @param selector By object.
   * @return WebElement object.
   */
  public static WebElement getElement(By selector) {
    return getElement(BaseTest.getDriver(), selector);
  }

  /**
   * @brief Obtain the web element from a selector with a timeout.
   * @param driver WebDriver object to use.
   * @param selector By object.
   * @param timeout Maximum amount of time to wait before aborting.
   * @return WebElement object.
   */
  public static WebElement getElement(WebDriver driver, By selector, int timeout) {
    WebDriverWait wdw = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    return wdw.until(ExpectedConditions.presenceOfElementLocated(selector));
  }

  /**
   * @brief Obtain the web element from a selector with a timeout.
   * @param selector By object.
   * @param timeout Maximum amount of time to wait before aborting.
   * @return WebElement object.
   */
  public static WebElement getElement(By selector, int timeout) {
    return getElement(BaseTest.getDriver(), selector, timeout);
  }

  /**
   * @brief Obtain the entire HTML page as string (without formatting).
   * @param driver WebDriver object to use.
   * @return Entire HTML as String.
   */
  public static String getHTML(WebDriver driver) {
    return driver.getPageSource();
  }

  /**
   * @brief Obtain the entire HTML page as string (without formatting).
   * @return Entire HTML as String.
   */
  public static String getHTML() {
    return getHTML(BaseTest.getDriver());
  }

  /**
   * @brief Switch context to a different frame specified by index.
   * @param idx Index which identifies the frame.
   * @return WebDriver Driver focused on the selected frame.
   */
  public static WebDriver switchToFrame(int idx) {
    return BaseTest.getDriver().switchTo().frame(idx);
  }

  /**
   * @brief Sleeps the JVM thread for specified amount of seconds.
   * @param time Amount of seconds.
   */
  public static void sleep(int time) {
    try { Thread.sleep(time * 1000); }
    catch (InterruptedException e) {
      String err = "[ERROR] Utils.sleep :: timeout interrupted";
      System.err.println(err);
      BaseTest.log.error(err);
    }
  }

  /**
   * @brief Scroll the window with a timeout.
   * @param percent_x Percent value to scroll in the X-axis.
   * @param percent_y Percent value to scroll in the Y-axis.
   * @param timeout Maximum amount of time to wait before aborting.
   */
  public static void scroll(int percent_x, int percent_y, int timeout) {
    timeout = Math.abs(timeout);
    JavascriptExecutor js = (JavascriptExecutor) BaseTest.getDriver();
    float factor = (long) js.executeScript("return document.body.scrollHeight;") / 100;
    js.executeScript(String.format("window.scrollTo(%d, %d);", (long) (percent_x * factor), (long) (percent_y * factor)));
    try { Thread.sleep(timeout); }
    catch (InterruptedException e) {
      String err = "[ERROR] Utils.scroll :: timeout interrupted";
      System.err.println(err);
      BaseTest.log.error(err);
    }
  }

  /**
   * @brief Scroll the window to the bottom (Y-axis) with a timeout.
   * @param timeout Maximum amount of time to wait before aborting.
   */
  public static void scrollToBottom(int timeout) {
    scroll(0, 100, timeout);
  }

  /**
   * @brief Scroll the window to the top (Y-axis) with a timeout.
   * @param timeout Maximum amount of time to wait before aborting.
   */
  public static void scrollToTop(int timeout) {
    scroll(0, 0, timeout);
  }

  /**
   * @brief Define new step within a test case.
   * @param name Name of the test step.
   */
  public static void step(String name) {
    ExtentManager.addNode(name);
  }

  /**
   * @brief Add an anotation within the current test case context.
   * @param level Logging level to use.
   * @param msg Message to write inside the annotation.
   */
  public static void anotate(LogLevel level, String msg) {
    switch (level) {
    case WARNING:
      ExtentManager.getNode().warning(msg);
      break;
    case PASS:
      ExtentManager.getNode().pass(msg);
      break;
    case SKIP:
      ExtentManager.getNode().skip(msg);
      break;
    case INFO:
      ExtentManager.getNode().info(msg);
      break;
    default:
      String warn = "[WARNING] `level` not valid; skipping anotation";
      BaseTest.log.warn(warn);
      System.out.println(warn);
    }
  }

  /**
   * @brief Take a new screenshot of the current browser's viewport and attach it to the current test case context.
   * @param caption Description to add to the screenshot.
   */
  public static void screenshot(String caption) {
    ExtentManager.getNode().addScreenCaptureFromBase64String(BaseTest.getTS().getScreenshotAs(OutputType.BASE64), caption);
  }

  /**
   * @brief Handle the end of the test case successfully.
   * @param browser Name of the browser the test case has run on.
   * @param method Method which implements the test case.
   */
  public static void endTestAsOK(String browser, Method method) {
    String msg = String.format("[INFO] %s :: {%s} :: PASSED", method.getName(), browser);
    BaseTest.log.info(msg);
    System.out.println(msg);
  }

  /**
   * @brief Handle the end of the test case with error.
   * @param browser Name of the browser the test case has run on.
   * @param method Method which implements the test case.
   * @param e Throwable Exception which represents the error.
   */
  public static void endTestAsKO(String browser, Method method, Throwable e) throws Throwable {
    ExtentManager.getNode().fail(e);
    screenshot(e.getClass().getSimpleName());
    String err = String.format("[ERROR] %s :: {%s} :: FAILED", method.getName(), browser);
    BaseTest.log.error(err);
    System.err.println(err);
    throw e;
  }
}
