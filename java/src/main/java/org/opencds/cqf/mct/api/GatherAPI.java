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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.opencds.cqf.cql.evaluator.fhir.util.r4.Parameters.parameters;
import static org.opencds.cqf.cql.evaluator.fhir.util.r4.Parameters.part;

/**
 * The Gather API.
 */
public class GatherAPI {

   /**
    * The $gather operation.
    *
    * @param patients          the <a href="http://hl7.org/fhir/group.html">Group</a> of <a href="http://hl7.org/fhir/patient.html">Patient</a> resource references
    * @param facilities        the facilities
    * @param measureIdentifier the measure identifier
    * @param period            the measurement <a href="http://hl7.org/fhir/datatypes.html#Period">Period</a>
    * @return the population-level and patient-level reports, evaluated resources and validation messages populated
    * in a <a href="http://hl7.org/fhir/parameters.html">Parameters</a> resource
    */
   @Operation(name = MctConstants.GATHER_OPERATION_NAME)
   public Parameters gather(@OperationParam(name = MctConstants.GATHER_PARAM_PATIENTS) Group patients,
                        @OperationParam(name = MctConstants.GATHER_PARAM_FACILITIES) List<String> facilities,
                        @OperationParam(name = MctConstants.GATHER_PARAM_MEASURE) String measureIdentifier,
                        @OperationParam(name = MctConstants.GATHER_PARAM_PERIOD) Period period) {
      try {
         validateParameters(patients, facilities, measureIdentifier, period);
      } catch (Exception e) {
         OperationOutcome outcome = new OperationOutcome();
         OperationOutcomeUtil.addIssue(SpringContext.getBean(FhirContext.class), outcome,
                 MctConstants.SEVERITY_ERROR, e.getMessage(), null, MctConstants.CODE_PROCESSING);
         return parameters(
                 part(MctConstants.SEVERITY_ERROR, outcome)
         );
      }
      return new GatherService().gather(patients, facilities, measureIdentifier, period);
   }

   /**
    * Validates input parameters.
    *
    * @param patients               the patients
    * @param facilities             the facilities
    * @param measureIdentifier      the measure identifier
    * @param period                 the measurement period
    * @throws NullPointerException  missing parameter values are present - transformed into an <a href="http://hl7.org/fhir/operationoutcome.html">OperationOutcome</a> resource
    */
   public void validateParameters(Group patients, List<String> facilities, String measureIdentifier, Period period) {
      checkNotNull(patients, MctConstants.GATHER_PARAM_NULL_PATIENTS);
      checkArgument(patients.hasMember(), MctConstants.GATHER_PARAM_NULL_MEMBERS);
      checkNotNull(facilities, MctConstants.GATHER_PARAM_NULL_FACILITIES);
      checkNotNull(measureIdentifier, MctConstants.GATHER_PARAM_NULL_MEASURE);
      checkNotNull(period, MctConstants.GATHER_PARAM_NULL_PERIOD);
      checkNotNull(period.getStart(), MctConstants.GATHER_PARAM_NULL_PERIOD_START);
      checkNotNull(period.getEnd(), MctConstants.GATHER_PARAM_NULL_PERIOD_END);
   }

}
