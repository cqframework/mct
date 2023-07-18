# MCT
Measure Calculation Tool for reporting and calculating FHIR-based digital quality measures (dQMs).

## Getting started

#### Pre-requisities
- NodeJs version 18 is required
- Docker is required

#### Steps
1. Run script `./setup_app_files` to prep data files
2. Standup services with `docker-compose build backend && docker-compose up` and wait 1 minute before proceeding to next step
3. Load data with `./load-local-data.sh`
4. go to `frontend` directory and run `yarn install && yarn start`
5. Wait until `http://localhost:8088` loads with `Whitelabel Error` message
6. Navigate to app at `http://localhost:3000`
