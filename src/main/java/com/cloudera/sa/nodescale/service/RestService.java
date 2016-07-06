package com.cloudera.sa.nodescale.service;

import com.cloudera.sa.nodescale.model.ClusterDefinition;
import com.cloudera.sa.nodescale.model.ClusterStatus;
import com.cloudera.sa.nodescale.model.InstanceStatus;
import com.cloudera.sa.nodescale.model.MutationRequest;
import com.cloudera.sa.nodescale.service.collector.ClusterStatusCollector;
import com.cloudera.sa.nodescale.service.collector.MockCollector;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Path("rest")
public class RestService {
  private Logger log = Logger.getLogger(RestService.class.getName());

  static HashMap<String, ClusterManager> clusterManagerMap = new HashMap<>();
  @GET
  @Path("hello")
  @Produces(MediaType.TEXT_PLAIN)
  public String helloWorld() {
    return "Hello, world!";
  }

  @PUT
  @Path("cluster")
  @Produces(MediaType.APPLICATION_JSON)
  public HashMap<String, ClusterDefinition> createCluster(ClusterDefinition clusterDefinition) {
    log.info("REST: createCluster");
    ClusterManager clusterManager = new ClusterManager(clusterDefinition);
    if (clusterManagerMap.containsKey(clusterDefinition.getName())) {
      throw new RuntimeException("cluster definision '" + clusterDefinition.getName() + "' all ready exist");
    }
    clusterManagerMap.put(clusterDefinition.getName(), clusterManager);
    return getAllClusters();
  }

  @POST
  @Path("cluster/{name}/startup")
  @Produces(MediaType.APPLICATION_JSON)
  public ClusterDefinition startUpCluster(@PathParam("name") String name) {
    log.info("REST: startup");
    clusterManagerMap.get(name).startup();
    return clusterManagerMap.get(name).getClusterDefinition();
  }

  @POST
  @Path("cluster/{name}/shutdown")
  @Produces(MediaType.APPLICATION_JSON)
  public ClusterDefinition shutdownClusterCluster(@PathParam("name") String name) {
    log.info("REST: shutdown");
    clusterManagerMap.get(name).shutdown();
    return clusterManagerMap.get(name).getClusterDefinition();
  }

  @POST
  @Path("cluster")
  @Produces(MediaType.APPLICATION_JSON)
  public HashMap<String, ClusterDefinition> modifyCluster(ClusterDefinition clusterDefinition) {
    log.info("REST: modifyCluster");
    deleteCluster(clusterDefinition.getName());
    createCluster(clusterDefinition);
    return getAllClusters();
  }

  @GET
  @Path("cluster")
  @Produces(MediaType.APPLICATION_JSON)
  public HashMap<String, ClusterDefinition> getAllClusters() {
    log.info("REST: getAllClusters");
    HashMap<String, ClusterDefinition> definitionMap = new HashMap<>();
    for(Map.Entry<String, ClusterManager> entry: clusterManagerMap.entrySet()) {
      definitionMap.put(entry.getKey(), entry.getValue().getClusterDefinition());
    }
    return definitionMap;
  }

  @GET
  @Path("cluster/{name}")
  @Produces(MediaType.APPLICATION_JSON)
  public ClusterDefinition getClusters(@PathParam("name") String name) {
    log.info("REST: getClusters");
    return clusterManagerMap.get(name).getClusterDefinition();
  }

  @GET
  @Path("cluster/{name}/collect")
  @Produces(MediaType.APPLICATION_JSON)
  public MutationRequest collectNewClusterStatus(@PathParam("name") String name) {
    log.info("REST: collectNewClusterStatus");
    ClusterManager clusterManager = clusterManagerMap.get(name);
    return clusterManager.getLatestMutationRequest();
  }

  @POST
  @Path("cluster/{name}/mock")
  @Produces(MediaType.APPLICATION_JSON)
  public ClusterStatus setMockCollector(@PathParam("name") String name,
                                                          ClusterStatus clusterStatus) {
    log.info("REST: setMockCollector");
    ClusterManager clusterManager = clusterManagerMap.get(name);
    ClusterStatusCollector collector = clusterManager.getCollector();
    if (collector instanceof MockCollector) {
      ((MockCollector)collector).setReply(clusterStatus);
    } else {
      throw new RuntimeException("collector is of type " +
              collector.getClass() + " but it needs to be a MockCollector to mutate it");
    }
    return clusterStatus;
  }

  @GET
  @Path("cluster/{name}/existingstatus")
  @Produces(MediaType.APPLICATION_JSON)
  public ClusterStatus getExistingStatusFromCluster(@PathParam("name") String name) {
    log.info("REST: getExistingStatusFromCluster");
    return clusterManagerMap.get(name).getExistingClusterStatus();
  }

  @DELETE
  @Path("cluster/{name}")
  @Produces(MediaType.APPLICATION_JSON)
  public HashMap<String, ClusterDefinition> deleteCluster(@PathParam("name") String name) {
    log.info("REST: deleteCluster");
    ClusterManager clusterManager = clusterManagerMap.get(name);
    clusterManager.shutdown();
    clusterManagerMap.remove(name);
    return getAllClusters();
  }
}
