import MeasureReportMCT from 'fixtures/MeasureReportMCT.json';

const extractDescription = (measureReport) => {
  const extUrl = 'http://hl7.org/fhir/5.0/StructureDefinition/extension-MeasureReport.population.description';
  return measureReport?.extension?.find((extension) => extension.url === extUrl)?.valueString;
};

const parseMeasureReport = (measureReport) => {
  const { type } = measureReport;
  switch (type) {
    case 'individual':
      break;
    case 'subject-list':
      break;
    case 'summary':
      break;
    default:
      break;
  }
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

const gatherIndividualList = (measureReportBundle) => {
  const measureReport = measureReportBundle.entry.find(i => i.resource.type === 'individual').resource
  if (measureReport?.type !== 'individual') {
    console.warn('This is not a individual measure report');
    return null;
  }

  return MeasureReportMCT.contained?.[0]?.entry.reduce((acc, entry) => {
    const resourceType = entry.resource.resourceType;
    if (resourceType !== 'List') {
      if (resourceType === 'Patient') {
        acc['patient'] = entry.resource;
        return acc;
      }
      acc['resources'] = [...(acc['resources'] || []), entry.resource];
    }
    return acc;
  }, {});
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

export { extractDescription, parseMeasureReport, gatherIndividualList, populationGather, parseStratifier };
