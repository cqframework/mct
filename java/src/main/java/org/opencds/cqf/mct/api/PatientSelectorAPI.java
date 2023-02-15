package org.opencds.cqf.mct.api;

import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import org.hl7.fhir.r4.model.Group;
import org.opencds.cqf.mct.SpringContext;
import org.opencds.cqf.mct.config.MctConstants;
import org.opencds.cqf.mct.service.PatientSelectorService;

public class PatientSelectorAPI {

   private final PatientSelectorService patientSelectorService;

   public PatientSelectorAPI() {
      patientSelectorService = SpringContext.getBean(PatientSelectorService.class);
   }

   @Operation(name = MctConstants.PATIENT_SELECTOR_OPERATION_NAME, idempotent = true)
   public Group listPatients(@OperationParam(name = MctConstants.PATIENT_SELECTOR_ORG_ID_PARAM) String organizationId) {
      return patientSelectorService.getPatientsForOrganization(organizationId);
   }
}
