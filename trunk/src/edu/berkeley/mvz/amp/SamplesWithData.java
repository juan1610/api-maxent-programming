/*
 * Copyright 2009 University of California at Berkeley
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package edu.berkeley.mvz.amp;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;

/**
 * An immutable class that can be used to represent Samples With Data (SWD). It
 * encapsulates a list of samples and a list of layers. For each sample and
 * layer combination you can get the sampled value for the layer. It is not
 * designed for inheritance and is therefore prohibited.
 * 
 */
public class SamplesWithData {

  /**
   * A builder pattern class that is used to build a {@link SamplesWithData}
   * instance.
   * 
   */
  public static class SwdBuilder {
    private final Map<Sample, Data> sampleData = new HashMap<Sample, Data>();

    /**
     * Adds the sample value for the layer.
     * 
     * @param sample the sample
     * @param Layer the layer
     * @param value the sampled layer value
     * @return the builder
     */
    public SwdBuilder addSampleData(Sample sample, Layer Layer, double value) {
      if (!sampleData.containsKey(sample)) {
        sampleData.put(sample, new Data());
      }
      sampleData.get(sample).putGridValue(Layer, value);
      return this;
    }

    /**
     * Builds and returns a {@link SamplesWithData} instance.
     * 
     * @return samples with data
     */
    public SamplesWithData build() {
      return new SamplesWithData(sampleData);
    }
  }

  private static class Data {
    Map<Layer, Double> gridValues = new HashMap<Layer, Double>();

    @Override
    public String toString() {
      return gridValues.toString();
    }

    List<Layer> getGrids() {
      Set<Layer> grids = gridValues.keySet();
      return new ArrayList<Layer>(grids);
    }

    double getGridValue(Layer Layer) {
      return gridValues.get(Layer);
    }

    void putGridValue(Layer Layer, double value) {
      gridValues.put(Layer, value);
    }
  }

  /**
   * Returns samples from data loaded from a CSV file.
   * 
   * @param path the CSV file path
   * @return samples with data
   * @throws IOException problem reading SWD file
   */
  public static SamplesWithData fromCsv(String path) throws IOException {
    SamplesWithData swd = null;
    SwdBuilder sb = new SwdBuilder();
    CSVReader reader = new CSVReader(new FileReader(path));
    String[] line;

    // Maps header index number to layer name:
    String[] header = reader.readNext();
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    for (int i = 3; i < header.length; i++) {
      map.put(i, String.format("%s.asc", header[i]));
    }

    Sample s;
    Layer l;
    double lat, lng, value;
    while ((line = reader.readNext()) != null) {
      lng = Double.parseDouble(line[1]);
      lat = Double.parseDouble(line[2]);
      s = Sample.newInstance(line[0], 0, LatLng.newInstance(lat, lng));
      for (Integer i : map.keySet()) {
        l = MaxEntService.getLayer(map.get(i));
        value = Double.parseDouble(line[i]);
        sb.addSampleData(s, l, value);
      }
    }
    swd = sb.build();
    return swd;
  }

  /**
   * Writes samples with data to disk in the MaxEnt SWD format.
   * 
   * @param path path to write SWD file
   * @throws IOException problems writing to path
   */
  public static void toCsv(SamplesWithData swd) throws IOException {
    // TODO(eighty)
  }

  private final Map<Sample, Data> sampleData;

  private SamplesWithData(Map<Sample, Data> sampleData) {
    this.sampleData = sampleData;
  }

  /**
   * Returns the list of {@link Layer}s that provided data for these samples
   * with data.
   * 
   * @return list of layers
   */
  public List<Layer> getLayers() {
    List<Layer> layers = new ArrayList<Layer>();
    Sample s = sampleData.keySet().iterator().next();
    if (s != null) {
      List<Layer> lg = sampleData.get(s).getGrids();
      layers = new ArrayList<Layer>(lg);
    }
    return layers;
  }

  /**
   * Returns the layer value associated with the given sample.
   * 
   * @param sample the sample
   * @param Layer the layer
   * @return layer value associated with sample
   */
  public double getSampleData(Sample sample, Layer Layer) {
    return sampleData.get(sample).getGridValue(Layer);
  }

  /**
   * Returns the list of samples encapsulated by these samples with data.
   * 
   * @return list of samples
   */
  public List<Sample> getSamples() {
    Set<Sample> samples = sampleData.keySet();
    return new ArrayList<Sample>(samples);
  }

  @Override
  public String toString() {
    return sampleData.toString();
  }

}
