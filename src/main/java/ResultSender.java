/**
 * @file ResultSender.java
 * @author juan.a.mora, wasym.atieh
 * @copyright Copyright (C) 2023, 2024 CTTI
 * @brief InfluxDB connection handling.
 *
 * This file is part of mat-selenium.
 */


package cat.gencat.mat;

import com.influxdb.client.write.Point;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.InfluxDBClientFactory;

/**
 * @class ResultSender
 * @brief Connection establishment with InfluxDB via its client API.
 */
public final class ResultSender {
  private static WriteApiBlocking influxdb_api;
  protected static InfluxDBClient influxdb_client;

  /**
   * @brief Instantiate the InfluxDB client and its write API.
   * @return True if instantiated correctly, false otherwise.
   */
  protected static boolean setup() {
    if (ConfigParameters.influxdb_url     == null ||
        ConfigParameters.influxdb_token   == null ||
        ConfigParameters.influxdb_company == null ||
        ConfigParameters.influxdb_bucket  == null) return false;
    influxdb_client = InfluxDBClientFactory.create(ConfigParameters.influxdb_url,
                                                   ConfigParameters.influxdb_token.toCharArray(),
                                                   ConfigParameters.influxdb_company,
                                                   ConfigParameters.influxdb_bucket);
    influxdb_api = influxdb_client.getWriteApiBlocking();
    return true;
  }

  /**
   * @brief Write a data entry to InfluxDB.
   * @param p Data entry to write as a Point object.
   */
  protected static void send(final Point p) {
    influxdb_api.writePoint(p);
  }
}
