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
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.junit.Test;

public class ApiTest {
  private static Logger log = Logger.getLogger(ApiTest.class);

  public static String path(String name) {
    return MaxEntServiceTest.class.getResource(name).getPath();
  }

  @Test
  public void swdUseCase() throws IOException {
    List<Layer> layers = LayerTest.getTestLayers();
    List<Sample> samples = SampleTest.getTestSamples();
    SamplesWithData swd = MaxEntService.swd(samples, layers);
    log.info("SWD size: " + swd.size());
    for (Entry<Sample, CellData> entry : swd.getData()) {
      Sample sample = entry.getKey();
      CellData data = entry.getValue();
      log.info(String.format("Sample %s Data %s", sample.getPoint(), data));
    }
    String path = File.createTempFile("swdtest", ".csv").getPath();
    swd.toCsv(path);
  }
}
