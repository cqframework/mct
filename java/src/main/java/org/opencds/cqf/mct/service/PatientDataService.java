package org.opencds.cqf.mct.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.util.ClasspathUtil;
import ca.uhn.fhir.util.DateUtils;
import ca.uhn.fhir.util.OperationOutcomeUtil;
import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.CqlTranslatorOptions;
import org.cqframework.cql.cql2elm.DefaultLibrarySourceProvider;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.LibrarySourceLoader;
import org.cqframework.cql.cql2elm.LibrarySourceProvider;
import org.cqframework.cql.cql2elm.ModelManager;
import org.cqframework.cql.cql2elm.PriorityLibrarySourceLoader;
import org.cqframework.cql.elm.execution.Library;
import org.cqframework.cql.elm.execution.VersionedIdentifier;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.PrimitiveType;
import org.hl7.fhir.r4.model.Resource;
import org.opencds.cqf.cql.engine.data.DataProvider;
import org.opencds.cqf.cql.engine.data.ExternalFunctionProvider;
import org.opencds.cqf.cql.engine.data.SystemExternalFunctionProvider;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.serializing.jackson.JsonCqlLibraryReader;
import org.opencds.cqf.mct.SpringContext;
import org.opencds.cqf.mct.config.MctConstants;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import static org.opencds.cqf.cql.evaluator.fhir.util.r4.Parameters.parameters;
import static org.opencds.cqf.cql.evaluator.fhir.util.r4.Parameters.part;

public class PatientDataService {

   private final FhirContext fhirContext;
   private List<OperationOutcome> missingDataRequirements;
   private final DataProvider dataProvider;

   public PatientDataService() {
      fhirContext = SpringContext.getBean(FhirContext.class);
      dataProvider = SpringContext.getBean(DataProvider.class);
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

   public Bundle getPatients(String facilityUrl) {
      IGenericClient client = fhirContext.newRestfulGenericClient(facilityUrl);
      return (Bundle) client.search().forResource(Patient.class).count(500).execute();
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

   public Bundle generatePatientData() throws NoSuchMethodException, IOException {
      VersionedIdentifier versionedIdentifier =
              new VersionedIdentifier().withId("CMS104TestDataGenerator").withVersion("1.0.0");
      File cqlFile = new File(Objects.requireNonNull(Objects.requireNonNull(ClasspathUtil.class.getClassLoader().getResource(
              "configuration/patient-data-gen-libraries/" + "CMS104TestDataGenerator" + ".cql")).getFile(), "UTF-8"));
      ModelManager modelManager = new ModelManager();
      LibraryManager libraryManager = new LibraryManager(modelManager);
      LibrarySourceLoader librarySourceLoader = new PriorityLibrarySourceLoader();
      LibrarySourceProvider librarySourceProvider = new DefaultLibrarySourceProvider(
              Path.of(Objects.requireNonNull(ClasspathUtil.class.getClassLoader().getResource(
                      "configuration/patient-data-gen-libraries")).getPath()));
      librarySourceLoader.registerProvider(librarySourceProvider);
      CqlTranslatorOptions options = CqlTranslatorOptions.defaultOptions();
      CqlTranslator translator = CqlTranslator.fromFile(cqlFile, modelManager, libraryManager, null, options);
      Library library = new JsonCqlLibraryReader().read(translator.toJson());
      Context context = new Context(library);
      ExternalFunctionProvider externalFunctionProvider = new SystemExternalFunctionProvider(Collections.singletonList(PatientDataService.class.getMethod("getRandomNumber")));
      context.registerExternalFunctionProvider(versionedIdentifier, externalFunctionProvider);
      context.registerDataProvider("http://hl7.org/fhir", dataProvider);
      Object result = context.resolveExpressionRef("TestDataGenerationResult").evaluate(context);

      return (Bundle) result;
   }

   public static BigDecimal getRandomNumber() {
      return BigDecimal.valueOf(new Random().nextDouble());
   }

}
