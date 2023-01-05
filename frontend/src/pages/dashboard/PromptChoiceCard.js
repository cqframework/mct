import React from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardActions from '@mui/material/CardActions';
import CardContent from '@mui/material/CardContent';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import { ArrowLeftOutlined } from '@ant-design/icons';

export default function PromptChoiceCard({ message }) {
  return (
    <Box sx={{ minWidth: 275 }}>
      <Card variant="outlined">
        <CardContent>
        <Typography sx={{ mt: 10, fontSize: 30 }} variant="h1" gutterBottom>
          <ArrowLeftOutlined /> {message}
        </Typography>
      </CardContent>
      <CardActions>
        <Button size="small">Learn More</Button>
    </CardActions>
      </Card>
    </Box>
  );
}
