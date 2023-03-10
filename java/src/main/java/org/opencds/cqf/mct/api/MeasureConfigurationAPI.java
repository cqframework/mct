package org.opencds.cqf.mct.api;

import ca.uhn.fhir.rest.annotation.Operation;
import org.hl7.fhir.r4.model.Bundle;
import org.opencds.cqf.mct.SpringContext;
import org.opencds.cqf.mct.config.MctConstants;
import org.opencds.cqf.mct.service.MeasureConfigurationService;

/**
 * The Measure Configuration API.
 */
public class MeasureConfigurationAPI {

   private final MeasureConfigurationService measureConfigurationService;

   /**
    * Instantiates a new Measure configuration API.
    */
   public MeasureConfigurationAPI() {
      measureConfigurationService = SpringContext.getBean(MeasureConfigurationService.class);
   }

   /**
    * The $list-measures operation.
    *
    * @return the configured measures in a bundle
    */
   @Operation(name = MctConstants.LIST_MEASURE_OPERATION_NAME, idempotent = true)
   public Bundle listMeasures() {
      return measureConfigurationService.listMeasures();
   }

}
