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

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Unit tests for {@link LatLng}.
 * 
 */
public class LatLngUnitTests {

  private static boolean throwsException(double lat, double lng) {
    try {
      LatLng.newInstance(lat, lng);
      return false;
    } catch (Exception e) {
      return true;
    }
  }

  @Test
  public void compareTo() {
    LatLng p1 = LatLng.newInstance(90, -180);
    LatLng p2 = LatLng.newInstance(-90, 180);
    LatLng p3 = LatLng.newInstance(90, -180);
    LatLng p4 = LatLng.newInstance(-90, -180);

    Assert.assertEquals(1, p1.compareTo(p2));
    Assert.assertEquals(-1, p2.compareTo(p1));

    Assert.assertEquals(0, p1.compareTo(p3));
    Assert.assertEquals(0, p3.compareTo(p1));

    Assert.assertEquals(1, p1.compareTo(p2));
    Assert.assertEquals(1, p2.compareTo(p4));
    Assert.assertEquals(1, p1.compareTo(p4));
  }

  @Test
  public void equals() {
    double lat = 90, lng = 180;
    LatLng point = LatLng.newInstance(lat, lng);
    Map<LatLng, String> map = new HashMap<LatLng, String>();
    map.put(point, "point");
    Assert.assertEquals("point", map.get(LatLng.newInstance(lat, lng)));
  }

  @Test
  public void newInstance() {
    Assert.assertTrue(throwsException(-100, 180));
    Assert.assertTrue(throwsException(90, -200));
    Assert.assertTrue(throwsException(-100, -200));
    Assert.assertFalse(throwsException(-90, -180));
    Assert.assertFalse(throwsException(90, 180));
    Assert.assertFalse(throwsException(0, 0));
  }
}
