import * as React from 'react';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';

export default function AlertDialog({ isVisible, setVisibility, organizationId, organizationName, setStatusMessage }) {
  const handleClose = () => {
    setVisibility(false);
    setStatusMessage(true);
  };

  const handleSubmit = async () => {
    // await fetch(`${baseUrl}/mct/$submit?organization${organizationId}`,{
    //   method: 'POST'
    // });
    handleClose();
  };

  return (
    <div>
      <Dialog open={isVisible} onClose={handleClose} aria-labelledby="alert-dialog-title" aria-describedby="alert-dialog-description">
        <DialogTitle id="alert-dialog-title">{'Measure Report Submission'}</DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            Are you sure you want to submit Measure Report for {organizationName}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancel</Button>
          <Button onClick={handleSubmit}>Submit</Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}
