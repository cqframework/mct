#!/usr/bin/env bash

set -e
set -o pipefail

DIR="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
sed -i '' 's/localhost:8080/cqf-ruler-a:8080/g' ${DIR}/../java/src/main/resources/configuration/facilities/facilities-bundle.json
sed -i '' 's/localhost:8082/cqf-ruler-b:8080/g' ${DIR}/../java/src/main/resources/configuration/facilities/facilities-bundle.json