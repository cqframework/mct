package org.opencds.cqf.mct.api;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.util.OperationOutcomeUtil;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Organization;
import org.opencds.cqf.mct.SpringContext;
import org.opencds.cqf.mct.service.FacilityRegistrationService;

import java.util.List;

public class FacilityRegistrationAPI {

   private final FhirContext fhirContext;
   private final FacilityRegistrationService facilityRegistrationService;

   public FacilityRegistrationAPI(FhirContext fhirContext) {
      this.fhirContext = fhirContext;
      facilityRegistrationService = SpringContext.getBean(FacilityRegistrationService.class);
   }

   @Operation(name = "$register-facilities")
   public OperationOutcome registerFacility(
           @OperationParam(name = "locations") List<Location> locations,
           @OperationParam(name = "organization") Organization organization) {
      OperationOutcome result = new OperationOutcome();
      try {
         if (locations != null && !locations.isEmpty()) {
            facilityRegistrationService.registerFacility(locations);
         }
         else if (organization != null) {
            facilityRegistrationService.registerFacility(organization);
         }
         else {
            OperationOutcomeUtil.addIssue(fhirContext, result, "error", "A list of locations or an organization must be provided", null, "processing");
            return result;
         }
      } catch (Exception e) {
         OperationOutcomeUtil.addIssue(fhirContext, result, "error", e.getMessage(), null, "processing");
         return result;
      }
      OperationOutcomeUtil.addIssue(fhirContext, result, "information", "Successfully registered the facility", null, "informational");
      return result;
   }

   @Operation(name = "$unregister-facilities")
   public OperationOutcome unregisterFacility(@OperationParam(name = "facilityId") String facilityId) {
      OperationOutcome result = new OperationOutcome();
      try {
         facilityRegistrationService.unregisterFacility(facilityId);
      } catch (Exception e) {
         OperationOutcomeUtil.addIssue(fhirContext, result, "error", e.getMessage(), null, "processing");
         return result;
      }
      OperationOutcomeUtil.addIssue(fhirContext, result, "information", "Successfully unregistered the facility", null, "informational");
      return result;
   }
}
