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
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import density.Getval;
import density.tools.RandomSample;
import edu.berkeley.mvz.amp.Config.ConfigBuilder;
import edu.berkeley.mvz.amp.Config.Option;
import edu.berkeley.mvz.amp.Layer.LayerProvider;
import edu.berkeley.mvz.amp.SamplesWithData.SwdBuilder;

/**
 * This singleton class can be used to access MaxEnt as a service.
 * 
 */
public enum MaxEntService {

  INSTANCE;

  private static Logger log = Logger.getLogger(MaxEntService.class);

  public static SamplesWithData backgroundSwd(int n, List<Layer> layers) {
    long start = System.currentTimeMillis();
    SamplesWithData swd = null;
    try {
      // Configures background SWD run:
      ConfigBuilder cb = new ConfigBuilder();
      cb.addLayers(layers);
      String[] argv = backgroundSwdArgv(cb, n);

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
      for (Layer l : layers) {
        layerNames.put(l.getFilename(), l);
      }
      swd = SamplesWithData.fromCsv(swdout.getPath(), new LayerProvider() {
        public Layer getLayerByFilename(String filename) {
          return layerNames.get(filename);
        }
      });
    } catch (Exception e) {
      log.error(e);
    }
    log.info(String.format(
        "MaxEntService.backgroundSwd runtime in seconds: %f", (System
            .currentTimeMillis() - start) / 1000.0));
    return swd;
  }
  public static void fit(Config config, SamplesWithData swd) {
    // TODO(eighty)
  }

  public static SamplesWithData getRandomBackgroundData(Config config,
      List<Layer> grids, int n) {
    SwdBuilder builder = new SwdBuilder();
    // TODO(eighty)
    return builder.build();
  }

  /**
   * Returns samples with data given a list of samples and layers.
   * 
   * @param samples list of samples
   * @param layers list of layers
   * @return samples with data
   */
  public static SamplesWithData swd(List<Sample> samples, List<Layer> layers) {
    long start = System.currentTimeMillis();
    SamplesWithData swd = null;
    SwdBuilder sb = new SwdBuilder();
    try {
      // Writes samples to file:
      File sout = File.createTempFile("samples", ".csv");
      Sample.toCsv(sout.getPath(), samples);

      // Configures SWD run:
      ConfigBuilder cb = new ConfigBuilder();
      cb.addOption(Option.SAMPLESFILE, sout.getPath());
      cb.addLayers(layers);
      String[] argv = swdArgv(cb);

      // Redirects standard output to SWD file:
      File swdout = File.createTempFile("swd", ".csv");
      FileOutputStream fos = new FileOutputStream(swdout);
      PrintStream ps = new PrintStream(fos);
      System.setOut(ps);

      // Dispatches to MaxEnt to get SWD:
      Getval.main(argv);
      System.setOut(System.out);

      // Loads samples with data from SWD CSV that MaxEnt just created:
      final Map<String, Layer> layerNames = new HashMap<String, Layer>();
      for (Layer l : layers) {
        layerNames.put(l.getFilename(), l);
      }
      swd = SamplesWithData.fromCsv(swdout.getPath(), new LayerProvider() {
        public Layer getLayerByFilename(String filename) {
          return layerNames.get(filename);
        }
      });
    } catch (Exception e) {
      log.error(e);
      swd = sb.build();
    }
    log.info(String.format("MaxEntService.swd runtime in seconds: %f", (System
        .currentTimeMillis() - start) / 1000.0));
    return swd;
  }

  private static String[] backgroundSwdArgv(ConfigBuilder cb, int n) {
    List<Layer> layers = cb.getLayers();
    String[] argv = new String[layers.size() + 1];
    argv[0] = Integer.toString(n);
    int count = 1;
    for (Layer l : layers) {
      argv[count++] = l.getPath();
    }
    return argv;
  }

  private static String[] swdArgv(ConfigBuilder cb) {
    List<Layer> layers = cb.getLayers();
    String samplesPath = cb.getOption(Option.SAMPLESFILE);
    String[] argv = new String[layers.size() + 1];
    argv[0] = samplesPath;
    int count = 1;
    for (Layer l : layers) {
      argv[count++] = l.getPath();
    }
    return argv;
  }
}