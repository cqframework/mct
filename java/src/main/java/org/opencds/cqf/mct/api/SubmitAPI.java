package org.opencds.cqf.mct.api;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.util.OperationOutcomeUtil;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.MeasureReport;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Resource;
import org.opencds.cqf.mct.SpringContext;
import org.opencds.cqf.mct.config.MctConstants;

import java.util.ArrayList;
import java.util.List;
import static org.opencds.cqf.cql.evaluator.fhir.util.r4.Parameters.parameters;
import static org.opencds.cqf.cql.evaluator.fhir.util.r4.Parameters.part;

public class SubmitAPI {

   private final FhirContext fhirContext;
   private MeasureReport populationMeasureReport;
   private final List<SubmitDataParameters> submitDataParameters;

   public SubmitAPI() {
      fhirContext = SpringContext.getBean(FhirContext.class);
      populationMeasureReport = null;
      submitDataParameters = new ArrayList<>();
   }

   @Operation(name = MctConstants.SUBMIT_OPERATION_NAME, global = true)
   public OperationOutcome submit(
           @OperationParam(name = MctConstants.SUBMIT_REC_SYSTEM_PARAM) String recSystemUrl,
           @OperationParam(name = MctConstants.SUBMIT_GATHER_RESULT_PARAM) Parameters gatherResult) {
      IGenericClient client = fhirContext.newRestfulGenericClient(recSystemUrl);
      processGatherResult(gatherResult);
      for (SubmitDataParameters parameters : submitDataParameters) {
         client.operation().onInstance(parameters.getMeasureId()).named("$submit-data")
                 .withParameters(parameters.getParameters()).execute();
      }
      client.create().resource(populationMeasureReport).execute();

      OperationOutcome outcome = new OperationOutcome();
      OperationOutcomeUtil.addIssue(fhirContext, outcome, MctConstants.SEVERITY_INFORMATION, "Successfully submitted data to the receiving system", null, MctConstants.CODE_INFORMATIONAL);
      return outcome;
   }

   private void processGatherResult(Parameters gatherResult) {
      for (Parameters.ParametersParameterComponent paramComponent : gatherResult.getParameter()) {
         if (paramComponent.getName().equals(MctConstants.GATHER_POP_MEASURE_PARAM_NAME)
                 && paramComponent.hasResource() && paramComponent.getResource() instanceof MeasureReport) {
            populationMeasureReport = (MeasureReport) paramComponent.getResource();
         }
         else if (paramComponent.hasResource() && paramComponent.getResource() instanceof Bundle) {
            submitDataParameters.add(new SubmitDataParameters((Bundle) paramComponent.getResource()));
         }
      }
   }

   private static class SubmitDataParameters {
      private IdDt measureId;
      private MeasureReport patientReport;
      private final List<Resource> patientResources;

      private final Parameters parameters;

      public SubmitDataParameters(Bundle patientBundle) {
         patientResources = new ArrayList<>();
         parameters = parameters();
         parseBundle(patientBundle);
      }

      private void parseBundle(Bundle bundleToParse) {
         for (Bundle.BundleEntryComponent entry : bundleToParse.getEntry()) {
            if (entry.hasResource() && entry.getResource() instanceof MeasureReport) {
               patientReport = (MeasureReport) entry.getResource();
               measureId = new IdDt(patientReport.getMeasure());
               parameters.addParameter(part("measureReport", patientReport));
            }
            else if (entry.hasResource() && !(entry.getResource() instanceof OperationOutcome)) {
               patientResources.add(entry.getResource());
               parameters.addParameter(part("resource", entry.getResource()));
            }
         }
      }

      public IdDt getMeasureId() {
         return measureId;
      }

      public MeasureReport getPatientReport() {
         return patientReport;
      }

      public List<Resource> getPatientResources() {
         return patientResources;
      }

      public Parameters getParameters() {
         return parameters;
      }
   }
}
