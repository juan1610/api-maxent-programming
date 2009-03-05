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
      options.put(CommandLineOption.OUTPUTDIRECTORY, outputDirectory);
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
    APPLYTHRESHOLDRULE(
        "applythresholdrule",
        "",
        "For each output grid, use the <rule> threshold rule to additionally make a thresholded version of the output grid. here <rule> should exactly match one of the rules in the description column of the threshold table in the .html outputs (for example, minimum training presence).."),
    AUTORUN("autorun", "-a",
        "Start immediately, without waiting for run button to be pushed."),
    BETAMULTIPLIER("betamultiplier", "-b",
        "Set the regularization multiplier (default 1.0)."),
    BETA_CATEGORICAL("beta_categorical", "",
        "Override default beta for categorical features."),
    BETA_HINGE("beta_hinge", "",
        "Override default beta for linear, quadratic and product features."),
    BETA_LQP("beta_lqp", "",
        "Override default beta for linear, quadratic and product features."),
    BETA_THRESHOLD("beta_threshold", "",
        "Override default beta for threshold features."),
    CONVERGENCETHRESHOLD("convergencethreshold", "-c",
        "Set the convergence threshold (default 1.0e-5)."),
    CUMULATIVE("cumulative", "-C",
        "Use cumulative rather than logistic output format."),
    DONTADDSAMPLESTOFEATURES(
        "dontaddsamplestofeatures",
        "-d",
        "By default the presence samples are added to the background data, to ensure that the constraints are all feasible. this flag prevents them from being added, for example if you give background data in swd format that already contains the presence samples.."),
    DONTCACHE(
        "dontcache",
        "",
        "By default, a compressed .mxe format version of each .asc file is cached in a directory called maxent.cache, to speed up future use of the file. dontcache turns off this feature.."),
    DONTEXTRAPOLATE(
        "dontextrapolate",
        "",
        "When projecting a model, give zero output rather than clamped value wherever clamping would have occurred."),
    DONTWRITECLAMPGRID(
        "dontwriteclampgrid",
        "",
        "By default, when a model is projected onto a different set of environmental variables, a grid and associated picture are written, showing where clamping occurs. this flag stops the grid and picture from being made.."),
    ENVIRONMENTALLAYERS("environmentallayers", "-e",
        "Location of environmental layers."),
    GRD("grd", "-H", "Set the output grid format to .grd."),
    INVISIBLE("invisible", "-z",
        "Do the run without showing the interface (requires autorun)."),
    JACKKNIFE("jackknife", "-J", "Turn on jackknifing."),
    MAXIMUMBACKGROUND("maximumbackground", "-B",
        "Set the maximum number of background points (default 10000)."),
    MAXIMUMITERATIONS("maximumiterations", "-m",
        "Set the maximum iterations (default 500)."),
    NOASKOVERWRITE("noaskoverwrite", "-r",
        "Don't ask before remodelling species with existing .lambdas file."),
    NOAUTOFEATURE("noautofeature", "-A", "Turn off auto feature selection."),
    NOHINGE("nohinge", "-h",
        "Turn off hinge features (even under auto features)."),
    NOLINEAR("nolinear", "-l",
        "Turn off linear features (even under auto features)."),
    NOOUTPUTGRIDS("nooutputgrids", "-x",
        "Don't write .asc or .grd output grids."),
    NOPLOTS("noplots", "", "Don't make roc plots or the jackknife bar chart."),
    NOPRODUCT("noproduct", "-p",
        "Turn off product features (even under auto features)."),
    NOQUADRATIC("noquadratic", "-q",
        "Turn off quadratic features (even under auto features)."),
    NOTHRESHOLD("nothreshold", "",
        "Turn off threshold features (even under auto features)."),
    NOTOOLTIPS("notooltips", "", "Don't show any tooltips."),
    NOWARNINGS(
        "nowarnings",
        "",
        "Don't give popup warnings about suspicious data in the presence localities file."),
    OUTPUTDIRECTORY("outputdirectory", "-o", "Location of output directory."),
    PICTURES("pictures", "-K", "Turn on picture making."),
    PROJECTIONLAYERS("projectionlayers", "-j",
        "Location of projection environmental layers."),
    RANDOMSEED(
        "randomseed",
        "",
        "Use a different random seed for each run (affects choice of random test points, random background points)."),
    RANDOMTESTPOINTS("randomtestpoints", "-X",
        "Set the random test percentage (default 0)."),
    RAW("raw", "-Q", "Use raw rather than logistic output format."),
    REMOVEDUPLICATES("removeduplicates", "-u",
        "Remove duplicates if multiple samples lie in the same grid cell."),
    RESPONSECURVES("responsecurves", "-P", "Turn on response curves."),
    RESPONSECURVESEXPONENT(
        "responsecurvesexponent",
        "",
        "When making response curves, plot the exponent of the exponential maxent model rather than the logistic prediction.."),
    SAMPLESFILE("samplesfile", "-s", "Location of samples file."),
    SKIPIFEXISTS("skipifexists", "-S",
        "Skip any species with existing .lambdas file."),
    TESTSAMPLESFILE("testsamplesfile", "-T", "Set the test samples file."),
    TOGGLELAYERSELECTED(
        "togglelayerselected",
        "-N",
        "Toggle selection of environmental layers whose names begin with=<prefix> (default: all selected)."),
    TOGGLELAYERTYPE(
        "togglelayertype",
        "-t",
        "Toggle continuous/categorical for environmental layers whose names begin with=<prefix> (default: all continuous)."),
    TOGGLESPECIESSELECTED(
        "togglespeciesselected",
        "-E",
        "Toggle selection of species whose names begin with=<prefix> (default: all selected)."),
    WRITEPLOTDATA("writeplotdata", "",
        "Write the raw data for response curves to .dat files in the output directory.");

    private final String flag, abbreviation, summary;

    CommandLineOption(String flag, String abbreviation, String summary) {
      this.flag = flag;
      this.abbreviation = abbreviation;
      this.summary = summary;
    }

    public String getAbbreviation() {
      return abbreviation;
    }

    public String getFlag() {
      return flag;
    }

    public String getSummary() {
      return this.summary;
    }

    @Override
    public String toString() {
      return flag;
    }
  }

  private static String toString(Map<CommandLineOption, String> options) {
    StringBuilder sb = new StringBuilder();
    sb.append("[ ");
    for (CommandLineOption o : options.keySet()) {
      sb.append(String.format("%s=%s ", o, options.get(o)));
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
