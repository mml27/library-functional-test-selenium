/**
 * @file ExtentManager.java
 * @author juan.a.mora, wasym.atieh
 * @copyright Copyright (C) 2023, 2024 CTTI
 * @brief Tests results HTML reporter.
 *
 * This file is part of mat-selenium.
 */


package cat.gencat.mat;

import java.io.IOException;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.ViewName;

/**
 * @class ExtentManager
 * @brief Create and manage an interactive HTML report of all tests run.
 */
public final class ExtentManager {
  private static ExtentReports report;
  private static ExtentSparkReporter reporter;
  private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
  private static ThreadLocal<ExtentTest> node = new ThreadLocal<>();

  /**
   * @brief Load the ExtentReport's Spark reporter config in JSON format.
   * @param filename Name of the configuration file.
   */
  private static void loadReporterConfigFile(String filename) {
    try {
      reporter.loadJSONConfig(new String(ClassLoader
                                         .getSystemClassLoader()
                                         .getResourceAsStream(filename)
                                         .readAllBytes()));
    }
    catch (IOException e) {
      String err = String.format("[ERROR] Could not load `./%s` file", filename);
      System.err.println(err);
      BaseTest.log.error(err);
      System.exit(1);
    }
  }

  /**
   * @brief Initialize the report.
   */
  protected static void setup() {
    if (report != null) return;
    report = new ExtentReports();
    reporter = new ExtentSparkReporter("target/report/index.html")
      .viewConfigurer()
      .viewOrder()
      .as(new ViewName[] {
          ViewName.DASHBOARD,
          ViewName.TEST,
          ViewName.AUTHOR,
          ViewName.DEVICE,
          ViewName.CATEGORY,
          ViewName.EXCEPTION,
          ViewName.LOG
        }).apply();
    loadReporterConfigFile("report-config.json");
    report.attachReporter(reporter);
  }

  /**
   * @brief Getter for the ExtentTest's test case.
   * @return ExtentTest's test object.
   */
  protected static synchronized ExtentTest getTest() {
    return test.get();
  }

  /**
   * @brief Setter for the ExtentTest's test case.
   * @param i ExtentTest's test object.
   */
  protected static synchronized void setTest(ExtentTest i) {
    test.set(i);
  }

  /**
   * @brief Deleter for the ExtentTest's test case.
   */
  protected static synchronized void deleteTest() {
    test.remove();
  }

  /**
   * @brief Create a new test case entry in the report.
   * @param name Name of the test case.
   * @param browser Name of the browser the test case has run on.
   */
  protected static void addTest(String name, String browser) {
    String category = ConfigParameters.environment != null
      ? String.format("%s-%s", ConfigParameters.app, ConfigParameters.environment)
      : ConfigParameters.app;
    setTest(report.createTest(name)
            .assignCategory(category)
            .assignAuthor(ConfigParameters.maintainer)
            .assignDevice(browser));
  }

  /**
   * @brief Getter for the ExtentTest's internal test step.
   * @return ExtentTest's node object.
   */
  protected static synchronized ExtentTest getNode() {
    return node.get();
  }

  /**
   * @brief Setter for the ExtentTest's internal test step.
   * @param i ExtentTest's node object.
   */
  protected static synchronized void setNode(ExtentTest i) {
    node.set(i);
  }

  /**
   * @brief Deleter for the ExtentTest's internal test step.
   */
  protected static synchronized void deleteNode() {
    node.remove();
  }

  /**
   * @brief Create a new test step entry inside the current test case entry.
   * @param name Name of the test step.
   */
  protected static void addNode(String name) {
    setNode(getTest().createNode(name));
  }

  /**
   * @brief Clean up all resources related to the test case entry.
   */
  protected static void flush() {
    if (report == null) return;
    report.flush();
    deleteTest();
    deleteNode();
  }
}
