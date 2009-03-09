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
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class is used to encapsulate the results of a MaxEnt run which may
 * include outputs for each modeled species.
 * 
 */
public class MaxEntResults {

  /**
   * This class is used to build MaxEnt results.
   * 
   */
  public static class ResultBuilder {

    private String outputDir;

    private SamplesWithData swd;

    /**
     * Constructs a result builder.
     */
    public ResultBuilder() {
    }

    /**
     * Constructs a result builder that builds results from the output
     * directory.
     * 
     * @param outputDir directory from which results are built
     */
    public ResultBuilder(String outputDir) {
      File f = new File(outputDir);
      if (!f.exists() || !f.canRead()) {
        throw new IllegalArgumentException("Bad output dir: " + outputDir);
      }
      this.outputDir = outputDir;
    }

    /**
     * Builds and returns a new MaxEnt results object.
     * 
     * @return MaxEnt results
     */
    public MaxEntResults build() {
      return new MaxEntResults(this);
    }

    /**
     * Adds samples with data to builder.
     * 
     * @param swd the samples with data
     * @return builder
     */
    public ResultBuilder samplesWithData(SamplesWithData swd) {
      this.swd = swd;
      return this;
    }
  }

  private final Map<String, MaxEntOutputs> outputs = new HashMap<String, MaxEntOutputs>();

  private final SamplesWithData swd;

  private final String dir;

  private Set<String> names;

  private MaxEntResults(ResultBuilder builder) {
    swd = builder.swd;
    dir = builder.outputDir;
    if (dir != null) {
      for (String s : getSpeciesNames()) {
        outputs.put(s, MaxEntOutputs.newInstance(dir, s));
      }
    }
  }

  /**
   * Returns a mapping of species names to their MaxEnt results.
   * 
   * @return mapping of species names to MaxEnt results
   */
  public Map<String, MaxEntOutputs> getOutputs() {
    return new HashMap<String, MaxEntOutputs>(outputs);
  }

  /**
   * Returns the samples with data associated with these results.
   * 
   * @return samples with data
   */
  public SamplesWithData getSamplesWithData() {
    return swd;
  }

  /**
   * Returns the set of species names for which MaxEnt results are available.
   * 
   * @return set of species names with available results.
   */
  public Set<String> getSpeciesNames() {
    if (names == null) {
      names = new HashSet<String>();
      for (File f : new File(dir).listFiles(new FilenameFilter() {
        public boolean accept(File dir, String name) {
          return name.endsWith(".lambdas");
        }
      })) {
        names.add(f.getName().split(".lambdas")[0]);
      }
    }
    return new HashSet<String>(names);
  }

  /**
   * Returns the size of these results.
   * 
   * @return size of results
   */
  public int size() {
    return outputs.size();
  }
}
