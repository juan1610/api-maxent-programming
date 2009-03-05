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

/**
 * An immutable class that encapsulates the decimal latitude and longitude of a
 * geographic coordinate. It is not designed for inheritance and is therefore
 * prohibited.
 * 
 */
public class LatLng implements Comparable<LatLng> {

  /**
   * Factory method that returns a LatLng instance.
   * 
   * @param latitude the decimal latitude
   * @param longitude the decimal longitude
   * @throws IllegalArgumentException if (-180 >= longitude <= 180) or (-90 >=
   *           latitude <= 90) doesn't hold
   * @return instance of LatLng
   */
  public static LatLng newInstance(double latitude, double longitude) {
    String msg = "Illegal %s: %f";
    if (!isLatitudeValid(latitude)) {
      throw new IllegalArgumentException(String.format(msg, "Latitude",
          latitude));
    }
    if (!isLongitudeValid(longitude)) {
      throw new IllegalArgumentException(String.format(msg, "Longitude",
          longitude));
    }
    return new LatLng(latitude, longitude);
  }

  private static int hashDouble(double val) {
    long longBits = Double.doubleToLongBits(val);
    return (int) (longBits ^ (longBits >>> 32));
  }

  /**
   * Returns true if the latitude is valid:
   * 
   * -90 >= latitude <= 90
   */
  private static boolean isLatitudeValid(double latitude) {
    if (Double.compare(latitude, -90) < 0) {
      return false;
    }
    if (Double.compare(latitude, 90) > 0) {
      return false;
    }
    return true;
  }

  /**
   * Returns true if the longitude is valid:
   * 
   * -180 >= longitude <= 180
   */
  private static boolean isLongitudeValid(double longitude) {
    if (Double.compare(longitude, -180) < 0) {
      return false;
    }
    if (Double.compare(longitude, 180) > 0) {
      return false;
    }
    return true;
  }

  private double latitude, longitude;

  private LatLng(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  /**
   * Sorts by latitude and then by longitude.
   */
  public int compareTo(LatLng other) {
    int latCompare = Double.compare(latitude, other.latitude);
    if (latCompare != 0) {
      return latCompare;
    }
    int lngCompare = Double.compare(longitude, other.longitude);
    if (lngCompare != 0) {
      return lngCompare;
    }
    return 0;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof LatLng)) {
      return false;
    }
    LatLng x = (LatLng) o;
    return (Double.compare(latitude, x.latitude) == 0)
        && (Double.compare(longitude, x.longitude) == 0);
  }

  /**
   * Returns the decimal latitude.
   * 
   * @return decimal latitude
   */
  public double getLatitude() {
    return latitude;
  }

  /**
   * Returns the decimal longitude.
   * 
   * @return decimal longitude
   */
  public double getLongitude() {
    return longitude;
  }

  @Override
  public int hashCode() {
    int result = 17 + hashDouble(latitude);
    result = 31 * result + hashDouble(longitude);
    return result;
  }

  @Override
  public String toString() {
    return String.format("[Lat=%s,Long=%s]", Double.toString(latitude), Double
        .toString(longitude));
  }

  @SuppressWarnings("unused")
  private void clamp() {
    if (latitude < -90) {
      latitude = -90;
    } else if (latitude > 90) {
      latitude = 90;
    }
    if (longitude < -180) {
      longitude = -180;
    } else if (longitude > 180) {
      longitude = 180;
    }
  }
}
