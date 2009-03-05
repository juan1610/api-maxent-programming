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

/**
 * An immutable class that encapsulates the row and column number of a
 * {@link Layer} cell. It is not designed for inheritance and is therefore
 * prohibited.
 * 
 */
public class Cell {

  private static Map<String, Cell> cache = new HashMap<String, Cell>();

  /**
   * Factory method that returns a new cell object.
   * 
   * @param row the cell row number
   * @param col the cell column number
   * @return a new cell instance
   */
  public static Cell newInstance(int row, int col) {
    if (row < 0 || col < 0) {
      throw new IllegalArgumentException(
          "Cell row and column numbers can't be negative");
    }
    Cell cell;
    String key = String.format("%d-%d", row, col);
    if (cache.keySet().contains(key)) {
      cell = cache.get(key);
    } else {
      cache.put(key, cell = new Cell(row, col));
    }
    return cell;
  }

  private final int row, col;

  private volatile int hashCode;

  private Cell(int row, int col) {
    this.row = row;
    this.col = col;
  }

  /**
   * Sorts rows by row and then by column.
   * 
   */
  public int compareTo(Cell o) {
    if (o.row < row) {
      return -1;
    }
    if (o.row > row) {
      return 1;
    }
    if (o.col < col) {
      return -1;
    }
    if (o.col > col) {
      return 1;
    }
    return 0;
  }

  /**
   * Cells are equal if their rows and columns are equal.
   */
  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Cell)) {
      return false;
    }
    Cell c = (Cell) other;
    return row == c.row && col == c.col;
  }

  /**
   * Returns the column number.
   * 
   * @return column number
   */
  public int getColumn() {
    return col;
  }

  /**
   * Returns the row number.
   * 
   * @return row number
   */
  public int getRow() {
    return row;
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = 17;
      result = 31 * result + col;
      result = 31 * result + row;
      hashCode = result;
    }
    return result;
  }

  @Override
  public String toString() {
    return String.format("[row=%d col=%d]", row, col);
  }
}
