import * as React from 'react';
import { styled } from '@mui/material/styles';
import { LeftOutlined, WarningOutlined, ExclamationCircleOutlined, InfoCircleOutlined, CheckCircleOutlined } from '@ant-design/icons';
import MuiAccordion from '@mui/material/Accordion';
import MuiAccordionSummary from '@mui/material/AccordionSummary';
import MuiAccordionDetails from '@mui/material/AccordionDetails';
import Typography from '@mui/material/Typography';

const Accordion = styled((props) => <MuiAccordion disableGutters elevation={0} square {...props} />)(({ theme }) => ({
  border: `1px solid ${theme.palette.divider}`,
  '&:not(:last-child)': {
    borderBottom: 0
  },
  '&:before': {
    display: 'none'
  }
}));

const AccordionSummary = styled((props) => (
  <MuiAccordionSummary
    expandIcon={!props.isOperationOutcome ? <CheckCircleOutlined style={{ color: 'green' }} /> : <LeftOutlined />}
    {...props}
  />
))(({ theme, isOperationOutcome }) => ({
  backgroundColor: theme.palette.mode === 'dark' ? 'rgba(255, 255, 255, .05)' : 'rgba(0, 0, 0, .03)',
  flexDirection: 'row-reverse',
  '& .MuiAccordionSummary-expandIconWrapper.Mui-expanded': {
    transform: 'rotate(-90deg)'
  },
  '& .MuiAccordionSummary-content': {
    marginLeft: theme.spacing(1)
  }
}));

const AccordionDetails = styled(MuiAccordionDetails)(({ theme }) => ({
  padding: theme.spacing(2),
  borderTop: '1px solid rgba(0, 0, 0, .125)'
}));

const SeverityIcon = ({ severity }) => {
  switch (severity) {
    case 'warning':
      return <WarningOutlined style={{ color: 'orange' }} />;
    case 'error':
      return <ExclamationCircleOutlined style={{ color: 'red' }} />;
    case 'information':
      return <InfoCircleOutlined style={{ color: '#1890ff' }} />;
  }
};

export default function ValidationDataTable({ resources }) {
  const [expanded, setExpanded] = React.useState('panel1');

  const handleChange = (panel) => (_event, newExpanded) => {
    setExpanded(newExpanded ? panel : false);
  };

  return (
    <div>
      {resources.map((i) => {
        const isOperationOutcome = i.resourceType === 'OperationOutcome';
        return (
          <Accordion key={i.id} defaultExpanded={isOperationOutcome} onChange={handleChange(i.id)}>
            <AccordionSummary aria-controls={`panel${i.id}-content`} id={`panel${i.id}-header`} isOperationOutcome={isOperationOutcome}>
              <Typography>{i.resourceType}</Typography>
            </AccordionSummary>
            {isOperationOutcome &&
              i.issue.map((issue, index) => (
                <AccordionDetails key={`${i.id}-${index}`}>
                  <Typography>
                    <SeverityIcon severity={issue.severity} />
                    {'  '}{issue.diagnostics}
                  </Typography>
                </AccordionDetails>
              ))}
          </Accordion>
        );
      })}
    </div>
  );
}
