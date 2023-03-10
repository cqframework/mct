package org.opencds.cqf.mct.service;

import ca.uhn.fhir.util.DateUtils;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.Group;
import org.hl7.fhir.r4.model.Measure;
import org.hl7.fhir.r4.model.MeasureReport;
import org.hl7.fhir.r4.model.Period;
import org.opencds.cqf.cql.evaluator.builder.DataProviderFactory;
import org.opencds.cqf.cql.evaluator.builder.EndpointConverter;
import org.opencds.cqf.cql.evaluator.builder.FhirDalFactory;
import org.opencds.cqf.cql.evaluator.builder.LibrarySourceProviderFactory;
import org.opencds.cqf.cql.evaluator.builder.TerminologyProviderFactory;
import org.opencds.cqf.cql.evaluator.measure.r4.R4MeasureProcessor;
import org.opencds.cqf.mct.SpringContext;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Measure Evaluation Service.
 */
public class MeasureEvaluationService {
   private final Measure measure;
   private final MeasureDataRequirementService measureDataRequirementService;
   private final R4MeasureProcessor measureProcessor;
   private final String measurementPeriodStart;
   private final String measurementPeriodEnd;
   private final Endpoint configurationResourcesEndpoint;

   /**
    * Instantiates a new Measure Evaluation Service.
    *
    * @param measureId the measure id
    * @param period    the measurement period
    */
   public MeasureEvaluationService(String measureId, Period period) {
      measure = SpringContext.getBean(MeasureConfigurationService.class).getMeasure(measureId);
      measureDataRequirementService = new MeasureDataRequirementService(measure);
      measureProcessor = new R4MeasureProcessor(
              SpringContext.getBean(TerminologyProviderFactory.class),
              SpringContext.getBean(DataProviderFactory.class),
              SpringContext.getBean(LibrarySourceProviderFactory.class),
              SpringContext.getBean("fileFhirDalFactory", FhirDalFactory.class),
              SpringContext.getBean(EndpointConverter.class)
      );
      measurementPeriodStart = DateUtils.convertDateToIso8601String(period.getStart());
      measurementPeriodEnd = DateUtils.convertDateToIso8601String(period.getEnd());
      configurationResourcesEndpoint = new Endpoint().setAddress(
              SpringContext.getBean("pathToConfigurationResources", String.class));
   }

   /**
    * Gets the measure url.
    *
    * @return the measure url
    */
   public String getMeasureUrl() {
      return measure.getUrl();
   }

   /**
    * Gets the measure data requirements service.
    *
    * @see MeasureDataRequirementService
    * @return the measure data requirements service
    */
   public MeasureDataRequirementService getMeasureDataRequirementsService() {
      return measureDataRequirementService;
   }

   /**
    * Gets the patient report.
    *
    * @see PatientDataService#resolvePatientBundles(MeasureEvaluationService)
    * @param patientBundle the patient bundle
    * @return the patient <a href="http://hl7.org/fhir/measurereport.html">MeasureReport</a>
    */
   public MeasureReport getPatientReport(GatherService.PatientBundle patientBundle) {
      return evaluate(Collections.singletonList("Patient/" + patientBundle.getPatientId()), patientBundle.getPatientData());
   }

   /**
    * Gets the population report.
    *
    * @see GatherService#gather(Group, List, String, Period)
    * @param patientIds         the patient ids
    * @param patientDataBundles the patient data bundles
    * @return the population <a href="http://hl7.org/fhir/measurereport.html">MeasureReport</a>
    */
   public MeasureReport getPopulationReport(List<String> patientIds, List<GatherService.PatientBundle> patientDataBundles) {
      return evaluate(patientIds, new Bundle().setEntry(patientDataBundles.stream().map(
              bundle -> bundle.getPatientData().getEntry()).flatMap(List::stream).collect(Collectors.toList())));
   }

   /**
    * Evaluate the patient-level and population-level <a href="http://hl7.org/fhir/measure.html">Measure</a>.
    *
    * @param patientIds  the patient ids
    * @param patientData the patient data
    * @return the result of the $evaluate-measure operation (<a href="http://hl7.org/fhir/measurereport.html">MeasureReport</a>)
    */
   public MeasureReport evaluate(List<String> patientIds, Bundle patientData) {
      return measureProcessor.evaluateMeasure(getMeasureUrl(), measurementPeriodStart, measurementPeriodEnd,
              null, patientIds, null, configurationResourcesEndpoint,
              configurationResourcesEndpoint, null, patientData);
   }
}
