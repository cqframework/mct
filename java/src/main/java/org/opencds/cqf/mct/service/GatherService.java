package org.opencds.cqf.mct.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.util.OperationOutcomeUtil;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Group;
import org.hl7.fhir.r4.model.MeasureReport;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Period;
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo;
import org.opencds.cqf.mct.SpringContext;
import org.opencds.cqf.mct.config.MctConstants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.opencds.cqf.cql.evaluator.fhir.util.r4.Parameters.parameters;
import static org.opencds.cqf.cql.evaluator.fhir.util.r4.Parameters.part;

/**
 * The Gather service logic for the {@link org.opencds.cqf.mct.api.GatherAPI}.
 */
public class GatherService {
   private static final FhirContext fhirContext = SpringContext.getBean(FhirContext.class);

   /**
    * The $gather operation logic.
    *
    * @see org.opencds.cqf.mct.api.GatherAPI#gather(Group, List, String, Period)
    * @param patients          the patients
    * @param facilities        the facilities
    * @param measureIdentifier the measure identifier
    * @param period            the period
    * @return the population-level and patient-level reports, evaluated resources and validation messages populated
    * in a <a href="http://hl7.org/fhir/parameters.html">Parameters</a> resource
    */
   public Parameters gather(Group patients, List<String> facilities, String measureIdentifier, Period period) {
      PatientDataService patientDataService = new PatientDataService(patients, facilities);
      MeasureEvaluationService measureEvaluationService = new MeasureEvaluationService(measureIdentifier, period);

      GatherResult result = new GatherResult();
      result.patientBundles = patientDataService.resolvePatientBundles(measureEvaluationService);
      result.populationReport = measureEvaluationService.getPopulationReport(patientDataService.getPatientReferences(), result.patientBundles);
      List<Parameters.ParametersParameterComponent> components = new ArrayList<>();
      components.add(part("population-report", result.populationReport));
      components.addAll(result.getReturnFormat());
      return parameters().setParameter(components);
   }

   private static class GatherResult {
      private MeasureReport populationReport;
      private List<PatientBundle> patientBundles;

      /**
       * Formats the result for the $gather operation.
       *
       * @return List of <a href="http://hl7.org/fhir/parameters.html">Parameters</a> resource components for the result
       */
      public List<Parameters.ParametersParameterComponent> getReturnFormat() {
         return patientBundles.stream().map(x -> {
            Bundle returnBundle = new Bundle();
            returnBundle.addEntry(new Bundle.BundleEntryComponent().setResource(x.patientReport));
            x.patientData.getEntry().forEach(returnBundle::addEntry);
            returnBundle.addEntry(new Bundle.BundleEntryComponent().setResource(x.missingPatientData));
            return part(x.patientId, returnBundle);
         }).collect(Collectors.toList());
      }
   }

   /**
    * The Patient bundle.
    */
   public static class PatientBundle {
      private String patientId;
      private MeasureReport patientReport;
      private Bundle patientData;
      private OperationOutcome missingPatientData;

      /**
       * Sets patient id.
       *
       * @param patientId the patient id
       */
      public void setPatientId(String patientId) {
         this.patientId = patientId;
      }

      /**
       * Sets patient report.
       *
       * @param patientReport the patient report
       */
      public void setPatientReport(MeasureReport patientReport) {
         this.patientReport = patientReport;
      }

      /**
       * Gets patient id.
       *
       * @return the patient id
       */
      public String getPatientId() {
         return patientId;
      }

      /**
       * Gets patient data.
       *
       * @return the patient data
       */
      public Bundle getPatientData() {
         if (patientData == null) {
            patientData = new Bundle();
         }
         return patientData;
      }

      /**
       * Add missing patient data.
       *
       * @param resourceType    the resource type
       * @param valueSetInfoMap the value set info map
       */
      public void addMissingPatientData(String resourceType, Map<String, List<ValueSetInfo>> valueSetInfoMap) {
         if (missingPatientData == null) {
            missingPatientData = new OperationOutcome();
         }
         Set<String> paths = new HashSet<>();
         List<ValueSetInfo> valueSetInfos = new ArrayList<>();
         if (valueSetInfoMap != null) {
            paths = valueSetInfoMap.keySet();
            valueSetInfos = valueSetInfoMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
         }
         String details = String.format(
                 "The patient: %s does not satisfy the data requirement for resource: %s with path(s): %s in the following valueset(s): %s",
                 patientId, resourceType, paths, valueSetInfos.stream().map(ValueSetInfo::getId).collect(Collectors.toList()));
         OperationOutcomeUtil.addIssue(fhirContext, missingPatientData, MctConstants.SEVERITY_INFORMATION, details, null, MctConstants.CODE_INFORMATIONAL);
      }
   }
}
