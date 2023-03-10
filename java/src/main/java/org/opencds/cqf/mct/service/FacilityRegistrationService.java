package org.opencds.cqf.mct.service;

import ca.uhn.fhir.context.FhirContext;
import org.apache.commons.collections4.IterableUtils;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Resource;
import org.opencds.cqf.cql.evaluator.engine.retrieve.BundleRetrieveProvider;
import org.opencds.cqf.mct.SpringContext;
import org.opencds.cqf.mct.api.FacilityRegistrationAPI;
import org.opencds.cqf.mct.config.MctConstants;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The Facility Registration Service used by the {@link org.opencds.cqf.mct.api.FacilityRegistrationAPI}.
 */
public class FacilityRegistrationService {

   private final BundleRetrieveProvider bundleRetrieveProvider;

   /**
    * Instantiates a new Facility Registration Service.
    */
   public FacilityRegistrationService() {
      bundleRetrieveProvider = new BundleRetrieveProvider(
              SpringContext.getBean(FhirContext.class),
              SpringContext.getBean("facilitiesBundle", Bundle.class));
   }

   /**
    * The $list-organizations operation logic.
    * @see FacilityRegistrationAPI#listOrganizations()
    *
    * @return a bundle with all the configured <a href="http://hl7.org/fhir/organization.html">Organization</a> resources
    */
   public Bundle listOrganizations() {
      Bundle orgs = new Bundle().setType(Bundle.BundleType.COLLECTION);
      bundleRetrieveProvider.retrieve(null, null, null, "Organization",
              null, null, null, null, null, null,
              null, null).forEach(x -> orgs.addEntry().setResource((Resource) x));
      return orgs;
   }

   /**
    * The $list-facilities operation logic.
    * @see  FacilityRegistrationAPI#listFacilities(String)
    *
    * @param organizationId the organization id
    * @return the bundle of all facilities (<a href="http://hl7.org/fhir/location.html">Location</a> resources)
    * referencing the <a href="http://hl7.org/fhir/organization.html">Organization</a>
    */
   public Bundle listFacilities(String organizationId) {
      Bundle facilities = new Bundle().setType(Bundle.BundleType.COLLECTION);
      getLocations(organizationId).forEach(x -> facilities.addEntry().setResource(x));
      return facilities;
   }

   /**
    * Gets the <a href="http://hl7.org/fhir/location.html">Location</a> resources.
    *
    * @see PatientSelectorService#getPatientsForOrganization(String)
    * @param organizationId the organization id
    * @return either all the configured facilities (<a href="http://hl7.org/fhir/location.html">Location</a> resources)
    * or the configured facilities referencing the <a href="http://hl7.org/fhir/organization.html">Organization</a>
    */
   public List<Location> getLocations(String organizationId) {
      Iterable<Object> results;
      if (organizationId == null) {
         results = bundleRetrieveProvider.retrieve(null, null, null,
                 "Location", null, null, null, null,
                 null, null, null, null);
      }
      else {
         if (organizationId.startsWith("Organization/")) {
            organizationId = organizationId.replace("Organization/", "");
         }
         results = bundleRetrieveProvider.retrieve("Organization", "managingOrganization",
                 organizationId, "Location", null, null, null, null,
                 null, null, null, null);
      }
      return IterableUtils.toList(results).stream().map(Location.class::cast).collect(Collectors.toList());
   }

   /**
    * Retrieves the specified facility (<a href="http://hl7.org/fhir/location.html">Location</a> resources).
    *
    * @param locationId the location id
    * @return the facility
    */
   public Location getFacility(String locationId) {
      if (locationId.startsWith("Location/")) {
         locationId = locationId.replace("Location/", "");
      }
      Iterable<Object> results = bundleRetrieveProvider.retrieve("Location", "id",
              locationId, "Location", null, null, null, null,
              null, null, null, null);
      Object result = results.iterator().hasNext() ? results.iterator().next() : null;
      return (Location) result;
   }

   /**
    * Gets the facility url.
    *
    * @see FacilityDataService
    * @param facilityId the facility id
    * @return the facility url
    */
   public String getFacilityUrl(String facilityId) {
      Location facility = getFacility(facilityId);
      for (Resource containedResource : facility.getContained()) {
         if (containedResource instanceof Endpoint) {
            Endpoint endpoint = (Endpoint) containedResource;
            if (endpoint.hasConnectionType() && endpoint.getConnectionType().hasCode() && endpoint.hasAddress()
                    && endpoint.getConnectionType().getCode().equals(MctConstants.FHIR_REST_CONNECTION_TYPE)) {
               return endpoint.getAddress();
            }
         }
      }
      throw new FHIRException(MctConstants.MISSING_FHIR_REST_ENDPOINT);
   }
}
