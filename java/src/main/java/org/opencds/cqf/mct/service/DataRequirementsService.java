package org.opencds.cqf.mct.service;

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
import org.opencds.cqf.mct.SpringContext;
import org.opencds.cqf.mct.processor.DataRequirementsProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataRequirementsService {
   private final LibrarySourceProvider librarySourceProvider;

   public DataRequirementsService() {
      librarySourceProvider = SpringContext.getBean(LibrarySourceProvider.class);
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
