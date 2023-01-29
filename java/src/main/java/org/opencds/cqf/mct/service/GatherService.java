package org.opencds.cqf.mct.service;

import ca.uhn.fhir.util.DateUtils;
import ca.uhn.fhir.validation.ValidationResult;
import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Group;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.MeasureReport;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.opencds.cqf.cql.evaluator.builder.DataProviderFactory;
import org.opencds.cqf.cql.evaluator.builder.EndpointConverter;
import org.opencds.cqf.cql.evaluator.builder.EndpointInfo;
import org.opencds.cqf.cql.evaluator.builder.FhirDalFactory;
import org.opencds.cqf.cql.evaluator.builder.LibrarySourceProviderFactory;
import org.opencds.cqf.cql.evaluator.builder.TerminologyProviderFactory;
import org.opencds.cqf.cql.evaluator.fhir.dal.FhirDal;
import org.opencds.cqf.cql.evaluator.measure.r4.R4MeasureProcessor;
import org.opencds.cqf.mct.SpringContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GatherService {

   private final TerminologyProviderFactory terminologyProviderFactory;
   private final DataProviderFactory dataProviderFactory;
   private final LibrarySourceProviderFactory librarySourceProviderFactory;
   private final FhirDalFactory fileFhirDalFactory;
   private final FhirDalFactory restFhirDalFactory;
   private final EndpointConverter endpointConverter;
   private final ValidationService validationService;
   private final FacilityRegistrationService facilityRegistrationService;
   private final String pathToConfigurationResource;

   public GatherService() {
      terminologyProviderFactory = SpringContext.getBean(TerminologyProviderFactory.class);
      dataProviderFactory = SpringContext.getBean(DataProviderFactory.class);
      librarySourceProviderFactory = SpringContext.getBean(LibrarySourceProviderFactory.class);
      fileFhirDalFactory = SpringContext.getBean("fileFhirDalFactory", FhirDalFactory.class);
      restFhirDalFactory = SpringContext.getBean("restFhirDalFactory", FhirDalFactory.class);
      endpointConverter = SpringContext.getBean(EndpointConverter.class);
      validationService = SpringContext.getBean(ValidationService.class);
      facilityRegistrationService = SpringContext.getBean(FacilityRegistrationService.class);
      pathToConfigurationResource = SpringContext.getBean("pathToConfigurationResources", String.class);
   }

   public Parameters gatherOperation(Group patients, List<String> facilities, String measureIdentifier, Period period) {
      Parameters parameters = new Parameters();
      R4MeasureProcessor measureProcessor = new R4MeasureProcessor(
              terminologyProviderFactory, dataProviderFactory, librarySourceProviderFactory,
              fileFhirDalFactory, endpointConverter);
      for (String facility: facilities) {
         String facilityUrl = getFacilityUrl(facility);
         Endpoint facilityEndpoint = new Endpoint().setAddress(facilityUrl);
         Endpoint configurationResourcesEndpoint = new Endpoint().setAddress(pathToConfigurationResource);
         MeasureReport report = measureProcessor.evaluateMeasure(getMeasureUrl(facility, measureIdentifier),
                 DateUtils.convertDateToIso8601String(period.getStart()),
                 DateUtils.convertDateToIso8601String(period.getEnd()), null,
                 getPatientIds(patients), null, configurationResourcesEndpoint,
                 configurationResourcesEndpoint, facilityEndpoint, null);
         report.addExtension(getLocationExtension(facility));
         Bundle returnBundle = new Bundle().setType(Bundle.BundleType.COLLECTION);
         returnBundle.addEntry().setResource(report);
         List<DomainResource> validationResults = validation(facilityUrl, report);
         validationResults.forEach(x -> returnBundle.addEntry().setResource(x));
         parameters.addParameter().setName("return-bundle").setResource(returnBundle);
      }
      return parameters;
   }

   public String getMeasureUrl(String facilityId, String measureIdentifier) {
      if (measureIdentifier.startsWith("http")) {
         return measureIdentifier;
      }
      if (measureIdentifier.startsWith("Measure/")) {
         return getFacilityUrl(facilityId) + "/" + measureIdentifier;
      }
      return getFacilityUrl(facilityId) + "/Measure/" + measureIdentifier;
   }

   public String getFacilityUrl(String facilityId) {
      return facilityRegistrationService.getFacilityUrl(facilityId);
   }

   public List<String> getPatientIds(Group patients) {
      return patients.getMember().stream().map(x -> x.getEntity().getReference()).collect(Collectors.toList());
   }

   private Extension getLocationExtension(String locationReference) {
      return new Extension().setUrl("http://cms.gov/fhir/mct/StructureDefinition/measurereport-location").setValue(new Reference(locationReference));
   }

   private Extension getValidationExtension(String reference) {
      return new Extension().setUrl("http://cms.gov/fhir/mct/StructureDefinition/validation-result").setValue(new Reference(reference));
   }

   private List<DomainResource> validation(String url, MeasureReport report) {
      List<DomainResource> resources = new ArrayList<>();
      FhirDal fhirDal = restFhirDalFactory.create(new EndpointInfo().setAddress(url));
      for (Reference reference : report.getEvaluatedResource()) {
         DomainResource resource = (DomainResource) fhirDal.read(new IdType(reference.getReference()));
         ValidationResult result = validationService.validate(resource);
         if (!result.isSuccessful()) {
            String id = UUID.randomUUID().toString();
            IBaseOperationOutcome validationResult = result.toOperationOutcome();
            validationResult.setId(id);
            resource.addExtension(getValidationExtension("#" + id));
            resource.addContained((Resource) validationResult);
         }
         resources.add(resource);
      }
      return resources;
   }

}
