package com.cloudera.sa.nodescale.service.collector;


import com.cloudera.sa.nodescale.model.ClusterStatus;
import com.cloudera.sa.nodescale.model.InstanceStatus;
import org.apache.hadoop.yarn.api.records.*;
import org.apache.hadoop.yarn.client.api.AMRMClient;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.exceptions.YarnException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class YarnCollector extends AbstractCollector {
  @Override
  public void init(HashMap<String, String> metadata) {


    //yarnClient.getNodeReports();
  }

  @Override
  public ClusterStatus collect() {

    ClusterStatus clusterStatus = new ClusterStatus();


    try {
      YarnClient yarnClient = YarnClient.createYarnClient();

      int vCoresUsed = 0;
      int memoryUsed = 0;
      int vCoresQueued = 0;
      int memoryQueued = 0;

      YarnClusterMetrics yarnClusterMetrics = yarnClient.getYarnClusterMetrics();
      for (QueueInfo queueInfo : yarnClient.getAllQueues()) {
        for (ApplicationReport appReport : queueInfo.getApplications()) {
          Resource reservedResources = appReport.getApplicationResourceUsageReport().getReservedResources();
          Resource usedResources = appReport.getApplicationResourceUsageReport().getUsedResources();
          Resource neededResoruces = appReport.getApplicationResourceUsageReport().getNeededResources();
          YarnApplicationState yarnApplicationState = appReport.getYarnApplicationState();
          if (yarnApplicationState.equals(YarnApplicationState.RUNNING) ||
                  yarnApplicationState.equals(YarnApplicationState.ACCEPTED) ||
                  yarnApplicationState.equals(YarnApplicationState.SUBMITTED)) {
            vCoresUsed += reservedResources.getVirtualCores();
            memoryUsed += reservedResources.getMemory();
            vCoresQueued += neededResoruces.getVirtualCores();
            memoryQueued += neededResoruces.getMemory();
          }
        }
      }

      List<InstanceStatus> instanceStatusList = new ArrayList<>();
      for (NodeReport nodeReport:  yarnClient.getNodeReports(NodeState.RUNNING)) {
        NodeId nodeId = nodeReport.getNodeId();
        InstanceStatus instanceStatus = new InstanceStatus();
        instanceStatus.setInstanceId(nodeId.getHost());
        instanceStatus.setInternalUUID(nodeId.getHost() + ":" + nodeId.getPort());
        instanceStatus.setUsableVCores(nodeReport.getCapability().getVirtualCores());
        instanceStatus.setUsableGbMemory(nodeReport.getCapability().getMemory());
        instanceStatusList.add(instanceStatus);
      }

      AMRMClient amrmClient = AMRMClient.createAMRMClient();
      int vCoreCount = amrmClient.getAvailableResources().getVirtualCores();
      int memoryCount = amrmClient.getAvailableResources().getMemory();

      clusterStatus.setUsedVCores(vCoresUsed);
      clusterStatus.setUsedGbMemory(memoryUsed);
      clusterStatus.setQueuedVCores(vCoresQueued);
      clusterStatus.setQueuedGbMemory(memoryQueued);
      clusterStatus.setTotalVCoresAvaliable(vCoreCount);
      clusterStatus.setTotalGbMemoryAvaliable(memoryCount);
      clusterStatus.setInstanceStatusList(instanceStatusList);

      return clusterStatus;
    } catch (YarnException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
