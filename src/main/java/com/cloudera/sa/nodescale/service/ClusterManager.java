package com.cloudera.sa.nodescale.service;

import com.cloudera.sa.nodescale.model.ClusterDefinition;
import com.cloudera.sa.nodescale.model.ClusterStatus;
import com.cloudera.sa.nodescale.model.InstanceStatus;
import com.cloudera.sa.nodescale.model.MutationRequest;
import com.cloudera.sa.nodescale.service.collector.ClusterStatusCollector;
import com.cloudera.sa.nodescale.service.collector.CollectionBuilder;
import com.cloudera.sa.nodescale.service.rule.RuleCollection;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class ClusterManager {
  private Logger log = Logger.getLogger(ClusterManager.class.getName());

  private ClusterDefinition clusterDefinition;
  private ClusterStatusCollector collector;
  private ClusterStatus existingClusterStatus;
  private RuleCollection ruleCollection;
  private MutationRequest unresolvedMutationRequest = new MutationRequest();
  protected boolean active = false;
  protected Boolean collectionThreadRunning = false;

  public ClusterManager() {}

  public ClusterManager(ClusterDefinition clusterDefinition) {
    this.clusterDefinition = clusterDefinition;

    collector = CollectionBuilder.build(clusterDefinition.getCollector());

    ruleCollection = new RuleCollection(clusterDefinition.getRules());
  }

  public void startup() {
    existingClusterStatus = null;
    active=true;
    collector.startup();
    Thread nextCollectionRun = new Thread(new CollectorRunnable(this));
    nextCollectionRun.start();
  }

  public void shutdown() {
    active=false;

    while (collectionThreadRunning != false) {
      log.info("waiting for collection to stop ...");
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    collector.shutdown();
  }

  public ClusterStatus collectClusterStatus() {
    ClusterStatus newStatus = collector.collect();

    newStatus.addAppendPastValues(existingClusterStatus);

    int nodeChange = newStatus.getInstanceStatuseList().size() -
            existingClusterStatus.getInstanceStatuseList().size();

    log.info("Collection Cluster Status: Node Change -> " + nodeChange);
    if (nodeChange > 0) {
      //Remove calls to add from unresolved
      unresolvedMutationRequest.setRequestedNumberOfNodesToAdd(
              unresolvedMutationRequest.getRequestedNumberOfNodesToAdd() - nodeChange);
    } else if (nodeChange < 0) {
      //Remove items from unresolved
      Set<String> killSet = unresolvedMutationRequest.getInstanceIdsToKill();
      for (InstanceStatus instanceStatuse: newStatus.getInstanceStatuseList()) {
        if (killSet.contains(instanceStatuse.getInstanceId())) {
          killSet.remove(instanceStatuse.getInstanceId());
        }
      }
    }
    existingClusterStatus = newStatus;

    return existingClusterStatus;
  }

  public MutationRequest getLatestMutationRequest() {

    MutationRequest newRequest = ruleCollection.runRules(existingClusterStatus, unresolvedMutationRequest);

    unresolvedMutationRequest.setRequestedNumberOfNodesToAdd(
            unresolvedMutationRequest.getRequestedNumberOfNodesToAdd() +
                    newRequest.getRequestedNumberOfNodesToAdd());

    unresolvedMutationRequest.getInstanceIdsToKill().addAll(newRequest.getInstanceIdsToKill());

    return newRequest;
  }

  public ClusterDefinition getClusterDefinition() {
    return clusterDefinition;
  }

  public ClusterStatus getExistingClusterStatus() {
    return existingClusterStatus;
  }

  public ClusterStatusCollector getCollector() {
    return collector;
  }

  public void setCollector(ClusterStatusCollector collector) {
    this.collector = collector;
  }

  public boolean isActive() {
    return active;
  }
}

class CollectorRunnable implements Runnable {

  private ClusterManager clusterManager;
  public CollectorRunnable(ClusterManager clusterManager) {
    this.clusterManager = clusterManager;
  }

  @Override
  public void run() {

    synchronized(clusterManager.collectionThreadRunning) {
      clusterManager.collectionThreadRunning = true;
    }

    if (clusterManager.isActive()) {
      clusterManager.collectClusterStatus();
    }
    try {
      Thread.sleep(clusterManager.getClusterDefinition().getCollectInterval());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    if (clusterManager.isActive()) {
      Thread nextRun = new Thread(this);
      nextRun.start();
    } else {
      synchronized(clusterManager.collectionThreadRunning) {
        clusterManager.collectionThreadRunning = false;
      }
    }
  }
}
