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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;

/**
 * A class that can be used to load and represent a single ArcInfo ASCII Grid
 * file. It doesn't provide access to actual grid cell values but does provide
 * access to it's geographic domain and resolution. It can be used to return a
 * grid {@link Cell} given a {@link LatLng} point.
 * 
 * This class is immutable and it is not designed for inheritance.
 */
public class Layer {

  /**
   * Enumeration of layer types.
   * 
   */
  public static enum LayerType {
    CLIMATE, FOREST, GEOLOGY, SOIL, MARINE
  }

  private static class AsciiHeader {
    Double xllcorner = null;
    Double yllcorner = null;
    Double cellSize = null;
    Integer noDataValue = null;
    Integer nRows = null;
    Integer nCols = null;
  }

  private static class Extent {
    LatLng nw, ne, se, sw;

    Extent(AsciiHeader header) {
      double swlat = header.yllcorner;
      double swlng = header.xllcorner;
      sw = LatLng.newInstance(swlat, swlng);
      ne = LatLng.newInstance(swlat + (header.nRows * header.cellSize), swlng
          + (header.nCols * header.cellSize));
      nw = LatLng.newInstance(ne.getLatitude(), swlng);
      se = LatLng.newInstance(sw.getLatitude(), ne.getLongitude());
    }
  }

  private static class Parser {

    private static enum State {
      HEADER_KEY, HEADER_VAL, DATA_INIT, DATA_VAL, DONE
    }

    static AsciiHeader parse(String filePath) throws IOException {
      State parserState = State.HEADER_KEY;
      AsciiHeader header = new AsciiHeader();
      BufferedReader reader = new BufferedReader(new FileReader(filePath));
      StreamTokenizer st = new StreamTokenizer(reader);
      st.parseNumbers();
      st.wordChars('_', '_');
      String headerKey = "";
      int headerCount = 0;
      int tokenType = st.nextToken();
      while (tokenType != StreamTokenizer.TT_EOF && parserState != State.DONE) {
        if (tokenType == StreamTokenizer.TT_EOL) {
          continue;
        }
        String tokenString = st.sval;
        double tokenNumber = st.nval;
        switch (parserState) {
        case HEADER_KEY:
          if (null == tokenString) {
            parserState = State.DATA_INIT;
          } else {
            headerKey = tokenString;
            tokenType = st.nextToken();
            parserState = State.HEADER_VAL;
          }
          break;
        case HEADER_VAL:
          if (headerKey.equals("ncols"))
            header.nCols = (int) tokenNumber;
          else if (headerKey.equals("nrows"))
            header.nRows = (int) tokenNumber;
          else if (headerKey.equals("xllcorner"))
            header.xllcorner = tokenNumber;
          else if (headerKey.equals("yllcorner"))
            header.yllcorner = tokenNumber;
          else if (headerKey.equals("cellsize"))
            header.cellSize = tokenNumber;
          else if (headerKey.equals("NODATA_value"))
            header.noDataValue = (int) tokenNumber;
          tokenType = st.nextToken();
          if (++headerCount == 6) {
            parserState = State.DONE;
          } else {

            parserState = State.HEADER_KEY;
          }
          break;
        }
      }
      return header;
    }
  }

  /**
   * Factory method that returns a {@link Layer} instance. The type, name, and
   * path cannot be null.
   * 
   * @param type the layer type
   * @param name the layer name
   * @param year the layer year
   * @param path the layer path
   * @return a layer instance
   */
  public static Layer newInstance(LayerType type, String name, int year,
      String path) {
    if (type == null) {
      throw new NullPointerException("Layer type was null");
    }
    if (name == null) {
      throw new NullPointerException("Layer name was null");
    }
    if (path == null) {
      throw new NullPointerException("Layer path was null");
    }
    return new Layer(type, name, year, path);
  }

  private final int nRows;

  private final int nCols;
  private final int year;
  private final double res;
  private final int noData;
  private final String path;
  private final String name;
  private final LayerType type;
  private final Extent extent;

  private Layer(LayerType type, String name, int year, String path) {
    this.type = type;
    this.name = name;
    this.year = year;
    this.path = path;

    AsciiHeader header;
    try {
      header = Parser.parse(path);
    } catch (IOException e) {
      throw new IllegalArgumentException(String.format("Invalid layer %s - %s",
          path, e));
    }

    String f = "Bad argument [%s]: %s";
    if (header.xllcorner == 0) {
      throw new IllegalArgumentException(String.format(f, "xllcorner",
          "Cannot be null"));
    }
    if (header.yllcorner == null) {
      throw new IllegalArgumentException(String.format(f, "yllcorner",
          "Cannot be null"));
    }
    if (header.xllcorner == null) {
      throw new IllegalArgumentException(String.format(f, "xllcorner",
          "Cannot be null"));
    }

    if (header.nRows == null || header.nCols == null) {
      throw new IllegalArgumentException(String.format(f, "nrows, ncols",
          "Must be non-zero"));
    }
    if (header.noDataValue == null) {
      throw new NullPointerException(String.format(f, "nodata",
          "Cannot be null"));
    }

    extent = new Extent(header);
    nRows = header.nRows;
    nCols = header.nCols;
    res = header.cellSize;
    noData = header.noDataValue;
  }

  /**
   * Returns the {@link Cell} corresponding to where the point is located.
   * 
   * This method assumes that the origin of the layer is the upper left and
   * terminus at the lower right. A point located in the north west corner
   * corresponds to row 0 and column 0. A point located in the south east corner
   * corresponds to row <code>nRows</code> column <code>nCols</code>.
   * 
   * @param point point from which a corresponding cell is returned. Must be
   *          non-null and fall within the extent of the layer.
   * @return the cell corresponding to the point or null if the point falls
   *         outside of the layer extent.
   */
  public Cell asCell(LatLng point) {
    if ((point == null) || (!containsPoint(point))) {
      return null;
    }

    double plat = point.getLatitude();
    double plng = point.getLongitude();
    int col = -1, row = -1;

    // Calculates column:
    if (plng == extent.ne.getLongitude()) {
      col = nCols;
    } else {
      col = (int) Math.ceil(((plng - extent.sw.getLongitude()) / res));
    }

    // Calculates row:
    if (plat == extent.ne.getLatitude()) {
      row = 0;
    } else {
      row = (int) Math.ceil(((extent.ne.getLatitude() - plat) / res));
    }

    return Cell.newInstance(row, col);
  }

  /**
   * Returns true if the point falls within the layer extent.
   * 
   * This method assumes that the south west layer corner is defined at the edge
   * of the layer, not at the center of the edge cell. As a result, the south
   * west layer coordinate is contained by the layer, but the south east, the
   * north east, and the north west points are not contained by the layer.
   * 
   * @param point the point to check
   * @return true if the point is contained by this layer, false otherwise.
   */
  public boolean containsPoint(LatLng point) {
    double plat = point.getLatitude();
    double plng = point.getLongitude();
    boolean latHit = (plat >= extent.sw.getLatitude())
        && (plat < extent.ne.getLatitude());
    boolean lngHit = (plng >= extent.sw.getLongitude())
        && (plng < extent.ne.getLongitude());
    return latHit && lngHit;
  }

  /**
   * Returns true if layers have equal paths.
   */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Layer)) {
      return false;
    }
    Layer l = (Layer) o;
    return l.getPath().equals(path);
  }

  /**
   * Returns the layer name.
   * 
   * @return layer name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the number of layer columns as defined in the header of the ArcInfo
   * ASCII Grid format.
   * 
   * @return number of columns
   */
  public int getNColumns() {
    return nCols;
  }

  /**
   * Returns the the north east point of the layer extent.
   * 
   * @return north east point of layer extent
   */
  public LatLng getNePoint() {
    return extent.ne;
  }

  /**
   * Returns the no data value as defined in the header of the ArcInfo ASCII
   * Grid format.
   * 
   * @return
   */
  public int getNoData() {
    return noData;
  }

  /**
   * Returns the number of layer rows as defined in the header of the ArcInfo
   * ASCII Grid format.
   * 
   * @return number of rows
   */
  public int getNRows() {
    return nRows;
  }

  /**
   * Returns the the north west point of the layer extent.
   * 
   * @return north west point of layer extent
   */
  public LatLng getNwPoint() {
    return extent.nw;
  }

  public String getPath() {
    return path;
  }

  /**
   * Returns the resolution in decimal degrees of each cell as defined in the
   * header of the ArcInfo ASCII Grid format.
   * 
   * @return cell resolution in decimal degrees
   */
  public double getResolution() {
    return res;
  }

  /**
   * Returns the the south east point of the layer extent.
   * 
   * @return south east point of layer extent
   */
  public LatLng getSePoint() {
    return extent.se;
  }

  /**
   * Returns the the south west point of the layer extent.
   * 
   * @return south west point of layer extent
   */
  public LatLng getSwPoint() {
    return extent.sw;
  };

  /**
   * Returns the layer type.
   * 
   * @return the layer type
   */
  public LayerType getType() {
    return type;
  }

  /**
   * Returns the year this layer is associated with.
   * 
   * @return the year this layer is associated with
   */
  public int getYear() {
    return year;
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + path.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return path;
  }
}
