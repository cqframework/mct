package org.opencds.cqf.mct;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.mct.config.MctConstants;
import org.opencds.cqf.mct.service.FacilityRegistrationService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import static org.opencds.cqf.cql.evaluator.fhir.util.r4.Parameters.parameters;
import static org.opencds.cqf.cql.evaluator.fhir.util.r4.Parameters.part;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(MctApplication.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { FacilityRegistrationTest.class })
class FacilityRegistrationTest {

   @LocalServerPort
   private int port;

   private IGenericClient client;

   @BeforeEach
   public void setup() {
      client = SpringContext.getBean(FhirContext.class)
              .newRestfulGenericClient("http://localhost:" + port + "/mct/");
   }

   @Test
   void listOrganizations() {
      Bundle result = client.operation().onServer().named(MctConstants.LIST_ORGANIZATIONS_OPERATION_NAME)
              .withNoParameters(Parameters.class).useHttpGet().returnResourceType(Bundle.class).execute();
      assertTrue(result.hasEntry());
      assertEquals(2, result.getEntry().size());
      assertTrue(result.getEntryFirstRep().hasResource());
      assertTrue(result.getEntryFirstRep().getResource() instanceof Organization);
   }

   @Test
   void listFacilities() {
      Parameters params = parameters(part(MctConstants.LIST_FACILITIES_PARAM, "Organization/acme"));
      Bundle result = client.operation().onServer().named(MctConstants.LIST_FACILITIES_OPERATION_NAME)
              .withParameters(params).useHttpGet().returnResourceType(Bundle.class).execute();
      assertTrue(result.hasEntry());
      assertEquals(2, result.getEntry().size());
      for (Bundle.BundleEntryComponent entryComponent : result.getEntry()) {
         assertTrue(entryComponent.hasResource());
         assertTrue(entryComponent.getResource() instanceof Location);
         assertTrue(((Location) entryComponent.getResource()).hasManagingOrganization());
         assertEquals("Organization/acme", ((Location) entryComponent.getResource()).getManagingOrganization().getReference());
      }
   }

   @Test
   void facilityUrl() {
      FacilityRegistrationService service = SpringContext.getBean(FacilityRegistrationService.class);
      String url = service.getFacilityUrl("Location/local-test");
      assertEquals("http://facility-a:8080/fhir", url);
   }
}
