package com.cloudera.sa.server

import com.cloudera.sa.nodescale.model.{ClusterDefinition, ObjectDefinition}
import com.cloudera.sa.nodescale.service.RestService
import com.sun.jersey.spi.container.servlet.ServletContainer
import org.mortbay.jetty.Server
import org.mortbay.jetty.servlet.{Context, ServletHolder}

object NodeScale {
  def main(args:Array[String]) {
    val server = new Server(4243)

    val sh = new ServletHolder(classOf[ServletContainer])
    sh.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig")
    sh.setInitParameter("com.sun.jersey.config.property.packages", "com.cloudera.sa.nodescale.service")
    sh.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true")

    val context = new Context(server, "/", Context.SESSIONS)
    context.addServlet(sh, "/*")

    createBasicMockCluster

    println("starting server")
    server.start()
    println("started server")
    server.join()
  }

  private def createBasicMockCluster(): Unit = {
    val restServer = new RestService

    val clusterDefinition = new ClusterDefinition
    clusterDefinition.setName("mock")
    clusterDefinition.setCollectInterval(5000l)

    val collectionObjDefinition = new ObjectDefinition
    collectionObjDefinition.setType("com.cloudera.sa.nodescale.service.collector.MockCollector")
    clusterDefinition.setCollector(collectionObjDefinition)

    val rule3ObjDefinition = new ObjectDefinition
    rule3ObjDefinition.setType("com.cloudera.sa.nodescale.service.rule.UsageRule")
    rule3ObjDefinition.addMetadata("name", "usageRule")
    rule3ObjDefinition.addMetadata("waitTimeToGrow", "60000")
    rule3ObjDefinition.addMetadata("waitTimeToShrink", "60000")
    rule3ObjDefinition.addMetadata("minQueueSizeToWarrantGrowth", "2")
    rule3ObjDefinition.addMetadata("minIdealSizeToWarrantShrink", "2")
    rule3ObjDefinition.addMetadata("baseInstanceUsableVCores", "12")
    rule3ObjDefinition.addMetadata("baseInstanceUsableGbMemory", "128")
    clusterDefinition.addRule(rule3ObjDefinition)

    val rule1ObjDefinition = new ObjectDefinition
    rule1ObjDefinition.setType("com.cloudera.sa.nodescale.service.rule.MinMaxTimeRangeRule")
    rule1ObjDefinition.addMetadata("name", "minMaxRule1")
    rule1ObjDefinition.addMetadata("minNumberOfNodes", "3")
    rule1ObjDefinition.addMetadata("maxNumberOfNodes", "10")
    rule1ObjDefinition.addMetadata("daysOfWeekToApplyRule", "0,1,2,3,4,5,6")
    rule1ObjDefinition.addMetadata("startTimeOfDay", "0:00")
    rule1ObjDefinition.addMetadata("endTimeOfDay", "12:00")
    clusterDefinition.addRule(rule1ObjDefinition)

    val rule2ObjDefinition = new ObjectDefinition
    rule2ObjDefinition.setType("com.cloudera.sa.nodescale.service.rule.MinMaxTimeRangeRule")
    rule2ObjDefinition.addMetadata("name", "minMaxRule2")
    rule2ObjDefinition.addMetadata("minNumberOfNodes", "5")
    rule2ObjDefinition.addMetadata("maxNumberOfNodes", "10")
    rule2ObjDefinition.addMetadata("daysOfWeekToApplyRule", "0,1,2,3,4,5,6")
    rule2ObjDefinition.addMetadata("startTimeOfDay", "12:01")
    rule2ObjDefinition.addMetadata("endTimeOfDay", "24:00")
    clusterDefinition.addRule(rule2ObjDefinition)

    restServer.createCluster(clusterDefinition)
  }
}
