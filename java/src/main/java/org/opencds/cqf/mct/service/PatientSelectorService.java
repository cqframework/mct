package org.opencds.cqf.mct.service;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Group;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.opencds.cqf.mct.SpringContext;
import org.opencds.cqf.mct.data.PatientData;

import java.util.List;
import java.util.stream.Collectors;

public class PatientSelectorService {
   private final PatientData patientDataService;
   private final FacilityRegistrationService facilityRegistrationService;

   public PatientSelectorService() {
      patientDataService = new PatientData();
      facilityRegistrationService = SpringContext.getBean(FacilityRegistrationService.class);
   }

   public Group getPatientsForOrganization(String organizationId) {
      List<String> facilities = facilityRegistrationService.getLocations(organizationId).stream().map(Resource::getId).collect(Collectors.toList());
      return getPatientsForFacilities(facilities);
   }

   public Group getPatientsForFacilities(List<String> facilities) {
      Group group = new Group();
      for (String facility : facilities) {
         Bundle patients = patientDataService.getPatientsFromFacility(facilityRegistrationService.getFacilityUrl(facility));
         for (Bundle.BundleEntryComponent bundleComponent: patients.getEntry()) {
            group.addMember().setEntity(new Reference("Patient/" + bundleComponent.getResource().getIdElement().getIdPart()));
         }
      }
      return group;
   }
}
