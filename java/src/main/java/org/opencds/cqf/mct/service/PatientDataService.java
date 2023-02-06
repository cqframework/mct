package org.opencds.cqf.mct.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.util.DateUtils;
import ca.uhn.fhir.util.OperationOutcomeUtil;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.PrimitiveType;
import org.hl7.fhir.r4.model.Resource;
import org.opencds.cqf.mct.SpringContext;
import org.opencds.cqf.mct.config.MctConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.opencds.cqf.cql.evaluator.fhir.util.r4.Parameters.parameters;
import static org.opencds.cqf.cql.evaluator.fhir.util.r4.Parameters.part;

public class PatientDataService {

   private final FhirContext fhirContext;
   private List<OperationOutcome> missingDataRequirements;

   public PatientDataService() {
      fhirContext = SpringContext.getBean(FhirContext.class);
      missingDataRequirements = new ArrayList<>();
   }

   public Bundle getPatientData(String facilityUrl, String facility, String patientId, Period period, Map<String, String> types) {
      IGenericClient client = fhirContext.newRestfulGenericClient(facilityUrl);
      Parameters inParams = parameters(
              part("start", DateUtils.convertDateToIso8601String(period.getStart())),
              part("end", DateUtils.convertDateToIso8601String(period.getEnd())));
      if (!types.isEmpty()) {
         types.keySet().forEach(x -> inParams.addParameter(part("_type", x)));
      }
      Bundle everythingResult = client.operation().onInstance(patientId).named("$everything")
              .withParameters(inParams).returnResourceType(Bundle.class).execute();
      recordMissingDataRequirements(everythingResult, patientId, types);
      return addTags(everythingResult, facility, types);
   }

   public Bundle getPatientData(String facilityUrl, String facility, List<String> patientIds, Period period, Map<String, String> types) {
      Bundle returnBundle = new Bundle();
      for (String patientId : patientIds) {
         returnBundle.getEntry().addAll(getPatientData(facilityUrl, facility, patientId, period, types).getEntry());
      }
      return returnBundle;
   }

   public List<OperationOutcome> getMissingDataRequirementsAndClear() {
      List<OperationOutcome> missingDataReqs = new ArrayList<>(missingDataRequirements);
      missingDataRequirements = new ArrayList<>();
      return missingDataReqs;
   }

   private Bundle addTags(Bundle patientData, String facility, Map<String, String> types) {
      for (Bundle.BundleEntryComponent component : patientData.getEntry()) {
         if (component.hasResource()) {
            Resource resource = component.getResource();
            resource.getMeta().addTag().setSystem(MctConstants.LOCATION_TAG_SYSTEM).setDisplay(facility);
            if (!resource.getMeta().hasProfile()) {
               resource.getMeta().addProfile(types.get(component.getResource().fhirType()));
            }
            else {
               List<String> profiles = resource.getMeta().getProfile().stream().map(PrimitiveType::getValueAsString).collect(Collectors.toList());
               if (profiles.retainAll(types.values())) {
                  resource.getMeta().addProfile(types.get(component.getResource().fhirType()));
               }
            }
         }
      }
      return patientData;
   }

   private void recordMissingDataRequirements(Bundle bundle, String patientId, Map<String, String> types) {
      List<String> retrievedFhirTypes = bundle.getEntry().stream().map(x -> x.hasResource() ? x.getResource().fhirType() : null).collect(Collectors.toList());
      List<String> allProfiles = new ArrayList<>(types.keySet());
      retrievedFhirTypes.forEach(allProfiles::remove);
      if (!allProfiles.isEmpty()) {
         OperationOutcome missingProfile = new OperationOutcome();
         for (String missingDataReq : allProfiles) {
            OperationOutcomeUtil.addIssue(fhirContext, missingProfile, MctConstants.SEVERITY_INFORMATION,
                    String.format("No %s resources found for patient: %s", missingDataReq, patientId),
                    null, MctConstants.CODE_INFORMATIONAL);
         }
         missingDataRequirements.add(missingProfile);
      }
   }

}
