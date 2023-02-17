const extractDescription = (measureReport) => {
  const extUrl = 'http://hl7.org/fhir/5.0/StructureDefinition/extension-MeasureReport.population.description';
  return measureReport?.extension?.find((extension) => extension.url === extUrl)?.valueString;
};

const parseStratifier = (measureReport) => {
  const stratifier = {};
  measureReport.group[0].stratifier.forEach((data) => {
    const stratKey = data.code[0].coding?.[0]?.code;
    const stratumData = {};
    data?.stratum?.forEach((stratum) => {
      const key = stratum.value.text;
      stratumData[key] = populationGather(stratum);
    });

    stratifier[stratKey] = {
      ...data.code[0].coding?.[0],
      data: stratumData,
      title: data.extension?.[0]?.valueString
    };
  });
  return stratifier;
};

const processMeasureReportPayload = (measureReportParameters) => {
  if (measureReportParameters.parameter.length === 1) {
    return gatherIndividualLevelData(measureReportParameters.parameter?.[0]?.resource?.entry);
  } else {
    const populationLevelData = {
      individualLevelData: [],
      populationData: null,
      measureReport: null
    };
    measureReportParameters.parameter.forEach(({ name, resource }) => {
      if (name === 'population-report') {
        const populationData = populationGather(resource.group[0]);
        populationLevelData.populationData = populationData;
        populationLevelData.measureReport = resource;
      } else {
        const individualLevelData = gatherIndividualLevelData(resource?.entry);
        populationLevelData.individualLevelData.push(individualLevelData);
      }
    });

    return populationLevelData;
  }
};

const gatherIndividualLevelData = (measureReportEntries) => {
  const individualLevelData = {
    patients: [],
    resources: [],
    measureReport: null,
    operationOutcome: null
  };

  measureReportEntries.forEach(({ resource }) => {
    if (resource.resourceType === 'MeasureReport') {
      individualLevelData.measureReport = resource;
    } else if (resource.resourceType === 'Patient') {
      individualLevelData.patients.push(resource);
    } else if (resource.resourceType === 'OperationOutcome') {
      individualLevelData.operationOutcome = resource;
    } else {
      individualLevelData.resources.push(resource);
    }
  });

  return individualLevelData;
};

const populationGather = (measureReportGroup) => {
  const population = {};

  measureReportGroup?.population?.forEach((data) => {
    const key = data.code.coding?.[0]?.code;

    population[key] = {
      ...data.code.coding?.[0],
      id: data.id,
      count: data.count,
      reference: data.subjectResults?.reference,
      description: data.extension?.[0]?.valueString
    };
  });

  return population;
};

export { extractDescription, processMeasureReportPayload, populationGather, parseStratifier };
