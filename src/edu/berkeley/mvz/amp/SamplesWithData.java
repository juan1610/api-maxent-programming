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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import edu.berkeley.mvz.amp.Layer.LayerProvider;

/**
 * An immutable class that can be used to represent Samples With CellData (SWD).
 * It encapsulates a list of samples and a list of layers. For each sample and
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

    private final Map<Sample, Map<Layer, Double>> sampleData;

    public SwdBuilder() {
      sampleData = new HashMap<Sample, Map<Layer, Double>>();
    }

    /**
     * Adds the sample value for the layer.
     * 
     * @param sample the sample
     * @param Layer the layer
     * @param value the sampled layer value
     * @return the builder
     */
    public SwdBuilder addData(Sample sample, Layer Layer, double value) {
      if (!sampleData.containsKey(sample)) {
        sampleData.put(sample, new HashMap<Layer, Double>());
      }
      sampleData.get(sample).put(Layer, value);
      return this;
    }

    /**
     * Builds and returns a {@link SamplesWithData} instance.
     * 
     * @return samples with CellData
     */
    public SamplesWithData build() {
      Map<Sample, Data> data = new HashMap<Sample, Data>();
      Sample s;
      Cell c;
      Map<Layer, Double> layerValues;
      for (Entry<Sample, Map<Layer, Double>> e : sampleData.entrySet()) {
        s = e.getKey();
        layerValues = e.getValue();
        c = layerValues.keySet().iterator().next().asCell(s.getPoint());
        data.put(s, Data.newInstance(c, layerValues));
      }
      return new SamplesWithData(data);
    }

  }

  /**
   * This interface can be used to filter
   * 
   */
  public static interface SwdFilter {
    public boolean acceptLayer(Layer layer);

    public boolean acceptSample(Sample sample);
  }

  /**
   * Returns samples from CellData loaded from a CSV file.
   * 
   * @param path the CSV file path
   * @return samples with CellData
   * @throws IOException problem reading SWD file
   */
  public static SamplesWithData fromCsv(String path, LayerProvider provider)
      throws IOException {
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

    // Reads sample with CellData from CSV and adds CellData to the builder:
    Sample s;
    Layer l;
    double lat, lng, value;
    String name = null;
    while ((line = reader.readNext()) != null) {
      lng = Double.parseDouble(line[1]);
      lat = Double.parseDouble(line[2]);
      if (line[0].contains("_")) {
        name = line[0].split("_")[0] + " " + line[0].split("_")[1];
      } else {
        name = line[0];
      }
      s = Sample.newInstance(name, 0, LatLng.newInstance(lat, lng));
      for (Integer i : map.keySet()) {
        l = provider.getLayerByFilename(map.get(i));
        value = Double.parseDouble(line[i]);
        sb.addData(s, l, value);
      }
    }

    swd = sb.build();
    return swd;
  }

  private final Map<Sample, Data> sampleData;
  private final HashSet<String> sampleNames = new HashSet<String>();

  private SamplesWithData(Map<Sample, Data> sampleData) {
    this.sampleData = sampleData;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SamplesWithData)) {
      return false;
    }
    SamplesWithData swd = (SamplesWithData) o;
    boolean b = sampleData.equals(swd.sampleData);
    return b;
  }

  public Data getData(Sample sample) {
    return sampleData.get(sample);
  }

  /**
   * Returns the layer value associated with the given sample.
   * 
   * @param sample the sample
   * @param Layer the layer
   * @return layer value associated with sample
   */
  public double getData(Sample sample, Layer Layer) {
    return sampleData.get(sample).getValue(Layer);
  }

  /**
   * Returns the list of {@link Layer}s that provided CellData for these samples
   * with CellData.
   * 
   * @return list of layers
   */
  public List<Layer> getLayers() {
    return new ArrayList<Layer>(sampleData.values().iterator().next()
        .getLayers());
  }

  public Set<String> getSampleNames() {
    if (sampleNames.isEmpty()) {
      for (Sample s : sampleData.keySet()) {
        sampleNames.add(s.getName());
      }
    }
    return sampleNames;
  }

  /**
   * Returns the list of samples encapsulated by these samples with CellData.
   * 
   * @return list of samples
   */
  public List<Sample> getSamples() {
    Set<Sample> samples = sampleData.keySet();
    return new ArrayList<Sample>(samples);
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + sampleData.hashCode();
    return result;
  }

  /**
   * Returns the size (number of unique samples) of this object.
   * 
   * @return
   */
  public int size() {
    return sampleData.size();
  }

  /**
   * Writes samples with CellData to disk in the MaxEnt SWD format.
   * 
   * @param path path to write SWD file
   * @throws IOException problems writing to path
   */
  public void toCsv(String path) throws IOException {
    CSVWriter writer = new CSVWriter(new FileWriter(path), ',');

    // Writes the header that includes the layer filenames:
    String[] header = new String[3 + getLayers().size()];
    header[0] = "species";
    header[1] = "dd long";
    header[2] = "dd lat";
    int count = 3;
    for (Layer l : getLayers()) {
      // MaxEnt header doesn't include file extension:
      String[] name = l.getFilename().split(".asc");
      header[count++] = name[0];
    }
    writer.writeNext(header);

    String[] line = null;
    LatLng p = null;
    for (Sample s : getSamples()) {
      p = s.getPoint();
      String CellData = String.format("%s,%f,%f", s.getName(),
          p.getLongitude(), p.getLatitude());
      for (Layer l : getLayers()) {
        CellData += "," + getData(s, l);
      }
      line = CellData.split(",");
      writer.writeNext(line);
    }
    writer.close();
  }

  public void toCsv(String path, SwdFilter filter) throws IOException {
    CSVWriter writer = new CSVWriter(new FileWriter(path), ',');

    // Writes the header that includes the layer filenames:
    String[] header = new String[3 + getLayers().size()];
    header[0] = "species";
    header[1] = "dd long";
    header[2] = "dd lat";
    int count = 3;
    for (Layer l : getLayers()) {
      // MaxEnt header doesn't include file extension:
      String[] name = l.getFilename().split(".asc");
      if (filter.acceptLayer(l)) {
        header[count++] = name[0];
      }
    }
    writer.writeNext(header);

    String[] line = null;
    LatLng p = null;
    for (Sample s : getSamples()) {
      if (!filter.acceptSample(s)) {
        continue;
      }
      p = s.getPoint();
      String CellData = String.format("%s,%f,%f", s.getName(),
          p.getLongitude(), p.getLatitude());
      for (Layer l : getLayers()) {
        CellData += "," + getData(s, l);
      }
      line = CellData.split(",");
      writer.writeNext(line);
    }
    writer.close();
  }

  @Override
  public String toString() {
    return sampleData.toString();
  }

  public String toTempCsv() throws IOException {
    String path = File.createTempFile("swd", ".csv").getPath();
    toCsv(path);
    return path;
  }

  public String toTempCsv(SwdFilter filter) throws IOException {
    String path = File.createTempFile("swd", ".csv").getPath();
    toCsv(path, filter);
    return path;
  }

}
