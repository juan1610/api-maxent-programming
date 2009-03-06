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

import static edu.berkeley.mvz.amp.Layer.LayerType.CLIMATE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * Unit tests for {@link MaxEntService}.
 * 
 */
public class MaxEntServiceUnitTests {

  private static String OUTPUT_PATH = "/Users/eighty/";

  private static Logger log = Logger.getLogger(MaxEntServiceUnitTests.class);

  private static String path(String name) {
    return MaxEntServiceUnitTests.class.getResource(name).getPath();
  }

  @Test
  public void samplesWithData() {
    try {
      List<Sample> samples = Sample.fromCsv(path("samples.csv"), 2009);
      List<Layer> layers = new ArrayList<Layer>();
      layers.add(Layer.newInstance(CLIMATE, "foo", 0, path("cld6190_ann.asc")));
      layers.add(Layer.newInstance(CLIMATE, "bar", 0, path("h_dem.asc")));

      try {
        MaxEntService.swd(samples, layers);
        Assert.fail();
      } catch (Exception e) {
        log.info(e);
      }

      MaxEntService.initService(OUTPUT_PATH, layers);
      SamplesWithData swd = MaxEntService.swd(samples, layers);
      log.info(swd);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
