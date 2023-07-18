# MCT
Measure Calculation Tool for reporting and calculating FHIR-based digital quality measures (dQMs).

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
