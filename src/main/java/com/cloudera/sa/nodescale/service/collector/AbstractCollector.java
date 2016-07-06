package com.cloudera.sa.nodescale.service.collector;

import com.cloudera.sa.nodescale.model.ClusterStatus;
import com.cloudera.sa.nodescale.model.ObjectDefinition;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public abstract class AbstractCollector implements ClusterStatusCollector{

  public void startup() {}

  public void shutdown() {}

  public abstract ClusterStatus collect();
}
