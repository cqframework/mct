package org.opencds.cqf.mct;

import ca.uhn.fhir.context.FhirVersionEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@ConfigurationProperties(prefix = "hapi.fhir")
@Configuration
@EnableConfigurationProperties
public class MctProperties {
   private FhirVersionEnum fhirVersion = null;
   private String packageServerUrl = null;
   private boolean installTransitiveIgDependencies = true;

   private boolean requireProfileForValidation = true;
   private Map<String, ImplementationGuide> implementationGuides = null;

   public FhirVersionEnum getFhirVersion() {
      return fhirVersion;
   }

   public void setFhirVersion(FhirVersionEnum fhirVersion) {
      this.fhirVersion = fhirVersion;
   }

   public String getPackageServerUrl() {
      return packageServerUrl;
   }

   public void setPackageServerUrl(String packageServerUrl) {
      this.packageServerUrl = packageServerUrl;
   }

   public boolean getInstallTransitiveIgDependencies() {
      return installTransitiveIgDependencies;
   }

   public void setInstallTransitiveIgDependencies(boolean installTransitiveIgDependencies) {
      this.installTransitiveIgDependencies = installTransitiveIgDependencies;
   }

   public boolean getRequireProfileForValidation() {
      return requireProfileForValidation;
   }

   public void setRequireProfileForValidation(boolean requireProfileForValidation) {
      this.requireProfileForValidation = requireProfileForValidation;
   }

   public Map<String, ImplementationGuide> getImplementationGuides() {
      return implementationGuides;
   }

   public void setImplementationGuides(Map<String, ImplementationGuide> implementationGuides) {
      this.implementationGuides = implementationGuides;
   }

   public static class ImplementationGuide {
      private String url;
      private String name;
      private String version;

      public String getUrl() {
         return url;
      }

      public void setUrl(String url) {
         this.url = url;
      }

      public String getName() {
         return name;
      }

      public void setName(String name) {
         this.name = name;
      }

      public String getVersion() {
         return version;
      }

      public void setVersion(String version) {
         this.version = version;
      }
   }
}
