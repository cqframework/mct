package org.opencds.cqf.mct.api;

import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import org.hl7.fhir.r4.model.Bundle;
import org.opencds.cqf.mct.SpringContext;
import org.opencds.cqf.mct.config.MctConstants;
import org.opencds.cqf.mct.service.FacilityRegistrationService;

public class FacilityRegistrationAPI {
   private final FacilityRegistrationService facilityRegistrationService;

   public FacilityRegistrationAPI() {
      facilityRegistrationService = SpringContext.getBean(FacilityRegistrationService.class);
   }

   @Operation(name = MctConstants.LIST_ORGANIZATIONS_OPERATION_NAME, idempotent = true)
   public Bundle listOrganizations() {
      return facilityRegistrationService.listOrganizations();
   }

   @Operation(name = MctConstants.LIST_FACILITIES_OPERATION_NAME, idempotent = true)
   public Bundle listFacilities(@OperationParam(name = MctConstants.LIST_FACILITIES_PARAM) String organizationId) {
      return facilityRegistrationService.listFacilities(organizationId);
   }
}
