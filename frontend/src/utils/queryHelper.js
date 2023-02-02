import moment from 'moment';

const createPeriodFromQuarter = (quarter) => {
  let start, end;
  // const currentYear = new Date().getFullYear();
  const targetYear = '2022'
  switch (quarter) {
    case 'q1':
      start = moment(`Janurary 1, ${targetYear}`).startOf('quarter').startOf('day').format('YYYY-MM-DDTHH:mm:ssZ');
      end = moment(`Janurary 1, ${targetYear}`).endOf('quarter').startOf('day').format('YYYY-MM-DDTHH:mm:ssZ');
      break;
    case 'q2':
      start = moment(`April 1, ${targetYear}`).startOf('quarter').startOf('day').format('YYYY-MM-DDTHH:mm:ssZ');
      end = moment(`April 1, ${targetYear}`).endOf('quarter').startOf('day').format('YYYY-MM-DDTHH:mm:ssZ');
      break;
    case 'q3':
      start = moment(`July 1, ${targetYear}`).startOf('quarter').startOf('day').format('YYYY-MM-DDTHH:mm:ssZ');
      end = moment(`July 1, ${targetYear}`).endOf('quarter').startOf('day').format('YYYY-MM-DDTHH:mm:ssZ');
      break;
    case 'q4':
      start = moment(`October 1, ${targetYear}`).startOf('quarter').startOf('day').format('YYYY-MM-DDTHH:mm:ssZ');
      end = moment(`October 1, ${targetYear}`).endOf('quarter').startOf('day').format('YYYY-MM-DDTHH:mm:ssZ');
      break;
  }
  return { start, end };
};

export { createPeriodFromQuarter };
