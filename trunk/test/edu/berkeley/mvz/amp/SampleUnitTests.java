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

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * Unit tests for {@link Sample}.
 * 
 */
public class SampleUnitTests {
  private static Logger log = Logger.getLogger(SampleUnitTests.class);

  private static boolean throwsException(String name, LatLng point) {
    try {
      Sample.newInstance(name, 0, point);
      return false;
    } catch (Exception e) {
      log.info(e);
      return true;
    }
  }

  @Test
  public void newInstance() {
    Assert.assertTrue(throwsException(null, null));
    Assert.assertTrue(throwsException(null, LatLng.newInstance(0, 0)));
    Assert.assertTrue(throwsException("sample", null));
    Assert.assertTrue(throwsException("", LatLng.newInstance(0, 0)));
    Assert.assertFalse(throwsException("sample", LatLng.newInstance(0, 0)));

    Sample s = Sample.newInstance("sample", 2009, LatLng.newInstance(0, 0));
    Assert.assertEquals("sample", s.getName());
    Assert.assertEquals(2009, s.getYear());
    Assert.assertEquals(LatLng.newInstance(0, 0), s.getPoint());
  }
}
