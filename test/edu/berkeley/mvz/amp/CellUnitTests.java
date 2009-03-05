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
 * Unit tests for {@link Cell}.
 * 
 */
public class CellUnitTests {

  private static void createInvalidCell(int row, int col) {
    try {
      Cell.newInstance(row, col);
      Assert.fail();
    } catch (Exception e) {
    }
  }

  @Test
  public void compareTo() {
    Cell c1 = Cell.newInstance(0, 0);
    Cell c2 = Cell.newInstance(0, 1);
    Cell c3 = Cell.newInstance(0, 0);
    Cell c4 = Cell.newInstance(1, 1);

    Assert.assertEquals(1, c1.compareTo(c2));
    Assert.assertEquals(-1, c2.compareTo(c1));

    Assert.assertEquals(0, c1.compareTo(c3));
    Assert.assertEquals(0, c3.compareTo(c1));

    Assert.assertEquals(1, c2.compareTo(c4));
    Assert.assertEquals(1, c1.compareTo(c4));
  }

  @Test
  public void equals() {
    Map<Cell, String> map = new HashMap<Cell, String>();
    Cell c = Cell.newInstance(0, 100);
    map.put(c, "cell");
    String s = map.get(Cell.newInstance(0, 100));
    Assert.assertEquals("cell", s);
  }

  @Test
  public void newInstance() {
    createInvalidCell(-1, 0);
    createInvalidCell(-1, -1);
    createInvalidCell(0, -1);
    Cell c = Cell.newInstance(0, 100);
    Assert.assertEquals(0, c.getRow());
    Assert.assertEquals(100, c.getColumn());
  }
}
