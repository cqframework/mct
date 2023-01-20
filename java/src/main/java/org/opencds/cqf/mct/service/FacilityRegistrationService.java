package org.opencds.cqf.mct.service;

import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Resource;
import org.opencds.cqf.mct.config.MctConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FacilityRegistrationService {

   private final Map<String, Location> locationMap;
   private final Map<String, Organization> organizationMap;
   private final Map<String, Endpoint> fhirEndpointMap;

   public FacilityRegistrationService() {
      locationMap = new HashMap<>();
      organizationMap = new HashMap<>();
      fhirEndpointMap = new HashMap<>();
   }

   public void registerFacility(List<Location> locations) {
      for (Location location : locations) {
         resolveFhirEndpoint(location);
         locationMap.put(location.getIdElement().getValue(), location);
      }
   }

   public void registerFacility(Organization organization) {
      resolveFhirEndpoint(organization);
      organizationMap.put(organization.getIdElement().getValue(), organization);
   }

   public void unregisterFacility(String facilityId) {
      Object removedLocation = locationMap.remove(facilityId);
      Object removedOrg = organizationMap.remove(facilityId);
      fhirEndpointMap.remove(facilityId);
      if (removedLocation == null && removedOrg == null) {
         throw new FHIRException("Facility: " + facilityId + " not found");
      }
   }

   private void resolveFhirEndpoint(DomainResource parent) {
      for (Resource containedResource : parent.getContained()) {
         if (containedResource instanceof Endpoint) {
            Endpoint endpoint = (Endpoint) containedResource;
            if (endpoint.hasConnectionType() && endpoint.getConnectionType().hasCode()
                    && endpoint.getConnectionType().getCode().equals(MctConstants.FHIR_REST_CONNECTION_TYPE)) {
               fhirEndpointMap.put(parent.getIdElement().getValue(), endpoint);
               return;
            }
         }
      }
      throw new FHIRException(MctConstants.MISSING_FHIR_REST_ENDPOINT);
   }

   public String getFhirUrl(String facilityId) {
      if (fhirEndpointMap.containsKey(facilityId)) {
         return fhirEndpointMap.get(facilityId).getAddress();
      }
      else return null;
   }
}
