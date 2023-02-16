package org.opencds.cqf.mct.validation;

import java.util.List;

public class DataRequirementsReport {

   private List<MissingDataRequirements> missingDataRequirements;

   private static class MissingDataRequirements {
      private String type;
      private String path;
      private String valueSet;
   }

}
