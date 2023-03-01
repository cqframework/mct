import { useState } from 'react';
import { styled } from '@mui/material/styles';
import { LeftOutlined, InfoCircleOutlined, CheckCircleOutlined } from '@ant-design/icons';
import { IconButton, Tooltip, Typography, FormLabel, Switch, Grid } from '@mui/material';

import MuiAccordion from '@mui/material/Accordion';
import MuiAccordionSummary from '@mui/material/AccordionSummary';
import MuiAccordionDetails from '@mui/material/AccordionDetails';
import SeverityIcon from 'components/SeverityIcon';
import { issueTypesMap } from 'constants/issueTypesMap';
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

export default function ValidationDataTable({ resources }) {
  const [showDetailedMessages, setShowDetailedMessages] = useState(true);

  const handleChange = (panel) => (_event, newExpanded) => {
    setExpanded(newExpanded ? panel : false);
  };

  return (
    <Grid container>
      <Grid item xs={9}>
        <Typography sx={{ mb: 2 }} variant={'h4'}>
          Validation Messages
          <Tooltip title={'Issues discovered from measure calculation'}>
            <IconButton>
              <InfoCircleOutlined />
            </IconButton>
          </Tooltip>
        </Typography>
      </Grid>
      <Grid item xs={3}>
        <FormLabel> Show Detailed Messaging</FormLabel>
        <Switch checked={showDetailedMessages} onChange={() => setShowDetailedMessages(!showDetailedMessages)} />
      </Grid>
      <Grid item xs={12}>
        {resources.map((i, index) => {
          let resourceOperationOutcomes = i?.contained?.filter((i) => i.resourceType === 'OperationOutcome')?.[0];
          if (i.resourceType === 'OperationOutcome') {
            resourceOperationOutcomes = i;
          }

          return (
            <Accordion key={`${i.id}-${index}`} defaultExpanded={resourceOperationOutcomes != null} onChange={handleChange(i.id)}>
              <AccordionSummary
                aria-controls={`panel${i.id}-content`}
                id={`panel${i.id}-header`}
                isOperationOutcome={resourceOperationOutcomes != null}
              >
                <Typography>{i.resourceType === 'OperationOutcome' ? 'Missing Data Requirements' : `${i.resourceType}/${i.id}`}</Typography>
                {resourceOperationOutcomes && (
                  <Typography
                    sx={{ color: 'red', ml: 1.5, fontWeight: 'bold' }}
                  >{`(${resourceOperationOutcomes?.issue?.length}) Issues`}</Typography>
                )}
              </AccordionSummary>
              {resourceOperationOutcomes?.issue?.map((issue, index) => (
                <AccordionDetails key={`${i.id}-${index}`}>
                  <Typography sx={{ fontWeight: 'bold' }}>
                    <SeverityIcon severity={issue.severity} />
                    {'  '}
                    {issueTypesMap[issue.code] || issue.code}
                  </Typography>
                  {showDetailedMessages && <Typography sx={{ ml: 1.5 }}>{issue.diagnostics}</Typography>}
                </AccordionDetails>
              ))}
            </Accordion>
          );
        })}
      </Grid>
    </Grid>
  );
}
