package org.opencds.cqf.mct.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.util.SearchParameterUtil;
import org.cqframework.cql.cql2elm.CqlTranslatorOptions;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.LibrarySourceLoader;
import org.cqframework.cql.cql2elm.LibrarySourceProvider;
import org.cqframework.cql.cql2elm.ModelManager;
import org.cqframework.cql.cql2elm.PriorityLibrarySourceLoader;
import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.fhir.convertors.advisors.impl.BaseAdvisor_40_50;
import org.hl7.fhir.convertors.conv40_50.VersionConvertor_40_50;
import org.hl7.fhir.r4.model.Measure;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r5.model.DataRequirement;
import org.hl7.fhir.r5.model.Library;
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo;
import org.opencds.cqf.mct.SpringContext;
import org.opencds.cqf.mct.processor.DataRequirementsProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The Measure Data Requirement Service.
 */
public class MeasureDataRequirementService {
   private final FhirContext fhirContext;
   private final Measure measure;
   private final Map<String, Map<String, List<IQueryParameterType>>> searchParamMap;
   private final Map<String, Map<String, List<ValueSetInfo>>> valuesetInfoMap;
   private final Map<String, List<String>> profileMap;

   /**
    * Instantiates a new Measure Data Requirement Service.
    *
    * @param measure the measure
    */
   public MeasureDataRequirementService(Measure measure) {
      fhirContext = SpringContext.getBean(FhirContext.class);
      this.measure = measure;
      valuesetInfoMap = new HashMap<>();
      profileMap = new HashMap<>();
      searchParamMap = getSearchParamsByType();
   }

   /**
    * Gets search parameter map.
    *
    * @return the search parameter map
    */
   public Map<String, Map<String, List<IQueryParameterType>>> getSearchParamMap() {
      return searchParamMap;
   }

   /**
    * Gets valueset info map.
    *
    * @return the valueset info map
    */
   public Map<String, Map<String, List<ValueSetInfo>>> getValuesetInfoMap() {
      return valuesetInfoMap;
   }

   /**
    * Gets profile map.
    *
    * @return the profile map
    */
   public Map<String, List<String>> getProfileMap() {
      return profileMap;
   }

   /**
    * Gets search parameter map for the specified patient.
    *
    * @see FacilityDataService#getPatientData(MeasureDataRequirementService, GatherService.PatientBundle) 
    * @param patientId the patient id
    * @return the patient-specific search parameter map
    */
   public Map<String, Map<String, List<IQueryParameterType>>> getSearchParamMapForPatient(String patientId) {
      Map<String, Map<String, List<IQueryParameterType>>> patientSearchParamMap = new HashMap<>(searchParamMap);
      patientSearchParamMap.forEach(
              (key, value) -> {
                 if (key.equals("Patient")) {
                    value.put("_id", Collections.singletonList(new StringParam(patientId)));
                 } else {
                    SearchParameterUtil.getOnlyPatientSearchParamForResourceType(fhirContext, key).ifPresent(
                            patientParam -> value.put(patientParam.getName(),
                                    Collections.singletonList(new ReferenceParam(patientId)))
                    );
                 }
              }
      );
      return patientSearchParamMap;
   }

   private Library getDataRequirementLibrary() {
      DataRequirementsProcessor processor = new DataRequirementsProcessor();
      LibraryManager libraryManager = new LibraryManager(new ModelManager());
      LibrarySourceLoader librarySourceLoader = new PriorityLibrarySourceLoader();
      librarySourceLoader.registerProvider(SpringContext.getBean(LibrarySourceProvider.class));
      libraryManager.setLibrarySourceLoader(librarySourceLoader);
      CqlTranslatorOptions options = CqlTranslatorOptions.defaultOptions();
      CompiledLibrary library = libraryManager.resolveLibrary(
              new VersionedIdentifier()
                      .withId("DischargedonAntithromboticTherapyQICore4")
                      .withVersion("0.0.006"), options, new ArrayList<>()
      );
      return processor.gatherDataRequirements(libraryManager, library, options, null, true, true);
   }

   private List<DataRequirement> getDataRequirements() {
      Library dataReqLibrary = getDataRequirementLibrary();
      return dataReqLibrary.getDataRequirement();
   }

   /**
    * Gets search parameters by type and populates the valueset info map.
    *
    * @return the search parameters by type
    */
   public Map<String, Map<String, List<IQueryParameterType>>> getSearchParamsByType() {
      Optional<Resource> hasDataReqLibrary = measure.getContained().stream()
              .filter(x -> x instanceof org.hl7.fhir.r4.model.Library
                      && x.getIdElement().getIdPart().equals("effective-data-requirements")
              ).findFirst();
      List<DataRequirement> dataRequirements;
      if (hasDataReqLibrary.isPresent()) {
         dataRequirements = ((Library) new VersionConvertor_40_50(new BaseAdvisor_40_50()).convertResource(hasDataReqLibrary.get())).getDataRequirement();
      }
      else {
         dataRequirements = getDataRequirements();
      }
      Map<String, Map<String, List<IQueryParameterType>>> result = new HashMap<>();
      for (DataRequirement dataRequirement: dataRequirements) {
         if (!dataRequirement.hasType()) continue;
         String fhirType = dataRequirement.getType().toCode();
         result.computeIfAbsent(fhirType, x -> new HashMap<>());
         if (dataRequirement.hasProfile()) {
            profileMap.computeIfAbsent(fhirType, x -> new ArrayList<>());
            dataRequirement.getProfile().forEach(
                    profile -> {
                       if (!profileMap.get(fhirType).contains(profile.getValueAsString())) {
                          profileMap.get(fhirType).add(profile.getValueAsString());
                       }
                    }
            );
         }
         if (dataRequirement.hasCodeFilter()) {
            valuesetInfoMap.computeIfAbsent(fhirType, x -> new HashMap<>());
            dataRequirement.getCodeFilter().forEach(
                    codeFilter -> {
                       valuesetInfoMap.get(fhirType).computeIfAbsent(codeFilter.getPath(), x -> new ArrayList<>());
                       valuesetInfoMap.get(fhirType).get(codeFilter.getPath()).add(new ValueSetInfo().withId(codeFilter.getValueSet()));
                    }
            );
         }
      }
      return result;
   }
}
