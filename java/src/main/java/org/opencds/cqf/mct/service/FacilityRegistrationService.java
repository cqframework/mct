package org.opencds.cqf.mct.service;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Resource;
import org.opencds.cqf.cql.evaluator.engine.retrieve.BundleRetrieveProvider;
import org.opencds.cqf.mct.SpringContext;
import org.opencds.cqf.mct.config.MctConstants;

public class FacilityRegistrationService {

   private final BundleRetrieveProvider bundleRetrieveProvider;

   public FacilityRegistrationService() {
      bundleRetrieveProvider = new BundleRetrieveProvider(
              SpringContext.getBean(FhirContext.class),
              SpringContext.getBean("facilitiesBundle", Bundle.class));
   }

   public Bundle listOrganizations() {
      Bundle orgs = new Bundle().setType(Bundle.BundleType.COLLECTION);
      bundleRetrieveProvider.retrieve(null, null, null, "Organization",
              null, null, null, null, null, null,
              null, null).forEach(x -> orgs.addEntry().setResource((Resource) x));
      return orgs;
   }

   public Bundle listFacilities(String organizationId) {
      Bundle facilities = new Bundle().setType(Bundle.BundleType.COLLECTION);
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
      results.forEach(x -> facilities.addEntry().setResource((Resource) x));
      return facilities;
   }

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
