package org.opencds.cqf.mct.service;

import ca.uhn.fhir.util.DateUtils;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
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
import java.util.stream.Collectors;

public class GatherService {

   private final TerminologyProviderFactory terminologyProviderFactory;
   private final DataProviderFactory dataProviderFactory;
   private final LibrarySourceProviderFactory librarySourceProviderFactory;
   private final FhirDalFactory fhirDalFactory;
   private final EndpointConverter endpointConverter;
   private final FhirValidator fhirValidator;

   private final ValidationService validationService;

   public GatherService() {
      terminologyProviderFactory = SpringContext.getBean(TerminologyProviderFactory.class);
      dataProviderFactory = SpringContext.getBean(DataProviderFactory.class);
      librarySourceProviderFactory = SpringContext.getBean(LibrarySourceProviderFactory.class);
      fhirDalFactory = SpringContext.getBean(FhirDalFactory.class);
      endpointConverter = SpringContext.getBean(EndpointConverter.class);
      fhirValidator = SpringContext.getBean(FhirValidator.class);
      validationService = SpringContext.getBean(ValidationService.class);
   }

   public Parameters gatherOperation(Group patients, List<String> facilities, String measureIdentifier, Period period) {
      Parameters parameters = new Parameters();
      R4MeasureProcessor measureProcessor = new R4MeasureProcessor(
              terminologyProviderFactory, dataProviderFactory, librarySourceProviderFactory,
              fhirDalFactory, endpointConverter);
      for (String facility: facilities) {
         String facilityUrl = getFacilityUrl(facility);
         Endpoint facilityEndpoint = new Endpoint().setAddress(facilityUrl);
         MeasureReport report = measureProcessor.evaluateMeasure(getMeasureUrl(facility, measureIdentifier),
                 DateUtils.convertDateToIso8601String(period.getStart()),
                 DateUtils.convertDateToIso8601String(period.getEnd()), null,
                 getPatientIds(patients), null, facilityEndpoint, facilityEndpoint,
                 facilityEndpoint, null);
         report.addExtension(getLocationExtension(facility));
         List<DomainResource> validationResults = validation(facilityUrl, report);
         for (DomainResource validationResult : validationResults) {
            report.addContained(validationResult);
         }
         parameters.addParameter().setName("return").setResource(report);
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
      // TODO: update once facility registration is fleshed out
      return "http://localhost:8080/fhir";
   }

   public List<String> getPatientIds(Group patients) {
      return patients.getMember().stream().map(x -> x.getEntity().getReference()).collect(Collectors.toList());
   }

   private Extension getLocationExtension(String locationReference) {
      return new Extension().setUrl("http://cms.gov/fhir/mct/StructureDefinition/measurereport-location").setValue(new Reference(locationReference));
   }

   private List<DomainResource> validation(String url, MeasureReport report) {
      List<DomainResource> resources = new ArrayList<>();
      FhirDal fhirDal = fhirDalFactory.create(new EndpointInfo().setAddress(url));
      for (Reference reference : report.getEvaluatedResource()) {
         DomainResource resource = (DomainResource) fhirDal.read(new IdType(reference.getType(), reference.getId()));
         reference.setReference("#" + reference.getId());
         ValidationResult result = validationService.validate(resource);
         if (!result.isSuccessful()) {
            resource.addContained((Resource) result.toOperationOutcome());
         }
         resources.add(resource);
      }
      return resources;
   }

}
