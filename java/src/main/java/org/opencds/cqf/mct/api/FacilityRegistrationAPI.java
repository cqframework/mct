package org.opencds.cqf.mct.api;

import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import org.hl7.fhir.r4.model.Bundle;
import org.opencds.cqf.mct.SpringContext;
import org.opencds.cqf.mct.config.MctConstants;
import org.opencds.cqf.mct.service.FacilityRegistrationService;

/**
 * The Facility Registration API.
 */
public class FacilityRegistrationAPI {
   private final FacilityRegistrationService facilityRegistrationService;

   /**
    * Instantiates a new Facility Registration API.
    */
   public FacilityRegistrationAPI() {
      facilityRegistrationService = SpringContext.getBean(FacilityRegistrationService.class);
   }

   /**
    * The $list-organizations operation.
    *
    * @return a bundle with all the configured <a href="http://hl7.org/fhir/organization.html">Organization</a> resources
    */
   @Operation(name = MctConstants.LIST_ORGANIZATIONS_OPERATION_NAME, idempotent = true)
   public Bundle listOrganizations() {
      return facilityRegistrationService.listOrganizations();
   }

   /**
    * The $list-facilities operation.
    *
    * @param organizationId the organization id
    * @return a bundle with all facilities (<a href="http://hl7.org/fhir/location.html">Location</a> resources) referencing the <a href="http://hl7.org/fhir/organization.html">Organization</a>
    */
   @Operation(name = MctConstants.LIST_FACILITIES_OPERATION_NAME, idempotent = true)
   public Bundle listFacilities(@OperationParam(name = MctConstants.LIST_FACILITIES_PARAM) String organizationId) {
      return facilityRegistrationService.listFacilities(organizationId);
   }
}
