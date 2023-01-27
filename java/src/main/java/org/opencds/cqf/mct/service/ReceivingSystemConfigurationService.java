package org.opencds.cqf.mct.service;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;
import org.opencds.cqf.cql.evaluator.engine.retrieve.BundleRetrieveProvider;
import org.opencds.cqf.mct.SpringContext;

public class ReceivingSystemConfigurationService {

   private final BundleRetrieveProvider bundleRetrieveProvider;

   public ReceivingSystemConfigurationService() {
      bundleRetrieveProvider = new BundleRetrieveProvider(
              SpringContext.getBean(FhirContext.class),
              SpringContext.getBean("receivingSystemsBundle", Bundle.class));
   }

   public Bundle listReceivingSystems() {
      Bundle endpoints = new Bundle().setType(Bundle.BundleType.COLLECTION);
      bundleRetrieveProvider.retrieve(null, null, null, "Endpoint",
              null, null, null, null, null, null,
              null, null).forEach(x -> endpoints.addEntry().setResource((Resource) x));
      return endpoints;
   }

}
