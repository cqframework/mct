const processForSummary = (state) => {
  const { facilities, measures, patients, measureReport } = state.data;
  const { facility, measure, selectedPatients } = state.filter;

  // Organization
  // Facility name
  // Measure name

  // patient count
  // total issues broken down by severity (maybe on warn on the errors)

  // Population details to remark about?
};

const getFacility = (state) => {
  const { facilities } = state.data;
  return facilities.find((facility) => facility.id === facilityId);
};

const getMeasure = (state, measureId) => {
  const { measures } = state.data;
  return measures.find((measure) => measure.id === measureId);
};
