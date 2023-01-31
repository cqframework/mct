package org.opencds.cqf.mct.api;

import ca.uhn.fhir.rest.annotation.Operation;
import org.hl7.fhir.r4.model.Bundle;
import org.opencds.cqf.mct.SpringContext;
import org.opencds.cqf.mct.config.MctConstants;
import org.opencds.cqf.mct.service.MeasureConfigurationService;

public class MeasureConfigurationAPI {

   private final MeasureConfigurationService measureConfigurationService;

   public MeasureConfigurationAPI() {
      measureConfigurationService = SpringContext.getBean(MeasureConfigurationService.class);
   }

   @Operation(name = MctConstants.LIST_MEASURE_OPERATION_NAME, idempotent = true)
   public Bundle listMeasures() {
      return measureConfigurationService.listMeasures();
   }

}
