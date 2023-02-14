package org.opencds.cqf.mct.config;

public class MctConstants {

   private MctConstants() {}

   // Gather Constants
   public static final String GATHER_OPERATION_NAME = "$gather";
   public static final String GATHER_PARAM_PATIENTS = "patients";
   public static final String GATHER_PARAM_FACILITIES = "facilities";
   public static final String GATHER_PARAM_MEASURE = "measure";
   public static final String GATHER_PARAM_PERIOD = "period";
   public static final String GATHER_PARAM_NULL_PATIENTS = "Missing required Group FHIR resource \"patients\" parameter that identifies the patients for the $gather operation";
   public static final String GATHER_PARAM_NULL_MEMBERS = "Group FHIR resource \"patients\" parameter has no members";
   public static final String GATHER_PARAM_NULL_FACILITIES = "Missing required List<String> \"facilities\" parameter that identifies the facilities for the $gather operation";
   public static final String GATHER_PARAM_NULL_MEASURE = "Missing required Measure FHIR resource identifier \"measure\" parameter that identifies the measure for the $gather operation";
   public static final String GATHER_PARAM_NULL_PERIOD = "Missing required Period FHIR data type \"period\" parameter that identifies the measurement period for the $gather operation";
   public static final String GATHER_PARAM_NULL_PERIOD_START = "Period FHIR data type \"period\" parameter is missing a start date";
   public static final String GATHER_PARAM_NULL_PERIOD_END = "Period FHIR data type \"period\" parameter is missing an end date";

   // Facility Registration Constants
   public static final String LIST_ORGANIZATIONS_OPERATION_NAME = "$list-organizations";
   public static final String LIST_FACILITIES_OPERATION_NAME = "$list-facilities";
   public static final String LIST_FACILITIES_PARAM = "organization";
   public static final String FHIR_REST_CONNECTION_TYPE = "hl7-fhir-rest";
   public static final String MISSING_FHIR_REST_ENDPOINT = "No contained REST FHIR endpoint was present";

   // Measure Configuration Constants
   public static final String LIST_MEASURE_OPERATION_NAME = "$list-measures";

   // Receiving System Configuration Constants
   public static final String LIST_REC_SYSTEM_OPERATION_NAME = "$list-receiving-systems";

   // Patient Selector Constants
   public static final String PATIENT_SELECTOR_OPERATION_NAME = "$list-patients";
   public static final String PATIENT_SELECTOR_ORG_ID_PARAM = "organizationId";

   // Generate Patient Data Constants
   public static final String GENERATE_PATIENT_DATA_OPERATION_NAME = "$generate-patient-data";

   // Extensions
   public static final String VALIDATION_EXTENSION_URL = "http://cms.gov/fhir/mct/StructureDefinition/validation-result";
   public static final String LOCATION_EXTENSION_URL = "http://cms.gov/fhir/mct/StructureDefinition/measurereport-location";

   // Tags
   public static final String LOCATION_TAG_SYSTEM = "http://cms.gov/fhir/mct/tags/Location";

   // OperationOutcome Constants
   public static final String SEVERITY_ERROR = "error";
   public static final String SEVERITY_INFORMATION = "information";
   public static final String CODE_PROCESSING = "processing";
   public static final String CODE_INFORMATIONAL = "informational";

}
