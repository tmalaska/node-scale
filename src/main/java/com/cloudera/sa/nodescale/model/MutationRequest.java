package com.cloudera.sa.nodescale.model;

import org.codehaus.jackson.map.ObjectMapper;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.util.*;

@XmlRootElement
public class MutationRequest {
  Integer requestedNumberOfNodesToAdd;
  Set<String> instanceIdsToKill;

  public MutationRequest() {
    this(0, new HashSet<>());
  }

  public MutationRequest(Integer requestedNumberOfNodesToAdd,
                         Set<String> instanceIdsToKill) {
    this.requestedNumberOfNodesToAdd = requestedNumberOfNodesToAdd;
    this.instanceIdsToKill = instanceIdsToKill;
  }

  public Integer getRequestedNumberOfNodesToAdd() {
    return requestedNumberOfNodesToAdd;
  }

  public void setRequestedNumberOfNodesToAdd(Integer requestedNumberOfNodesToAdd) {
    this.requestedNumberOfNodesToAdd = requestedNumberOfNodesToAdd;
  }

  public Set<String> getInstanceIdsToKill() {
    return instanceIdsToKill;
  }

  public void setInstanceIdsToKill(Set<String> instanceIdsToKill) {
    this.instanceIdsToKill = instanceIdsToKill;
  }

  public String toString() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(this);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
