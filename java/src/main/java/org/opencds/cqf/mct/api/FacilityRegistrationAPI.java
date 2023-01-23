package org.opencds.cqf.mct.api;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.util.OperationOutcomeUtil;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Organization;
import org.opencds.cqf.mct.SpringContext;
import org.opencds.cqf.mct.config.MctConstants;
import org.opencds.cqf.mct.service.FacilityRegistrationService;

import java.util.List;

public class FacilityRegistrationAPI {

   private final FhirContext fhirContext;
   private final FacilityRegistrationService facilityRegistrationService;

   public FacilityRegistrationAPI() {
      fhirContext = SpringContext.getBean(FhirContext.class);
      facilityRegistrationService = SpringContext.getBean(FacilityRegistrationService.class);
   }

   @Operation(name = MctConstants.REGISTER_OPERATION_NAME)
   public OperationOutcome registerFacility(
           @OperationParam(name = MctConstants.REGISTER_PARAM_LOCATIONS) List<Location> locations,
           @OperationParam(name = MctConstants.REGISTER_PARAM_ORGANIZATION) Organization organization) {
      OperationOutcome result = new OperationOutcome();
      try {
         if (locations != null && !locations.isEmpty()) {
            facilityRegistrationService.registerFacility(locations);
         }
         else if (organization != null) {
            facilityRegistrationService.registerFacility(organization);
         }
         else {
            OperationOutcomeUtil.addIssue(fhirContext, result, MctConstants.SEVERITY_ERROR, MctConstants.REGISTER_MISSING_PARAMS, null, MctConstants.CODE_PROCESSING);
            return result;
         }
      } catch (Exception e) {
         OperationOutcomeUtil.addIssue(fhirContext, result, MctConstants.SEVERITY_ERROR, e.getMessage(), null, MctConstants.CODE_PROCESSING);
         return result;
      }
      OperationOutcomeUtil.addIssue(fhirContext, result, MctConstants.SEVERITY_INFORMATION, MctConstants.REGISTER_SUCCESS, null, MctConstants.CODE_INFORMATIONAL);
      return result;
   }

   @Operation(name = MctConstants.UNREGISTER_OPERATION_NAME, idempotent = true)
   public OperationOutcome unregisterFacility(
           @OperationParam(name = MctConstants.UNREGISTER_PARAM_FACILITY_ID) String facilityId) {
      OperationOutcome result = new OperationOutcome();
      if (facilityId == null || facilityId.isBlank()) {
         OperationOutcomeUtil.addIssue(fhirContext, result, MctConstants.SEVERITY_ERROR, MctConstants.UNREGISTER_MISSING_PARAMS, null, MctConstants.CODE_PROCESSING);
         return result;
      }
      try {
         facilityRegistrationService.unregisterFacility(facilityId);
      } catch (Exception e) {
         OperationOutcomeUtil.addIssue(fhirContext, result, MctConstants.SEVERITY_ERROR, e.getMessage(), null, MctConstants.CODE_PROCESSING);
         return result;
      }
      OperationOutcomeUtil.addIssue(fhirContext, result, MctConstants.SEVERITY_INFORMATION, MctConstants.UNREGISTER_SUCCESS, null, MctConstants.CODE_INFORMATIONAL);
      return result;
   }
}
