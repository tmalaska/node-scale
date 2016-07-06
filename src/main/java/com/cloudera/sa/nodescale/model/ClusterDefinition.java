package com.cloudera.sa.nodescale.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@XmlRootElement
public class ClusterDefinition {
  String name;
  List<ObjectDefinition> rules = new ArrayList<>();
  ObjectDefinition collector;
  Long collectInterval;
  HashMap additionMetadata;

  public ClusterDefinition() {}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<ObjectDefinition> getRules() {
    return rules;
  }

  public void addRule(ObjectDefinition rule) {
    rules.add(rule);
  }

  public void setRules(List<ObjectDefinition> rules) {
    this.rules = rules;
  }

  public ObjectDefinition getCollector() {
    return collector;
  }

  public void setCollector(ObjectDefinition collector) {
    this.collector = collector;
  }

  public Long getCollectInterval() {
    return collectInterval;
  }

  public void setCollectInterval(Long collectInterval) {
    this.collectInterval = collectInterval;
  }

  public HashMap getAdditionMetadata() {
    return additionMetadata;
  }

  public void setAdditionMetadata(HashMap additionMetadata) {
    this.additionMetadata = additionMetadata;
  }
}
