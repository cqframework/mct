package org.opencds.cqf.mct.service;

import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationOptions;
import ca.uhn.fhir.validation.ValidationResult;
import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.opencds.cqf.mct.SpringContext;
import org.opencds.cqf.mct.config.MctConstants;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The Validation Service.
 */
public class ValidationService {
   private final FhirValidator validator;

   /**
    * Instantiates a new Validation Service.
    */
   public ValidationService() {
      validator = SpringContext.getBean(FhirValidator.class);
   }

   /**
    * Validates the patient data within the {@link org.opencds.cqf.mct.service.GatherService.PatientBundle}.
    *
    * @see PatientDataService#resolvePatientBundles(MeasureEvaluationService)
    * @param patientBundle the {@link org.opencds.cqf.mct.service.GatherService.PatientBundle}
    * @param facilityId    the facility id
    * @param profileMap    the profile map
    */
   public void validate(GatherService.PatientBundle patientBundle, String facilityId, Map<String, List<String>> profileMap) {
      patientBundle.getPatientData().getEntry().forEach(
              entry -> {
                 if (entry.hasResource()) {
                    ValidationOptions options = new ValidationOptions();
                    DomainResource resource = (DomainResource) entry.getResource();
                    resource.addExtension(new Extension()
                            .setUrl(MctConstants.LOCATION_EXTENSION_URL).setValue(new Reference(facilityId))
                    );
                    resource.getMeta().addTag().setSystem(MctConstants.LOCATION_TAG_SYSTEM).setDisplay(facilityId);
                    if (profileMap.containsKey(resource.fhirType())) {
                       profileMap.get(resource.fhirType()).forEach(options::addProfile);
                    }
                    ValidationResult result = validator.validateWithResult(resource, options);
                    if (!result.isSuccessful()) {
                       String id = UUID.randomUUID().toString();
                       IBaseOperationOutcome validationResult = result.toOperationOutcome();
                       validationResult.setId(id);
                       resource.addExtension(new Extension()
                               .setUrl(MctConstants.VALIDATION_EXTENSION_URL)
                               .setValue(new Reference("#" + id)));
                       resource.addContained((Resource) validationResult);
                    }
                 }
              }
      );
   }
}
