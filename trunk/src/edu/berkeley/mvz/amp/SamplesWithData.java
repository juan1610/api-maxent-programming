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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
  public static interface SwdSpec {
    public Layer getLayer(String layerName, Sample sample);
    public List<String> getLayerNames();
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
    String name = null, nameYear;
    int year;
    while ((line = reader.readNext()) != null) {
      lng = Double.parseDouble(line[1]);
      lat = Double.parseDouble(line[2]);
      if (line[0].contains("_")) {
        nameYear = line[0].split("_")[0] + " " + line[0].split("_")[1];
      } else {
        nameYear = line[0];
      }
      name = nameYear.split("-")[0];
      if (nameYear.contains("-")) {
        year = Integer.parseInt(nameYear.split("-")[1]);
        s = Sample.newInstance(name, year, LatLng.newInstance(lat, lng));
      } else {
        s = Sample.newInstance(name, -1, LatLng.newInstance(lat, lng));
      }
      for (Integer i : map.keySet()) {
        l = provider.getLayerByFilename(map.get(i));
        value = Double.parseDouble(line[i]);
        sb.addData(s, l, value);
      }
    }

    swd = sb.build();
    return swd;
  }

  private static int randomSampleYear(List<Layer> layers) {
    int r = Math.abs(new Random().nextInt()) % (layers.size() - 1);
    return layers.get(r).getYear();
  }

  private final Map<Sample, Data> sampleData;

  private final HashSet<String> sampleNames = new HashSet<String>();

  private SamplesWithData(Map<Sample, Data> sampleData) {
    this.sampleData = sampleData;
  }

  public void bind(Sample sample, String layerId, Layer layer) {

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
   * Writes SWD to a temporary CSV file without appending sample year data and
   * returns the path to the file.
   * 
   * @return
   * @throws IOException
   */
  public String toCsv() throws IOException {
    String path = File.createTempFile("swd", ".csv").getPath();
    toCsv(path, false);
    return path;
  }

  /**
   * Writes SWD to a temporary CSV file and returns the file path.
   * 
   * @param appendYear
   * @return
   * @throws IOException
   */
  public String toCsv(boolean appendYear) throws IOException {
    String path = File.createTempFile("swd", ".csv").getPath();
    toCsv(path, appendYear);
    return path;
  }

  /**
   * Writes SWD to a CSV at path without appending sample year data.
   * 
   * @param path path to write SWD file
   * @throws IOException problems writing to path
   */
  public void toCsv(String path) throws IOException {
    toCsv(path, false);
  }

  /**
   * Writes samples with CellData to disk in the MaxEnt SWD format.
   * 
   * @param path path to write SWD file
   * @throws IOException problems writing to path
   */
  public void toCsv(String path, boolean appendYear) throws IOException {
    CSVWriter writer = new CSVWriter(new FileWriter(path), ',',
        CSVWriter.NO_QUOTE_CHARACTER);

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
      // header[count++] = l.getName();
    }
    writer.writeNext(header);

    String[] line = null;
    LatLng p = null;
    for (Sample s : getSamples()) {
      p = s.getPoint();
      String cellData;
      if (appendYear) {
        cellData = String.format("%s-%d,%f,%f", s.getName(), s.getYear(), p
            .getLongitude(), p.getLatitude());
      } else {
        cellData = String.format("%s,%f,%f", s.getName(), p.getLongitude(), p
            .getLatitude());
      }

      for (Layer l : getLayers()) {
        cellData += "," + getData(s, l);
      }
      line = cellData.split(",");
      writer.writeNext(line);
    }
    writer.close();
  }

  public void toCsv(String path, SwdSpec spec) throws IOException {
    toCsv(path, spec, false);
  }

  public void toCsv(String path, SwdSpec filter, boolean appendYear)
      throws IOException {
    CSVWriter writer = new CSVWriter(new FileWriter(path), ',',
        CSVWriter.NO_QUOTE_CHARACTER);
    StringBuilder header = new StringBuilder();
    header.append("species,dd long,dd lat");
    List<String> layerNames = filter.getLayerNames();
    Collections.sort(layerNames);
    for (String name : layerNames) {
      header.append(","
          + (name.endsWith(".asc") ? name.replace(".asc", "") : name));
    }
    writer.writeNext(header.toString().split(","));
    StringBuilder csv = new StringBuilder();
    String sName;
    double lat, lng;
    for (Sample s : getSamples()) {
      csv = new StringBuilder();
      sName = s.getName();
      lat = s.getPoint().getLatitude();
      lng = s.getPoint().getLongitude();
      csv.append(appendYear ? String.format("%s-%d,%f,%f", sName, s.getYear(),
          lat, lng) : String.format("%s,%f,%f", sName, lat, lng));
      for (String name : layerNames) {
        Layer l = filter.getLayer(name, s);
        if (l == null) {
          continue;
        }
        csv.append("," + getData(s, l));
      }
      writer.writeNext(csv.toString().split(","));
    }
    writer.close();
  }

  @Override
  public String toString() {
    return sampleData.toString();
  }

  public String toTempCsv(SwdSpec filter) throws IOException {
    String path = File.createTempFile("swd", ".csv").getPath();
    toCsv(path, filter, false);
    return path;
  }

  public String toTempCsv(SwdSpec filter, boolean appendYear)
      throws IOException {
    String path = File.createTempFile("swd", ".csv").getPath();
    toCsv(path, filter, appendYear);
    return path;
  }

}
