package org.opencds.cqf.mct;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.validation.FhirValidator;
import org.hl7.fhir.common.hapi.validation.support.CachingValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.RemoteTerminologyServiceValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.SnapshotGeneratingValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.utilities.npm.NpmPackage;
import org.opencds.cqf.cql.engine.fhir.model.R4FhirModelResolver;
import org.opencds.cqf.cql.engine.fhir.retrieve.RestFhirRetrieveProvider;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver;
import org.opencds.cqf.cql.engine.model.ModelResolver;
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider;
import org.opencds.cqf.cql.evaluator.builder.DataProviderFactory;
import org.opencds.cqf.cql.evaluator.builder.EndpointConverter;
import org.opencds.cqf.cql.evaluator.builder.LibrarySourceProviderFactory;
import org.opencds.cqf.cql.evaluator.builder.ModelResolverFactory;
import org.opencds.cqf.cql.evaluator.builder.TerminologyProviderFactory;
import org.opencds.cqf.cql.evaluator.builder.FhirDalFactory;
import org.opencds.cqf.cql.evaluator.builder.dal.FhirRestFhirDalFactory;
import org.opencds.cqf.cql.evaluator.builder.dal.TypedFhirDalFactory;
import org.opencds.cqf.cql.evaluator.builder.data.FhirModelResolverFactory;
import org.opencds.cqf.cql.evaluator.builder.data.TypedRetrieveProviderFactory;
import org.opencds.cqf.cql.evaluator.builder.library.FhirRestLibrarySourceProviderFactory;
import org.opencds.cqf.cql.evaluator.builder.library.TypedLibrarySourceProviderFactory;
import org.opencds.cqf.cql.evaluator.builder.terminology.FhirRestTerminologyProviderFactory;
import org.opencds.cqf.cql.evaluator.builder.terminology.TypedTerminologyProviderFactory;
import org.opencds.cqf.cql.evaluator.cql2elm.util.LibraryVersionSelector;
import org.opencds.cqf.cql.evaluator.fhir.ClientFactory;
import org.opencds.cqf.cql.evaluator.fhir.adapter.r4.AdapterFactory;
import org.opencds.cqf.mct.service.ValidationService;
import org.opencds.cqf.mct.validation.MctNpmPackageValidationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
@Import({ MctProperties.class })
public class MctConfig {
   @Autowired
   private MctProperties properties;
   @Bean
   public FhirContext fhirContext() {
      return FhirContext.forCached(properties.getFhirVersion());
   }

   @Bean
   public ClientFactory clientFactory(FhirContext fhirContext) {
      return new ClientFactory(fhirContext);
   }

   @Bean
   public Set<TypedTerminologyProviderFactory> typedTerminologyProviderFactories(
           FhirContext fhirContext, ClientFactory clientFactory) {
      return Collections.singleton(new FhirRestTerminologyProviderFactory(fhirContext, clientFactory));
   }

   @Bean
   public TerminologyProviderFactory terminologyProviderFactory(
           FhirContext fhirContext, Set<TypedTerminologyProviderFactory> typedTerminologyProviderFactories) {
      return new org.opencds.cqf.cql.evaluator.builder.terminology.TerminologyProviderFactory(
              fhirContext, typedTerminologyProviderFactories);
   }

   @Bean
   public Set<ModelResolverFactory> modelResolverFactories() {
      return Collections.singleton(new FhirModelResolverFactory());
   }

   @Bean
   public ModelResolver modelResolver() {
      return new R4FhirModelResolver();
   }

   @Bean
   public Set<TypedRetrieveProviderFactory> typedRetrieveProviderFactories(
           FhirContext fhirContext, ModelResolver modelResolver, ClientFactory clientFactory) {
      return Collections.singleton(new TypedRetrieveProviderFactory() {
         @Override
         public String getType() {
            return "hl7-fhir-rest";
         }

         @Override
         public RetrieveProvider create(String url, List<String> headers) {
            IGenericClient fhirClient = clientFactory.create(url, headers);
            return new RestFhirRetrieveProvider(new SearchParameterResolver(fhirContext), modelResolver, fhirClient);
         }
      });
   }

   @Bean
   public DataProviderFactory dataProviderFactory(
           FhirContext fhirContext, Set<ModelResolverFactory> modelResolverFactories,
           Set<TypedRetrieveProviderFactory> typedRetrieveProviderFactories) {
      return new org.opencds.cqf.cql.evaluator.builder.data.DataProviderFactory(
              fhirContext, modelResolverFactories, typedRetrieveProviderFactories);
   }

   @Bean
   public AdapterFactory adapterFactory() {
      return new AdapterFactory();
   }

   @Bean
   public LibraryVersionSelector libraryVersionSelector(AdapterFactory adapterFactory) {
      return new LibraryVersionSelector(adapterFactory);
   }

   @Bean
   public Set<TypedLibrarySourceProviderFactory> librarySourceProviderFactories(
           ClientFactory clientFactory, AdapterFactory adapterFactory,
           LibraryVersionSelector libraryVersionSelector) {
      return Collections.singleton(new FhirRestLibrarySourceProviderFactory(
              clientFactory, adapterFactory, libraryVersionSelector));
   }

   @Bean
   public LibrarySourceProviderFactory librarySourceProviderFactory(
           FhirContext fhirContext, AdapterFactory adapterFactory,
           Set<TypedLibrarySourceProviderFactory> librarySourceProviderFactories,
           LibraryVersionSelector libraryVersionSelector) {
      return new org.opencds.cqf.cql.evaluator.builder.library.LibrarySourceProviderFactory(
              fhirContext, adapterFactory, librarySourceProviderFactories, libraryVersionSelector);
   }

   @Bean
   public Set<TypedFhirDalFactory> fhirDalFactories(ClientFactory clientFactory) {
      return Collections.singleton(new FhirRestFhirDalFactory(clientFactory));
   }

   @Bean
   public FhirDalFactory fhirDalFactory(FhirContext fhirContext, Set<TypedFhirDalFactory> fhirDalFactories) {
      return new org.opencds.cqf.cql.evaluator.builder.dal.FhirDalFactory(fhirContext, fhirDalFactories);
   }

   @Bean
   public EndpointConverter endpointConverter(AdapterFactory adapterFactory) {
      return new EndpointConverter(adapterFactory);
   }

   @Bean
   public MctNpmPackageValidationSupport mctNpmPackageValidationSupport(
           FhirContext fhirContext, MctProperties properties) throws IOException {
      MctNpmPackageValidationSupport validationSupport = new MctNpmPackageValidationSupport(fhirContext);
      NpmPackage basePackage;
      for (Map.Entry<String, MctProperties.ImplementationGuide> igs : properties.getImplementationGuides().entrySet()) {
         if (igs.getValue().getUrl() != null) {
            basePackage = NpmPackage.fromUrl(igs.getValue().getUrl());
         }
         else if (properties.getPackageServerUrl() == null) {
            throw new ConfigurationException("The package_server_url property must be present if the implementationguides url property is absent");
         }
         else if (igs.getValue().getName() != null && igs.getValue().getVersion() != null) {
            basePackage = NpmPackage.fromUrl(properties.getPackageServerUrl() + "/" + igs.getValue().getName() + "/" + igs.getValue().getVersion());
         }
         else if (igs.getValue().getName() != null) {
            basePackage = NpmPackage.fromUrl(properties.getPackageServerUrl() + "/" + igs.getValue().getName());
         }
         else {
            throw new ConfigurationException("The implementationguides property must include either a url or a name with an optional version");
         }
         validationSupport.loadPackage(basePackage);
         if (properties.getInstallTransitiveIgDependencies()) {
            for (String dependency : basePackage.dependencies()) {
               if (properties.getPackageServerUrl() == null) {
                  throw new ConfigurationException("The package_server_url property must be present to resolve implementationguides dependencies");
               }

               validationSupport.loadPackage(NpmPackage.fromUrl(properties.getPackageServerUrl() + "/" + dependency.replace("#", "/")));
            }
         }
      }

      return validationSupport;
   }

   @Bean
   public ValidationSupportChain validationSupportChain(FhirContext fhirContext, MctNpmPackageValidationSupport mctNpmPackageValidationSupport) {
      return new ValidationSupportChain(
              mctNpmPackageValidationSupport,
              new CommonCodeSystemsTerminologyService(fhirContext),
              new DefaultProfileValidationSupport(fhirContext),
              new RemoteTerminologyServiceValidationSupport(fhirContext, "http://tx.fhir.org/r4/"),
              new InMemoryTerminologyServerValidationSupport(fhirContext),
              new SnapshotGeneratingValidationSupport(fhirContext)
      );
   }

   @Bean
   public FhirValidator fhirValidator(FhirContext fhirContext, ValidationSupportChain validationSupportChain) {
      CachingValidationSupport validationSupport = new CachingValidationSupport(validationSupportChain);
      FhirValidator validator = fhirContext.newValidator();
      FhirInstanceValidator instanceValidator = new FhirInstanceValidator(validationSupport);
      validator.registerValidatorModule(instanceValidator);
      return validator;
   }

   @Bean
   public ValidationService validationService(FhirContext fhirContext, FhirValidator fhirValidator, MctProperties properties) {
      return new ValidationService(fhirContext, fhirValidator, properties.getRequireProfileForValidation());
   }
}
