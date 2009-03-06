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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import density.Getval;
import edu.berkeley.mvz.amp.Config.ConfigBuilder;
import edu.berkeley.mvz.amp.Config.Option;
import edu.berkeley.mvz.amp.SamplesWithData.SwdBuilder;

/**
 * This singleton class is used to access MaxEnt as a service. Clients must
 * invoke the <code>initService</code> method before invoking any service
 * methods. Doing so results in an {@link IllegalStateException}.
 * 
 */
public enum MaxEntService {

  INSTANCE;

  private static Logger log = Logger.getLogger(MaxEntService.class);
  private static List<Layer> layers = new ArrayList<Layer>();
  private static Map<String, Layer> layerMap = new HashMap<String, Layer>();

  public static void fit(Config config, SamplesWithData swd) {
    // TODO(eighty)
  }

  /**
   * Returns a layer by it's filename.
   * 
   * @param filename name of the layer
   * @return the layer
   */
  public static Layer getLayer(String filename) {
    return layerMap.get(filename);
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
    MaxEntService.layers.addAll(layers);
    for (Layer l : layers) {
      layerMap.put(new File(l.getPath()).getName(), l);
    }

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
      String[] argv = samplesWithDataArgv(cb);

      // Redirects standard output to SWD file:
      File swdout = File.createTempFile("swd", ".csv");
      FileOutputStream fos = new FileOutputStream(swdout);
      PrintStream ps = new PrintStream(fos);
      System.setOut(ps);

      // Dispatches to MaxEnt to get SWD:
      Getval.main(argv);

      swd = SamplesWithData.fromCsv(swdout.getPath());
    } catch (Exception e) {
      log.error(e);
      swd = sb.build();
    }
    return swd;
  }

  private static String[] samplesWithDataArgv(ConfigBuilder cb) {
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