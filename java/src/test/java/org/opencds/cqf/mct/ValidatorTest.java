package org.opencds.cqf.mct;

import ca.uhn.fhir.validation.ValidationResult;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.mct.service.ValidationService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(MctApplication.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { ValidatorTest.class })
class ValidatorTest {
   private ValidationService validationService;

   @BeforeEach
   public void setup() {
      validationService = SpringContext.getBean(ValidationService.class);
   }

   @Test
   void validatePatientValid() {
      Patient patient = new Patient();
      patient.setId("example");
      patient.setMeta(new Meta().addProfile("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-patient"));
      patient.addIdentifier().setUse(Identifier.IdentifierUse.USUAL).setSystem("urn:oid:1.2.36.146.595.217.0.1").setValue("12345");
      patient.addName().setFamily("Doe").addGiven("Jane");
      patient.setGender(Enumerations.AdministrativeGender.FEMALE);
      patient.setBirthDate(new Date(25200000L));

      ValidationResult result = validationService.validate(patient);
      assertTrue(result.isSuccessful());
   }

   @Test
   void validatePatientMissingIdentifier() {
      Patient patient = new Patient();
      patient.setId("example");
      patient.setMeta(new Meta().addProfile("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-patient"));
      patient.addName().setFamily("Doe").addGiven("Jane");
      patient.setGender(Enumerations.AdministrativeGender.FEMALE);
      patient.setBirthDate(new Date(25200000L));

      ValidationResult result = validationService.validate(patient);
      assertFalse(result.isSuccessful());
   }

   @Test
   void validatePatientMissingProfile() {
      Patient patient = new Patient();
      patient.setId("example");
      patient.addIdentifier().setUse(Identifier.IdentifierUse.USUAL).setSystem("urn:oid:1.2.36.146.595.217.0.1").setValue("12345");
      patient.addName().setFamily("Doe").addGiven("Jane");
      patient.setGender(Enumerations.AdministrativeGender.FEMALE);
      patient.setBirthDate(new Date(25200000L));

      ValidationResult result = validationService.validate(patient);
      assertFalse(result.isSuccessful());
   }

   @Test
   void validateConditionValid() {
      Condition condition = new Condition();
      condition.setId("example");
      condition.setMeta(new Meta().addProfile("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-condition"));
      condition.addCategory(new CodeableConcept().addCoding(
              new Coding().setCode("problem-list-item").
                      setSystem("http://terminology.hl7.org/CodeSystem/condition-category")));
      condition.setCode(new CodeableConcept().addCoding(
              new Coding().setCode("109006").setSystem("http://snomed.info/sct")));
      condition.setSubject(new Reference("Patient/example"));

      ValidationResult result = validationService.validate(condition);
      assertTrue(result.isSuccessful());
   }

   @Test
   void validateConditionMissingCategory() {
      Condition condition = new Condition();
      condition.setId("example");
      condition.setMeta(new Meta().addProfile("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-condition"));
      condition.setCode(new CodeableConcept().addCoding(
              new Coding().setCode("404684003").setSystem("http://snomed.info/sct")));
      condition.setSubject(new Reference("Patient/example"));

      ValidationResult result = validationService.validate(condition);
      assertFalse(result.isSuccessful());
   }

   @Test
   void validateConditionInvalidCode() {
      Condition condition = new Condition();
      condition.setId("example");
      condition.setMeta(new Meta().addProfile("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-condition"));
      condition.addCategory(new CodeableConcept().addCoding(
              new Coding().setCode("problem-list-item").
                      setSystem("http://terminology.hl7.org/CodeSystem/condition-category")));
      condition.setCode(new CodeableConcept().addCoding(
              new Coding().setCode("12345").setSystem("http://snomed.info/sct")));
      condition.setSubject(new Reference("Patient/example"));

      ValidationResult result = validationService.validate(condition);
      assertFalse(result.isSuccessful());
   }

}
