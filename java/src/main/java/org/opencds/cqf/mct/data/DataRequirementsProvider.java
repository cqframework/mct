package org.opencds.cqf.mct.data;

import ca.uhn.fhir.rest.param.InternalCodingDt;
import org.cqframework.cql.cql2elm.CqlTranslatorOptions;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.LibrarySourceLoader;
import org.cqframework.cql.cql2elm.LibrarySourceProvider;
import org.cqframework.cql.cql2elm.ModelManager;
import org.cqframework.cql.cql2elm.PriorityLibrarySourceLoader;
import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.fhir.r5.model.CanonicalType;
import org.hl7.fhir.r5.model.DataRequirement;
import org.hl7.fhir.r5.model.Library;
import org.hl7.fhir.r5.model.PrimitiveType;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo;
import org.opencds.cqf.mct.SpringContext;
import org.opencds.cqf.mct.processor.DataRequirementsProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DataRequirementsProvider {
   private final LibrarySourceProvider librarySourceProvider;
   private final TerminologyProvider terminologyProvider;
   private final Map<String, QueryAggregator> queryMap;
   private final Map<String, Set<String>> resourceProfileMap;

   public DataRequirementsProvider() {
      librarySourceProvider = SpringContext.getBean(LibrarySourceProvider.class);
      terminologyProvider = SpringContext.getBean(TerminologyProvider.class);
      queryMap = new HashMap<>();
      resourceProfileMap = new HashMap<>();
   }

   private Library getDataRequirementLibrary() {
      DataRequirementsProcessor processor = new DataRequirementsProcessor();
      LibraryManager libraryManager = new LibraryManager(new ModelManager());
      LibrarySourceLoader librarySourceLoader = new PriorityLibrarySourceLoader();
      librarySourceLoader.registerProvider(librarySourceProvider);
      libraryManager.setLibrarySourceLoader(librarySourceLoader);
      CqlTranslatorOptions options = CqlTranslatorOptions.defaultOptions();
      CompiledLibrary library = libraryManager.resolveLibrary(new VersionedIdentifier().withId("DischargedonAntithromboticTherapyQICore4").withVersion("0.0.006"), options, new ArrayList<>());
      return processor.gatherDataRequirements(libraryManager, library, options, null, true, true);
   }

   public Map<String, QueryAggregator> getQueryMap() {
      if (queryMap.isEmpty()) {
         List<DataRequirement> dataRequirements = getDataRequirementLibrary().getDataRequirement();
         for (DataRequirement dataRequirement : dataRequirements) {
            resolveDataRequirement(dataRequirement);
         }
      }
      return queryMap;
   }

   public Map<String, Set<String>> getResourceProfileMap() {
      if (resourceProfileMap.isEmpty()) {
         getQueryMap();
      }
      return resourceProfileMap;
   }

   private void resolveDataRequirement(DataRequirement dataRequirement) {
      if (!dataRequirement.hasType()) return;
      String fhirType = dataRequirement.getType().toCode();
      if (dataRequirement.hasProfile()) {
         populateResourceProfileMap(fhirType, dataRequirement.getProfile());
      }
      queryMap.computeIfAbsent(fhirType, x -> new QueryAggregator());
      if (dataRequirement.hasCodeFilter()) {
         for (DataRequirement.DataRequirementCodeFilterComponent codeFilter: dataRequirement.getCodeFilter()) {
            resolveCodeFilter(codeFilter, fhirType);
         }
      }
   }

   private void populateResourceProfileMap(String fhirType, List<CanonicalType> profileUrls) {
      if (resourceProfileMap.containsKey(fhirType)) {
         resourceProfileMap.get(fhirType).addAll(profileUrls.stream().map(PrimitiveType::getValueAsString).collect(Collectors.toList()));
      }
      else {
         resourceProfileMap.put(fhirType, new HashSet<>(profileUrls.stream().map(PrimitiveType::getValueAsString).collect(Collectors.toList())));
      }
   }

   private void resolveCodeFilter(DataRequirement.DataRequirementCodeFilterComponent codeFilter, String fhirType) {
      if (codeFilter.hasPath() && codeFilter.hasValueSet()) {
         ValueSetInfo vsInfo = new ValueSetInfo().withId(codeFilter.getValueSet());
         List<InternalCodingDt> codeList = new ArrayList<>();
         for (Code code : terminologyProvider.expand(vsInfo)) {
            codeList.add(new InternalCodingDt().setSystem(code.getSystem()).setCode(code.getCode()));
         }
         queryMap.get(fhirType).addQuery(codeFilter.getPath(), codeList, vsInfo);
      }
   }
}
