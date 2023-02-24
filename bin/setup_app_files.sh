#!/usr/bin/env bash

set -e
set -o pipefail

DIR="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
sed -i '' 's/localhost:8080/cqf-ruler:8080/g' ${DIR}/../java/src/main/resources/configuration/facilities/facilities-bundle.json