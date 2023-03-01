# MCT
Measure Calculation Tool for reporting and calculating FHIR-based digital quality measures (dQMs).

## Getting started

#### Pre-requisities
- NodeJs version 18 is required
- Docker is required

#### Steps
1. Run script `./setup_app_files` to prep data files
2. Standup services with `docker-compose up` and wait 1 minute before proceeding to next step
3. Load data with `./load-local-data.sh`
4. go to `frontend` directory and run `yarn install && yarn start`
5. Wait until `http://localhost:8088` loads with `Whitelabel Error` message
6. Navigate to app at `http://localhost:3000`

# License
All code in this repository is licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0). All documentation is licensed under the [Creative Common Attribution 4.0 International license (CC BY 4.0)](https://creativecommons.org/licenses/by/4.0/).

Copyright 2022 The Centers for Medicare & Medicaid Services

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.