package com.cloudera.sa.nodescale.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class InstanceStatus {
  String instanceId;
  String internalUUID;
  Integer usableVCores;
  Integer usableGbMemory;
  Long startTime;
  Long billableInterval = 60000 * 60l;
  Integer status;
  Boolean isFixedNode;

  public final static int RUNNING_STATUS = 0;
  public final static int MARKED_FOR_DELETION_STATUS = 1;
  public final static int REQUESTED_TO_START_STATUS = 2;

  public InstanceStatus() {}

  public InstanceStatus(String nodeId,
                        String internalUUID,
                        int usableVCores,
                        int usableGbMemory,
                        Long startTime,
                        Long billableInterval,
                        int status,
                        boolean isStorageNode) {
    this.instanceId = nodeId;
    this.internalUUID = internalUUID;
    this.usableVCores = usableVCores;
    this.usableGbMemory = usableGbMemory;
    this.startTime = startTime;
    this.billableInterval = billableInterval;
    this.status = status;
    this.isFixedNode = isStorageNode;
  }

  public String getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(String instanceId) {
    this.instanceId = instanceId;
  }

  public String getInternalUUID() {
    return internalUUID;
  }

  public void setInternalUUID(String internalUUID) {
    this.internalUUID = internalUUID;
  }

  public Integer getUsableVCores() {
    return usableVCores;
  }

  public void setUsableVCores(Integer usableVCores) {
    this.usableVCores = usableVCores;
  }

  public Integer getUsableGbMemory() {
    return usableGbMemory;
  }

  public void setUsableGbMemory(Integer usableGbMemory) {
    this.usableGbMemory = usableGbMemory;
  }

  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  public Long getBillableInterval() {
    return billableInterval;
  }

  public void setBillableInterval(Long billableInterval) {
    this.billableInterval = billableInterval;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public Boolean getIsFixedNode() {
    return isFixedNode;
  }

  public void setIsFixedNode(Boolean isFixedNode) {
    this.isFixedNode = isFixedNode;
  }
}
