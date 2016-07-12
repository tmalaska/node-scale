package com.cloudera.sa.nodescale.service.collector;

import com.cloudera.sa.nodescale.model.ClusterStatus;
import com.cloudera.sa.nodescale.model.InstanceStatus;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

@XmlRootElement
public class MockCollector extends AbstractCollector {
  private Logger log = Logger.getLogger(MockCollector.class.getName());

  ClusterStatus reply = new ClusterStatus();


  public MockCollector() {
    reply.setUsedVCores(0);
    reply.setUsedGbMemory(0);
    reply.setTotalVCores(36);
    reply.setTotalGbMemory(384);
    reply.setQueuedGbMemory(0);
    reply.setQueuedVCores(0);
    List<InstanceStatus> instanceList = new ArrayList<>();
    instanceList.add(new InstanceStatus("nodeId-1",
            "asdf",
            12,
            128,
            System.currentTimeMillis(),
            60000 * 60l,
            InstanceStatus.RUNNING_STATUS,
            false));
    instanceList.add(new InstanceStatus("nodeId-2",
            "asdf",
            12,
            128,
            System.currentTimeMillis(),
            60000 * 60l,
            InstanceStatus.RUNNING_STATUS,
            false));
    instanceList.add(new InstanceStatus("nodeId-2",
            "asdf",
            12,
            128,
            System.currentTimeMillis(),
            60000 * 60l,
            InstanceStatus.RUNNING_STATUS,
            false));

    reply.setInstanceStatusList(instanceList);
  }

  public MockCollector(ClusterStatus reply) {

    this.reply = reply;
  }

  @Override
  public void init(HashMap<String, String> metadata) {

  }

  @Override
  public ClusterStatus collect() {
    log.info("MockCollector collecting");
    return reply;
  }

  public ClusterStatus getReply() {

    return reply;
  }

  public void setReply(ClusterStatus reply) {

    this.reply = reply;
  }
}
