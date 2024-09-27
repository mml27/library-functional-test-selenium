/**
 * @file ExecutionListener.java
 * @author juan.a.mora, wasym.atieh
 * @copyright Copyright (C) 2023, 2024 CTTI
 * @brief Tests execution handler and InfluxDB reporter.
 *
 * This file is part of mat-selenium.
 */


package cat.gencat.mat;

import java.time.Instant;
import org.testng.ITestResult;
import org.testng.ITestContext;
import org.testng.ITestListener;
import com.influxdb.client.write.Point;
import com.influxdb.client.domain.WritePrecision;

/**
 * @class ExecutionListener
 * @brief Behavior definition for each test case result.
 */
public final class ExecutionListener implements ITestListener {
  /**
   * @brief Handle end of test case execution.
   * @param ctx ITestContext object of the test case.
   */
  public void onFinish(ITestContext ctx) {
    sendTestClassStatus(ctx);
  }

  /**
   * @brief Handle success of test case execution.
   * @param r Result of the specific test execution.
   */
  public void onTestSuccess(ITestResult r) {
    sendTestMethodStatus(r, r.getTestContext(), "PASS");
  }

  /**
   * @brief Handle omission of test case execution.
   * @param r Result of the specific test execution.
   */
  public void onTestSkipped(ITestResult r) {
    sendTestMethodStatus(r, r.getTestContext(), "SKIPPED");
  }

  /**
   * @brief Handle failure of test case execution.
   * @param r Result of the specific test execution.
   */
  public void onTestFailure(ITestResult r) {
    sendTestMethodStatus(r, r.getTestContext(), "FAIL");
    String e_name = r.getThrowable().getClass().getSimpleName();
    String e_msg = r.getThrowable().getMessage().split("\n")[0];
    Throwable e = new Throwable(e_msg == null ? e_name : String.format("%s :: %s", e_name, e_msg));
    e.setStackTrace(new StackTraceElement[0]);
    r.setThrowable(e);
  }

  /**
   * @brief Send to InfluxDB execution details of the test case body (method).
   * @param r Result of the specific test execution.
   * @param ctx ITestContext object of the test case.
   * @param status Final result of the test case.
   */
  private static void sendTestMethodStatus(ITestResult r, ITestContext ctx, String status) {
    if (!ResultSender.setup()) return;
    Point point = Point.measurement("testmethod")
      .time(Long.valueOf(Instant.now().toEpochMilli()), WritePrecision.MS)
      .addTag("testclass", r.getTestClass().getName())
      .addTag("name", r.getName())
      .addTag("description", r.getMethod().getDescription())
      .addTag("result", status)
      .addTag("environment", ConfigParameters.environment)
      .addTag("browser", ctx.getCurrentXmlTest().getParameter("browser"))
      .addTag("application", ConfigParameters.app)
      .addTag("maintainer", ConfigParameters.maintainer)
      .addTag("ambit", ConfigParameters.ambit)
      .addTag("buildnumber", ConfigParameters.build_id)
      .addTag("jobname", ConfigParameters.job_name)
      .addTag("jira_pk", ConfigParameters.jira_pk)
      .addTag("jira_issue", ConfigParameters.jira_issue)
      .addTag("suite", ctx.getSuite().getName())
      .addTag("error", r.getThrowable() == null ? "" : r.getThrowable().getMessage())
      .addField("duration", r.getEndMillis() - r.getStartMillis());
    try { ResultSender.send(point); }
    catch (Exception e) {
      String err = "[ERROR] Could not write data point to InfluxDB";
      System.err.println(err);
      BaseTest.log.error(err);
    }
  }

  /**
   * @brief Send to InfluxDB execution details of the test case (class).
   * @param ctx ITestContext object of the test case.
   */
  private static void sendTestClassStatus(ITestContext ctx) {
    if (!ResultSender.setup()) return;
    Point point = Point.measurement("testclass")
      .addTag("name", ctx.getAllTestMethods()[0].getTestClass().getName())
      .addField("duration", (ctx.getEndDate().getTime() - ctx.getStartDate().getTime()))
      .time(Long.valueOf(Instant.now().toEpochMilli()), WritePrecision.MS);
    try { ResultSender.send(point); }
    catch (Exception e) {
      String err = "[ERROR] Could not write data point to InfluxDB";
      System.err.println(err);
      BaseTest.log.error(err);
    }
  }
}
