package org.opencds.cqf.mct.api;

import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IntegerType;
import org.opencds.cqf.mct.config.MctConstants;
import org.opencds.cqf.mct.data.PatientData;

import java.io.IOException;

public class GeneratePatientDataAPI {

   @Operation(name = MctConstants.GENERATE_PATIENT_DATA_OPERATION_NAME, idempotent = true)
   public Bundle generatePatientData(@OperationParam(name = "numTestCases") IntegerType numTestCases) throws IOException, NoSuchMethodException {
      if (numTestCases == null) numTestCases = new IntegerType(200);
      return new PatientData().generatePatientData(numTestCases.getValue());
   }
}
