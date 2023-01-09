package org.opencds.cqf.mct;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
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
import org.opencds.cqf.cql.evaluator.builder.data.FhirRestRetrieveProviderFactory;
import org.opencds.cqf.cql.evaluator.builder.data.TypedRetrieveProviderFactory;
import org.opencds.cqf.cql.evaluator.builder.library.FhirRestLibrarySourceProviderFactory;
import org.opencds.cqf.cql.evaluator.builder.library.TypedLibrarySourceProviderFactory;
import org.opencds.cqf.cql.evaluator.builder.terminology.FhirRestTerminologyProviderFactory;
import org.opencds.cqf.cql.evaluator.builder.terminology.TypedTerminologyProviderFactory;
import org.opencds.cqf.cql.evaluator.cql2elm.util.LibraryVersionSelector;
import org.opencds.cqf.cql.evaluator.fhir.ClientFactory;
import org.opencds.cqf.cql.evaluator.fhir.adapter.r4.AdapterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Configuration
public class MctConfig {
   @Bean
   public FhirContext fhirContext() {
      return FhirContext.forR4Cached();
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
//      return Collections.singleton(new FhirRestRetrieveProviderFactory(fhirContext, clientFactory));
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
}
