package com.cloudera.sa.server

import java.util

import com.cloudera.sa.nodescale.model.InstanceStatus
import com.cloudera.sa.nodescale.service.rule._
import org.codehaus.jackson.map.ObjectMapper

object JsonFun {
  def main(args:Array[String]): Unit = {

    val baseInstance = new InstanceStatus("base",
      "42",
      8,
      128,
      System.currentTimeMillis(),
      60000l * 60l,
      InstanceStatus.RUNNING_STATUS,
      false)
    val usageRule = new UsageRule("usageRule", 1000, 1000, 1000, 1000, baseInstance)

    /*
    ruleName: String,
                             mixNumberOfNodes: Integer,
                             maxNumberOfNodes: Integer,
                             daysOfWeekToApplyRule: util.Set[Integer],
                             startTimeOfDay: String,
                             endTimeOfDay: String
     */
    val set = new util.HashSet[Integer]
    set.add(0)
    set.add(2)
    set.add(3)

    val minMaxTimeRangeRule = new MinMaxTimeRangeRule("minMax",
      10, 100, set, "5:00", "15:25")

    val mapper = new ObjectMapper;

    println(">> Rules to JSON")
    val jsonStringUsage = mapper.writeValueAsString(usageRule)
    println(jsonStringUsage)
    val jsonStringMinMax = mapper.writeValueAsString(minMaxTimeRangeRule)
    println(jsonStringMinMax)

    //Back to objects
    val jsonUsage = mapper.readValue(jsonStringUsage, classOf[UsageRule])
    println(">> Usage Object")
    println(jsonUsage)

    //Collection
    val ruleList = new util.ArrayList[SizingRule]
    ruleList.add(usageRule)
    ruleList.add(minMaxTimeRangeRule)
    val ruleCollection = new RuleCollection()
    ruleCollection.setRules(ruleList)

    println(">> Collection")
    println(mapper.writeValueAsString(ruleCollection))

  }
}
