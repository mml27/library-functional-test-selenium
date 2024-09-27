/**
 * @file BrowserOptions.java
 * @author juan.a.mora, wasym.atieh
 * @copyright Copyright (C) 2023, 2024 CTTI
 * @brief Browser CLI options and capabilities management.
 *
 * This file is part of mat-selenium.
 */


package cat.gencat.mat;

import java.io.IOException;
import java.util.Properties;
import java.io.FileInputStream;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

/**
 * @class BrowserOptions
 * @brief Obtain browser CLI options and capabilities.
 */
public final class BrowserOptions {
  /**
   * @brief Obtain capabilities of a specific browser.
   * @param browser Name of the browser to get the capabilities off of.
   * @return Capabilities object.
   */
  protected static Capabilities getCapabilities(String browser) {
    if (browser.equals("firefox")) return getFirefoxOptions();
    else if (browser.equals("chrome")) return getChromeOptions();
    else if (browser.equals("edge")) return getEdgeOptions();
    else {
      String err = String.format("[ERROR] BrowserOptions.getCapabilities :: `browser` is not valid (%s)", browser);
      System.err.println(err);
      BaseTest.log.error(err);
      System.out.println("[WARNING] BrowserOptions.getCapabilities :: defaulting to Chrome capabilities");
      return getChromeOptions();
    }
  }

  /**
   * @brief Obtain Chrome-specific CLI options and capabilities.
   * @return ChromeOptions object.
   */
  private static ChromeOptions getChromeOptions() {
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--start-maximized");
    options.addArguments("--ignore-certificate-errors");
    options.addArguments("--disable-popup-blocking");
    if (ConfigParameters.headless) options.addArguments("--headless=new");
    return options;
  }

  /**
   * @brief Obtain Edge-specific CLI options and capabilities.
   * @return EdgeOptions object.
   */
  private static EdgeOptions getEdgeOptions() {
    EdgeOptions options = new EdgeOptions();
    options.addArguments("--start-maximized");
    options.addArguments("--ignore-certificate-errors");
    options.addArguments("--disable-popup-blocking");
    if (ConfigParameters.headless) options.addArguments("--headless");
    return options;
  }

  /**
   * @brief Obtain Firefox-specific CLI options and capabilities.
   * @return FirefoxOptions object.
   */
  private static FirefoxOptions getFirefoxOptions() {
    FirefoxProfile profile = new FirefoxProfile();
    profile.setAcceptUntrustedCertificates(true);
    profile.setAssumeUntrustedCertificateIssuer(false);
    profile.setPreference("network.proxy.type", 0);
    FirefoxOptions options = new FirefoxOptions();
    options.setProfile(profile);
    if (ConfigParameters.headless) options.addArguments("-headless");
    if (ConfigParameters.selenium_firefox_driver != null) {
      options.setBinary(ConfigParameters.selenium_firefox_driver);
    }
    return options;
  }
}
