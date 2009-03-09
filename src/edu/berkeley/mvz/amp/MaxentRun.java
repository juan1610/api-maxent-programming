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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class can be used to configure a Maxent run by adding command line
 * options, samples, samples with data, environmental layers, and projection
 * layers. A run is executed by {@link MaxentService}.
 * 
 * Note: This class is immutable and is not designed for inheritance.
 * 
 */
public class MaxentRun {

  /**
   * Enumeration of MaxEnt command line options.
   */
  public static enum Option {
    APPLYTHRESHOLDRULE("applythresholdrule", "", "For each output grid, use the <rule> threshold rule to additionally make a thresholded version of the output grid. here <rule> should exactly match one of the rules in the description column of the threshold table in the .html outputs (for example, minimum training presence).."),
    AUTORUN("autorun", "-a", "Start immediately, without waiting for run button to be pushed."),
    BETAMULTIPLIER("betamultiplier", "-b", "Set the regularization multiplier (default 1.0)."),
    BETA_CATEGORICAL("beta_categorical", "", "Override default beta for categorical features."),
    BETA_HINGE("beta_hinge", "", "Override default beta for linear, quadratic and product features."),
    BETA_LQP("beta_lqp", "", "Override default beta for linear, quadratic and product features."),
    BETA_THRESHOLD("beta_threshold", "", "Override default beta for threshold features."),
    CONVERGENCETHRESHOLD("convergencethreshold", "-c", "Set the convergence threshold (default 1.0e-5)."),
    CUMULATIVE("cumulative", "-C", "Use cumulative rather than logistic output format."),
    DONTADDSAMPLESTOFEATURES("dontaddsamplestofeatures", "-d", "By default the presence samples are added to the background data, to ensure that the constraints are all feasible. this flag prevents them from being added, for example if you give background data in swd format that already contains the presence samples.."),
    DONTCACHE("dontcache", "", "By default, a compressed .mxe format version of each .asc file is cached in a directory called maxent.cache, to speed up future use of the file. dontcache turns off this feature.."),
    DONTEXTRAPOLATE("dontextrapolate", "", "When projecting a model, give zero output rather than clamped value wherever clamping would have occurred."),
    DONTWRITECLAMPGRID("dontwriteclampgrid", "", "By default, when a model is projected onto a different set of environmental variables, a grid and associated picture are written, showing where clamping occurs. this flag stops the grid and picture from being made.."),
    ENVIRONMENTALLAYERS("environmentallayers", "-e", "Location of environmental layers."),
    GRD("grd", "-H", "Set the output grid format to .grd."),
    INVISIBLE("invisible", "-z", "Do the run without showing the interface (requires autorun)."),
    JACKKNIFE("jackknife", "-J", "Turn on jackknifing."),
    MAXIMUMBACKGROUND("maximumbackground", "-B", "Set the maximum number of background points (default 10000)."),
    MAXIMUMITERATIONS("maximumiterations", "-m", "Set the maximum iterations (default 500)."),
    NOASKOVERWRITE("noaskoverwrite", "-r", "Don't ask before remodelling species with existing .lambdas file."),
    NOAUTOFEATURE("noautofeature", "-A", "Turn off auto feature selection."),
    NOHINGE("nohinge", "-h", "Turn off hinge features (even under auto features)."),
    NOLINEAR("nolinear", "-l", "Turn off linear features (even under auto features)."),
    NOOUTPUTGRIDS("nooutputgrids", "-x", "Don't write .asc or .grd output grids."),
    NOPLOTS("noplots", "", "Don't make roc plots or the jackknife bar chart."),
    NOPRODUCT("noproduct", "-p", "Turn off product features (even under auto features)."),
    NOQUADRATIC("noquadratic", "-q", "Turn off quadratic features (even under auto features)."),
    NOTHRESHOLD("nothreshold", "", "Turn off threshold features (even under auto features)."),
    NOTOOLTIPS("notooltips", "", "Don't show any tooltips."),
    NOWARNINGS("nowarnings", "", "Don't give popup warnings about suspicious data in the presence localities file."),
    OUTPUTDIRECTORY("outputdirectory", "-o", "Location of output directory."),
    PICTURES("pictures", "-K", "Turn on picture making."),
    PROJECTIONLAYERS("projectionlayers", "-j", "Location of projection environmental layers."),
    RANDOMSEED("randomseed", "", "Use a different random seed for each run (affects choice of random test points, random background points)."),
    RANDOMTESTPOINTS("randomtestpoints", "-X", "Set the random test percentage (default 0)."),
    RAW("raw", "-Q", "Use raw rather than logistic output format."),
    REMOVEDUPLICATES("removeduplicates", "-u", "Remove duplicates if multiple samples lie in the same grid cell."),
    RESPONSECURVES("responsecurves", "-P", "Turn on response curves."),
    RESPONSECURVESEXPONENT("responsecurvesexponent", "", "When making response curves, plot the exponent of the exponential maxent model rather than the logistic prediction.."),
    SAMPLESFILE("samplesfile", "-s", "Location of samples file."),
    SKIPIFEXISTS("skipifexists", "-S", "Skip any species with existing .lambdas file."),
    TESTSAMPLESFILE("testsamplesfile", "-T", "Set the test samples file."),
    TOGGLELAYERSELECTED("togglelayerselected", "-N", "Toggle selection of environmental layers whose names begin with=<prefix> (default: all selected)."),
    TOGGLELAYERTYPE("togglelayertype", "-t", "Toggle continuous/categorical for environmental layers whose names begin with=<prefix> (default: all continuous)."),
    TOGGLESPECIESSELECTED("togglespeciesselected", "-E", "Toggle selection of species whose names begin with=<prefix> (default: all selected)."),
    WRITEPLOTDATA("writeplotdata", "", "Write the raw data for response curves to .dat files in the output directory."),
    REPLICATES("replicates", "", "Used to do multiple runs for the same species."),
    BACKGROUNDPOINTS("n", "n", "Number of background points.");

    private final String flag, abbreviation, summary;

    Option(String flag, String abbreviation, String summary) {
      this.flag = flag;
      this.abbreviation = abbreviation;
      this.summary = summary;
    }

    /**
     * @return the option abbreviation
     */
    public String getAbbreviation() {
      return abbreviation;
    }

    /**
     * @return the option flag
     */
    public String getFlag() {
      return flag;
    }

    /**
     * @return the option summary
     */
    public String getSummary() {
      return this.summary;
    }

    @Override
    public String toString() {
      return flag;
    }
  }

  /**
   * This class is used to build a {@link MaxentRun}.
   * 
   */
  public static class RunConfig {
    private Map<Option, String> commandLine;
    private final RunType runType;
    private List<Sample> samples;
    private List<Layer> layers;
    private List<Layer> backgroundLayers;
    private List<Layer> projectionLayers;

    /**
     * Constructs a run configuration from an actual run.
     * 
     * @param run a MaxEnt run
     */
    public RunConfig(MaxentRun run) {
      this(run.type);
      layers = run.layers;
      backgroundLayers = run.backgroundLayers;
      projectionLayers = run.projectionLayers;
      samples = run.samples;
      for (Entry<Option, String> o : run.getOptions().entrySet()) {
        add(o.getKey(), o.getValue());
      }
    }

    /**
     * Constructs a run configuration of the given type.
     * 
     * @param type the run type
     */
    public RunConfig(RunType type) {
      if (type == null) {
        throw new NullPointerException("Run type was null");
      }
      this.runType = type;
    }

    /**
     * Adds an {@link Option} to the configuration.
     * 
     * @param option the option to add
     * @return the builder instance
     */
    public RunConfig add(Option option) {
      if (option == null) {
        throw new IllegalArgumentException("Options can't be null");
      }
      if (commandLine == null) {
        commandLine = new HashMap<Option, String>();
      }
      commandLine.put(option, "true");
      return this;
    }

    /**
     * Adds an {@link Option} and a corresponding value to the configuration.
     * 
     * @param option the command line option
     * @param value the option value
     * @return the builder instance
     */
    public RunConfig add(Option option, String value) {
      if (option == null || value == null) {
        throw new IllegalArgumentException("Options and values can't be null");
      }
      if (commandLine == null) {
        commandLine = new HashMap<Option, String>();
      }
      if (option.equals(Option.REPLICATES)
          || option.equals(Option.RANDOMTESTPOINTS)) {
        Integer.parseInt(value);
      }
      commandLine.put(option, value);
      return this;
    }

    /**
     * Adds a list of background layers to this config.
     * 
     * @param layers background layers
     * @return the run config
     */
    public RunConfig backgroundLayers(List<Layer> layers) {
      backgroundLayers = new ArrayList<Layer>(layers);
      return this;
    }

    /**
     * Builds and returns the {@link MaxentRun} object.
     * 
     * @return the {@link MaxentRun} object
     */
    public MaxentRun build() {
      return new MaxentRun(this);
    }

    /**
     * Adds layers to the configuration.
     * 
     * @param layers list of layers
     * @return the configuration
     */
    public RunConfig layers(List<Layer> layers) {
      this.layers = new ArrayList<Layer>(layers);
      return this;
    }

    /**
     * Adds a list of projection layers to this config.
     * 
     * @param layers projection layers
     * @return the run config
     */
    public RunConfig projectionLayers(List<Layer> layers) {
      projectionLayers = new ArrayList<Layer>(layers);
      return this;
    }

    /**
     * Adds samples to the configuration.
     * 
     * @param samples list of samples
     * @return the configuration
     */
    public RunConfig samples(List<Sample> samples) {
      this.samples = new ArrayList<Sample>(samples);
      return this;
    }

    @Override
    public String toString() {
      return MaxentRun.toString(commandLine);
    }
  }

  public static enum RunType {
    MODEL, PROJECTION, SWD, BACKGROUND_SWD;
  }

  private static String toString(Map<Option, String> options) {
    StringBuilder sb = new StringBuilder();
    sb.append("[ ");
    for (Option o : options.keySet()) {
      sb.append(String.format("%s=%s ", o, options.get(o)));
    }
    sb.append("]");
    return sb.toString();
  }

  private final Map<Option, String> commandLine;
  private final RunType type;
  private final ArrayList<Sample> samples;
  private final ArrayList<Layer> layers;
  private final ArrayList<Layer> backgroundLayers;
  private final ArrayList<Layer> projectionLayers;

  private MaxentRun(RunConfig options) {
    this.type = options.runType;
    this.commandLine = options.commandLine == null ? new HashMap<Option, String>()
        : new HashMap<Option, String>(options.commandLine);
    this.samples = options.samples == null ? new ArrayList<Sample>()
        : new ArrayList<Sample>(options.samples);
    this.layers = options.layers == null ? new ArrayList<Layer>()
        : new ArrayList<Layer>(options.layers);
    this.backgroundLayers = options.backgroundLayers == null ? new ArrayList<Layer>()
        : new ArrayList<Layer>(options.backgroundLayers);
    this.projectionLayers = options.projectionLayers == null ? new ArrayList<Layer>()
        : new ArrayList<Layer>(options.projectionLayers);
  }

  /**
   * Returns this configuration as an array of strings.
   * 
   * @param cb the config to convert
   * @return config converted to an array of string
   */
  public String[] asArgv() {
    Map<Option, String> opts = getOptions();
    String[] argv = new String[opts.size()];
    int count = 0;
    for (Option o : opts.keySet()) {
      if (opts.get(o).equals("true")) {
        argv[count++] = o.getAbbreviation();
      } else {
        argv[count++] = String.format("%s=%s", o.getFlag(), opts.get(o));
      }
    }
    return argv;
  }

  /**
   * @return the backgroundLayers
   */
  public ArrayList<Layer> getBackgroundLayers() {
    return backgroundLayers;
  }

  /**
   * @return the layers
   */
  public ArrayList<Layer> getLayers() {
    return layers;
  }

  /**
   * Returns the value for a configuration option.
   * 
   * @param option configuration option
   * @return value associated with the configuration option
   */
  public String getOption(Option option) {
    return commandLine.get(option);
  }

  /**
   * Returns the options for this configuration.
   * 
   * @return options
   */
  public Map<Option, String> getOptions() {
    return new HashMap<Option, String>(commandLine);
  }

  /**
   * @return the projectionLayers
   */
  public ArrayList<Layer> getProjectionLayers() {
    return projectionLayers;
  }

  /**
   * @return the samples
   */
  public ArrayList<Sample> getSamples() {
    return samples;
  }

  /**
   * @return the run type
   */
  public RunType getType() {
    return type;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("Type: %s ", type));
    for (Entry<Option, String> o : commandLine.entrySet()) {
      if (o.getValue().equals("true")) {
        sb.append(o.getKey().flag + " ");
      } else {
        sb.append(String.format("%s=%s ", o.getKey().getFlag(), o.getValue()));
      }
    }
    return sb.toString();
  }

}
