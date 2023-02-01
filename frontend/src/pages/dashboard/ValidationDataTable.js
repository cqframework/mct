import { useState, useEffect} from 'react';
import { styled } from '@mui/material/styles';
import { LeftOutlined, WarningOutlined, ExclamationCircleOutlined, InfoCircleOutlined, CheckCircleOutlined } from '@ant-design/icons';
import { IconButton, Tooltip, Typography, Grid } from '@mui/material';
import { useDispatch, useSelector } from 'react-redux';

import MuiAccordion from '@mui/material/Accordion';
import MuiAccordionSummary from '@mui/material/AccordionSummary';
import MuiAccordionDetails from '@mui/material/AccordionDetails';
import Selection from 'components/Selection'
import { inputSelection } from 'store/reducers/filter';

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
  const [expanded, setExpanded] = useState('panel1');
  const { facility } = useSelector((state) => state.filter);
  const { facilities } = useSelector((state) => state.data);
  const dispatch = useDispatch();

  const handleChange = (panel) => (_event, newExpanded) => {
    setExpanded(newExpanded ? panel : false);
  };

  return (
    <Grid container spacing={2}>    
        <Grid item xs={6}>
        <Typography variant={'h4'}>
          Validation Messages
        <Tooltip title={'Issues discovered from measure calculation'}>
          <IconButton>
            <InfoCircleOutlined />
          </IconButton>
        </Tooltip>    
        </Typography>
      </Grid>
      <Grid item xs={6}>
        <Selection
          options={facilities}
          label="Facilities"
          currentSelection={facility || facilities[0]}
          handleChange={(newFacility) => {
            dispatch(inputSelection({ type: 'facility', value: newFacility }));
          }}
        />
        </Grid>  
      <Grid item xs={12}>
      {resources.map((i) => {
        const resourceOperationOutcomes = i?.contained?.filter(i => i.resourceType === 'OperationOutcome')?.[0]
        return (
          <Accordion key={i.id} defaultExpanded={resourceOperationOutcomes != null} onChange={handleChange(i.id)}>
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
      </Grid>
    </Grid>
  );
}
