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
import edu.berkeley.mvz.amp.SamplesWithData.SwdBuilder;

/**
 * Unit tests for {@link SamplesWithData}.
 * 
 */
public class SamplesWithDataUnitTests {
  private static Logger log = Logger.getLogger(SamplesWithDataUnitTests.class);

  @Test
  public void builder() {
    String path = SamplesWithDataUnitTests.class.getResource(
        "bradypus-samples.csv").getPath();
    List<Sample> samples = null;
    try {
      samples = Sample.fromCsv(path, 2009);
    } catch (Exception e) {
      log.error(e);
      Assert.fail();
    }
    List<Layer> layers = new ArrayList<Layer>();
    String lpath = SamplesWithDataUnitTests.class.getResource(
        "cld6190_ann_layer.asc").getPath();
    layers.add(Layer.newInstance(LayerType.CLIMATE, "cld6190_ann_layer.asc",
        2009, lpath));
    SwdBuilder builder = new SwdBuilder();

  }
}
