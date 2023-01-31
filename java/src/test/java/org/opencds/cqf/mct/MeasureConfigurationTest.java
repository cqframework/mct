package org.opencds.cqf.mct;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Measure;
import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.mct.config.MctConstants;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(MctApplication.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { MeasureConfigurationTest.class })
class MeasureConfigurationTest {
   @LocalServerPort
   private int port;

   private IGenericClient client;

   @BeforeEach
   public void setup() {
      client = SpringContext.getBean(FhirContext.class)
              .newRestfulGenericClient("http://localhost:" + port + "/mct/");
   }

   @Test
   void listMeasures() {
      Bundle result = client.operation().onServer().named(MctConstants.LIST_MEASURE_OPERATION_NAME)
              .withNoParameters(Parameters.class).useHttpGet().returnResourceType(Bundle.class).execute();
      assertTrue(result.hasEntry());
      assertEquals(1, result.getEntry().size());
      assertTrue(result.getEntryFirstRep().hasResource());
      assertTrue(result.getEntryFirstRep().getResource() instanceof Measure);
   }
}
