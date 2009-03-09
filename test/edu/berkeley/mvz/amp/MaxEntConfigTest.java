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

import org.apache.log4j.Logger;

/**
 * Unit tests for {@link MaxEntRun}.
 * 
 */
public class MaxEntConfigTest {

  private static Logger log = Logger.getLogger(MaxEntConfigTest.class);

  // @Test
  // public void addOption() {
  // Options b = new Options(".");
  // b.add(Option.RANDOMSEED, "20");
  // b.add(Option.JACKKNIFE);
  // Assert.assertEquals(b.getOptions().get(Option.RANDOMSEED), "20");
  // Assert.assertEquals(b.getOptions().get(Option.JACKKNIFE), "true");
  // log.info(b.toString());
  //
  // b = new Options(".");
  // try {
  // b.add(null);
  // Assert.fail();
  // } catch (IllegalArgumentException e) {
  // log.info(e);
  // }
  //
  // b = new Options(".");
  // try {
  // b.add(null, "");
  // Assert.fail();
  // } catch (IllegalArgumentException e) {
  // log.info(e);
  // }
  //
  // b = new Options(".");
  // try {
  // b.add(null, null);
  // Assert.fail();
  // } catch (IllegalArgumentException e) {
  // log.info(e);
  // }
  //
  // b = new Options(".");
  // try {
  // b.add(Option.JACKKNIFE, null);
  // Assert.fail();
  // } catch (IllegalArgumentException e) {
  // log.info(e);
  // }
  // }
  // @Test
  // public void asArgv() {
  // Options b = new Options("/Users/eighty");
  // b.add(Option.JACKKNIFE);
  // b.add(Option.SAMPLESFILE, "/samples/file.csv");
  // String path = LayerTest.class.getResource("valid-header.asc").getPath();
  // List<Layer> layers = new ArrayList<Layer>();
  // layers.add(Layer.newInstance(LayerType.CLIMATE, "valid-header", 0, path));
  // String[] argv = b.build().asArgv();
  // StringBuilder sb = new StringBuilder();
  // for (String s : argv) {
  // sb.append(String.format("%s ", s));
  // }
  // String commandLine = sb.toString().trim();
  // log.info(commandLine);
  // Assert
  // .assertEquals(
  // "-J outputdirectory=/Users/eighty samplesfile=/samples/file.csv /Users/eighty/Projects/Workspace/AMP/bin/edu/berkeley/mvz/amp/valid-header.asc"
  // ,
  // commandLine);
  // }
  //
  // @Test
  // public void builderConstructor() {
  // Options b;
  // try {
  // b = new Options("BOGUS PATH");
  // Assert.fail();
  // } catch (IllegalArgumentException e) {
  // }
  //
  // b = new Options(".");
  // Assert.assertNotNull(b);
  // }
}
