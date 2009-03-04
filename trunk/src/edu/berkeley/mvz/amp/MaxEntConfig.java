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
import java.util.HashMap;
import java.util.Map;

/**
 * An immutable object that encapsulates a set of MaxEnt command line parameter
 * options. It is used to configure MaxEnt runs.
 * 
 * A MaxEntConfig object cannot contain duplicate options. Each option has at
 * most one value. It is not designed for inheritance and is therefore
 * prohibited. Because there are more than a handful of parameters, it uses the
 * builder pattern to construct instances.
 * 
 * Example usage:
 * 
 * Builder builder = new Builder("/path/to/output/directory");
 * builder.addOption(Option.RANDOM_SEED, "20").addOption(Option.JACK_KNIFE);
 * MaxEntConfig config = builder.build();
 */
public class MaxEntConfig {

  /**
   * A builder pattern class used to build a MaxEntConfig instance.
   */
  public static class Builder {
    private final Map<CommandLineOption, String> options = new HashMap<CommandLineOption, String>();

    /**
     * Constructs a new instance of builder given the output directory where
     * MaxEnt will write model output files to.
     * 
     * @param outputDirectory path to an output directory
     */
    public Builder(String outputDirectory) {
      File f = new File(outputDirectory);
      if (!f.exists() || !f.canWrite() || !f.isDirectory()) {
        throw new IllegalArgumentException("Bad output directory: " + f);
      }
      options.put(CommandLineOption.OUTPUT_DIRECTORY, outputDirectory);
    }

    /**
     * Adds an {@link CommandLineOption} to the configuration.
     * 
     * @param option the option to add
     * @return the builder instance
     */
    public Builder addOption(CommandLineOption option) {
      if (option == null) {
        throw new IllegalArgumentException("Options can't be null");
      }
      options.put(option, "true");
      return this;
    }

    /**
     * Adds an {@link CommandLineOption} and a corresponding value to the
     * configuration.
     * 
     * @param option the command line option
     * @param value the option value
     * @return the builder instance
     */
    public Builder addOption(CommandLineOption option, String value) {
      if (option == null || value == null) {
        throw new IllegalArgumentException("Options and values can't be null");
      }
      options.put(option, value);
      return this;
    }

    /**
     * Builds and returns the {@link MaxEntConfig} object.
     * 
     * @return the {@link MaxEntConfig} object
     */
    public MaxEntConfig build() {
      return new MaxEntConfig(options);
    }

    /**
     * Returns the builder's map of options.
     * 
     * @return options
     */
    public Map<CommandLineOption, String> getOptions() {
      return new HashMap<CommandLineOption, String>(options);
    }

    @Override
    public String toString() {
      return MaxEntConfig.toString(options);
    }
  }

  /**
   * Enumeration of MaxEnt command line options.
   */
  public static enum CommandLineOption {
    RANDOM_SEED("randomseed", ""), JACK_KNIFE("jackknife", "-J"),
    OUTPUT_DIRECTORY("outputdirectory", "-o");

    private final String flag, abbreviation;

    CommandLineOption(String flag, String abbreviation) {
      this.flag = flag;
      this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
      return abbreviation;
    }

    public String getFlag() {
      return flag;
    }
  }

  private static String toString(Map<CommandLineOption, String> options) {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (CommandLineOption o : options.keySet()) {
      sb.append(String.format("%s=%s, ", o, options.get(o)));
    }
    sb.append("]");
    return sb.toString();
  }

  private final Map<CommandLineOption, String> options;

  private MaxEntConfig(Map<CommandLineOption, String> options) {
    this.options = new HashMap<CommandLineOption, String>(options);
  }

  /**
   * Returns the options for this configuration.
   * 
   * @return options
   */
  public Map<CommandLineOption, String> getOptions() {
    return new HashMap<CommandLineOption, String>(options);
  }

  @Override
  public String toString() {
    return toString(options);
  }
}
