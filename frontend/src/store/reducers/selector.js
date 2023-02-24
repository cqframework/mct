export const getFacility = (state) => {
  const { facilities } = state.data;
  const { facility: facilityId } = state.filter;
  return facilities.find((facility) => facility.id === facilityId);
};

export const getMeasure = (state) => {
  const { measures } = state.data;
  const { measure } = state.filter;

  return measures.find((i) => i.id === measure);
};

export const getOrganization = (state) => {
  const { organizations } = state.data;
  const { organization: organizationId } = state.filter;
  return organizations.find((organization) => organization.id === organizationId);
};
