package com.eneco.trading.kafka.connect.nationalgrid.config


import java.time.Duration

import com.eneco.trading.kafka.connect.nationalgrid.domain.PullMap

import scala.collection.JavaConverters._

case class NGSourceSettings(ifrTopic: String, mipiRequests: Set[PullMap], mipiTopic: String, refreshRate: Duration,
maxBackOff: Duration, historicFetch: Int)

object NGSourceSettings {
  def apply(config: NGSourceConfig): NGSourceSettings = {
    val mipiRequestsRaw = config.getString(NGSourceConfig.MIPI_REQUESTS).split('|')

    val ifrTopic = config.getString(NGSourceConfig.IFR_TOPIC)
    val mipiTopic = config.getString(NGSourceConfig.MIPI_TOPIC)
    val mipiRequests  = mipiRequestsRaw
                        .map(mipi => mipi.split(";"))
                        .map(m => {
                          val hourMin = m(1).split(":")
                          PullMap(m(0), hourMin(0).toInt, hourMin(1).toInt, m(2).toInt)
                        }).toSet

    val refresh = Duration.parse(config.getString(NGSourceConfig.REFRESH_RATE))
    val backOff = Duration.parse(config.getString(NGSourceConfig.MAX_BACK_OFF))
    val historic = config.getInt(NGSourceConfig.HISTORIC_FETCH)
    NGSourceSettings(ifrTopic, mipiRequests, mipiTopic, refresh, backOff, historic)
  }
}
