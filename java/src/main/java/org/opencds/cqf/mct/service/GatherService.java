package org.opencds.cqf.mct.service;

import ca.uhn.fhir.context.FhirContext;
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
import org.opencds.cqf.cql.evaluator.builder.FhirDalFactory;
import org.opencds.cqf.cql.evaluator.builder.LibrarySourceProviderFactory;
import org.opencds.cqf.cql.evaluator.builder.TerminologyProviderFactory;
import org.opencds.cqf.cql.evaluator.fhir.dal.BundleFhirDal;
import org.opencds.cqf.cql.evaluator.measure.r4.R4MeasureProcessor;
import org.opencds.cqf.mct.SpringContext;
import org.opencds.cqf.mct.config.MctConstants;
import org.opencds.cqf.mct.data.DataRequirementsReport;
import org.opencds.cqf.mct.data.PatientData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class GatherService {
   private final FhirContext fhirContext;
   private final TerminologyProviderFactory terminologyProviderFactory;
   private final DataProviderFactory dataProviderFactory;
   private final LibrarySourceProviderFactory librarySourceProviderFactory;
   private final FhirDalFactory fileFhirDalFactory;
   private final EndpointConverter endpointConverter;
   private final ValidationService validationService;
   private final FacilityRegistrationService facilityRegistrationService;
   private final MeasureConfigurationService measureConfigurationService;
   private final String pathToConfigurationResource;

   public GatherService() {
      fhirContext = SpringContext.getBean(FhirContext.class);
      terminologyProviderFactory = SpringContext.getBean(TerminologyProviderFactory.class);
      dataProviderFactory = SpringContext.getBean(DataProviderFactory.class);
      librarySourceProviderFactory = SpringContext.getBean(LibrarySourceProviderFactory.class);
      fileFhirDalFactory = SpringContext.getBean("fileFhirDalFactory", FhirDalFactory.class);
      endpointConverter = SpringContext.getBean(EndpointConverter.class);
      validationService = SpringContext.getBean(ValidationService.class);
      facilityRegistrationService = SpringContext.getBean(FacilityRegistrationService.class);
      measureConfigurationService = SpringContext.getBean(MeasureConfigurationService.class);
      pathToConfigurationResource = SpringContext.getBean("pathToConfigurationResources", String.class);
   }

   public Parameters gatherOperation(Group patients, List<String> facilities, String measureIdentifier, Period period) {
      Parameters parameters = new Parameters();
      R4MeasureProcessor measureProcessor = new R4MeasureProcessor(
              terminologyProviderFactory, dataProviderFactory, librarySourceProviderFactory,
              fileFhirDalFactory, endpointConverter);
      Bundle populationData = new Bundle();
      List<String> patientIds = getPatientIds(patients);
      Endpoint configurationResourcesEndpoint = new Endpoint().setAddress(pathToConfigurationResource);
      String measureUrl = getMeasureUrl(measureIdentifier);
      Map<String, Bundle> patientBundles = new HashMap<>();
      for (String facility: facilities) {
         String facilityUrl = getFacilityUrl(facility);
         Bundle patientData;
         MeasureReport report;
         for (String patientId : patientIds) {
            PatientData patientDataService = new PatientData();
            patientData = patientDataService.getPatientDataBundle(facilityUrl, facility, patientId);
            populationData.getEntry().addAll(patientData.getEntry());
            report = measureProcessor.evaluateMeasure(measureUrl,
                    DateUtils.convertDateToIso8601String(period.getStart()),
                    DateUtils.convertDateToIso8601String(period.getEnd()), null,
                    Collections.singletonList(patientId), null, configurationResourcesEndpoint,
                    configurationResourcesEndpoint, null, patientData);
            report.addExtension(getLocationExtension(facility));
            Bundle returnBundle = new Bundle().setType(Bundle.BundleType.COLLECTION);
            returnBundle.addEntry().setResource(report);
            List<DomainResource> validationResults = validation(patientData, report, patientDataService.getDataRequirementsReport());
            validationResults.forEach(x -> returnBundle.addEntry().setResource(x));
            patientBundles.put(patientId, returnBundle);
         }
      }
      if (patientIds.size() > 1) {
         parameters.addParameter().setName("population-report").setResource(
                 measureProcessor.evaluateMeasure(measureUrl,
                         DateUtils.convertDateToIso8601String(period.getStart()),
                         DateUtils.convertDateToIso8601String(period.getEnd()), null,
                         patientIds, null, configurationResourcesEndpoint,
                         configurationResourcesEndpoint, null, populationData));
      }
      for (Map.Entry<String, Bundle> entry : patientBundles.entrySet()) {
         parameters.addParameter().setName(entry.getKey()).setResource(entry.getValue());
      }
      return parameters;
   }

   public String getMeasureUrl(String measureIdentifier) {
      return measureConfigurationService.getMeasure(measureIdentifier).getUrl();
   }

   public String getFacilityUrl(String facilityId) {
      return facilityRegistrationService.getFacilityUrl(facilityId);
   }

   public List<String> getPatientIds(Group patients) {
      return patients.getMember().stream().map(x -> x.getEntity().getReference()).collect(Collectors.toList());
   }

   private Extension getLocationExtension(String locationReference) {
      return new Extension().setUrl(MctConstants.LOCATION_EXTENSION_URL).setValue(new Reference(locationReference));
   }

   private Extension getValidationExtension(String reference) {
      return new Extension().setUrl(MctConstants.VALIDATION_EXTENSION_URL).setValue(new Reference(reference));
   }

   private List<DomainResource> validation(Bundle bundle, MeasureReport report, DataRequirementsReport dataRequirementsReport) {
      List<DomainResource> resources = new ArrayList<>();
      BundleFhirDal bundleFhirDal = new BundleFhirDal(fhirContext, bundle);
      for (Reference reference : report.getEvaluatedResource()) {
         DomainResource resource = (DomainResource) bundleFhirDal.read(new IdType(reference.getReference()));
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
      resources.add(dataRequirementsReport.getReport());
      return resources;
   }
}
