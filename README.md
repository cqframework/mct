# MCT
Measure Calculation Tool for reporting and calculating FHIR-based digital quality measures (dQMs).


## Status and Maintainance

This tool is a prototype which we are releasing in the hope that it serves as a useful starting point for further development by interested parties. It currently lacks important features, such as including authentication, and allows for the client to make arbitrary changes to the report before submission. **It is not suitable for production use in its current state.**

This is a meritocratic, consensus-based community project. Anyone with interest in the project can join the community, contribute to the project design and participate in the decision making process. Participation in these processes is completely voluntary. This document describes how that participation takes place and how to set about earning merit within the project community.

Although these processes are adapted from the OSSWatch Meritocratic Model, this documentation is a formalization of existing processes involving relevant stakeholders.

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