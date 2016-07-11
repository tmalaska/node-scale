package com.cloudera.sa.nodescale.service.rule;

import com.cloudera.sa.nodescale.model.ClusterStatus;
import com.cloudera.sa.nodescale.model.MutationRequest;
import com.cloudera.sa.nodescale.service.rule.utils.CommonRuleLogic;

import java.util.*;
import java.util.logging.Logger;

public class MinMaxTimeRangeRule extends AbstractSizingRule {

  Logger log = Logger.getLogger(MinMaxTimeRangeRule.class.getName());

  Integer minNumberOfNodes;
  Integer maxNumberOfNodes;
  Set<Integer> daysOfWeekToApplyRule;
  String startTimeOfDay;
  String endTimeOfDay;
  private Integer startHour;
  private Integer startMinute;
  private Integer endHour;
  private Integer endMinute;

  public MinMaxTimeRangeRule () {}

  public MinMaxTimeRangeRule(String ruleName,
                             int minNumberOfNodes,
                             int maxNumberOfNodes,
                             Set<Integer> daysOfWeekToApplyRule,
                             String startTimeOfDay,
                             String endTimeOfDay) {
    setRuleName(ruleName);
    this.minNumberOfNodes = minNumberOfNodes;
    this.maxNumberOfNodes = maxNumberOfNodes;
    this.daysOfWeekToApplyRule = daysOfWeekToApplyRule;
    setEndTimeOfDay(endTimeOfDay);
    setStartTimeOfDay(startTimeOfDay);
  }

  @Override
  public void init(HashMap<String, String> metadata) {
    ruleName = CommonRuleLogic.getMetadataString("name", metadata, log);
    minNumberOfNodes = CommonRuleLogic.getMetadataInt("minNumberOfNodes", metadata, log);
    maxNumberOfNodes = CommonRuleLogic.getMetadataInt("maxNumberOfNodes", metadata, log);
    daysOfWeekToApplyRule = new HashSet<>();
    daysOfWeekToApplyRule.addAll(CommonRuleLogic.getMetadataIntList("daysOfWeekToApplyRule", metadata, log));
    startTimeOfDay = CommonRuleLogic.getMetadataString("startTimeOfDay", metadata, log);
    endTimeOfDay = CommonRuleLogic.getMetadataString("endTimeOfDay", metadata, log);
    setEndTimeOfDay(endTimeOfDay);
    setStartTimeOfDay(startTimeOfDay);
  }

  @Override
  public MutationRequest applyRuleInternal(ClusterStatus clusterStatus,
                                           MutationRequest existingMutationRequest,
                                           MutationRequest unresolvedMutationRequest) {
    //See if the rule applies to now
    Calendar now = Calendar.getInstance();
    Integer dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
    Integer hourOfDay = now.get(Calendar.HOUR_OF_DAY);
    Integer minuteOfHour = now.get(Calendar.MINUTE);

    int futureClusterState = clusterStatus.getInstanceStatuseList().size() +
            unresolvedMutationRequest.getRequestedNumberOfNodesToAdd() -
            unresolvedMutationRequest.getInstanceIdsToKill().size();

    if (daysOfWeekToApplyRule.contains(dayOfWeek) &&
            (hourOfDay > startHour || (hourOfDay == startHour && minuteOfHour >= startMinute)) &&
            (hourOfDay < endHour || (hourOfDay == endHour && minuteOfHour <= endMinute))) {
      log.info("  > Rule " + ruleName + " applies");
      Integer existingNodesRequested = futureClusterState +
              existingMutationRequest.getRequestedNumberOfNodesToAdd() -
              existingMutationRequest.getInstanceIdsToKill().size();

      if (existingNodesRequested < minNumberOfNodes) {
        log.info(" > > The future requested " + existingMutationRequest + " is under the min rule " + minNumberOfNodes);
        //Undo a kill command
        while (existingMutationRequest.getInstanceIdsToKill().size() > 0 &&
                existingNodesRequested < minNumberOfNodes) {

          existingMutationRequest.getInstanceIdsToKill().remove(0);

          existingNodesRequested = futureClusterState +
                  existingMutationRequest.getRequestedNumberOfNodesToAdd() -
                  existingMutationRequest.getInstanceIdsToKill().size();
        }

        //Add some more adds
        if (existingNodesRequested < minNumberOfNodes) {
          Integer newAddNodeCount = existingMutationRequest.getRequestedNumberOfNodesToAdd() +
                  minNumberOfNodes - existingNodesRequested;

          existingMutationRequest.setRequestedNumberOfNodesToAdd(newAddNodeCount);
        }

        return existingMutationRequest;

      } else if (existingNodesRequested > maxNumberOfNodes) {
        log.info(" > > The future requested " + existingMutationRequest + " is above the max rule " + maxNumberOfNodes);

        //Remove possible adds
        Integer newAddNodeCount = existingMutationRequest.getRequestedNumberOfNodesToAdd() -
                (existingNodesRequested - maxNumberOfNodes);

        //If we still need to shrink start removing nodes
        if (newAddNodeCount < 0) {
          int nodesToTryToDelete = Math.abs(newAddNodeCount);
          Set<String> unresolvedKillList = unresolvedMutationRequest.getInstanceIdsToKill();
          Set<String> killList = existingMutationRequest.getInstanceIdsToKill();

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
          newAddNodeCount = 0;
        }
        existingMutationRequest.setRequestedNumberOfNodesToAdd(newAddNodeCount);
        return existingMutationRequest;
      } else {
        return existingMutationRequest;
      }

    } else {
      log.info(" > time doesn't apply to rule.");
      return existingMutationRequest;
    }

  }

  public Integer getMinNumberOfNodes() {
    return minNumberOfNodes;
  }

  public void setMinNumberOfNodes(Integer minNumberOfNodes) {
    this.minNumberOfNodes = minNumberOfNodes;
  }

  public Integer getMaxNumberOfNodes() {
    return maxNumberOfNodes;
  }

  public void setMaxNumberOfNodes(Integer maxNumberOfNodes) {
    this.maxNumberOfNodes = maxNumberOfNodes;
  }

  public Set<Integer> getDaysOfWeekToApplyRule() {
    return daysOfWeekToApplyRule;
  }

  public void setDaysOfWeekToApplyRule(Set<Integer> daysOfWeekToApplyRule) {
    this.daysOfWeekToApplyRule = daysOfWeekToApplyRule;
  }

  public String getStartTimeOfDay() {
    return startTimeOfDay;
  }

  public void setStartTimeOfDay(String startTimeOfDay) {
    this.startTimeOfDay = startTimeOfDay;
    String[] timeParts = startTimeOfDay.split(":");
    startHour = Integer.parseInt(timeParts[0]);
    startMinute = Integer.parseInt(timeParts[1]);
  }

  public String getEndTimeOfDay() {
    return endTimeOfDay;
  }

  public void setEndTimeOfDay(String endTimeOfDay) {
    this.endTimeOfDay = endTimeOfDay;
    String[] timeParts = endTimeOfDay.split(":");
    endHour = Integer.parseInt(timeParts[0]);
    endMinute = Integer.parseInt(timeParts[1]);
  }


}
