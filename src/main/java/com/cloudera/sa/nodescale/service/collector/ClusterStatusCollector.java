package com.cloudera.sa.nodescale.service.collector;

import com.cloudera.sa.nodescale.model.ClusterStatus;

import java.util.HashMap;

public interface ClusterStatusCollector {

  void startup();

  void init(HashMap<String, String> metadata);

  ClusterStatus collect();

  void shutdown();
}
