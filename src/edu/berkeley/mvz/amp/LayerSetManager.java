package edu.berkeley.mvz.amp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LayerSetManager {

  public static class Builder {
    private final Map<String, Set<Layer>> layerSets = new HashMap<String, Set<Layer>>();

    public Builder() {
    }

    public Builder add(String setId, Set<Layer> layerSet) {
      layerSets.put(setId, layerSet);
      return this;
    }

    public LayerSetManager build() {
      return new LayerSetManager(layerSets);
    }
  }

  public static void apiTest() {
  }

  private final HashMap<String, Set<Layer>> layerSets;

  public LayerSetManager(Map<String, Set<Layer>> layerSets) {
    this.layerSets = new HashMap<String, Set<Layer>>(layerSets);
  }

  public void bind(Sample sample, String layerId, Layer layer) {

  }

  public Set<String> getLayerNames() {
    return new HashSet<String>(layerSets.keySet());
  }
}
