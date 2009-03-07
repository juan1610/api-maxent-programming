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
import java.util.Set;
import java.util.Map.Entry;

/**
 * This class can be used to encapsulate a cell's mapping of layer values. The
 * {@link SamplesWithData} class uses it to capture the mapping of layer values
 * for the cell associated with each sample.
 * 
 * This class is immutable and is not designed for inheritance which is
 * prohibited.
 * 
 */
class CellData {

  /**
   * Returns a new cell data instance given a cell and a mapping of layer
   * values. The cell and layer values cannot be null.
   * 
   * @param cell the layer cell
   * @param values the mapping of layer values for the cell
   * @throws NullPointerException if cell or values is null
   * @return cell data instance
   */
  public static CellData newInstance(Cell cell, Map<Layer, Double> values) {
    if (cell == null) {
      throw new NullPointerException("Cell was null");
    }
    if (values == null) {
      throw new NullPointerException("Values were null");
    }
    return new CellData(cell, values);
  }

  private final Map<Layer, Double> layerValues;
  private final Cell cell;

  private CellData(Cell cell, Map<Layer, Double> values) {
    this.cell = cell;
    layerValues = new HashMap<Layer, Double>(values);
  }

  /**
   * Cell data objects are equal if their layer value mappings are equal.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CellData)) {
      return false;
    }
    CellData CellValues = (CellData) o;
    boolean b = layerValues.equals(CellValues.layerValues);
    return b;
  }

  /**
   * Returns the cell.
   * 
   * @return the cell
   */
  public Cell getCell() {
    return cell;
  }

  /**
   * Returns the cell value for a given layer.
   * 
   * @param Layer the layer
   * @return cell value for the layer
   */
  public double getCellValue(Layer Layer) {
    return layerValues.get(Layer);
  }

  /**
   * Returns the list of layers that corresponding cell values are available for
   * reading.
   * 
   * @return list of layers
   */
  public Set<Layer> getLayers() {
    return layerValues.keySet();
  }

  /**
   * Returns the iterable mapping of layer values.
   * 
   * @return iterable mapping of layer values
   */
  public Iterable<Entry<Layer, Double>> getValues() {
    return new HashMap<Layer, Double>(layerValues).entrySet();
  }

  @Override
  public int hashCode() {
    int result = 17;
    for (Entry<Layer, Double> entry : layerValues.entrySet()) {
      result = 31 * result + entry.getKey().hashCode();
      result = 31 * result + entry.getValue().hashCode();
    }
    return result;
  }

  @Override
  public String toString() {
    return String.format("[%s %s]", cell, layerValues.toString());
  }
}
