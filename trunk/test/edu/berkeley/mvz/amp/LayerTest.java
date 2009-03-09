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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;

import edu.berkeley.mvz.amp.Layer.LayerType;

/**
 * Unit tests for {@link Layer}.
 * 
 */
public class LayerTest {
  private final static Map<String, Layer> layerNames = new HashMap<String, Layer>();

  private static Logger log = Logger.getLogger(LayerTest.class);

  private static ArrayList<Layer> layers;

  static {
    layers = new ArrayList<Layer>();
    layers.add(Layer.newInstance(CLIMATE, "foo", 0, path("cld6190_ann.asc")));
    layers.add(Layer.newInstance(CLIMATE, "bar", 0, path("h_dem.asc")));
    for (Layer l : layers) {
      layerNames.put(l.getFilename(), l);
    }
  }

  public static Map<String, Layer> getTestLayerMap() {
    return new HashMap<String, Layer>(layerNames);
  }

  public static List<Layer> getTestLayers() {
    return layers;
  }

  private static String path(String name) {
    return MaxentServiceTest.class.getResource(name).getPath();
  }

  @Test
  public void testConstructor() {
    String path = LayerTest.class.getResource("valid-header.asc").getPath();
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
    String path = LayerTest.class.getResource("valid-header.asc").getPath();
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
    String path = LayerTest.class.getResource("valid-header.asc").getPath();
    Layer l = Layer.newInstance(LayerType.CLIMATE, "foo", 0, path);
    Map<Layer, String> map = new HashMap<Layer, String>();
    map.put(l, "foo");
    String name = map.get(Layer.newInstance(LayerType.CLIMATE, "valid-header",
        0, path));
    Assert.assertEquals(name, "foo");

    Map<Layer, String> map2 = new HashMap<Layer, String>();
    map2.put(Layer.newInstance(LayerType.CLIMATE, "foo", 0, path), "foo");
    Assert.assertEquals(map, map2);
  }

  @Test
  public void testHeader() {
    String path = LayerTest.class.getResource("valid-header.asc").getPath();
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

    path = LayerTest.class.getResource("invalid-header.asc").getPath();
    try {
      l = Layer.newInstance(LayerType.CLIMATE, "valid-header", 0, path);
      Assert.fail();
    } catch (Exception e) {
      log.info(e);
    }
  }
}
