package com.cloudera.sa.nodescale.service.rule;

import com.cloudera.sa.nodescale.model.ClusterStatus;
import com.cloudera.sa.nodescale.model.MutationRequest;
import com.cloudera.sa.nodescale.model.ObjectDefinition;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;

@XmlRootElement
public interface SizingRule {

  void init(HashMap<String, String> metadata);

  MutationRequest applyRule(ClusterStatus clusterStatus,
                                   MutationRequest existingMutationRequest,
                                   MutationRequest unresolvedMutationRequest);
}
