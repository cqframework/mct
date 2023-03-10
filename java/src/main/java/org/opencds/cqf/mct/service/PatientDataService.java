package org.opencds.cqf.mct.service;

import org.hl7.fhir.r4.model.Group;
import org.hl7.fhir.r4.model.Period;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Patient Data Service.
 */
public class PatientDataService {
   private final ValidationService validationService;
   private final List<String> patientIds;
   private final List<FacilityDataService> facilityDataServices;

   /**
    * Instantiates a new Patient Data Service.
    *
    * @param patients   the patients
    * @param facilities the facilities
    */
   public PatientDataService(Group patients, List<String> facilities) {
      validationService = new ValidationService();
      patientIds =
              patients.getMember().stream().map(
                      x -> x.getEntity().getReference().replace("Patient/", "")
              ).collect(Collectors.toList());
      facilityDataServices = facilities.stream().map(FacilityDataService::new).collect(Collectors.toList());
   }

   /**
    * Resolve patient bundles for the facilities.
    *
    * @see GatherService#gather(Group, List, String, Period)
    * @param measureEvaluationService the {@link MeasureEvaluationService}
    * @return the list of {@link org.opencds.cqf.mct.service.GatherService.PatientBundle} types for each facility
    */
   public List<GatherService.PatientBundle> resolvePatientBundles(MeasureEvaluationService measureEvaluationService) {
      List<GatherService.PatientBundle> patientBundles = new ArrayList<>();
      facilityDataServices.forEach(
              facilityDataService -> {
                 for (String patientId : facilityDataService.getFacilityPatientsFromGroup(patientIds)) {
                    GatherService.PatientBundle patientBundle = new GatherService.PatientBundle();
                    patientBundle.setPatientId(patientId);
                    facilityDataService.getPatientData(measureEvaluationService.getMeasureDataRequirementsService(), patientBundle);
                    validationService.validate(patientBundle, facilityDataService.getFacilityId(), measureEvaluationService.getMeasureDataRequirementsService().getProfileMap());
                    patientBundle.setPatientReport(measureEvaluationService.getPatientReport(patientBundle));
                    patientBundles.add(patientBundle);
                 }
              }
      );
      return patientBundles;
   }

   /**
    * Gets patient references.
    *
    * @return the list of patient references
    */
   public List<String> getPatientReferences() {
      return patientIds.stream().map(patientId -> "Patient/" + patientId).collect(Collectors.toList());
   }
}
