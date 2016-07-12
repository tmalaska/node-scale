package com.cloudera.sa.nodescale.service.rule.utils;

import com.cloudera.sa.nodescale.model.ClusterStatus;
import com.cloudera.sa.nodescale.model.InstanceStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class CommonRuleLogic {
  public static String IdentifyInstanceToKill(ClusterStatus clusterStatus,
                                              Set<String> unresolveKillList,
                                              Set<String> toKillList,
                                              Integer minumumNumberOfMinutesAfterTheHour) {
    for (InstanceStatus instanceStatus : clusterStatus.getInstanceStatusList()) {
      if (!instanceStatus.getIsFixedNode() &&
              !unresolveKillList.contains(instanceStatus.getInstanceId()) &&
              !toKillList.contains(instanceStatus.getInstanceId()) &&
              instanceStatus.getStartTime()/60000 % 60 > minumumNumberOfMinutesAfterTheHour)  {
        return instanceStatus.getInstanceId();
      }
    }
    //we couldn't find any nodes to kill
    return null;
  }

  public static Integer getMetadataInt(String key, HashMap<String, String> metadataMap, Logger log) {
    try {
      Integer value = Integer.parseInt(metadataMap.get(key));
      log.info("Metadata '" + key + "' of value " + value );
      return value;
    } catch (Exception e) {
      throw new RuntimeException("Something wrong getting key '" + key + "' from metadata");
    }
  }

  public static String getMetadataString(String key, HashMap<String, String> metadataMap, Logger log) {
    try {
      String value = metadataMap.get(key);
      log.info("Metadata '" + key + "' of value " + value );
      return value;
    } catch (Exception e) {
      throw new RuntimeException("Something wrong getting key '" + key + "' from metadata");
    }
  }

  public static List<Integer> getMetadataIntList(String key, HashMap<String, String> metadataMap, Logger log) {
    try {
      List<Integer> resultList = new ArrayList<>();
      String[] parts = metadataMap.get(key).split(",");
      for (String part: parts) {
        resultList.add(Integer.parseInt(part));
      }
      log.info("Metadata '" + key + "' of value " + parts );
      return resultList;
    } catch (Exception e) {
      throw new RuntimeException("Something wrong getting key '" + key + "' from metadata");
    }
  }
}
