package org.opencds.cqf.mct.api;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.util.OperationOutcomeUtil;
import org.hl7.fhir.r4.model.Group;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Period;
import org.opencds.cqf.mct.service.GatherService;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;
import static org.opencds.cqf.cql.evaluator.fhir.util.r4.Parameters.parameters;
import static org.opencds.cqf.cql.evaluator.fhir.util.r4.Parameters.part;

public class GatherAPI {

   private final FhirContext fhirContext;

   public GatherAPI(FhirContext fhirContext) {
      this.fhirContext = fhirContext;
   }

   @Operation(name = "$gather")
   public Parameters gather(@OperationParam(name = "patients") Group patients,
                        @OperationParam(name = "facilities") List<String> facilities,
                        @OperationParam(name = "measure") String measureIdentifier,
                        @OperationParam(name = "period") Period period) {
      try {
         validateParameters(patients, facilities, measureIdentifier, period);
      } catch (Exception e) {
         return parameters(
                 part("error", generateOutcome("error", e.getMessage(), "invalid"))
         );
      }
      return new GatherService().gatherOperation(patients, facilities, measureIdentifier, period);
   }

   public void validateParameters(Group patients, List<String> facilities, String measureIdentifier, Period period) {
      checkNotNull(patients, "Missing required Group FHIR resource \"patients\" parameter that identifies the patients for the $gather operation");
      checkArgument(patients.hasMember(), "Group FHIR resource \"patients\" parameter has no members");
      checkNotNull(facilities, "Missing required List<String> \"facilities\" parameter that identifies the facilities for the $gather operation");
      checkNotNull(measureIdentifier, "Missing required Measure FHIR resource identifier \"measure\" parameter that identifies the measure for the $gather operation");
      checkNotNull(period, "Missing required Period FHIR data type \"period\" parameter that identifies the measurement period for the $gather operation");
      checkNotNull(period.getStart(), "Period FHIR data type \"period\" parameter is missing a start date");
      checkNotNull(period.getEnd(), "Period FHIR data type \"period\" parameter is missing an end date");
   }

   public OperationOutcome generateOutcome(String severity, String details, String code) {
      OperationOutcome outcome = new OperationOutcome();
      OperationOutcomeUtil.addIssue(fhirContext, outcome, severity, details, null, code);
      return outcome;
   }

}
