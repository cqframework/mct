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

   // OperationOutcome Constants
   public static final String SEVERITY_ERROR = "error";
   public static final String CODE_PROCESSING = "processing";

}
