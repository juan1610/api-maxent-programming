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

import edu.berkeley.mvz.amp.MaxentService.MaxEntException;

/**
 * Unit tests for {@link MaxentService}.
 * 
 */
public class MaxentServiceTest {

  private static Logger log = Logger.getLogger(MaxentServiceTest.class);

  static {
    log.info(String.format("%s starting", MaxentServiceTest.class.getName()));
  }

  @Test
  public void swd() throws MaxEntException, IOException {
    List<Sample> samples = SampleTest.getTestSamples();
    Assert.assertEquals(10, samples.size());
    List<Layer> layers = LayerTest.getTestLayers();
    Assert.assertEquals(2, layers.size());
    MaxentRun swdRun = MaxentService.createSwdRun(samples, layers);
    SamplesWithData swd = MaxentService.execute(swdRun).getSamplesWithData();
    Assert.assertNotNull(swd);
    Assert.assertTrue(swd.size() == samples.size());
  }
}
