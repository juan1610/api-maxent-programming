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
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * Unit tests for {@link MaxEntService}.
 * 
 */
public class MaxEntServiceTest {

  private static Logger log = Logger.getLogger(MaxEntServiceTest.class);

  static {
    log.info(String.format("%s starting", MaxEntServiceTest.class.getName()));
  }

  @Test
  public void backgroundSwd() throws IOException {
    List<Layer> layers = LayerTest.getTestLayers();
    int n = 10000;
    SamplesWithData bswd = MaxEntService.backgroundSwd(n, layers);
    Assert.assertEquals(n, bswd.size());
  }

  @Test
  public void swd() {
    try {
      List<Sample> samples = SampleTest.getTestSamples();
      List<Layer> layers = LayerTest.getTestLayers();
      SamplesWithData swd = MaxEntService.swd(samples, layers);
      Assert.assertNotNull(swd);
      Assert.assertTrue(swd.size() > 0 && swd.size() <= samples.size());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
