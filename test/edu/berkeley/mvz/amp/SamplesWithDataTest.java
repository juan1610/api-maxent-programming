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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;

import edu.berkeley.mvz.amp.Layer.LayerType;
import edu.berkeley.mvz.amp.MaxentService.MaxEntException;
import edu.berkeley.mvz.amp.SamplesWithData.SwdSpec;

/**
 * Unit tests for {@link SamplesWithData}.
 * 
 */
public class SamplesWithDataTest {
  private static Logger log = Logger.getLogger(SamplesWithDataTest.class);

  @Test
  public void builder() {
    String path = SamplesWithDataTest.class.getResource("samples.csv")
        .getPath();
    try {
      Sample.fromCsv(path);
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

  @Test
  public void csvFile() throws IOException, MaxEntException {
    List<Sample> samples = SampleTest.getTestSamples();
    final List<Layer> layers = LayerTest.getTestLayers();
    MaxentRun swdRun = MaxentService.createSwdRun(samples, layers);
    SamplesWithData swd = MaxentService.execute(swdRun).getSamplesWithData();
    String path = swd.toTempCsv(new SwdSpec() {
      private final String[] names = { "cld", "h_dem" };

      public Layer getLayer(String layerName, Sample sample) {
        for (Layer layer : layers) {
          if (!layer.getType().equals(LayerType.CLIMATE)) {
            continue;
          }
          if (!layer.getFilename().contains(layerName)) {
            continue;
          }
          return layer;
        }
        return null;
      }

      public List<String> getLayerNames() {
        List<String> result = new ArrayList<String>();
        for (String name : names) {
          result.add(name);
        }
        return result;
      }

    });
    log.info(path);
  }

  @Test
  public void toCsvWithSpec() {

  }

}
