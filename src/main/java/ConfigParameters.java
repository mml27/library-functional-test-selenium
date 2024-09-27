/**
 * @file ConfigParameters.java
 * @author wasym.atieh
 * @copyright Copyright (C) 2023, 2024 CTTI
 * @brief Library configuration options management.
 *
 * This file is part of mat-selenium.
 */


package cat.gencat.mat;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.io.FileInputStream;

/**
 * @class ConfigParameters
 * @brief Parse and hold all config. parameters provided by the user.

 Order of precedence is as follows:
 System.getenv --> System.getProperty --> properties.getProperty
*/
public final class ConfigParameters {
  private static Properties properties            = new Properties();
  protected static boolean headless               = true;
  protected static String app                     = System.getenv("MAT_TF_APP");
  protected static String app_url                 = System.getenv("MAT_TF_APP_URL");
  protected static String maintainer              = System.getenv("MAT_TF_MAINTAINER");

  protected static String ambit                   = System.getenv("MAT_TF_AMBIT");

  protected static String selenium_url            = System.getenv("MAT_TF_SELENIUM_URL");
  protected static String influxdb_url            = System.getenv("MAT_TF_INFLUXDB_URL");
  protected static String influxdb_token          = System.getenv("MAT_TF_INFLUXDB_TOKEN");
  protected static String influxdb_bucket         = System.getenv("MAT_TF_INFLUXDB_BUCKET");
  protected static String influxdb_company        = System.getenv("MAT_TF_INFLUXDB_COMPANY");
  protected static String selenium_firefox_driver = System.getenv("MAT_TF_SELENIUM_FIREFOX_DRIVER");
  protected static String environment             = System.getProperty("environment");
  protected static String build_id                = System.getProperty("build_id");
  protected static String job_name                = System.getProperty("job_name");
  protected static String jira_pk                 = System.getProperty("jira_pk");
  protected static String jira_issue              = System.getProperty("jira_issue");

  /**
   * @brief Check whether specific path within the filesystem is valid or not.
   * @param path Path to check.
   * @return True if valid, false otherwise.
   */
  private static boolean isValidPath(String path) {
    File fd = new File(path);
    try {
      fd.getCanonicalPath();
      return true;
    }
    catch (IOException e) {
      System.err.printf("[ERROR] ConfigParameters.isValidPath :: `%s` is not valid%n", path);
      return false;
    }
  }

  /**
   * @brief Load and parse the properties configuration file.
   * @param filename Name of the configuration file.
   */
  private static void loadPropertiesFile(String filename) {
    try (FileInputStream fd = new FileInputStream(filename)) {
      properties.load(fd);
    } catch (IOException e) {
      String err = String.format("[ERROR] Could not load `./%s` file", filename);
      System.err.println(err);
      BaseTest.log.error(err);
      System.exit(1);
    }
  }

  /**
   * @brief Try to parse a specific parameter if not available via env. var.
   * @param param Initial value parsed via env. var.
   * @param name Name of the parameter to be further parsed.
   * @return Final parsed value.
   */
  private static String setParam(String param, String name) {
    if (param == null) { param = System.getProperty(name); }
    if (param == null) { param = properties.getProperty(name); }
    if (param == null) {
      String err = String.format("[ERROR] `%s` not set and required", name);
      System.err.println(err);
      BaseTest.log.error(err);
      System.exit(1);
    }
    return param;
  }

  /**
   * @brief Setup and parse all config. parameters.
   */
  protected static void setup() {
    // Disable headless mode (optional)
    if (System.getProperty("headless") != null &&
        System.getProperty("headless").equalsIgnoreCase("false")) {
      System.out.println("[INFO] Headless mode disabled");
      headless = false;
    }

    loadPropertiesFile("config.properties");
    app          = setParam(app, "app");
    app_url      = setParam(app_url, "app_url");
    maintainer   = setParam(maintainer, "maintainer");
    ambit        = setParam(ambit, "ambit");
    selenium_url = setParam(selenium_url, "selenium_url");

    // InfluxDB (optional)
    boolean is_influxdb_enabled = true;
    if (influxdb_url == null) {
      influxdb_url = System.getProperty("influxdb_url");
    }
    if (influxdb_url == null) {
      System.out.println("[WARNING] `influxdb_url` not set; InfluxDB data loading disabled");
      is_influxdb_enabled = false;
    }
    if (influxdb_token == null) {
      influxdb_token = System.getProperty("influxdb_token");
    }
    if (influxdb_token == null) {
      System.out.println("[WARNING] `influxdb_token` not set; InfluxDB data loading disabled");
      is_influxdb_enabled = false;
    }
    if (influxdb_bucket == null) {
      influxdb_bucket = System.getProperty("influxdb_bucket");
    }
    if (influxdb_bucket == null) {
      System.out.println("[WARNING] `influxdb_bucket` not set; InfluxDB data loading disabled");
      is_influxdb_enabled = false;
    }
    if (influxdb_company == null) {
      influxdb_company = System.getProperty("influxdb_company");
    }
    if (influxdb_company == null) {
      System.out.println("[WARNING] `influxdb_company` not set; InfluxDB data loading disabled");
      is_influxdb_enabled = false;
    }

    // Selenium Firefox Driver (optional)
    if (selenium_firefox_driver == null) {
      selenium_firefox_driver = System.getProperty("selenium_firefox_driver");
    }
    if (selenium_firefox_driver == null) {
      System.out.println("[INFO] `selenium_firefox_driver` not set; ignoring setting binary");
    }
    else if (!isValidPath(selenium_firefox_driver)) {
      System.out.println("[WARNING] `selenium_firefox_driver` is not a valid path; ignoring setting binary");
      selenium_firefox_driver = null;
    }

    // Environment, Build ID, Job Name (optional)
    if (is_influxdb_enabled &&
        (environment == null ||
         build_id == null    ||
         job_name == null    ||
         jira_pk == null     ||
         jira_issue == null)) {
      String err = "[ERROR] `environment`, `build_id`, `job_name`, `jira_pk` and `jira_issue` are required when InfluxDB data loading is enabled";
      System.err.println(err);
      BaseTest.log.error(err);
      System.exit(1);
    }
  }
}
