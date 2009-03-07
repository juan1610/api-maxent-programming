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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;

public class SampleTest {
  private static Logger log = Logger.getLogger(SampleTest.class);

  public static List<Sample> getTestSamples() throws IOException {
    String path = SamplesWithDataTest.class.getResource("samples.csv")
        .getPath();
    return Sample.fromCsv(path, 2009);
  }

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
  public void equals() {
    double lat = 90, lng = 180;
    LatLng point = LatLng.newInstance(lat, lng);
    Sample s1 = Sample.newInstance("foo", 0, point);
    Sample s2 = Sample.newInstance("foo", 0, point);
    Assert.assertEquals(s1, s2);

    Map<Sample, String> map1 = new HashMap<Sample, String>();
    map1.put(s1, "point");
    Map<Sample, String> map2 = new HashMap<Sample, String>();
    map2.put(Sample.newInstance("foo", 0, point), "point");
    Assert.assertEquals("point", map1.get(Sample.newInstance("foo", 0, point)));
    Assert.assertEquals(map1, map2);
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
