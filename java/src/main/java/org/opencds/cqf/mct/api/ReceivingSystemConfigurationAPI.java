package org.opencds.cqf.mct.api;

import ca.uhn.fhir.rest.annotation.Operation;
import org.hl7.fhir.r4.model.Bundle;
import org.opencds.cqf.mct.SpringContext;
import org.opencds.cqf.mct.config.MctConstants;
import org.opencds.cqf.mct.service.ReceivingSystemConfigurationService;

public class ReceivingSystemConfigurationAPI {

   private final ReceivingSystemConfigurationService receivingSystemConfigurationService;

   public ReceivingSystemConfigurationAPI() {
      receivingSystemConfigurationService = SpringContext.getBean(ReceivingSystemConfigurationService.class);
   }

   @Operation(name = MctConstants.LIST_REC_SYSTEM_OPERATION_NAME)
   public Bundle listReceivingSystems() {
      return receivingSystemConfigurationService.listReceivingSystems();
   }

}
