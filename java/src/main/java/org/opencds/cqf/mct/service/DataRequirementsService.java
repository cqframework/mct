package org.opencds.cqf.mct.service;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.InternalCodingDt;
import org.cqframework.cql.cql2elm.CqlTranslatorOptions;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.LibrarySourceLoader;
import org.cqframework.cql.cql2elm.LibrarySourceProvider;
import org.cqframework.cql.cql2elm.ModelManager;
import org.cqframework.cql.cql2elm.PriorityLibrarySourceLoader;
import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.fhir.r5.model.DataRequirement;
import org.hl7.fhir.r5.model.Library;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo;
import org.opencds.cqf.mct.SpringContext;
import org.opencds.cqf.mct.processor.DataRequirementsProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataRequirementsService {
   private final LibrarySourceProvider librarySourceProvider;
   private final TerminologyProvider terminologyProvider;

   public DataRequirementsService() {
      librarySourceProvider = SpringContext.getBean(LibrarySourceProvider.class);
      terminologyProvider = SpringContext.getBean(TerminologyProvider.class);
   }

   public Library getDataRequirementLibrary() {
      DataRequirementsProcessor processor = new DataRequirementsProcessor();
      LibraryManager libraryManager = new LibraryManager(new ModelManager());
      LibrarySourceLoader librarySourceLoader = new PriorityLibrarySourceLoader();
      librarySourceLoader.registerProvider(librarySourceProvider);
      libraryManager.setLibrarySourceLoader(librarySourceLoader);
      CqlTranslatorOptions options = CqlTranslatorOptions.defaultOptions();
      CompiledLibrary library = libraryManager.resolveLibrary(new VersionedIdentifier().withId("DischargedonAntithromboticTherapyQICore4").withVersion("0.0.006"), options, new ArrayList<>());
      return processor.gatherDataRequirements(libraryManager, library, options, null, true, true);
   }

   public List<DataRequirement> getDataRequirements() {
      Library dataReqLibrary = getDataRequirementLibrary();
      return dataReqLibrary.getDataRequirement();
   }

   public Map<String, Map<String, List<IQueryParameterType>>> getSearchParamsByType() {
      Map<String, Map<String, List<IQueryParameterType>>> result = new HashMap<>();
      List<DataRequirement> dataRequirements = getDataRequirements();
      for (DataRequirement dataRequirement: dataRequirements) {
         if (!dataRequirement.hasType()) continue;
         String fhirType = dataRequirement.getType().toCode();
         result.computeIfAbsent(fhirType, x -> new HashMap<>());
         if (dataRequirement.hasCodeFilter()) {
            for (DataRequirement.DataRequirementCodeFilterComponent codeFilter: dataRequirement.getCodeFilter()) {
               if (codeFilter.hasPath() && codeFilter.hasValueSet()) {
                  Map<String, List<IQueryParameterType>> pathMap = result.get(fhirType);
                  pathMap.computeIfAbsent(codeFilter.getPath(), x -> new ArrayList<>());
                  ValueSetInfo vsInfo = new ValueSetInfo().withId(codeFilter.getValueSet());
                  for (Code code : terminologyProvider.expand(vsInfo)) {
                     pathMap.get(codeFilter.getPath()).add(new InternalCodingDt().setSystem(code.getSystem()).setCode(code.getCode()));
                  }
               }
            }
         }
      }
      return result;
   }

   public Map<String, String> getProfiles() {
      Map<String, String> profiles = new HashMap<>();
      List<DataRequirement> dataRequirements = getDataRequirements();
      for (DataRequirement dataRequirement : dataRequirements) {
         if (dataRequirement.hasType() && dataRequirement.hasProfile()) {
            profiles.put(dataRequirement.getType().toCode(), dataRequirement.getProfile().get(0).asStringValue());
         }
      }
      return profiles;
   }
}
