package org.opencds.cqf.mct.api;

import ca.uhn.fhir.rest.annotation.Operation;
import org.hl7.fhir.r4.model.Bundle;
import org.opencds.cqf.mct.SpringContext;
import org.opencds.cqf.mct.config.MctConstants;
import org.opencds.cqf.mct.service.PatientDataService;

import java.io.IOException;

public class GeneratePatientDataAPI {

   private final PatientDataService patientDataService;

   public GeneratePatientDataAPI() {
      patientDataService = SpringContext.getBean(PatientDataService.class);
   }

   @Operation(name = MctConstants.GENERATE_PATIENT_DATA_OPERATION_NAME, idempotent = true)
   public Bundle generatePatientData() throws IOException, NoSuchMethodException {
      return patientDataService.generatePatientData();
   }
}
