const gatherPatientDisplayData = (pt) => {
  const name = getDisplayName(pt);
  const mrn = getMRNFromPatient(pt);
  const gender = pt?.gender?.[0]?.toUpperCase();
  const birthDate = pt?.birthDate;
  return {
    name,
    mrn,
    gender,
    birthDate
  };
};

const getMRNFromPatient = (patient) =>
  patient?.identifier?.find(
    (item) => item?.type?.coding?.[0]?.system === 'http://terminology.hl7.org/CodeSystem/v2-0203' || item?.type?.coding?.[0]?.code === 'MR'
  )?.value;

const getDisplayName = (patient) => {
  const patientName = patient?.name;
  if (patientName && patientName?.length > 0) {
    const firstName = patientName[0]?.given?.[0];
    const lastName = patientName[0]?.family;
    return `${firstName} ${lastName}`;
  }
};

export { gatherPatientDisplayData };
