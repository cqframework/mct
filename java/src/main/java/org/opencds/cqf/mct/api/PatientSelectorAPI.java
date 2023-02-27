package org.opencds.cqf.mct.api;

import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import org.hl7.fhir.r4.model.Group;
import org.opencds.cqf.mct.SpringContext;
import org.opencds.cqf.mct.config.MctConstants;
import org.opencds.cqf.mct.service.PatientSelectorService;

import java.util.List;

public class PatientSelectorAPI {

   @Operation(name = MctConstants.PATIENT_SELECTOR_OPERATION_NAME, idempotent = true)
   public Group listPatientsByOrganization(@OperationParam(name = MctConstants.PATIENT_SELECTOR_ORG_ID_PARAM) String organizationId) {
      return new PatientSelectorService().getPatientsForOrganization(organizationId);
   }

   @Operation(name = MctConstants.PATIENT_SELECTOR_OPERATION_NAME, idempotent = true)
   public Group listPatientsByFacility(@OperationParam(name = MctConstants.PATIENT_SELECTOR_FACILITY_IDS_PARAM) List<String> facilityIds) {
      return new PatientSelectorService().getPatientsForFacilities(facilityIds);
   }
}
