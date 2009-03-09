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

/**
 * This class is used to encapsulate the MaxEnt output files associated with a
 * species. It is immutable and is not designed for inheritance.
 * 
 */
public class MaxEntOutputs {

  /**
   * Returns a new instance of MaxEnt outputs.
   * 
   * @param dir directory where the output files are
   * @param speciesName the name of the species
   * @return MaxEnt outputs
   */
  public static MaxEntOutputs newInstance(String dir, String speciesName) {
    File f = new File(dir);
    if (!f.exists() || !f.canRead()) {
      throw new IllegalArgumentException("Bad output directory");
    }
    if (speciesName == null || speciesName.length() < 1) {
      throw new IllegalArgumentException("Bad species name");
    }
    return new MaxEntOutputs(dir, speciesName);
  }

  private final String omissions, predictions, results, logistics, html,
      lambdas, log, omissionsPlot, rocPlot, dir, species;

  private MaxEntOutputs(String outputDir, String speciesName) {
    String fs = File.separator;
    this.dir = outputDir.endsWith(fs) ? outputDir : outputDir + fs;
    this.species = speciesName;
    omissions = String.format("%s%s%s", dir, species, "_omission.csv");
    predictions = String.format("%s%s", dir, species, "_samplePredictions.csv");
    results = String.format("%s%s", dir, "maxentResults.csv");
    lambdas = String.format("%s%s%s", dir, species, ".lambdas");
    log = String.format("%s%s", dir, "maxent.log");
    omissionsPlot = String.format("%s%s%s", dir, species, "_omission.png");
    rocPlot = String.format("%s%s%s", dir, species, "_roc.png");
    html = String.format("%s%s%s", dir, species, ".html");
    logistics = String.format("%s%s%s", dir, species, ".csv");
  }

  // TODO: should equality be a function of file contents or file names or both?
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof MaxEntOutputs)) {
      return false;
    }
    MaxEntOutputs other = (MaxEntOutputs) o;
    return dir.equals(other.dir) && species.equals(other.species)
        && omissions.equals(other.omissions)
        && predictions.equals(other.predictions)
        && results.equals(other.results) && lambdas.equals(other.lambdas)
        && log.equals(other.log) && omissionsPlot.equals(other.omissionsPlot)
        && rocPlot.equals(other.rocPlot) && html.equals(other.html)
        && logistics.equals(other.logistics);
  }

  // TODO
  // public int hashCode() {
  // }

  /**
   * @return the dir
   */
  public String getDir() {
    return dir;
  }

  /**
   * @return the html
   */
  public String getHtml() {
    return html;
  }

  /**
   * @return the lambdas
   */
  public String getLambdas() {
    return lambdas;
  }

  /**
   * @return the log
   */
  public String getLog() {
    return log;
  }

  /**
   * @return the logistics
   */
  public String getLogistics() {
    return logistics;
  }

  /**
   * @return the omissions
   */
  public String getOmissions() {
    return omissions;
  }

  /**
   * @return the omissionsPlot
   */
  public String getOmissionsPlot() {
    return omissionsPlot;
  }

  /**
   * @return the predictions
   */
  public String getPredictions() {
    return predictions;
  }

  /**
   * @return the results
   */
  public String getResults() {
    return results;
  }

  /**
   * @return the rocPlot
   */
  public String getRocPlot() {
    return rocPlot;
  }

  /**
   * @return the species
   */
  public String getSpecies() {
    return species;
  }

  @Override
  public String toString() {
    return String.format("Dir=%s species=%s", dir, species);
  }
}
