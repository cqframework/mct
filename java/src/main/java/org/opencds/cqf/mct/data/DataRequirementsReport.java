package org.opencds.cqf.mct.data;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.util.OperationOutcomeUtil;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo;
import org.opencds.cqf.mct.SpringContext;
import org.opencds.cqf.mct.config.MctConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DataRequirementsReport {

   private final List<MissingDataRequirements> missingDataRequirements;
   private final FhirContext fhirContext;

   public DataRequirementsReport() {
      missingDataRequirements = new ArrayList<>();
      fhirContext = SpringContext.getBean(FhirContext.class);
   }

   public void addMissingDataRequirement(String patientId, String type, Collection<String> paths, Collection<ValueSetInfo> valueSetInfos) {
      missingDataRequirements.add(new MissingDataRequirements(patientId, type, paths, valueSetInfos));
   }

   public List<MissingDataRequirements> getMissingDataRequirements() {
      return missingDataRequirements;
   }

   public OperationOutcome getReport() {
      if (missingDataRequirements.isEmpty()) {
         return null;
      }
      OperationOutcome report = new OperationOutcome();
      for (MissingDataRequirements missingDR : missingDataRequirements) {
         String details = String.format(
                 "The patient: %s does not satisfy the data requirement for resource: %s with path(s): %s in the following valueset(s): %s",
                 missingDR.getPatientId(), missingDR.getType(), missingDR.getPaths(), missingDR.getValueSetInfos().stream().map(ValueSetInfo::getId).collect(Collectors.toList()));
         OperationOutcomeUtil.addIssue(fhirContext, report, MctConstants.SEVERITY_INFORMATION, details, null, MctConstants.CODE_INFORMATIONAL);
      }
      return report;
   }

   private static class MissingDataRequirements {
      private final String patientId;
      private final String type;
      private final Collection<String> paths;
      private final Collection<ValueSetInfo> valueSetInfos;

      public MissingDataRequirements(String patientId, String type, Collection<String> paths, Collection<ValueSetInfo> valueSetInfos) {
         this.patientId = patientId;
         this.type = type;
         this.paths = paths;
         this.valueSetInfos = valueSetInfos;
      }

      public String getPatientId() {
         return patientId;
      }

      public String getType() {
         return type;
      }

      public Collection<String> getPaths() {
         return paths;
      }

      public Collection<ValueSetInfo> getValueSetInfos() {
         return valueSetInfos;
      }
   }

}
