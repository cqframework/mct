package org.opencds.cqf.mct.service;

import ca.uhn.fhir.util.ClasspathUtil;
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
import org.hl7.fhir.r4.model.IntegerType;
import org.opencds.cqf.cql.engine.data.DataProvider;
import org.opencds.cqf.cql.engine.data.ExternalFunctionProvider;
import org.opencds.cqf.cql.engine.data.SystemExternalFunctionProvider;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.serializing.jackson.JsonCqlLibraryReader;
import org.opencds.cqf.mct.SpringContext;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

/**
 * The Patient Data Generator Service logic for the {@link org.opencds.cqf.mct.api.GeneratePatientDataAPI}.
 */
public class PatientDataGeneratorService {
   private final DataProvider dataProvider;
   private static final Random randomNumberGenerator = new Random();

   /**
    * Instantiates a new Patient Data Generator Service.
    */
   public PatientDataGeneratorService() {
      dataProvider = SpringContext.getBean(DataProvider.class);
   }

   /**
    * Generate test cases for the CMS104 pilot measure
    *
    * @see org.opencds.cqf.mct.api.GeneratePatientDataAPI#generatePatientData(IntegerType)
    * @param numTestCases the number of test cases to generate (200 by default)
    * @return the bundle of test cases containing the patient data
    * @throws IOException           when the cql file does not exist or is malformed
    * @throws NoSuchMethodException when the external function method is not present
    */
   public Bundle generatePatientData(Integer numTestCases) throws IOException, NoSuchMethodException {
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
      ExternalFunctionProvider externalFunctionProvider = new SystemExternalFunctionProvider(Collections.singletonList(PatientDataGeneratorService.class.getMethod("getRandomNumber")));
      context.registerExternalFunctionProvider(versionedIdentifier, externalFunctionProvider);
      context.registerDataProvider("http://hl7.org/fhir", dataProvider);
      Object result = context.resolveExpressionRef("TestDataGenerationResult").evaluate(context);

      return (Bundle) result;
   }

   /**
    * Gets random decimal. This is an external function used in the CQL logic.
    *
    * @return the random decimal
    */
   public static BigDecimal getRandomNumber() {
      return BigDecimal.valueOf(randomNumberGenerator.nextDouble());
   }
}
