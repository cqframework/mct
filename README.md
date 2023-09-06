# MCT
Measure Calculation Tool for reporting and calculating FHIR-based digital quality measures (dQMs).


## Status and Limitations

This tool is a prototype which we are releasing in the hope that it serves as a useful starting point for further development by interested parties. It currently lacks important features, such as including authentication, and allows for the client to make arbitrary changes to the report before submission. **It is not suitable or intended for production use in its current state.**

## Maintainance
This is a meritocratic, consensus-based community project. Anyone with interest in the project can join the community, contribute to the project design and participate in the decision making process. Participation in these processes is completely voluntary. This document describes how that participation takes place and how to set about earning merit within the project community.

Although these processes are adapted from the OSSWatch Meritocratic Model, this documentation is a formalization of existing processes involving relevant stakeholders.

## Getting started

#### Pre-requisities
- NodeJs version 18 is required
- Docker is required
- At least 8 GB RAM is required
  - If running Docker through a virtual machine (e.g. using Docker Desktop or Colima), ensure the this RAM is allocated to the virtual machine.

#### Steps
1. Run script `./bin/setup_app_files.sh` to prep data files
2. Standup services with `docker-compose up --build` and wait 1 minute before proceeding to next step
3. Load data with `./bin/load-local-data.sh`
4. go to `frontend` directory and run `yarn install && yarn start`
5. Wait until `http://localhost:8088` loads with `Whitelabel Error` message
6. Navigate to app at `http://localhost:3000`
