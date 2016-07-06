package com.cloudera.sa.nodescale.service.collector;

import com.cloudera.sa.nodescale.model.ObjectDefinition;

import java.lang.reflect.Constructor;

/**
 * Created by ted.malaska on 6/25/16.
 */
public class CollectionBuilder {
  public static ClusterStatusCollector build(ObjectDefinition objectDefinition) {
    try {
      Class<?> clazz = Class.forName(objectDefinition.getType());
      Constructor<?> ctor = clazz.getConstructor();
      ClusterStatusCollector collector = (ClusterStatusCollector)ctor.newInstance();

      collector.init(objectDefinition.getAdditionMetadata());

      return collector;

    } catch (Exception e) {
      throw new RuntimeException("Error when trying to create collector", e);
    }
  }
}
