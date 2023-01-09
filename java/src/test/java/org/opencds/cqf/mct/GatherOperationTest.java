package org.opencds.cqf.mct;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Group;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import java.util.Collections;
import java.util.Date;

import static org.opencds.cqf.cql.evaluator.fhir.util.r4.Parameters.parameters;
import static org.opencds.cqf.cql.evaluator.fhir.util.r4.Parameters.part;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(MctApplication.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { GatherOperationTest.class })
class GatherOperationTest {

   @LocalServerPort
   private int port;

   private IGenericClient client;

   private final Parameters.ParametersParameterComponent patients = part("patients",
           new Group().setMember(Collections.singletonList(new Group.GroupMemberComponent()
                   .setEntity(new Reference("Patient/patient-example")))));
   private final Parameters.ParametersParameterComponent noPatientEntity = part("patients",
           new Group().setId("missing-patients"));
   private final Parameters.ParametersParameterComponent facilities = part("facilities",
           "Location/facility-example");
   private final Parameters.ParametersParameterComponent measure = part("measure",
           "Measure/measure-example");
   private final Parameters.ParametersParameterComponent period = part("period",
           new Period().setStart(new Date()).setEnd(new Date()));
   private final Parameters.ParametersParameterComponent noPeriodStart = part("period",
           new Period().setEnd(new Date()));
   private final Parameters.ParametersParameterComponent noPeriodEnd = part("period",
           new Period().setStart(new Date()));

   @BeforeEach
   public void setup() {
      client = SpringContext.getBean(FhirContext.class)
              .newRestfulGenericClient("http://localhost:" + port + "/mct/");
   }

   @Test
   void missingPatients() {
      Parameters allButPatients = parameters(facilities, measure, period);
      Parameters response = client.operation().onServer().named("$gather").withParameters(allButPatients).execute();
      validateResponse(response, "Missing required Group FHIR resource \"patients\" parameter that identifies the patients for the $gather operation");
   }

   @Test
   void missingPatientsInGroup() {
      Parameters noPatients = parameters(noPatientEntity, facilities, measure, period);
      Parameters response = client.operation().onServer().named("$gather").withParameters(noPatients).execute();
      validateResponse(response, "Group FHIR resource \"patients\" parameter has no members");
   }

   @Test
   void missingFacilities() {
      Parameters allButFacilities = parameters(patients, measure, period);
      Parameters response = client.operation().onServer().named("$gather").withParameters(allButFacilities).execute();
      validateResponse(response, "Missing required List<String> \"facilities\" parameter that identifies the facilities for the $gather operation");
   }

   @Test
   void missingMeasure() {
      Parameters allButMeasure = parameters(patients, facilities, period);
      Parameters response = client.operation().onServer().named("$gather").withParameters(allButMeasure).execute();
      validateResponse(response, "Missing required Measure FHIR resource identifier \"measure\" parameter that identifies the measure for the $gather operation");
   }

   @Test
   void missingPeriod() {
      Parameters allButPeriod = parameters(patients, facilities, measure);
      Parameters response = client.operation().onServer().named("$gather").withParameters(allButPeriod).execute();
      validateResponse(response, "Missing required Period FHIR data type \"period\" parameter that identifies the measurement period for the $gather operation");
   }

   @Test
   void missingPeriodStart() {
      Parameters allButPeriod = parameters(patients, facilities, measure, noPeriodStart);
      Parameters response = client.operation().onServer().named("$gather").withParameters(allButPeriod).execute();
      validateResponse(response, "Period FHIR data type \"period\" parameter is missing a start date");
   }

   @Test
   void missingPeriodEnd() {
      Parameters allButPeriod = parameters(patients, facilities, measure, noPeriodEnd);
      Parameters response = client.operation().onServer().named("$gather").withParameters(allButPeriod).execute();
      validateResponse(response, "Period FHIR data type \"period\" parameter is missing an end date");
   }

   private void validateResponse(Parameters response, String errorMessage) {
      assertTrue(response.hasParameter("error"));
      assertTrue(response.getParameterFirstRep().hasResource());
      assertTrue(response.getParameterFirstRep().getResource() instanceof OperationOutcome);
      OperationOutcome errorResponse = (OperationOutcome) response.getParameterFirstRep().getResource();
      assertTrue(errorResponse.hasIssue());
      OperationOutcome.OperationOutcomeIssueComponent errorIssue = errorResponse.getIssueFirstRep();
      assertTrue(errorIssue.hasCode() && errorIssue.getCode() == OperationOutcome.IssueType.INVALID);
      assertTrue(errorIssue.hasSeverity() && errorIssue.getSeverity() == OperationOutcome.IssueSeverity.ERROR);
      assertTrue(errorIssue.hasDiagnostics());
      assertEquals(errorMessage, errorIssue.getDiagnostics());
   }
}
