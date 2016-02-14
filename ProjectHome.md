While the [Maxent](http://www.cs.princeton.edu/~schapire/maxent/) software for species habitat modeling provides a nice GUI and a fully featured command line interface to clients, programmatic access isn't very fun since it's not open source and it doesn't expose a formal API. Our design goal is to create a simple API for Maxent Programming (AMP) that makes it easier and more fun to work with in a programmatic way.

Here's a quick look at a bit of AMP in action:

```
@Test
public void modelAsync() throws IOException, MaxEntException {  
  List<Layer> layers = LayerTest.getTestLayers();
  List<Sample> samples = SampleTest.getTestSamples();

  // Gets samples with data:
  MaxEntRun swdRun = MaxEntService.createSwdRun(samples, layers);
  SamplesWithData swd = MaxEntService.execute(swdRun).getSamplesWithData();

  // Gets background samples with data:
  swdRun = MaxEntService.createSwdRun(10000, layers);
  SamplesWithData background = MaxEntService.execute(swdRun).getSamplesWithData();

  // Sets the model run configuration options:
  MaxentRun modelRun = new RunConfig(RunType.MODEL).add(
        Option.OUTPUTDIRECTORY, "/output/dir").add(
        Option.SAMPLESFILE, swd.toTempCsv()).layers(layers).projectionLayers(
        layers).add(Option.PICTURES).add(Option.REPLICATES, "3").add(
        Option.RANDOMTESTPOINTS, "25").build();

  // Executes the model run asynchronously:
  MaxEntService.executeAsync(modelRun, new AsyncRunCallback() {
      public void onFailure(Throwable t) {
        Assert.fail(t.toString());
      }
      public void onSuccess(MaxEntRun run, MaxEntResults results) {
        Assert.assertNotNull(results);
        log.info(results.getOutputs());
      }
    });
}
```