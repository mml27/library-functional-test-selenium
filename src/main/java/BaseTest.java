/**
 * @file BaseTest.java
 * @author juan.a.mora, wasym.atieh
 * @copyright Copyright (C) 2023, 2024 CTTI
 * @brief Main library's entrypoint.
 *
 * This file is part of mat-selenium.
 */


package cat.gencat.mat;

import java.net.URL;
import java.io.IOException;
import java.lang.reflect.Method;
import org.openqa.selenium.WebDriver;
import java.net.MalformedURLException;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Parameters;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.TakesScreenshot;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.ExtentReports;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * @class BaseTest
 * @brief Generic test case which abstracts away init and shutdown phases.
 */
public class BaseTest {
  protected static Logger log = LogManager.getLogger(BaseTest.class);
  private static ThreadLocal<TakesScreenshot> ts = new ThreadLocal<>();
  private static ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<>();

  /**
   * @brief Initialize the test suite.
   */
  @BeforeSuite
  public void suiteInit() {
    logo();
    log.info("Testing suite execution started");
    ConfigParameters.setup();
    ExtentManager.setup();
    System.out.println("[INFO] -------------------------------------------------------");
  }

  /**
   * @brief Setup process for a specific test case.
   * @param browser Name of the browser to run the test case on.
   * @param method Method that implements the test case.
   */
  @BeforeMethod
  @Parameters(value={"browser"})
  public void testInit(String browser, Method method) {
    setDriver(createRWD(browser));
    setTS(getDriver());
    log.info(String.format("Browser driver created: %s", browser));
    ExtentManager.addTest(method.getName(), browser);
  }

  /**
   * @brief Shutdown the web driver.
   */
  @AfterMethod
  public void testQuit() {
    getDriver().quit();
    log.info("Quitted driver successfully");
  }

  /**
   * @brief Clean up all resources related to the test case.
   */
  @AfterClass
  public void testShutdown() {
    deleteTS();
    deleteDriver();
    ExtentManager.flush();
  }

  /**
   * @brief Shutdown the test suite.
   */
  @AfterSuite
  public void suiteShutdown() {
    log.info("Testing suite execution ended");
    System.out.println("[INFO] -------------------------------------------------------");
    System.out.println("[INFO] Report written to: `target/report/index.html`");
  }

  /**
   * @brief Print the library's logo to stdout.
   */
  private static void logo() {
    try {
      System.out.println(new String(ClassLoader
                                    .getSystemClassLoader()
                                    .getResourceAsStream("logo.txt")
                                    .readAllBytes()));
    }
    catch (IOException e) {
      String err = "[ERROR] Could not load `logo.txt` resource";
      System.err.println(err);
      log.error(err);
      System.exit(1);
    }
  }

  /**
   * @brief Instantiate the web driver for a specific test case.
   * @param browser Name of the browser to instantiate the web driver for.
   * @return RemoteWebDriver object.
   */
  private static RemoteWebDriver createRWD(String browser) {
    RemoteWebDriver rwd = null;
    try {
      rwd =  new RemoteWebDriver(new URL(ConfigParameters.selenium_url),
                                 BrowserOptions.getCapabilities(browser),
                                 false);
    }
    catch (Exception e) {
      String err = String.format("[ERROR] Could not create browser session in Selenium Grid (%s)", ConfigParameters.selenium_url);
      System.err.println(err);
      log.error(err);
      System.exit(1);
    }
    return rwd;
  }

  /**
   * @brief Getter for the web driver.
   * @return RemoteWebDriver object.
   */
  protected static synchronized RemoteWebDriver getDriver() {
    return driver.get();
  }

  /**
   * @brief Setter for the web driver.
   * @param i RemoteWebDriver object.
   */
  protected static synchronized void setDriver(RemoteWebDriver i) {
    driver.set(i);
  }

  /**
   * @brief Deleter for the web driver.
   */
  protected static synchronized void deleteDriver() {
    driver.remove();
  }

  /**
   * @brief Getter for the screenshot driver.
   * @return TakesScreenshot object.
   */
  protected static synchronized TakesScreenshot getTS() {
    return ts.get();
  }

  /**
   * @brief Setter for the screenshot driver.
   * @param i TakesScreenshot object.
   */
  protected static synchronized void setTS(TakesScreenshot i) {
    ts.set(i);
  }

  /**
   * @brief Deleter for the screenshot driver.
   */
  protected static synchronized void deleteTS() {
    ts.remove();
  }
}
