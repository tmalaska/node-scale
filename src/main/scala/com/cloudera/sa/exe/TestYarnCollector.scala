package com.cloudera.sa.exe

import com.cloudera.sa.nodescale.service.collector.YarnCollector
import org.codehaus.jackson.map.ObjectMapper

object TestYarnCollector {
  def main(args:Array[String]): Unit = {

    println("Java Version:" + System.getProperty("java.version"))
    println("Java Home:" + System.getProperties().getProperty("java.home"))

    val yarnCollector = new YarnCollector
    yarnCollector.init(null)
    val clusterStatus = yarnCollector.collect()

    val mapper = new ObjectMapper
    println(mapper.writeValueAsString(clusterStatus))
  }
}
