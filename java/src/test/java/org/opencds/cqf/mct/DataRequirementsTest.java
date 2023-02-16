package org.opencds.cqf.mct;

import ca.uhn.fhir.model.api.IQueryParameterType;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.mct.service.DataRequirementsService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Map;

@Import(MctApplication.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { DataRequirementsTest.class })
public class DataRequirementsTest {

   @Test
   void testDataRequirements() {
      Map<String, Map<String, List<IQueryParameterType>>> result = new DataRequirementsService().getSearchParamsByType();
      String s = "";
   }
}
