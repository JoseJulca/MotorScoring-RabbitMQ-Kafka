#!/usr/bin/env sh
set -eu
PROFILE="${1:-mongodb}"
mvn clean verify
mvn -pl motor-scoring-bootstrap -am spring-boot:run -Dspring-boot.run.profiles="$PROFILE"
