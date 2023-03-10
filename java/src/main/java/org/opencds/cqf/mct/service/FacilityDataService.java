package org.opencds.cqf.mct.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.opencds.cqf.mct.SpringContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The Facility Data Service.
 */
public class FacilityDataService {
   private final String facilityId;
   private final IGenericClient facilityClient;

   /**
    * Instantiates a new Facility Data Service.
    *
    * @param facilityId the facility id
    */
   public FacilityDataService(String facilityId) {
      this.facilityId = facilityId;
      facilityClient = SpringContext.getBean(FhirContext.class).newRestfulGenericClient(
              SpringContext.getBean(FacilityRegistrationService.class).getFacilityUrl(facilityId)
      );
   }

   /**
    * Gets facility id.
    *
    * @return the facility id
    */
   public String getFacilityId() {
      return facilityId;
   }

   /**
    * Gets all facility patients.
    *
    * @return the all facility patients
    */
   public Bundle getAllFacilityPatients() {
      return facilityClient.search().forResource(Patient.class).count(500).returnBundle(Bundle.class).execute();
   }

   /**
    * Gets the patients present in the facility and the passed in list of patient ids.
    *
    * @param patientIds the patient ids
    * @return a list of patient ids from the facility that overlap with the patientIds parameter
    */
   public List<String> getFacilityPatientsFromGroup(List<String> patientIds) {
      return getAllFacilityPatients().getEntry().stream().filter(
              x -> patientIds.contains(x.getResource().getIdElement().getIdPart())).map(
                      x -> x.getResource().getIdElement().getIdPart()).collect(Collectors.toList());
   }

   /**
    * Gets patient data and populates the patient bundle.
    *
    * @param measureDataRequirementService the measure data requirement service {@link MeasureDataRequirementService}
    * @param patientBundle                 the patient bundle {@link org.opencds.cqf.mct.service.GatherService.PatientBundle}
    */
   public void getPatientData(MeasureDataRequirementService measureDataRequirementService, GatherService.PatientBundle patientBundle) {
      measureDataRequirementService.getSearchParamMapForPatient(patientBundle.getPatientId()).forEach(
              (key, value) -> {
                 Bundle searchResult = (Bundle) facilityClient.search().forResource(key).where(value).execute();
                 if (searchResult.hasEntry()) {
                    patientBundle.getPatientData().getEntry().addAll(searchResult.getEntry());
                 }
                 else {
                    patientBundle.addMissingPatientData(key, measureDataRequirementService.getValuesetInfoMap().get(key));
                 }
              }
      );
   }
}
