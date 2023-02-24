import { useEffect, useState } from 'react';

// material-ui
import { useTheme } from '@mui/material/styles';

// third-party
import ReactApexChart from 'react-apexcharts';

// chart options
const LOINC_MAP = {
  '2135-1': 'Hispanic or Latino',
  '2186-5': 'Non-Hispanic or Latino',
  '2054-5': 'Black or African American'
};

const PatientColumnChart = ({ stratifier, measureReport, numeratorDescription, denominatorDescription }) => {
  const theme = useTheme();
  // const allEthnicities = Object.keys(stratifier.data);
  // const numeratorDescription = stratifier.data[allEthnicities[0]].denominator.description;
  // const denominatorDescription = stratifier.data[allEthnicities[0]].numerator.description;
  const LOINC_MAP_KEYS = Object.keys(LOINC_MAP);
  const allEthnicitiesMap = {};
  measureReport.contained.forEach((i) => {
    //TODO:  There is a bug here that the string is all messed up together instead of being a javascript object
    if (i?.code?.coding?.[1]?.code === 'ethnicity') {
      allEthnicitiesMap[i?.code?.coding?.[0]?.code] = i.valueInteger;
    }
  });

  console.log(allEthnicitiesMap);
  const columnChartOptions = {
    chart: {
      type: 'bar',
      height: 430,
      toolbar: {
        show: false
      }
    },
    plotOptions: {
      bar: {
        columnWidth: '30%',
        borderRadius: 4
      }
    },
    dataLabels: {
      enabled: false
    },
    stroke: {
      show: true,
      width: 8,
      colors: ['transparent']
    },
    xaxis: {
      // categories: allEthnicities.map((code) => LOINC_MAP[code] || code)
      categories: Object.keys(allEthnicitiesMap).map((code) => LOINC_MAP[code] || code)
    },
    yaxis: {
      title: {
        text: 'Count'
      }
    },
    fill: {
      opacity: 1
    },
    tooltip: {
      y: {
        formatter(val) {
          return `${val}`;
        }
      }
    },
    legend: {
      show: true,
      position: 'bottom',
      onItemHover: {
        highlightDataSeries: true
      },
      fontFamily: `'Public Sans', sans-serif`,
      offsetX: 10,
      offsetY: 10,
      labels: {
        useSeriesColors: false
      },
      markers: {
        width: 16,
        height: 16,
        radius: '50%',
        offsexX: 2,
        offsexY: 2
      },
      itemMargin: {
        horizontal: 15,
        vertical: 60
      }
    },
    responsive: [
      {
        breakpoint: 600,
        options: {
          yaxis: {
            show: false
          }
        }
      }
    ]
  };
  const { primary, secondary } = theme.palette.text;
  const line = theme.palette.divider;

  const warning = theme.palette.warning.main;
  const primaryMain = theme.palette.primary.main;
  const successDark = theme.palette.success.dark;

  const [series] = useState([
    {
      name: 'Ethnicity',
      data: Object.values(allEthnicitiesMap)
    }
  ]);

  const [options, setOptions] = useState(columnChartOptions);

  useEffect(() => {
    setOptions((prevState) => ({
      ...prevState,
      colors: [primaryMain],
      xaxis: {
        labels: {
          style: {
            colors: [secondary, secondary, secondary, secondary, secondary, secondary]
          }
        }
      },
      yaxis: {
        labels: {
          style: {
            colors: [secondary]
          }
        }
      },
      grid: {
        borderColor: line
      },
      tooltip: {
        theme: 'light'
      },
      legend: {
        position: 'top',
        horizontalAlign: 'right',
        labels: {
          colors: 'grey.500'
        }
      }
    }));
  }, [primary, secondary, line, warning, primaryMain, successDark]);

  return (
    <div id="chart">
      <ReactApexChart options={options} series={series} type="bar" height={430} />
    </div>
  );
};

export default PatientColumnChart;
