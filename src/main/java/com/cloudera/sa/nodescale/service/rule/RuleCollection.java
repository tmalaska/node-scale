package com.cloudera.sa.nodescale.service.rule;

import com.cloudera.sa.nodescale.model.ClusterStatus;
import com.cloudera.sa.nodescale.model.MutationRequest;
import com.cloudera.sa.nodescale.model.ObjectDefinition;
import com.cloudera.sa.nodescale.service.collector.ClusterStatusCollector;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class RuleCollection {
  List<SizingRule> rules;

  public RuleCollection() {

  }

  public RuleCollection(List<ObjectDefinition> ruleDefinitions) {
    rules = new ArrayList<>();
    for (ObjectDefinition objectDefinition: ruleDefinitions) {
      try {
        Class<?> clazz = Class.forName(objectDefinition.getType());
        Constructor<?> ctor = clazz.getConstructor();
        SizingRule sizingRule = (SizingRule)ctor.newInstance();

        sizingRule.init(objectDefinition.getAdditionMetadata());

        rules.add(sizingRule);

      } catch (Exception e) {
        throw new RuntimeException("Error when trying to create collector", e);
      }
    }
  }

  public MutationRequest runRules(ClusterStatus clusterStatus,
                                MutationRequest unresolvedMutationRequest) {
    MutationRequest existinMutationRequest = new MutationRequest();
    for (SizingRule rule: rules) {
      existinMutationRequest = rule.applyRule(clusterStatus, existinMutationRequest, unresolvedMutationRequest);
    }

    return existinMutationRequest;
  }

  public List<SizingRule> getRules() {
    return rules;
  }

  public void setRules(List<SizingRule> rules) {
    this.rules = rules;
  }
}
