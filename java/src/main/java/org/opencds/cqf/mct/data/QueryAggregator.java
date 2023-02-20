package org.opencds.cqf.mct.data;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.InternalCodingDt;
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryAggregator {
   private final Map<String, List<IQueryParameterType>> pathQueryMap;
   private final List<ValueSetInfo> valueSetInfoList;

   public QueryAggregator() {
      pathQueryMap = new HashMap<>();
      valueSetInfoList = new ArrayList<>();
   }

   public void addQuery(String path, List<InternalCodingDt> codes, ValueSetInfo valueSetInfo) {
      if (pathQueryMap.containsKey(path)) {
         pathQueryMap.get(path).addAll(codes);
      }
      else {
         pathQueryMap.put(path, new ArrayList<>(codes));
      }
      // This shouldn't require a duplication check
      valueSetInfoList.add(valueSetInfo);
   }

   public Map<String, List<IQueryParameterType>> getPathQueryMap() {
      return pathQueryMap;
   }

   public List<ValueSetInfo> getValueSetInfoList() {
      return valueSetInfoList;
   }
}
