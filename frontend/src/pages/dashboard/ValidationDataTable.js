import * as React from 'react';
import { styled } from '@mui/material/styles';
import { LeftOutlined, WarningOutlined, ExclamationCircleOutlined, InfoCircleOutlined, CheckCircleOutlined } from '@ant-design/icons';
import { IconButton, Tooltip, Typography } from '@mui/material';

import MuiAccordion from '@mui/material/Accordion';
import MuiAccordionSummary from '@mui/material/AccordionSummary';
import MuiAccordionDetails from '@mui/material/AccordionDetails';

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
    transform: isOperationOutcome ? 'rotate(-90deg)' : 'rotate(0deg)'
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
      <Typography variant={'h4'}>
        Validation Messages
      <Tooltip title={'Issues discovered from measure calculation'}>
        <IconButton>
          <InfoCircleOutlined />
        </IconButton>
      </Tooltip>    
      </Typography>
      
      {resources.map((i) => {
        const resourceOperationOutcomes = i?.contained?.filter(i => i.resourceType === 'OperationOutcome')?.[0]

        return (
          <Accordion key={i.id} defaultExpanded={resourceOperationOutcomes} onChange={handleChange(i.id)}>
            <AccordionSummary aria-controls={`panel${i.id}-content`} id={`panel${i.id}-header`} isOperationOutcome={resourceOperationOutcomes != null}>
              <Typography>{i.resourceType}/{i.id}</Typography>
              {resourceOperationOutcomes && (
                <Typography sx={{color: 'red', ml: 1.5, fontWeight: 'bold'}}>{`(${resourceOperationOutcomes?.issue?.length}) issues`}</Typography>
              )}
            </AccordionSummary>
            {resourceOperationOutcomes &&
              resourceOperationOutcomes?.issue?.map((issue, index) => (
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
