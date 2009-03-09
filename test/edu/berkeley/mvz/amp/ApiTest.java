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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;

import edu.berkeley.mvz.amp.MaxentRun.Option;
import edu.berkeley.mvz.amp.MaxentRun.RunConfig;
import edu.berkeley.mvz.amp.MaxentRun.RunType;
import edu.berkeley.mvz.amp.MaxentService.AsyncRunCallback;
import edu.berkeley.mvz.amp.MaxentService.MaxEntException;

public class ApiTest {
  private static Logger log = Logger.getLogger(ApiTest.class);

  public static String path(String name) {
    return MaxentServiceTest.class.getResource(name).getPath();
  }

  @Test
  public void model() throws MaxEntException, IOException {
    List<Layer> layers = LayerTest.getTestLayers();
    List<Sample> samples = SampleTest.getTestSamples();

    MaxentRun swdRun = MaxentService.createSwdRun(samples, layers);
    SamplesWithData swd = MaxentService.execute(swdRun).getSamplesWithData();

    swdRun = MaxentService.createSwdRun(10000, layers);
    SamplesWithData background = MaxentService.execute(swdRun)
        .getSamplesWithData();

    MaxentRun modelRun = new RunConfig(RunType.MODEL).add(
        Option.OUTPUTDIRECTORY, "/Users/eighty/tmp").add(Option.SAMPLESFILE,
        swd.toTempCsv())
        .add(Option.ENVIRONMENTALLAYERS, background.toTempCsv()).build();

    MaxentResults results = MaxentService.execute(modelRun);
    Assert.assertNotNull(results);

  }

  @Test
  public void modelAsync() throws IOException, MaxEntException {
    List<Layer> layers = LayerTest.getTestLayers();
    List<Sample> samples = SampleTest.getTestSamples();

    MaxentRun swdRun = MaxentService.createSwdRun(samples, layers);
    SamplesWithData swd = MaxentService.execute(swdRun).getSamplesWithData();

    swdRun = MaxentService.createSwdRun(10000, layers);
    SamplesWithData background = MaxentService.execute(swdRun)
        .getSamplesWithData();

    MaxentRun modelRun = new RunConfig(RunType.MODEL).add(
        Option.OUTPUTDIRECTORY, "/Users/eighty/tmp-async").add(
        Option.SAMPLESFILE, swd.toTempCsv()).add(Option.ENVIRONMENTALLAYERS,
        background.toTempCsv()).build();

    MaxentService.executeAsync(modelRun, new AsyncRunCallback() {
      public void onFailure(Throwable t) {
        Assert.fail(t.toString());
      }

      public void onSuccess(MaxentRun run, MaxentResults results) {
        Assert.assertNotNull(results);

      }
    });

    new Timer().scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        log.info("waiting...");
      }
    }, 0, 1000);

    new Timer().schedule(new TimerTask() {
      @Override
      public void run() {
        Assert.fail();
      }
    }, 60000);
  }

  @Test
  public void modelPictures() throws MaxEntException, IOException {
    List<Layer> layers = LayerTest.getTestLayers();
    List<Sample> samples = SampleTest.getTestSamples();
    MaxentRun swdRun = MaxentService.createSwdRun(samples, layers);
    SamplesWithData swd = MaxentService.execute(swdRun).getSamplesWithData();
    MaxentRun modelRun = new RunConfig(RunType.MODEL).add(
        Option.OUTPUTDIRECTORY, "/Users/eighty/tmp-pics-reps").add(
        Option.SAMPLESFILE, swd.toTempCsv()).layers(layers).projectionLayers(
        layers).add(Option.PICTURES).add(Option.REPLICATES, "3").add(
        Option.RANDOMTESTPOINTS, "25").build();
    MaxentResults results = MaxentService.execute(modelRun);
    Assert.assertNotNull(results);

  }

  @Test
  public void modelPicturesNoReps() throws MaxEntException, IOException {
    List<Layer> layers = LayerTest.getTestLayers();
    List<Sample> samples = SampleTest.getTestSamples();
    MaxentRun swdRun = MaxentService.createSwdRun(samples, layers);
    SamplesWithData swd = MaxentService.execute(swdRun).getSamplesWithData();
    MaxentRun modelRun = new RunConfig(RunType.MODEL).add(
        Option.OUTPUTDIRECTORY, "/Users/eighty/tmp-pics-noreps").add(
        Option.SAMPLESFILE, swd.toTempCsv()).layers(layers).projectionLayers(
        layers).add(Option.PICTURES).build();
    MaxentResults results = MaxentService.execute(modelRun);
    Assert.assertNotNull(results);

  }
}
