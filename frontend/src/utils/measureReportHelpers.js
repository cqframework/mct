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
  const processedMeasureReport = {
    individualLevelData: [],
    populationData: null,
    measureReport: null
  };
  if (measureReportParameters.parameter.length === 1) {
    const individualData = gatherIndividualLevelData(
      measureReportParameters.parameter?.[0]?.resource?.entry,
      measureReportParameters.parameter?.[0]?.name
    );
    processedMeasureReport.measureReport = measureReportParameters.parameter?.[0]?.resource.entry.find(
      ({ resource }) => resource.resourceType === 'MeasureReport'
    ).resource;
    processedMeasureReport.individualLevelData = [individualData];
    return processedMeasureReport;
  } else {
    measureReportParameters.parameter.forEach(({ name, resource }) => {
      if (name === 'population-report') {
        const populationData = populationGather(resource.group[0]);
        processedMeasureReport.populationData = populationData;
        processedMeasureReport.measureReport = resource;
      } else {
        const individualLevelData = gatherIndividualLevelData(resource?.entry, name);
        processedMeasureReport.individualLevelData.push(individualLevelData);
      }
    });

    return processedMeasureReport;
  }
};

const summarizeMeasureReport = (measureReport) => {
  if (!measureReport) return null;
  const processedContents = processMeasureReportPayload(measureReport);
  const stats = {
    patientCount: processedContents?.individualLevelData?.length,
    resources: {}
  };
  // Summarize resources
  const resources = processedContents?.individualLevelData?.flatMap(({ resources }) => resources);

  resources.forEach((resource) => {
    if (!stats.resources[resource?.resourceType]) {
      stats.resources[resource?.resourceType] = {
        information: 0,
        warning: 0,
        error: 0,
        count: 0
      };
    }

    if (resource.resourceType === 'OperationOutcome') {
      resource.issue?.forEach((issue) => {
        stats.resources[resource?.resourceType][issue.severity] += 1;
      });
      stats.resources[resource?.resourceType].count += 1;
    } else {
      resource?.contained
        ?.find((i) => i.resourceType === 'OperationOutcome')
        ?.issue?.forEach((issue) => {
          stats.resources[resource?.resourceType][issue.severity] += 1;
        });
      stats.resources[resource.resourceType].count += 1;
    }
  });
  return stats;
};

const gatherIndividualLevelData = (measureReportEntries, name) => {
  const individualLevelData = {
    name,
    patient: null,
    resources: [],
    measureReport: null
  };
  // we will add this at the end
  let operationOutcome = null;

  measureReportEntries.forEach(({ resource }) => {
    if (resource.resourceType === 'MeasureReport') {
      individualLevelData.measureReport = resource;
    } else if (resource.resourceType === 'Patient') {
      individualLevelData.patient = resource;
    } else if (resource.resourceType === 'OperationOutcome') {
      operationOutcome = resource;
    } else {
      individualLevelData.resources.push(resource);
    }
  });

  individualLevelData.resources = individualLevelData.resources.sort((a, b) => b?.contained?.length || 0 - a?.contained?.length || 0);
  if (operationOutcome) individualLevelData.resources.push(operationOutcome);

  return individualLevelData;
};

const populationGather = (measureReport) => {
  const population = {
    gender: {
      M: 0,
      F: 0,
      U: 0
    }
  };

  // Parse for gender
  measureReport?.contained?.forEach((data) => {
    const code = data.code.coding.find((i) => i.code);
    if (population.gender[code?.code] !== undefined) {
      population.gender[code?.code] = data.valueInteger;
    }
  });

  const measureReportGroup = measureReport?.group?.[0] || measureReport; // Also used by parseStratifier which has a different structure

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

export { extractDescription, processMeasureReportPayload, summarizeMeasureReport, populationGather, parseStratifier };
