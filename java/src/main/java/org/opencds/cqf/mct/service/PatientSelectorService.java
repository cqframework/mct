package org.opencds.cqf.mct.service;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Group;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.opencds.cqf.mct.SpringContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Patient Selector Service logic used by the {@link org.opencds.cqf.mct.api.PatientSelectorAPI}.
 */
public class PatientSelectorService {
   private final FacilityRegistrationService facilityRegistrationService;

   /**
    * Instantiates a new Patient Selector Service.
    */
   public PatientSelectorService() {
      facilityRegistrationService = SpringContext.getBean(FacilityRegistrationService.class);
   }

   /**
    * The $list-org-patients operation logic.
    *
    * @see org.opencds.cqf.mct.api.PatientSelectorAPI#listPatientsByOrganization(String)
    * @param organizationId the organization id
    * @return the accessible patients for the specified organization populated in a <a href="http://hl7.org/fhir/group.html">Group</a> resource.
    */
   public Group getPatientsForOrganization(String organizationId) {
      List<String> facilities = facilityRegistrationService.getLocations(organizationId).stream().map(Resource::getId).collect(Collectors.toList());
      return getPatientsForFacilities(facilities);
   }

   /**
    * The $list-facility-patients operation logic.
    *
    * @see org.opencds.cqf.mct.api.PatientSelectorAPI#listPatientsByFacility(List)
    * @param facilities the facilities
    * @return the accessible patients for the specified facilities populated in a <a href="http://hl7.org/fhir/group.html">Group</a> resource.
    */
   public Group getPatientsForFacilities(List<String> facilities) {
      Group group = new Group();
      for (String facility : facilities) {
         FacilityDataService facilityDataService = new FacilityDataService(facility);
         Bundle patients = facilityDataService.getAllFacilityPatients();
         for (Bundle.BundleEntryComponent bundleComponent: patients.getEntry()) {
            group.addMember().setEntity(new Reference("Patient/" + bundleComponent.getResource().getIdElement().getIdPart()));
         }
      }
      return group;
   }
}
