#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail
set -o xtrace

readonly SCRIPT_DIR="$(cd "$(dirname "$0")"; pwd)"
readonly PROJECT_HOME="${SCRIPT_DIR}"/..

export LOG_LEVEL=DEBUG
export MYSQL_HOST="${MYSQL_HOST:-localhost}"
export MYSQL_PORT="${MYSQL_PORT:-3306}"
export MYSQL_DATABASE="${MYSQL_DATABASE:-janken}"
export MYSQL_USER="${MYSQL_USER:-user}"
export MYSQL_PASSWORD="${MYSQL_PASSWORD:-password}"

main() {
  local sub_command="$1"

  cd "${PROJECT_HOME}"

  case "${sub_command}" in

    develop)
      ./gradlew \
        clean \
        flywayMigrate \
        generateTablesJooqSchemaSource \
        bootRun
      ;;

    build)
      ./gradlew \
        clean \
        dependencyCheckAnalyze \
        flywayMigrate \
        generateTablesJooqSchemaSource \
        build
      ;;

    test)
      ./gradlew \
        clean \
        flywayMigrate \
        generateTablesJooqSchemaSource \
        test
      ;;

    jar)
      java -jar app/build/libs/*.jar
      ;;

    *)
      err "Unexpected sub command : '${sub_command}'"
      ;;

  esac
}

main "$@"
