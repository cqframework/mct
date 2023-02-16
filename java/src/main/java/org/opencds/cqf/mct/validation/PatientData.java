package org.opencds.cqf.mct.validation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.util.SearchParameterUtil;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Period;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PatientData {

   private DataRequirementsReport dataRequirementsReport;
   private Bundle patients;
   private Bundle patientDataBundle;

   private FhirContext fhirContext;

   public Bundle getPatients(String facilityUrl) {
      if (patients == null) {
         IGenericClient client = fhirContext.newRestfulGenericClient(facilityUrl);
         patients = (Bundle) client.search().forResource(Patient.class).count(500).execute();
      }
      return patients;
   }

   public Bundle getPatientDataBundle(String facilityUrl, String facility, String patientId, Period period, Map<String, Map<String, List<IQueryParameterType>>> searchParams) {
      IGenericClient client = fhirContext.newRestfulGenericClient(facilityUrl);
      Bundle result = new Bundle();
      for (Map.Entry<String, Map<String, List<IQueryParameterType>>> entry : searchParams.entrySet()) {
         String type = entry.getKey();
         Map<String, List<IQueryParameterType>> pathMap = entry.getValue();
         String patientParamPath = SearchParameterUtil.getOnlyPatientCompartmentRuntimeSearchParam(fhirContext, type).getPath();
         pathMap.put(patientParamPath, Collections.singletonList(new ReferenceParam(patientId)));
         result.getEntry().addAll(((Bundle) client.search().forResource(type).where(pathMap).execute()).getEntry());
      }
      return result;
   }

}
