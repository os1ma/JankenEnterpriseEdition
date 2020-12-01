#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail
set -o xtrace

readonly SCRIPT_DIR="$(cd "$(dirname "$0")"; pwd)"
readonly PROJECT_HOME="${SCRIPT_DIR}/.."

readonly JAR=${PROJECT_HOME}"/app/build/libs/app.jar"

# ビルド
"${PROJECT_HOME}/gradlew" \
  clean \
  build

# JAR の状態での実行をテスト
export DATA_DIR="${PROJECT_HOME}/data"
echo -e "0\n0" | java -jar "${JAR}"
