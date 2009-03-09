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

/**
 * Unit tests for {@link SamplesWithData}.
 * 
 */
public class SamplesWithDataTest {
  private static Logger log = Logger.getLogger(SamplesWithDataTest.class);

  // public static SamplesWithData getTestSwd() throws IOException {
  // List<Sample> samples = Sample.fromCsv(path("samples.csv"), 2009);
  // List<Layer> layers = new ArrayList<Layer>(LayerTest.getTestLayerMap()
  // .values());
  // return MaxEntService.swd(samples, layers);
  // }

  private static String path(String name) {
    return MaxentServiceTest.class.getResource(name).getPath();
  }

  @Test
  public void builder() {
    String path = SamplesWithDataTest.class.getResource("samples.csv")
        .getPath();
    try {
      Sample.fromCsv(path, 2009);
    } catch (Exception e) {
      log.error(e);
      Assert.fail();
    }
    List<Layer> layers = new ArrayList<Layer>();
    String lpath = SamplesWithDataTest.class.getResource("cld6190_ann.asc")
        .getPath();
    layers.add(Layer.newInstance(LayerType.CLIMATE, "cld6190_ann.asc", 2009,
        lpath));
  }

  // @Test
  // public void equals() throws IOException {
  // SamplesWithData swd = getTestSwd();
  // SamplesWithData swd2 = SamplesWithData.fromCsv(path("maxent-swd.txt"),
  // new LayerProvider() {
  // public Layer getLayerByFilename(String filename) {
  // return LayerTest.getTestLayerMap().get(filename);
  // }
  // });
  // Assert.assertEquals(swd, swd2);
  // }
  //
  // @Test
  // public void toCsv() throws IOException {
  // List<Sample> samples = Sample.fromCsv(path("samples.csv"), 2009);
  // List<Layer> layers = new ArrayList<Layer>(LayerTest.getTestLayerMap()
  // .values());
  // SamplesWithData swd = MaxEntService.swd(samples, layers);
  // Assert.assertNotNull(swd.getSamples());
  // Assert.assertFalse(swd.getSamples().isEmpty());
  // Assert.assertNotNull(swd.getLayers());
  // Assert.assertFalse(swd.getLayers().isEmpty());
  //
  // // Writes swd to csv:
  // String path = File.createTempFile("toCsv", ".csv").getPath();
  // swd.toCsv(path);
  // log.info("wrote SWD to " + path);
  //
  // // Reads swd from csv:
  // SamplesWithData swd2 = SamplesWithData.fromCsv(path, new LayerProvider() {
  // public Layer getLayerByFilename(String filename) {
  // return LayerTest.getTestLayerMap().get(filename);
  // }
  // });
  // log.info("loaded SWD to " + path);
  //
  // // Boths swds should be equal.
  // log.info("swd1: " + swd);
  // log.info("swd2: " + swd2);
  // Assert.assertEquals(swd, swd2);
  // }
}
