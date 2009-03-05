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

import org.apache.log4j.Logger;
import org.junit.Test;

import edu.berkeley.mvz.amp.Layer.LayerType;

/**
 * Unit tests for {@link Layer}.
 * 
 */
public class LayerUnitTests {

  private static Logger log = Logger.getLogger(LayerUnitTests.class);

  @Test
  public void testConstructor() {
    String path = LayerUnitTests.class.getResource("valid-header.asc").getPath();
    try {
      Layer.newInstance(null, "valid-header", 0, path);
      Assert.fail();
    } catch (Exception e) {
      log.info(e);
    }

    try {
      Layer.newInstance(LayerType.CLIMATE, null, 0, path);
      Assert.fail();
    } catch (Exception e) {
      log.info(e);
    }

    try {
      Layer.newInstance(LayerType.CLIMATE, "valid-header", 0, null);
      Assert.fail();
    } catch (Exception e) {
      log.info(e);
    }

    try {
      Layer.newInstance(LayerType.CLIMATE, "valid-header", 0, "/bogus/path");
      Assert.fail();
    } catch (Exception e) {
      log.info(e);
    }

  }

  @Test
  public void testContainsPointAndGetCell() {
    String path = LayerUnitTests.class.getResource("valid-header.asc").getPath();
    Layer l = Layer.newInstance(LayerType.CLIMATE, "valid-header", 0, path);
    log.info(l);
    int nrows = l.getNRows();
    int ncols = l.getNColumns();
    Cell c = null;

    Assert.assertFalse(l.containsPoint(l.getNePoint()));
    Assert.assertFalse(l.containsPoint(l.getNwPoint()));
    Assert.assertFalse(l.containsPoint(l.getSePoint()));

    Assert.assertTrue(l.containsPoint(l.getSwPoint()));
    c = l.asCell(l.getSwPoint());
    Assert.assertEquals(c.getColumn(), 0);
    Assert.assertEquals(c.getRow(), nrows);
    log.info(String.format("%s ncols=%d nrows=%d", c, ncols, nrows));

    LatLng point = LatLng.newInstance(l.getSwPoint().getLatitude()
        + (l.getNRows() / 2 * (l.getResolution())), l.getSwPoint()
        .getLongitude()
        + (l.getNColumns() / 2 * (l.getResolution())));
    Assert.assertTrue(l.containsPoint(point));
  }

  @Test
  public void testEquals() {
    String path = LayerUnitTests.class.getResource("valid-header.asc").getPath();
    Layer l = Layer.newInstance(LayerType.CLIMATE, "valid-header", 0, path);
    Map<Layer, String> map = new HashMap<Layer, String>();
    map.put(l, "valid-header");
    String name = map.get(Layer.newInstance(LayerType.CLIMATE, "valid-header",
        0, path));
    Assert.assertEquals(name, "valid-header");
  }

  @Test
  public void testHeader() {
    String path = LayerUnitTests.class.getResource("valid-header.asc").getPath();
    Layer l;
    try {
      l = Layer.newInstance(LayerType.CLIMATE, "valid-header", 0, path);
      Assert.assertEquals(l.getNColumns(), 386);
      Assert.assertEquals(l.getNRows(), 286);
      Assert.assertEquals(l.getSwPoint().getLongitude(), -128.66338);
      Assert.assertEquals(l.getSwPoint().getLatitude(), 13.7502065);
      Assert.assertEquals(l.getResolution(), 0.2);
      Assert.assertEquals(l.getNoData(), -9999);
      log.info(l);
    } catch (Exception e) {
      Assert.fail(e.toString());
    }

    path = LayerUnitTests.class.getResource("invalid-header.asc").getPath();
    try {
      l = Layer.newInstance(LayerType.CLIMATE, "valid-header", 0, path);
      Assert.fail();
    } catch (Exception e) {
      log.info(e);
    }
  }
}
