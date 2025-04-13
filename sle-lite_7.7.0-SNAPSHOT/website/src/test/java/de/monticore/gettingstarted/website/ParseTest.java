/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gettingstarted.website;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

public class ParseTest extends AbstractTest {

  @Test
  @Ignore
  public void testSERWTH() throws IOException {
    parse("src/test/resources/de/monticore/gettingstarted/website/valid/SERWTH.web");
  }

  @Test
  @Ignore
  public void testDream() throws IOException {
    parse("src/test/resources/de/monticore/gettingstarted/website/valid/Dream.web");
  }

  @Test
  @Ignore
  public void testSinglePage() throws IOException {
    parse("src/test/resources/de/monticore/gettingstarted/website/valid/SinglePage.web");
  }

}
