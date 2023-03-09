package org.opencds.cqf.mct.api;

import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import org.hl7.fhir.r4.model.Group;
import org.opencds.cqf.mct.config.MctConstants;
import org.opencds.cqf.mct.service.PatientSelectorService;

import java.util.List;

/**
 * The Patient selector API.
 */
public class PatientSelectorAPI {

   /**
    * The $list-org-patients operation.
    *
    * @param organizationId the organization id
    * @return the accessible patients for the specified organization populated in a <a href="http://hl7.org/fhir/group.html">Group</a> resource.
    */
   @Operation(name = MctConstants.PATIENT_SELECTOR_ORG_OPERATION_NAME, idempotent = true)
   public Group listPatientsByOrganization(@OperationParam(name = MctConstants.PATIENT_SELECTOR_ORG_ID_PARAM) String organizationId) {
      return new PatientSelectorService().getPatientsForOrganization(organizationId);
   }

   /**
    * The $list-facility-patients operation.
    *
    * @param facilityIds the facility ids
    * @return the accessible patients for the specified facilities populated in a <a href="http://hl7.org/fhir/group.html">Group</a> resource.
    */
   @Operation(name = MctConstants.PATIENT_SELECTOR_FACILITY_OPERATION_NAME, idempotent = true)
   public Group listPatientsByFacility(@OperationParam(name = MctConstants.PATIENT_SELECTOR_FACILITY_IDS_PARAM) List<String> facilityIds) {
      return new PatientSelectorService().getPatientsForFacilities(facilityIds);
   }
}
