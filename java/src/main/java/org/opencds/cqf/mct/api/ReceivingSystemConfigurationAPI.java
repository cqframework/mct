package org.opencds.cqf.mct.api;

import ca.uhn.fhir.rest.annotation.Operation;
import org.hl7.fhir.r4.model.Bundle;
import org.opencds.cqf.mct.SpringContext;
import org.opencds.cqf.mct.config.MctConstants;
import org.opencds.cqf.mct.service.ReceivingSystemConfigurationService;

/**
 * The Receiving System Configuration API.
 */
public class ReceivingSystemConfigurationAPI {

   private final ReceivingSystemConfigurationService receivingSystemConfigurationService;

   /**
    * Instantiates a new Receiving System Configuration API.
    */
   public ReceivingSystemConfigurationAPI() {
      receivingSystemConfigurationService = SpringContext.getBean(ReceivingSystemConfigurationService.class);
   }

   /**
    * The $list-receiving-systems operation.
    *
    * @return a bundle of receiving system <a href="http://hl7.org/fhir/endpoint.html">Endpoint</a> resources
    */
   @Operation(name = MctConstants.LIST_REC_SYSTEM_OPERATION_NAME, idempotent = true)
   public Bundle listReceivingSystems() {
      return receivingSystemConfigurationService.listReceivingSystems();
   }

}
