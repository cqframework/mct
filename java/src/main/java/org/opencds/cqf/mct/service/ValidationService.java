package org.opencds.cqf.mct.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationOptions;
import ca.uhn.fhir.validation.ValidationResult;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IPrimitiveType;

import java.util.Collections;

public class ValidationService {
   private final FhirContext fhirContext;
   private final FhirValidator validator;
   private final boolean requireProfile;

   public ValidationService(FhirContext fhirContext, FhirValidator validator, boolean requireProfile) {
      this.fhirContext = fhirContext;
      this.validator = validator;
      this.requireProfile = requireProfile;
   }

   public ValidationResult validate(IBaseResource resource) {
      ValidationOptions options = new ValidationOptions();
      if (requireProfile) {
         if (resource.getMeta() == null || resource.getMeta().getProfile() == null) {
            SingleValidationMessage message = new SingleValidationMessage();
            message.setSeverity(ResultSeverityEnum.ERROR);
            message.setMessage(String.format("The resource %s does not have a specified profile", resource.getIdElement().getValue()));
            return new ValidationResult(fhirContext, Collections.singletonList(message));
         }
         for (IPrimitiveType<String> profile : resource.getMeta().getProfile()) {
            options.addProfileIfNotBlank(profile.getValue());
         }
      }
      return validator.validateWithResult(resource, options);
   }
}
