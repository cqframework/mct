package org.opencds.cqf.mct.data;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.util.ClasspathUtil;
import ca.uhn.fhir.util.SearchParameterUtil;
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
import org.hl7.fhir.r4.model.Patient;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class PatientData {
   private Bundle patientDataBundle;
   private final FhirContext fhirContext;
   private final DataRequirementsProvider dataRequirementsProvider;
   private final DataRequirementsReport dataRequirementsReport;
   private final DataProvider dataProvider;
   private static final Random randomNumberGenerator = new Random();

   public PatientData() {
      fhirContext = SpringContext.getBean(FhirContext.class);
      dataRequirementsProvider = new DataRequirementsProvider();
      dataRequirementsReport = new DataRequirementsReport();
      dataProvider = SpringContext.getBean(DataProvider.class);
   }

   public Bundle getPatientsFromFacility(String facilityUrl) {
      IGenericClient client = fhirContext.newRestfulGenericClient(facilityUrl);
      return (Bundle) client.search().forResource(Patient.class).count(500).execute();
   }

   public Bundle getPatientDataBundle(String facilityUrl, String facility, String patientId) {
      if (patientDataBundle == null) {
         IGenericClient client = fhirContext.newRestfulGenericClient(facilityUrl);
         patientDataBundle = new Bundle();
         for (Map.Entry<String, QueryAggregator> entry : dataRequirementsProvider.getQueryMap().entrySet()) {
            String type = entry.getKey();
            // TODO: this is causing a timeout for searches with large code lists - just using the patient search parameter for the prototype
            // Map<String, List<IQueryParameterType>> pathQueryMap = entry.getValue().getPathQueryMap();
            Map<String, List<IQueryParameterType>> validationQueryMap = entry.getValue().getPathQueryMap();
            Map<String, List<IQueryParameterType>> pathQueryMap = new HashMap<>();
            if (type.equals("Patient")) {
               pathQueryMap.put("_id", Collections.singletonList(new StringParam(patientId)));
            }
            else {
               SearchParameterUtil.getOnlyPatientSearchParamForResourceType(fhirContext, type).ifPresent(
                       patientParam -> pathQueryMap.put(patientParam.getName(), Collections.singletonList(new ReferenceParam(patientId))));
            }
            Bundle searchResult = (Bundle) client.search().forResource(type).where(pathQueryMap).execute();
            if (searchResult.hasEntry()) {
               patientDataBundle.getEntry().addAll(searchResult.getEntry());
            }
            else {
               dataRequirementsReport.addMissingDataRequirement(patientId, type, validationQueryMap.keySet(), entry.getValue().getValueSetInfoList());
            }
         }
         addTags(patientDataBundle, facility, dataRequirementsProvider.getResourceProfileMap());
      }
      return patientDataBundle;
   }

   public DataRequirementsReport getDataRequirementsReport() {
      return dataRequirementsReport;
   }

   private void addTags(Bundle patientData, String facility, Map<String, Set<String>> resourceProfileMap) {
      for (Bundle.BundleEntryComponent component : patientData.getEntry()) {
         if (component.hasResource()) {
            Resource resource = component.getResource();
            resource.getMeta().addTag().setSystem(MctConstants.LOCATION_TAG_SYSTEM).setDisplay(facility);
            Set<String> profiles = resource.getMeta().getProfile().stream().map(PrimitiveType::getValueAsString).collect(Collectors.toSet());
            profiles.addAll(resourceProfileMap.get(resource.fhirType()));
            resource.getMeta().setProfile(new ArrayList<>());
            for (String profile : profiles) {
               resource.getMeta().addProfile(profile);
            }
         }
      }
   }

   public Bundle generatePatientData(Integer numTestCases) throws NoSuchMethodException, IOException {
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
      if (numTestCases != null) {
         // min 10, max 200
         Integer validTestCaseCount = numTestCases < 10 ? 10 : numTestCases > 200 ? 200 : numTestCases;
         context.setParameter(null, "NumberOfTests", validTestCaseCount);
      }
      ExternalFunctionProvider externalFunctionProvider = new SystemExternalFunctionProvider(Collections.singletonList(PatientData.class.getMethod("getRandomNumber")));
      context.registerExternalFunctionProvider(versionedIdentifier, externalFunctionProvider);
      context.registerDataProvider("http://hl7.org/fhir", dataProvider);
      Object result = context.resolveExpressionRef("TestDataGenerationResult").evaluate(context);

      return (Bundle) result;
   }

   public static BigDecimal getRandomNumber() {
      return BigDecimal.valueOf(randomNumberGenerator.nextDouble());
   }
}
