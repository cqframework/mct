package org.opencds.cqf.mct.service;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;
import org.opencds.cqf.cql.evaluator.engine.retrieve.BundleRetrieveProvider;
import org.opencds.cqf.mct.SpringContext;

/**
 * The Receiving System Configuration Service for the {@link org.opencds.cqf.mct.api.ReceivingSystemConfigurationAPI}.
 */
public class ReceivingSystemConfigurationService {

   private final BundleRetrieveProvider bundleRetrieveProvider;

   /**
    * Instantiates a new Receiving System Configuration Service.
    */
   public ReceivingSystemConfigurationService() {
      bundleRetrieveProvider = new BundleRetrieveProvider(
              SpringContext.getBean(FhirContext.class),
              SpringContext.getBean("receivingSystemsBundle", Bundle.class));
   }

   /**
    * The $list-receiving-systems operation logic.
    *
    * @return a bundle of receiving system <a href="http://hl7.org/fhir/endpoint.html">Endpoint</a> resources
    */
   public Bundle listReceivingSystems() {
      Bundle endpoints = new Bundle().setType(Bundle.BundleType.COLLECTION);
      bundleRetrieveProvider.retrieve(null, null, null, "Endpoint",
              null, null, null, null, null, null,
              null, null).forEach(x -> endpoints.addEntry().setResource((Resource) x));
      return endpoints;
   }

}
