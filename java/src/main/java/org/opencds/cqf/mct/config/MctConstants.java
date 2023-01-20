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
   public static final String REGISTER_OPERATION_NAME = "$register-facilities";
   public static final String REGISTER_PARAM_LOCATIONS = "locations";
   public static final String REGISTER_PARAM_ORGANIZATION = "organization";
   public static final String UNREGISTER_OPERATION_NAME = "$unregister-facility";
   public static final String UNREGISTER_PARAM_FACILITY_ID = "facilityId";
   public static final String FHIR_REST_CONNECTION_TYPE = "hl7-fhir-rest";
   public static final String MISSING_FHIR_REST_ENDPOINT = "No contained REST FHIR endpoint was present";
   public static final String REGISTER_MISSING_PARAMS = "A list of locations or an organization must be provided";
   public static final String UNREGISTER_MISSING_PARAMS = "A facility reference must be provided";
   public static final String REGISTER_SUCCESS = "Successfully registered facilities";
   public static final String UNREGISTER_SUCCESS = "Successfully unregistered the facility";

   // OperationOutcome Constants
   public static final String SEVERITY_ERROR = "error";
   public static final String SEVERITY_INFORMATION = "information";
   public static final String CODE_INFORMATIONAL = "informational";
   public static final String CODE_PROCESSING = "processing";

}
