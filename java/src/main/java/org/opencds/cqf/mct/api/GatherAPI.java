package org.opencds.cqf.mct.api;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.util.OperationOutcomeUtil;
import org.hl7.fhir.r4.model.Group;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Period;
import org.opencds.cqf.mct.SpringContext;
import org.opencds.cqf.mct.config.MctConstants;
import org.opencds.cqf.mct.service.GatherService;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.opencds.cqf.cql.evaluator.fhir.util.r4.Parameters.parameters;
import static org.opencds.cqf.cql.evaluator.fhir.util.r4.Parameters.part;

public class GatherAPI {

   private final FhirContext fhirContext;
   private final GatherService gatherService;

   public GatherAPI() {
      fhirContext = SpringContext.getBean(FhirContext.class);
      gatherService = SpringContext.getBean(GatherService.class);
   }

   @Operation(name = MctConstants.GATHER_OPERATION_NAME)
   public Parameters gather(@OperationParam(name = MctConstants.GATHER_PARAM_PATIENTS) Group patients,
                        @OperationParam(name = MctConstants.GATHER_PARAM_FACILITIES) List<String> facilities,
                        @OperationParam(name = MctConstants.GATHER_PARAM_MEASURE) String measureIdentifier,
                        @OperationParam(name = MctConstants.GATHER_PARAM_PERIOD) Period period) {
      try {
         validateParameters(facilities, measureIdentifier, period);
      } catch (Exception e) {
         return parameters(
                 part(MctConstants.SEVERITY_ERROR, generateOutcome(MctConstants.SEVERITY_ERROR, e.getMessage(), MctConstants.CODE_PROCESSING))
         );
      }
      return gatherService.gatherOperation(patients, facilities, measureIdentifier, period);
   }

   public void validateParameters(List<String> facilities, String measureIdentifier, Period period) {
      checkNotNull(facilities, MctConstants.GATHER_PARAM_NULL_FACILITIES);
      checkNotNull(measureIdentifier, MctConstants.GATHER_PARAM_NULL_MEASURE);
      checkNotNull(period, MctConstants.GATHER_PARAM_NULL_PERIOD);
      checkNotNull(period.getStart(), MctConstants.GATHER_PARAM_NULL_PERIOD_START);
      checkNotNull(period.getEnd(), MctConstants.GATHER_PARAM_NULL_PERIOD_END);
   }

   public OperationOutcome generateOutcome(String severity, String details, String code) {
      OperationOutcome outcome = new OperationOutcome();
      OperationOutcomeUtil.addIssue(fhirContext, outcome, severity, details, null, code);
      return outcome;
   }

}
