package com.cloudera.sa.nodescale.service.rule;


import com.cloudera.sa.nodescale.model.ClusterStatus;
import com.cloudera.sa.nodescale.model.InstanceStatus;
import com.cloudera.sa.nodescale.model.PastReading;
import com.cloudera.sa.nodescale.model.MutationRequest;
import com.cloudera.sa.nodescale.service.rule.utils.CommonRuleLogic;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

@XmlRootElement
public class UsageRule extends AbstractSizingRule {

  private Logger log = Logger.getLogger(UsageRule.class.getName());

  private Integer waitTimeToGrow;
  private Integer waitTimeToShrink;
  private Integer minQueueSizeToWarrantGrowth;
  private Integer minIdealSizeToWarrantShrink;
  private InstanceStatus baseInstanceType;

  public UsageRule() {}

  public UsageRule(String ruleName,
                   int waitTimeToGrow,
                   int waitTimeToShrink,
                   int minQueueSizeToWarrantGrowth,
                   int minIdealSizeToWarrantShrink,
                   InstanceStatus baseInstanceType) {
    this.ruleName = ruleName;
    this.waitTimeToGrow = waitTimeToGrow;
    this.waitTimeToShrink = waitTimeToShrink;
    this.minQueueSizeToWarrantGrowth = minQueueSizeToWarrantGrowth;
    this.minIdealSizeToWarrantShrink = minIdealSizeToWarrantShrink;
    this.baseInstanceType = baseInstanceType;
  }

  @Override
  public void init(HashMap<String, String> metadata) {
    ruleName = CommonRuleLogic.getMetadataString("name", metadata, log);
    waitTimeToGrow = CommonRuleLogic.getMetadataInt("waitTimeToGrow", metadata, log);
    waitTimeToShrink = CommonRuleLogic.getMetadataInt("waitTimeToShrink", metadata, log);
    minQueueSizeToWarrantGrowth = CommonRuleLogic.getMetadataInt("minQueueSizeToWarrantGrowth", metadata, log);
    minIdealSizeToWarrantShrink = CommonRuleLogic.getMetadataInt("minIdealSizeToWarrantShrink", metadata, log);
    baseInstanceType = new InstanceStatus();
    baseInstanceType.setUsableVCores(CommonRuleLogic.getMetadataInt("baseInstanceUsableVCores", metadata, log));
    baseInstanceType.setUsableGbMemory(CommonRuleLogic.getMetadataInt("baseInstanceUsableGbMemory", metadata, log));
  }

  @Override
  protected MutationRequest applyRuleInternal(ClusterStatus clusterStatus,
                                              MutationRequest existingMutationRequest,
                                              MutationRequest unresolvedMutationRequest) {
    Long now = System.currentTimeMillis();

    int totalPastQueueCores = 0;
    int totalPastQueueCoreReadings = 0;
    for (PastReading pastReading : clusterStatus.getPastQueuedVCoreList()) {
      if (pastReading.getTime() > now - waitTimeToGrow ) {
        totalPastQueueCoreReadings++;
        totalPastQueueCores += pastReading.getReading();
      }
    }

    if (totalPastQueueCoreReadings > 0) {
      int avgQueuedvCoresInstances = totalPastQueueCores / totalPastQueueCoreReadings / baseInstanceType.getUsableVCores();
      log.info(" > avgQueuedvCoresInstances = " + avgQueuedvCoresInstances + " = " + totalPastQueueCores + "/" + totalPastQueueCoreReadings + "/" + baseInstanceType.getUsableVCores());

      int totalPastQueueGbMemory = 0;
      int totalPastQueueMemoryReadings = 0;
      for (PastReading pastReading : clusterStatus.getPastQueuedGbMemoryList()) {
        if (pastReading.getTime() > now - waitTimeToGrow) {
          totalPastQueueMemoryReadings++;
          totalPastQueueGbMemory += pastReading.getReading();
        }
      }
      int avgQueuedGbMemoryInstances = totalPastQueueGbMemory / totalPastQueueMemoryReadings / baseInstanceType.getUsableGbMemory();
      log.info(" > avgQueuedGbMemoryInstances = " + avgQueuedGbMemoryInstances + " = " + totalPastQueueGbMemory + "/" + totalPastQueueMemoryReadings + "/" + baseInstanceType.getUsableGbMemory());

      int totalPastIdealvCores = 0;
      int totalPastIdealvCoreReadings = 0;
      for (PastReading pastReading : clusterStatus.getPastUsedVCoreList()) {
        if (pastReading.getTime() > now - waitTimeToShrink) {
          totalPastIdealvCoreReadings++;
          totalPastIdealvCores += clusterStatus.getTotalVCores() - pastReading.getReading();
        }
      }
      int avgIdealvCoresInstances = totalPastIdealvCores / totalPastIdealvCoreReadings / baseInstanceType.getUsableVCores();
      log.info(" > avgIdealvCoresInstances = " + avgIdealvCoresInstances + " = " + totalPastIdealvCores + "/" + totalPastIdealvCoreReadings + "/" + baseInstanceType.getUsableVCores());

      int totalPastIdealGbMemorys = 0;
      int totalPastIdealGbMemoryReadings = 0;
      for (PastReading pastReading : clusterStatus.getPastUsedGbMemoryList()) {
        if (pastReading.getTime() > now - waitTimeToShrink) {
          totalPastIdealGbMemoryReadings++;
          totalPastIdealGbMemorys += clusterStatus.getTotalGbMemory() - pastReading.getReading();
        }
      }
      int avgIdealGbMemoryInstances = totalPastIdealGbMemorys / totalPastIdealGbMemoryReadings / baseInstanceType.getUsableGbMemory();
      log.info(" > avgIdealGbMemoryInstances = " + avgIdealGbMemoryInstances + " = " + totalPastIdealGbMemorys + "/" + totalPastIdealGbMemoryReadings + "/" + baseInstanceType.getUsableGbMemory());

      int instancesToAdd = Math.max(0, Math.max(avgQueuedvCoresInstances, avgQueuedGbMemoryInstances) -
              existingMutationRequest.getRequestedNumberOfNodesToAdd() -
              unresolvedMutationRequest.getRequestedNumberOfNodesToAdd() -
              minQueueSizeToWarrantGrowth);



      int instancesToShrink = Math.max(0, Math.max(avgIdealvCoresInstances, avgIdealGbMemoryInstances) -
              existingMutationRequest.getInstanceIdsToKill().size() -
              unresolvedMutationRequest.getInstanceIdsToKill().size() -
              minIdealSizeToWarrantShrink);

      int finalResult = instancesToAdd - instancesToShrink;

      log.info("finalResult:" + finalResult + "=" + instancesToAdd + "-" + instancesToShrink);

      if (finalResult > 0) {
        existingMutationRequest.setRequestedNumberOfNodesToAdd(
                existingMutationRequest.getRequestedNumberOfNodesToAdd() + finalResult);
      } else {
        Set<String> unresolvedKillList = unresolvedMutationRequest.getInstanceIdsToKill();
        Set<String> killList = existingMutationRequest.getInstanceIdsToKill();

        int nodesToTryToDelete = finalResult * -1;

        for (int i = 0; i < nodesToTryToDelete; i++) {
          String nodeToKill = CommonRuleLogic.IdentifyInstanceToKill(clusterStatus,
                  unresolvedKillList,
                  killList,
                  30);

          if (nodeToKill == null) {
            break;
          } else {
            killList.add(nodeToKill);
          }
        }
      }
    }
    return existingMutationRequest;
  }

  public Integer getWaitTimeToGrow() {
    return waitTimeToGrow;
  }

  public void setWaitTimeToGrow(Integer waitTimeToGrow) {
    this.waitTimeToGrow = waitTimeToGrow;
  }

  public Integer getWaitTimeToShrink() {
    return waitTimeToShrink;
  }

  public void setWaitTimeToShrink(Integer waitTimeToShrink) {
    this.waitTimeToShrink = waitTimeToShrink;
  }

  public Integer getMinQueueSizeToWarrentGrowth() {
    return minQueueSizeToWarrantGrowth;
  }

  public void setMinQueueSizeToWarrentGrowth(Integer minQueueSizeToWarrentGrowth) {
    this.minQueueSizeToWarrantGrowth = minQueueSizeToWarrentGrowth;
  }

  public Integer getMinIdealSizeToWarrentShrink() {
    return minIdealSizeToWarrantShrink;
  }

  public void setMinIdealSizeToWarrentShrink(Integer minIdealSizeToWarrentShrink) {
    this.minIdealSizeToWarrantShrink = minIdealSizeToWarrentShrink;
  }

  public InstanceStatus getBaseInstanceType() {
    return baseInstanceType;
  }

  public void setBaseInstanceType(InstanceStatus baseInstanceType) {
    this.baseInstanceType = baseInstanceType;
  }


}
