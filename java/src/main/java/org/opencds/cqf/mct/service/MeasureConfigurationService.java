package org.opencds.cqf.mct.service;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;
import org.opencds.cqf.cql.evaluator.engine.retrieve.BundleRetrieveProvider;
import org.opencds.cqf.mct.SpringContext;

public class MeasureConfigurationService {

   private final BundleRetrieveProvider bundleRetrieveProvider;

   public MeasureConfigurationService() {
      bundleRetrieveProvider = new BundleRetrieveProvider(
              SpringContext.getBean(FhirContext.class),
              SpringContext.getBean("measuresBundle", Bundle.class));
   }

   public Bundle listMeasures() {
      Bundle measures = new Bundle().setType(Bundle.BundleType.COLLECTION);
      bundleRetrieveProvider.retrieve(null, null, null, "Measure",
              null, null, null, null, null, null,
              null, null).forEach(x -> measures.addEntry().setResource((Resource) x));
      return measures;
   }

}
