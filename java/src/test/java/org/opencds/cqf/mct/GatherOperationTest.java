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
import org.opencds.cqf.mct.config.MctConstants;
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

   private final Parameters.ParametersParameterComponent patients = part(
           MctConstants.GATHER_PARAM_PATIENTS,
           new Group().setMember(Collections.singletonList(new Group.GroupMemberComponent()
                   .setEntity(new Reference("Patient/patient-example")))));
   private final Parameters.ParametersParameterComponent noPatientEntity = part(
           MctConstants.GATHER_PARAM_PATIENTS, new Group().setId("missing-patients"));
   private final Parameters.ParametersParameterComponent facilities = part(
           MctConstants.GATHER_PARAM_FACILITIES, "Location/facility-example");
   private final Parameters.ParametersParameterComponent measure = part(
           MctConstants.GATHER_PARAM_MEASURE, "Measure/measure-example");
   private final Parameters.ParametersParameterComponent period = part(
           MctConstants.GATHER_PARAM_PERIOD, new Period().setStart(new Date()).setEnd(new Date()));
   private final Parameters.ParametersParameterComponent noPeriodStart = part(
           MctConstants.GATHER_PARAM_PERIOD, new Period().setEnd(new Date()));
   private final Parameters.ParametersParameterComponent noPeriodEnd = part(
           MctConstants.GATHER_PARAM_PERIOD, new Period().setStart(new Date()));

   @BeforeEach
   public void setup() {
      client = SpringContext.getBean(FhirContext.class)
              .newRestfulGenericClient("http://localhost:" + port + "/mct/");
   }

   @Test
   void missingPatients() {
      Parameters allButPatients = parameters(facilities, measure, period);
      Parameters response = client.operation().onServer().named(MctConstants.GATHER_OPERATION_NAME).withParameters(allButPatients).execute();
      validateResponse(response, MctConstants.GATHER_PARAM_NULL_PATIENTS);
   }

   @Test
   void missingPatientsInGroup() {
      Parameters noPatients = parameters(noPatientEntity, facilities, measure, period);
      Parameters response = client.operation().onServer().named(MctConstants.GATHER_OPERATION_NAME).withParameters(noPatients).execute();
      validateResponse(response, MctConstants.GATHER_PARAM_NULL_MEMBERS);
   }

   @Test
   void missingFacilities() {
      Parameters allButFacilities = parameters(patients, measure, period);
      Parameters response = client.operation().onServer().named(MctConstants.GATHER_OPERATION_NAME).withParameters(allButFacilities).execute();
      validateResponse(response, MctConstants.GATHER_PARAM_NULL_FACILITIES);
   }

   @Test
   void missingMeasure() {
      Parameters allButMeasure = parameters(patients, facilities, period);
      Parameters response = client.operation().onServer().named(MctConstants.GATHER_OPERATION_NAME).withParameters(allButMeasure).execute();
      validateResponse(response, MctConstants.GATHER_PARAM_NULL_MEASURE);
   }

   @Test
   void missingPeriod() {
      Parameters allButPeriod = parameters(patients, facilities, measure);
      Parameters response = client.operation().onServer().named(MctConstants.GATHER_OPERATION_NAME).withParameters(allButPeriod).execute();
      validateResponse(response, MctConstants.GATHER_PARAM_NULL_PERIOD);
   }

   @Test
   void missingPeriodStart() {
      Parameters allButPeriod = parameters(patients, facilities, measure, noPeriodStart);
      Parameters response = client.operation().onServer().named(MctConstants.GATHER_OPERATION_NAME).withParameters(allButPeriod).execute();
      validateResponse(response, MctConstants.GATHER_PARAM_NULL_PERIOD_START);
   }

   @Test
   void missingPeriodEnd() {
      Parameters allButPeriod = parameters(patients, facilities, measure, noPeriodEnd);
      Parameters response = client.operation().onServer().named(MctConstants.GATHER_OPERATION_NAME).withParameters(allButPeriod).execute();
      validateResponse(response, MctConstants.GATHER_PARAM_NULL_PERIOD_END);
   }

   private void validateResponse(Parameters response, String errorMessage) {
      assertTrue(response.hasParameter(MctConstants.SEVERITY_ERROR));
      assertTrue(response.getParameterFirstRep().hasResource());
      assertTrue(response.getParameterFirstRep().getResource() instanceof OperationOutcome);
      OperationOutcome errorResponse = (OperationOutcome) response.getParameterFirstRep().getResource();
      assertTrue(errorResponse.hasIssue());
      OperationOutcome.OperationOutcomeIssueComponent errorIssue = errorResponse.getIssueFirstRep();
      assertTrue(errorIssue.hasCode() && errorIssue.getCode() == OperationOutcome.IssueType.PROCESSING);
      assertTrue(errorIssue.hasSeverity() && errorIssue.getSeverity() == OperationOutcome.IssueSeverity.ERROR);
      assertTrue(errorIssue.hasDiagnostics());
      assertEquals(errorMessage, errorIssue.getDiagnostics());
   }
}
