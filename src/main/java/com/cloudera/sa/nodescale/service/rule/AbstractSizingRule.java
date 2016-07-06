package com.cloudera.sa.nodescale.service.rule;


import com.cloudera.sa.nodescale.model.ClusterStatus;
import com.cloudera.sa.nodescale.model.MutationRequest;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.logging.Logger;

public abstract class AbstractSizingRule implements SizingRule {

  Logger log = Logger.getLogger(AbstractSizingRule.class.getName());

  ObjectMapper mapper = new ObjectMapper();

  String ruleName = "undefinied";

  AbstractSizingRule() {
  }

  public MutationRequest applyRule(ClusterStatus clusterStatus,
                                   MutationRequest existingMutationRequest,
                                   MutationRequest unresolvedMutationRequest) {
    log.info("Starting rule:" + ruleName);
    MutationRequest newMutationRequest = applyRuleInternal(clusterStatus,
            existingMutationRequest,
            unresolvedMutationRequest);

    try {
      log.info(" > new mutationRequest: " + mapper.writeValueAsString(newMutationRequest));
    } catch (Exception e) {
      e.printStackTrace();
      log.warning(e.getMessage());
    }
    log.info("Finished rule:" + ruleName + " -> " + newMutationRequest);
    return newMutationRequest;
  }

  abstract protected MutationRequest applyRuleInternal(ClusterStatus clusterStatus,
                                                       MutationRequest existingMutationRequest,
                                                       MutationRequest unresolvedMutationRequest);

  public String getRuleName() {
    return ruleName;
  }

  public void setRuleName(String ruleName) {
    this.ruleName = ruleName;
  }
}
