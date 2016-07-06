package com.cloudera.sa.nodescale.model;

import java.util.HashMap;

public class ObjectDefinition {
  String type;
  HashMap<String, String> additionMetadata = new HashMap<>();

  public ObjectDefinition() {}

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public HashMap<String, String> getAdditionMetadata() {
    return additionMetadata;
  }

  public void addMetadata(String key, String value) {
    additionMetadata.put(key, value);
  }

  public void setAdditionMetadata(HashMap<String, String> additionMetadata) {
    this.additionMetadata = additionMetadata;
  }
}
