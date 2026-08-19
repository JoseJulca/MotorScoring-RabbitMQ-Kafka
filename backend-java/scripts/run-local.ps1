param(
    [ValidateSet("mongodb", "h2")]
    [string]$Profile = "mongodb"
)
$ErrorActionPreference = "Stop"
mvn clean verify
mvn -pl motor-scoring-bootstrap -am spring-boot:run "-Dspring-boot.run.profiles=$Profile"
