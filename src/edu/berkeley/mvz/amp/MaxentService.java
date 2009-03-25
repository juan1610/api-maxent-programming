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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import density.Getval;
import density.MaxEnt;
import density.tools.RandomSample;
import edu.berkeley.mvz.amp.Layer.LayerProvider;
import edu.berkeley.mvz.amp.MaxentResults.ResultBuilder;
import edu.berkeley.mvz.amp.MaxentRun.Option;
import edu.berkeley.mvz.amp.MaxentRun.RunConfig;
import edu.berkeley.mvz.amp.MaxentRun.RunType;

/**
 * This class can be used to execute {@link MaxentRun}s synchronously or
 * asynchronously.
 * 
 * Note: This class is a singleton.
 * 
 */
public enum MaxentService {

  INSTANCE;

  /**
   * This interface allows clients to execute MaxEnt runs asynchronously.
   * 
   */
  public static interface AsyncRunCallback {
    /**
     * Invoked if the async run fails.
     * 
     * @param t cause of failure
     */
    public void onFailure(Throwable t);

    /**
     * Invoked when a run completes.
     * 
     * @param run the run that completed
     * @param results the results
     */
    public void onSuccess(MaxentRun run, MaxentResults results);
  };

  /**
   * A class that can be used to encapsulate MaxEnt exceptions.
   * 
   */
  public static class MaxEntException extends Exception {
    private static final long serialVersionUID = 7923750259414643302L;

    public MaxEntException(Error e) {
      super(e);
    }

    public MaxEntException(Exception e) {
      super(e);
    }

    public MaxEntException(String msg, Exception e) {
      super(msg, e);
    }

    public MaxEntException(Throwable t) {
      super(t);
    }
  }

  private static Logger log = Logger.getLogger(MaxentService.class);

  /**
   * Creates and returns a new background SWD run.
   * 
   * @param n number of background points
   * @param layers background layers
   * @return background SWD run
   */
  public static MaxentRun createSwdRun(int n, List<Layer> layers) {
    if (n < 1) {
      throw new IllegalArgumentException("n can't be negative");
    }
    return new RunConfig(RunType.BACKGROUND_SWD).environmentLayers(layers).add(
        Option.BACKGROUNDPOINTS, n + "").build();
  }

  /**
   * Creates and returns a new SWD run.
   * 
   * @param samples the samples
   * @param layers the layers
   * @return SWD run
   */
  public static MaxentRun createSwdRun(List<Sample> samples, List<Layer> layers) {
    return new RunConfig(RunType.SWD).samples(samples)
        .environmentLayers(layers).build();
  }

  /**
   * Executes a MaxEnt run. This method blocks until the run completes or an
   * exception is thrown. To run asynchronously, use <code>executeAsync</code>.
   * 
   * @param run the run to execute
   * @return results
   * @throws MaxEntException
   */
  public static MaxentResults execute(MaxentRun run) throws MaxEntException {
    if (run == null) {
      throw new NullPointerException("The run options were null");
    }
    long start = System.currentTimeMillis();
    MaxentResults results = dispatch(run);
    log.info(String.format("%s runtime: %f sec ", run.getType(), (System
        .currentTimeMillis() - start) / 1000.0));
    return results;
  }

  /**
   * Executes a MaxEnt run asynchronously.
   * 
   * @param run the run to execute
   * @param cb the async callback
   */
  public static void executeAsync(final MaxentRun run, final AsyncRunCallback cb) {
    if (run == null) {
      throw new NullPointerException("The run options were null");
    }
    if (cb == null) {
      throw new NullPointerException("The callback was null");
    }
    final long start = System.currentTimeMillis();
    new Thread(new Runnable() {
      public void run() {
        try {
          MaxentResults results = dispatch(run);
          cb.onSuccess(run, results);
        } catch (MaxEntException e) {
          cb.onFailure(e);
        }
        log.info(String.format("%s runtime: %f sec ", run.getType(), (System
            .currentTimeMillis() - start) / 1000.0));
      }
    }).start();
  }

  private static String[] backgroundSwdArgv(RunConfig cb, int n,
      List<Layer> layers) {
    String[] argv = new String[layers.size() + 1];
    argv[0] = Integer.toString(n);
    int count = 1;
    for (Layer l : layers) {
      argv[count++] = l.getPath();
    }
    return argv;
  }

  private static MaxentResults dispatch(MaxentRun run) throws MaxEntException {
    ResultBuilder builder = null;
    try {
      RunConfig config = new RunConfig(run).add(Option.AUTORUN).add(
          Option.INVISIBLE);
      String dir = run.getOption(Option.OUTPUTDIRECTORY);
      if (dir != null) {
        dir = dir.endsWith(File.separator) ? dir : dir + File.separator;
        if (!run.getEnvironmentLayers().isEmpty()) {
          String d = String.format("%s%s", dir, "EnvLayers");
          config.add(Option.ENVIRONMENTALLAYERS, symlinkLayers(d, run
              .getEnvironmentLayers()));
        }
        if (!run.getProjectionLayers().isEmpty()) {
          String d = String.format("%s%s", dir, "ProjLayers");
          config.add(Option.PROJECTIONLAYERS, symlinkLayers(d, run
              .getProjectionLayers()));
        }
      }
      MaxentRun actualRun = config.build();
      switch (actualRun.getType()) {
      case MODEL:
        builder = new ResultBuilder(run.getOption(Option.OUTPUTDIRECTORY));
        String replicates = run.getOption(Option.REPLICATES);
        if (replicates != null) {
          int runCount = Integer.parseInt(replicates);
          builder.runCount(runCount);
        }
        MaxEnt.main(actualRun.asArgv());
        break;
      case BACKGROUND_SWD:
        builder = new ResultBuilder();
        builder.samplesWithData(dispatchBackgroundSwd(actualRun));
        break;
      case SWD:
        builder = new ResultBuilder();
        builder.samplesWithData(dispatchSwd(actualRun));
        break;
      case PROJECTION:
        break;
      }
      return builder.build();
    } catch (Exception e) {
      throw new MaxEntException(e);
    }
  }

  private static SamplesWithData dispatchBackgroundSwd(MaxentRun run)
      throws MaxEntException, IOException {
    String value = run.getOption(Option.BACKGROUNDPOINTS);
    int n;
    try {
      n = Integer.parseInt(value);
    } catch (Exception e) {
      throw new MaxEntException(String.format("%s invalid: %s - %s",
          Option.BACKGROUNDPOINTS, value, e), e);
    }
    // Configures background SWD run:
    RunConfig options = new RunConfig(RunType.BACKGROUND_SWD);
    String[] argv = backgroundSwdArgv(options, n, run.getEnvironmentLayers());
    // Redirects standard output to SWD file:
    File swdout = File.createTempFile("background-swd", ".csv");
    FileOutputStream fos = new FileOutputStream(swdout);
    PrintStream ps = new PrintStream(fos);
    System.setOut(ps);
    // Dispatches to MaxEnt to get SWD:
    RandomSample.main(argv);
    System.setOut(System.out);
    // Loads data from background SWD file that MaxEnt just created:
    final Map<String, Layer> layerNames = new HashMap<String, Layer>();
    for (Layer l : run.getEnvironmentLayers()) {
      layerNames.put(l.getFilename(), l);
    }
    return SamplesWithData.fromCsv(swdout.getPath(), new LayerProvider() {
      public Layer getLayerByFilename(String filename) {
        return layerNames.get(filename);
      }
    });
  }

  private static SamplesWithData dispatchSwd(MaxentRun run) throws Exception {

    SamplesWithData swd = null;
    RunConfig options = new RunConfig(RunType.SWD);
    options.add(Option.SAMPLESFILE, Sample.toTempCsv(run.getSamples()));
    String[] argv = swdArgv(options, run.getEnvironmentLayers());
    File swdout = File.createTempFile("swd", ".csv");
    FileOutputStream fos = new FileOutputStream(swdout);
    PrintStream ps = new PrintStream(fos);
    // Redirects standard output to SWD file:
    System.setOut(ps);
    // Dispatches to MaxEnt to get SWD:
    Getval.main(argv);
    // Restores standard output:
    System.setOut(System.out);
    final Map<String, Layer> layerNames = new HashMap<String, Layer>();
    for (Layer l : run.getEnvironmentLayers()) {
      layerNames.put(l.getFilename(), l);
    }
    // Loads SWD file that MaxEnt just created:
    swd = SamplesWithData.fromCsv(swdout.getPath(), new LayerProvider() {
      public Layer getLayerByFilename(String filename) {
        return layerNames.get(filename);
      }
    });
    return swd;
  }

  private static String[] swdArgv(RunConfig cb, List<Layer> layers) {
    String samplesPath = cb.build().getOption(Option.SAMPLESFILE);
    String[] argv = new String[layers.size() + 1];
    argv[0] = samplesPath;
    int count = 1;
    for (Layer l : layers) {
      argv[count++] = l.getPath();
    }
    return argv;
  }

  private static String[] swdArgv(String samplesFile, List<Layer> layers) {
    String[] argv = new String[layers.size() + 1];
    argv[0] = samplesFile;
    int count = 1;
    for (Layer l : layers) {
      argv[count++] = l.getPath();
    }
    return argv;
  }

  private static String symlinkLayers(String dir, List<Layer> layers)
      throws IOException {
    new File(dir).mkdir();
    File d = new File(dir);
    for (Layer l : layers) {
      String link = String.format("ln -s %s %s/%s", l.getPath(), d.getPath(), l
          .getFilename());
      Runtime.getRuntime().exec(link);
    }
    return d.getPath();
  }
}