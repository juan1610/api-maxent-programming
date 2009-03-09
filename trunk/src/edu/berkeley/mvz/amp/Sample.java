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
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * An immutable class that can be used to encapsulate information about a
 * sample. Not designed for inheritance which is prohibited.
 * 
 */
public class Sample implements Comparable<Sample> {

  /**
   * Loads samples from a MaxEnt samples CSV and returns them in a list. The
   * expected CSV format is:
   * 
   * Species,Long,Lat
   * 
   * @param path path to samples CSV file
   * @param sampleYear sample year
   * @return list of samples
   * @throws IOException problems reading path
   */
  public static List<Sample> fromCsv(String path, int sampleYear)
      throws IOException {
    CSVReader reader = new CSVReader(new FileReader(path));
    String[] line;
    List<Sample> samples = new ArrayList<Sample>();
    // Skips the header:
    reader.readNext();
    int SPECIES = 0;
    int LONG = 1;
    int LAT = 2;
    while ((line = reader.readNext()) != null) {
      samples.add(Sample.newInstance(line[SPECIES], sampleYear, LatLng
          .newInstance(Double.parseDouble(line[LAT]), Double
              .parseDouble(line[LONG]))));
    }
    return samples;
  }

  /**
   * Returns a new sample instance. A name, year, and point are required.
   * 
   * @param name sample name
   * @param year sample year
   * @param point sample point
   * @return sample instance
   */
  public static Sample newInstance(String name, int year, LatLng point) {
    String format = "%s. The value '%s' is invalid.";
    if (name == null || name.length() < 1) {
      throw new IllegalArgumentException(String.format(format,
          "A name is required", name));
    }
    if (point == null) {
      throw new IllegalArgumentException(String.format(format,
          "A point is required", point));
    }
    return new Sample(name, year, point);
  }

  public static String toTempCsv(List<Sample> samples) throws IOException {
    String path = File.createTempFile("samples", ".csv").getPath();
    CSVWriter writer = new CSVWriter(new FileWriter(path), ',');
    String[] line = { "species", "dd long", "dd lat" };
    writer.writeNext(line);
    for (Sample s : samples) {
      line = String.format("%s,%f,%f", s.getName(),
          s.getPoint().getLongitude(), s.getPoint().getLatitude()).split(",");
      writer.writeNext(line);
    }
    writer.close();
    return path;
  }

  /**
   * Writes the list of sample to file in MaxEnt CSV format:
   * 
   * Species,Long,Lat
   * 
   * @param path path to write CSV file to
   * @param samples samples to write
   * @throws IOException problems writing to path
   */
  public static void toCsv(String path, List<Sample> samples)
      throws IOException {
    CSVWriter writer = new CSVWriter(new FileWriter(path), ',');
    String[] line = { "species", "dd long", "dd lat" };
    writer.writeNext(line);
    for (Sample s : samples) {
      line = String.format("%s,%f,%f", s.getName(),
          s.getPoint().getLongitude(), s.getPoint().getLatitude()).split(",");
      writer.writeNext(line);
    }
    writer.close();
  }

  private volatile int hashCode;

  private final String name;

  private final int year;

  private final LatLng point;

  private Sample(String name, int year, LatLng point) {
    this.name = name;
    this.year = year;
    this.point = point;
  }

  /**
   * Sorts by sample name.
   */
  public int compareTo(Sample other) {
    return String.CASE_INSENSITIVE_ORDER.compare(name, other.getName());
  }

  /**
   * Samples are equal if their name, year, and point are equal.
   */
  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof Sample)) {
      return false;
    }
    Sample s = (Sample) other;
    boolean b = name.equals(s.name) && year == s.year && point.equals(s.point);
    return b;
  }

  /**
   * Returns the sample name.
   * 
   * @return sample name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the sample point.
   * 
   * @return sample point
   */
  public LatLng getPoint() {
    return point;
  }

  /**
   * Returns the sample year.
   * 
   * @return sample year
   */
  public int getYear() {
    return year;
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = 17;
      result = 31 * result + name.hashCode();
      result = 31 * result + year;
      result = 31 * result + point.hashCode();
      hashCode = result;
    }
    return result;
  }

  @Override
  public String toString() {
    return String.format("[name=%s year=%d point=%s]", name, year, point);
  }
}
