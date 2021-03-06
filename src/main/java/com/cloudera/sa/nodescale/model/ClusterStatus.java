package com.cloudera.sa.nodescale.model;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ClusterStatus {
  Long asOfTime = System.currentTimeMillis();
  Integer totalVCores;
  Integer totalGbMemory;
  Integer usedVCores;
  Integer usedGbMemory;
  Integer queuedVCores;
  Integer queuedGbMemory;
  List<InstanceStatus> instanceStatusList = new ArrayList<>();
  List<PastReading> pastUsedVCoreList = new ArrayList<>();
  List<PastReading> pastUsedGbMemoryList = new ArrayList<>();
  List<PastReading> pastQueuedVCoreList = new ArrayList<>();
  List<PastReading> pastQueuedGbMemoryList = new ArrayList<>();

  public ClusterStatus() {
  }

  public ClusterStatus(List<PastReading> pastQueuedGbMemoryList,
                       Integer totalVCoresAvaliable,
                       Integer totalGbMemoryAvaliable,
                       Integer usedVCores,
                       Integer usedGbMemory,
                       Integer queuedVCores,
                       Integer queuedGbMemory,
                       List<InstanceStatus> instanceStatuseList,
                       List<PastReading> pastUsedVCoreList,
                       List<PastReading> pastUsedGbMemoryList,
                       List<PastReading> pastQueuedVCoreList) {
    this.pastQueuedGbMemoryList = pastQueuedGbMemoryList;
    this.totalVCores = totalVCoresAvaliable;
    this.totalGbMemory = totalGbMemoryAvaliable;
    this.usedVCores = usedVCores;
    this.usedGbMemory = usedGbMemory;
    this.queuedVCores = queuedVCores;
    this.queuedGbMemory = queuedGbMemory;
    this.instanceStatusList = instanceStatuseList;
    this.pastUsedVCoreList = pastUsedVCoreList;
    this.pastUsedGbMemoryList = pastUsedGbMemoryList;
    this.pastQueuedVCoreList = pastQueuedVCoreList;
  }

  public Integer getTotalVCores() {
    return totalVCores;
  }

  public void setTotalVCores(Integer totalVCores) {
    this.totalVCores = totalVCores;
  }

  public Integer getTotalGbMemory() {
    return totalGbMemory;
  }

  public void setTotalGbMemory(Integer totalGbMemory) {
    this.totalGbMemory = totalGbMemory;
  }

  public Integer getUsedVCores() {
    return usedVCores;
  }

  public void setUsedVCores(Integer usedVCores) {
    this.usedVCores = usedVCores;
  }

  public Integer getUsedGbMemory() {
    return usedGbMemory;
  }

  public void setUsedGbMemory(Integer usedGbMemory) {
    this.usedGbMemory = usedGbMemory;
  }

  public Integer getQueuedVCores() {
    return queuedVCores;
  }

  public void setQueuedVCores(Integer queuedVCores) {
    this.queuedVCores = queuedVCores;
  }

  public Integer getQueuedGbMemory() {
    return queuedGbMemory;
  }

  public void setQueuedGbMemory(Integer queuedGbMemory) {
    this.queuedGbMemory = queuedGbMemory;
  }

  public List<InstanceStatus> getInstanceStatusList() {
    return instanceStatusList;
  }

  public void setInstanceStatusList(List<InstanceStatus> instanceStatuseList) {
    this.instanceStatusList = instanceStatuseList;
  }

  public List<PastReading> getPastUsedVCoreList() {
    return pastUsedVCoreList;
  }

  public void setPastUsedVCoreList(List<PastReading> pastUsedVCoreList) {
    this.pastUsedVCoreList = pastUsedVCoreList;
  }

  public List<PastReading> getPastUsedGbMemoryList() {
    return pastUsedGbMemoryList;
  }

  public void setPastUsedGbMemoryList(List<PastReading> pastUsedGbMemoryList) {
    this.pastUsedGbMemoryList = pastUsedGbMemoryList;
  }

  public List<PastReading> getPastQueuedVCoreList() {
    return pastQueuedVCoreList;
  }

  public void setPastQueuedVCoreList(List<PastReading> pastQueuedVCoreList) {
    this.pastQueuedVCoreList = pastQueuedVCoreList;
  }

  public List<PastReading> getPastQueuedGbMemoryList() {
    return pastQueuedGbMemoryList;
  }

  public void setPastQueuedGbMemoryList(List<PastReading> pastQueuedGbMemoryList) {
    this.pastQueuedGbMemoryList = pastQueuedGbMemoryList;
  }

  public void addAppendPastValues(ClusterStatus otherStatus) {
    if (otherStatus != null) {
      pastQueuedGbMemoryList = mergeSortLimitThePast(pastQueuedGbMemoryList,
              otherStatus.pastQueuedGbMemoryList,
              new PastReading(otherStatus.asOfTime, otherStatus.getQueuedGbMemory()));
      pastQueuedVCoreList = mergeSortLimitThePast(pastQueuedVCoreList,
              otherStatus.pastQueuedVCoreList,
              new PastReading(otherStatus.asOfTime, otherStatus.getQueuedVCores()));
      pastUsedGbMemoryList = mergeSortLimitThePast(pastUsedGbMemoryList,
              otherStatus.pastUsedGbMemoryList,
              new PastReading(otherStatus.asOfTime, otherStatus.getUsedGbMemory()));
      pastUsedVCoreList = mergeSortLimitThePast(pastUsedVCoreList,
              otherStatus.pastUsedVCoreList,
              new PastReading(otherStatus.asOfTime, otherStatus.getUsedVCores()));
    }
  }

  private List<PastReading> mergeSortLimitThePast(List<PastReading> l1, List<PastReading> l2,
                                                  PastReading lastCurrent) {
    ArrayList<PastReading> result = new ArrayList<>();
    if (l1 != null) {
      result.addAll(l1);
    }
    if (l2 != null) {
      result.addAll(l2);
    }
    result.add(lastCurrent);
    result.sort(new PastReadingComparator());
    while(result.size() > 100) {
      result.remove(result.size() -1);
    }
    return result;
  }
}

class PastReadingComparator implements Comparator {

  @Override
  public int compare(Object o1, Object o2) {
    PastReading pr1 = (PastReading)o1;
    PastReading pr2 = (PastReading)o2;

    return pr1.getTime().compareTo(pr2.getTime());
  }
}
