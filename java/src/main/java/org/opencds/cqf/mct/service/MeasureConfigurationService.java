package org.opencds.cqf.mct.service;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Measure;
import org.hl7.fhir.r4.model.Resource;
import org.opencds.cqf.cql.evaluator.engine.retrieve.BundleRetrieveProvider;
import org.opencds.cqf.mct.SpringContext;
import org.opencds.cqf.mct.api.MeasureConfigurationAPI;

/**
 * The Measure Configuration Service logic for {@link org.opencds.cqf.mct.api.MeasureConfigurationAPI}.
 */
public class MeasureConfigurationService {

   private final BundleRetrieveProvider bundleRetrieveProvider;

   /**
    * Instantiates a new Measure Configuration Service.
    */
   public MeasureConfigurationService() {
      bundleRetrieveProvider = new BundleRetrieveProvider(
              SpringContext.getBean(FhirContext.class),
              SpringContext.getBean("measuresBundle", Bundle.class));
   }

   /**
    * The $list-measures operation logic.
    *
    * @see MeasureConfigurationAPI#listMeasures()
    * @return the configured measures in a bundle
    */
   public Bundle listMeasures() {
      Bundle measures = new Bundle().setType(Bundle.BundleType.COLLECTION);
      bundleRetrieveProvider.retrieve(null, null, null, "Measure",
              null, null, null, null, null, null,
              null, null).forEach(x -> measures.addEntry().setResource((Resource) x));
      return measures;
   }

   /**
    * Gets the specified <a href="http://hl7.org/fhir/measure.html">Measure</a> resource.
    *
    * @param measureId the measure id
    * @return the measure or null if the measure is not present
    */
   public Measure getMeasure(String measureId) {
      Iterable<Object> measures = bundleRetrieveProvider.retrieve("Measure", "id", measureId, "Measure",
              null, null, null, null, null, null,
              null, null);
      if (measures.iterator().hasNext()) {
         Object measure = measures.iterator().next();
         if (measure instanceof Measure) {
            return (Measure) measure;
         }
      }
      return null;
   }

}
