import moment from 'moment';

const createPeriodFromQuarter = (quarter) => {
  let start, end;
  const currentYear = new Date().getFullYear();
  switch (quarter) {
    case 'q1':
      start = moment(`Janurary 1, ${currentYear}`).startOf('quarter').startOf('day').format('MM-DD-YYYY');
      end = moment(`Janurary 1, ${currentYear}`).endOf('quarter').startOf('day').format('MM-DD-YYYY');
      break;
    case 'q2':
      start = moment(`April 1, ${currentYear}`).startOf('quarter').startOf('day').format('MM-DD-YYYY');
      end = moment(`April 1, ${currentYear}`).endOf('quarter').startOf('day').format('MM-DD-YYYY');
      break;
    case 'q3':
      start = moment(`July 1, ${currentYear}`).startOf('quarter').startOf('day').format('MM-DD-YYYY');
      end = moment(`July 1, ${currentYear}`).endOf('quarter').startOf('day').format('MM-DD-YYYY');
      break;
    case 'q4':
      start = moment(`October 1, ${currentYear}`).startOf('quarter').startOf('day').format('MM-DD-YYYY');
      end = moment(`October 1, ${currentYear}`).endOf('quarter').startOf('day').format('MM-DD-YYYY');
      break;
  }
  return { start, end };
};

export { createPeriodFromQuarter };
