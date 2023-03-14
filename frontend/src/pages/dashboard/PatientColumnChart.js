import { useEffect, useState } from 'react';

// material-ui
import { useTheme } from '@mui/material/styles';

// third-party
import ReactApexChart from 'react-apexcharts';

const PatientColumnChart = ({ stratifier, measureReport, numeratorDescription, denominatorDescription }) => {
  const theme = useTheme();
  const allEthnicitiesMap = {};

  measureReport.contained.forEach((i) => {
    if (i?.code?.coding?.[1]?.code === 'ethnicity' || i?.code?.coding?.[1]?.code === 'race') {
      const identity = i?.code?.coding?.[0]?.code;
      allEthnicitiesMap[identity] = i.valueInteger;
    }
  });

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
      labels: {
        rotate: 0
      },
      categories: Object.keys(allEthnicitiesMap).map((code) => (code.includes('or') ? code.split(/(or)/g) : code))
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
      name: 'Ethnicity and Race',
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
            // colors: Object.keys(allEthnicitiesMap).map((i) => 'secondary')
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
