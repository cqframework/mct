package org.opencds.cqf.mct;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.mct.config.MctConstants;
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
   void addLocation() {
      Location location = buildLocation("valid-location", true, true);
      Parameters params = parameters(part(MctConstants.REGISTER_PARAM_LOCATIONS, location));
      OperationOutcome result = client.operation().onServer().named(MctConstants.REGISTER_OPERATION_NAME)
              .withParameters(params).returnResourceType(OperationOutcome.class).execute();
      validateSuccess(result);
   }

   @Test
   void addLocations() {
      Location location1 = buildLocation("valid-1", true, true);
      Location location2 = buildLocation("valid-2", true, true);
      Parameters params = parameters(
              part(MctConstants.REGISTER_PARAM_LOCATIONS, location1),
              part(MctConstants.REGISTER_PARAM_LOCATIONS, location2));
      OperationOutcome result = client.operation().onServer().named(MctConstants.REGISTER_OPERATION_NAME)
              .withParameters(params).returnResourceType(OperationOutcome.class).execute();
      validateSuccess(result);
   }

   @Test
   void addOrganization() {
      Organization organization = buildOrganization("valid-organization", true, true);
      Parameters params = parameters(part(MctConstants.REGISTER_PARAM_ORGANIZATION, organization));
      OperationOutcome result = client.operation().onServer().named(MctConstants.REGISTER_OPERATION_NAME)
              .withParameters(params).returnResourceType(OperationOutcome.class).execute();
      validateSuccess(result);
   }

   @Test
   void removeLocation() {
      Location location = buildLocation("valid-location-remove", true, true);
      Parameters params = parameters(part(MctConstants.REGISTER_PARAM_LOCATIONS, location));
      OperationOutcome result = client.operation().onServer().named(MctConstants.REGISTER_OPERATION_NAME)
              .withParameters(params).returnResourceType(OperationOutcome.class).execute();
      validateSuccess(result);

      params = parameters(part(MctConstants.UNREGISTER_PARAM_FACILITY_ID, "Location/valid-location-remove"));
      result = client.operation().onServer().named(MctConstants.UNREGISTER_OPERATION_NAME)
              .withParameters(params).returnResourceType(OperationOutcome.class).execute();

      assertTrue(result.hasIssue());
      assertEquals(1, result.getIssue().size());
      assertTrue(result.getIssueFirstRep().hasCode());
      assertEquals(MctConstants.SEVERITY_INFORMATION, result.getIssueFirstRep().getSeverity().toCode());
      assertEquals(MctConstants.CODE_INFORMATIONAL, result.getIssueFirstRep().getCode().toCode());
      assertEquals(MctConstants.UNREGISTER_SUCCESS, result.getIssueFirstRep().getDiagnostics());
   }

   @Test
   void removeOrganization() {
      Organization organization = buildOrganization("valid-organization-remove", true, true);
      Parameters params = parameters(part(MctConstants.REGISTER_PARAM_ORGANIZATION, organization));
      OperationOutcome result = client.operation().onServer().named(MctConstants.REGISTER_OPERATION_NAME)
              .withParameters(params).returnResourceType(OperationOutcome.class).execute();
      validateSuccess(result);

      params = parameters(part(MctConstants.UNREGISTER_PARAM_FACILITY_ID, "Organization/valid-organization-remove"));
      result = client.operation().onServer().named(MctConstants.UNREGISTER_OPERATION_NAME)
              .withParameters(params).returnResourceType(OperationOutcome.class).execute();

      assertTrue(result.hasIssue());
      assertEquals(1, result.getIssue().size());
      assertTrue(result.getIssueFirstRep().hasCode());
      assertEquals(MctConstants.SEVERITY_INFORMATION, result.getIssueFirstRep().getSeverity().toCode());
      assertEquals(MctConstants.CODE_INFORMATIONAL, result.getIssueFirstRep().getCode().toCode());
      assertEquals(MctConstants.UNREGISTER_SUCCESS, result.getIssueFirstRep().getDiagnostics());
   }

   @Test
   void noContainedEndpoints() {
      Location location = buildLocation("no-contained-endpoints", false, true);
      Parameters params = parameters(part(MctConstants.REGISTER_PARAM_LOCATIONS, location));
      OperationOutcome result = client.operation().onServer().named(MctConstants.REGISTER_OPERATION_NAME)
              .withParameters(params).returnResourceType(OperationOutcome.class).execute();

      assertTrue(result.hasIssue());
      assertEquals(1, result.getIssue().size());
      assertTrue(result.getIssueFirstRep().hasCode());
      assertEquals( MctConstants.SEVERITY_ERROR, result.getIssueFirstRep().getSeverity().toCode());
      assertEquals(MctConstants.CODE_PROCESSING, result.getIssueFirstRep().getCode().toCode());
      assertEquals(MctConstants.MISSING_FHIR_REST_ENDPOINT, result.getIssueFirstRep().getDiagnostics());
   }

   @Test
   void noFhirEndpoint() {
      Organization organization = buildOrganization("no-fhir-endpoints", true, false);
      Parameters params = parameters(part(MctConstants.REGISTER_PARAM_ORGANIZATION, organization));
      OperationOutcome result = client.operation().onServer().named(MctConstants.REGISTER_OPERATION_NAME)
              .withParameters(params).returnResourceType(OperationOutcome.class).execute();

      assertTrue(result.hasIssue());
      assertEquals(1, result.getIssue().size());
      assertTrue(result.getIssueFirstRep().hasCode());
      assertEquals( MctConstants.SEVERITY_ERROR, result.getIssueFirstRep().getSeverity().toCode());
      assertEquals(MctConstants.CODE_PROCESSING, result.getIssueFirstRep().getCode().toCode());
      assertEquals(MctConstants.MISSING_FHIR_REST_ENDPOINT, result.getIssueFirstRep().getDiagnostics());
   }

   @Test
   void removeFacilityNotAdded() {
      Parameters params = parameters(part(MctConstants.UNREGISTER_PARAM_FACILITY_ID, "Location/not-added"));
      OperationOutcome result = client.operation().onServer().named(MctConstants.UNREGISTER_OPERATION_NAME)
              .withParameters(params).returnResourceType(OperationOutcome.class).execute();

      assertTrue(result.hasIssue());
      assertEquals(1, result.getIssue().size());
      assertTrue(result.getIssueFirstRep().hasCode());
      assertEquals( MctConstants.SEVERITY_ERROR, result.getIssueFirstRep().getSeverity().toCode());
      assertEquals(MctConstants.CODE_PROCESSING, result.getIssueFirstRep().getCode().toCode());
      assertEquals("Facility: Location/not-added not found", result.getIssueFirstRep().getDiagnostics());
   }

   private void validateSuccess(OperationOutcome result) {
      assertTrue(result.hasIssue());
      assertEquals(1, result.getIssue().size());
      assertTrue(result.getIssueFirstRep().hasCode());
      assertEquals(MctConstants.SEVERITY_INFORMATION, result.getIssueFirstRep().getSeverity().toCode());
      assertEquals(MctConstants.CODE_INFORMATIONAL, result.getIssueFirstRep().getCode().toCode());
      assertEquals(MctConstants.REGISTER_SUCCESS, result.getIssueFirstRep().getDiagnostics());
   }

   private Location buildLocation(String id, boolean hasContained, boolean isFhirEndpoint) {
      Location location = new Location();
      location.setId(id);
      if (hasContained) {
         Endpoint endpoint = new Endpoint();
         if (isFhirEndpoint) {
            endpoint.setId("fhir-endpoint");
            endpoint.setConnectionType(
                    new Coding("http://terminology.hl7.org/CodeSystem/endpoint-connection-type",
                            "hl7-fhir-rest", null));
            location.addEndpoint(new Reference("#fhir-endpoint"));
            endpoint.setAddress("http://example.com/fhir");
         }
         else {
            endpoint.setId("non-fhir-endpoint");
            endpoint.setConnectionType(
                    new Coding("http://terminology.hl7.org/CodeSystem/endpoint-connection-type",
                            "dicom-stow-rs", null));
            location.addEndpoint(new Reference("#non-fhir-endpoint"));
            endpoint.setAddress("http://example.com");
         }
         location.addContained(endpoint);
      }
      else {
         location.addEndpoint(new Reference("Endpoint/example"));
      }
      return location;
   }
   private Organization buildOrganization(String id, boolean hasContained, boolean isFhirEndpoint) {
      Organization organization = new Organization();
      organization.setId(id);
      if (hasContained) {
         Endpoint endpoint;
         if (isFhirEndpoint) {
            endpoint = buildEndpoint("fhir-endpoint", MctConstants.FHIR_REST_CONNECTION_TYPE);
            organization.addEndpoint(new Reference("#fhir-endpoint"));
         }
         else {
            endpoint = buildEndpoint("non-fhir-endpoint", "dicom-stow-rs");
            organization.addEndpoint(new Reference("#non-fhir-endpoint"));
         }
         organization.addContained(endpoint);
      }
      else {
         organization.addEndpoint(new Reference("Endpoint/example"));
      }
      return organization;
   }

   private Endpoint buildEndpoint(String id, String connectionType) {
      Endpoint endpoint = new Endpoint();
      endpoint.setId(id);
      endpoint.setConnectionType(
              new Coding("http://terminology.hl7.org/CodeSystem/endpoint-connection-type",
                      connectionType, null));
      endpoint.setAddress(connectionType == MctConstants.FHIR_REST_CONNECTION_TYPE ? "http://example.com/fhir" : "http://example.com");
      return endpoint;
   }
}
