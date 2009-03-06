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
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;

import edu.berkeley.mvz.amp.Layer.LayerType;
import edu.berkeley.mvz.amp.Config.ConfigBuilder;
import edu.berkeley.mvz.amp.Config.Option;

/**
 * Unit tests for {@link Config}.
 * 
 */
public class MaxEntConfigTests {

  private static Logger log = Logger.getLogger(MaxEntConfigTests.class);

  @Test
  public void addOption() {
    ConfigBuilder b = new ConfigBuilder(".");
    b.addOption(Option.RANDOMSEED, "20");
    b.addOption(Option.JACKKNIFE);
    Assert.assertEquals(b.getOptions().get(Option.RANDOMSEED), "20");
    Assert.assertEquals(b.getOptions().get(Option.JACKKNIFE), "true");
    log.info(b.toString());

    b = new ConfigBuilder(".");
    try {
      b.addOption(null);
      Assert.fail();
    } catch (IllegalArgumentException e) {
      log.info(e);
    }

    b = new ConfigBuilder(".");
    try {
      b.addOption(null, "");
      Assert.fail();
    } catch (IllegalArgumentException e) {
      log.info(e);
    }

    b = new ConfigBuilder(".");
    try {
      b.addOption(null, null);
      Assert.fail();
    } catch (IllegalArgumentException e) {
      log.info(e);
    }

    b = new ConfigBuilder(".");
    try {
      b.addOption(Option.JACKKNIFE, null);
      Assert.fail();
    } catch (IllegalArgumentException e) {
      log.info(e);
    }
  }
  @Test
  public void asArgv() {
    ConfigBuilder b = new ConfigBuilder("/Users/eighty");
    b.addOption(Option.JACKKNIFE);
    b.addOption(Option.SAMPLESFILE, "/samples/file.csv");
    String path = LayerUnitTests.class.getResource("valid-header.asc")
        .getPath();
    List<Layer> layers = new ArrayList<Layer>();
    layers.add(Layer.newInstance(LayerType.CLIMATE, "valid-header", 0, path));
    b.addLayers(layers);
    String[] argv = Config.asArgv(b);
    StringBuilder sb = new StringBuilder();
    for (String s : argv) {
      sb.append(String.format("%s ", s));
    }
    String commandLine = sb.toString().trim();
    log.info(commandLine);
    Assert
        .assertEquals(
            "-J outputdirectory=/Users/eighty samplesfile=/samples/file.csv /Users/eighty/Projects/Workspace/AMP/bin/edu/berkeley/mvz/amp/valid-header.asc",
            commandLine);
  }

  @Test
  public void builderConstructor() {
    ConfigBuilder b;
    try {
      b = new ConfigBuilder("BOGUS PATH");
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }

    b = new ConfigBuilder(".");
    Assert.assertNotNull(b);
  }
}
