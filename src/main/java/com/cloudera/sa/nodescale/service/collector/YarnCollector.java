package com.cloudera.sa.nodescale.service.collector;


import com.cloudera.sa.nodescale.model.ClusterStatus;
import com.cloudera.sa.nodescale.model.InstanceStatus;
import org.apache.hadoop.yarn.api.records.*;
import org.apache.hadoop.yarn.client.api.AMRMClient;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.impl.AMRMClientImpl;
import org.apache.hadoop.yarn.client.api.impl.YarnClientImpl;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YarnCollector extends AbstractCollector {

  YarnClient yarnClient = YarnClient.createYarnClient();
  @Override
  public void init(HashMap<String, String> metadata) {

    YarnConfiguration conf = new YarnConfiguration();

    if (metadata != null) {
      for (Map.Entry<String, String> entity : metadata.entrySet()) {
        conf.set(entity.getKey(), entity.getValue());
      }
    }

    yarnClient.init(conf);
    yarnClient.start();
  }

  @Override
  public ClusterStatus collect() {

    ClusterStatus clusterStatus = new ClusterStatus();


    try {
      int vCoresUsed = 0;
      int memoryUsed = 0;
      int vCoresQueued = 0;
      int memoryQueued = 0;

      YarnClientImpl g = null;
      AMRMClientImpl h = null;

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

      int vCoreCount = 0;
      int memoryCount = 0;
      List<InstanceStatus> instanceStatusList = new ArrayList<>();
      for (NodeReport nodeReport:  yarnClient.getNodeReports(NodeState.RUNNING)) {
        NodeId nodeId = nodeReport.getNodeId();
        InstanceStatus instanceStatus = new InstanceStatus();
        instanceStatus.setInstanceId(nodeId.getHost());
        instanceStatus.setInternalUUID(nodeId.getHost() + ":" + nodeId.getPort());
        instanceStatus.setUsableVCores(nodeReport.getCapability().getVirtualCores());
        instanceStatus.setUsableGbMemory(nodeReport.getCapability().getMemory());
        instanceStatusList.add(instanceStatus);
        memoryCount += instanceStatus.getUsableGbMemory();
        vCoreCount += instanceStatus.getUsableVCores();
      }

      clusterStatus.setUsedVCores(vCoresUsed);
      clusterStatus.setUsedGbMemory(memoryUsed);
      clusterStatus.setQueuedVCores(vCoresQueued);
      clusterStatus.setQueuedGbMemory(memoryQueued);
      clusterStatus.setTotalVCoresAvaliable(vCoreCount/1000);
      clusterStatus.setTotalGbMemoryAvaliable(memoryCount/1000);
      clusterStatus.setInstanceStatusList(instanceStatusList);

      return clusterStatus;
    } catch (YarnException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
