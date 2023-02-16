import Patient from 'fixtures/Patient';

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

const gatherIndividualList = (measureReportParameters) => {
  if (measureReportParameters.parameter.length === 1) {
    const entries = measureReportParameters.parameter?.[0]?.resource?.entry;

    const extractedMeasureReport = {
      patients: [],
      resources: [],
      measureReport: null
    };

    entries.forEach(({ resource }) => {
      if (resource.resourceType === 'MeasureReport') {
        extractedMeasureReport.measureReport = resource;
      } else if (resource.resourceType === 'Patient') {
        extractedMeasureReport.patients.push(resource);
      } else {
        extractedMeasureReport.resources.push(resource);
      }
    });

    return extractedMeasureReport;
  }
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

export { extractDescription, gatherIndividualList, populationGather, parseStratifier };
