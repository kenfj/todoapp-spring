# Spring Boot REST API Demo Todo App

## Features

* Java 17
* Spring Boot 3
* Spring Web REST API
* Java record DTO
* HTTP PATCH using Java record
* Spring Data JDBC `ListCrudRepository`
* Spring Data JDBC custom SQL query
* Spring Data JDBC `ListQueryByExampleExecutor`
* Spring Data JDBC pagination, sort and filter
* Spring Data JDBC Event Listener to log to history table
* Flyway DB migration
* h2 `MODE=MySQL` file DB for dev and in-memory DB for test
* RFC7807 `ProblemDetail` `RestControllerAdvice` using `ResponseEntityExceptionHandler`
* RFC7807 `ProblemDetail` i18n custom message in `messages.properties`
* HTTP Client using `@HttpExchange`
* Custom properties in `application.properties`
* unit tests with coverage report

### Maven Packages in pom.xml

* org.springframework.boot
  - spring-boot-starter-web
  - spring-boot-starter-webflux
  - spring-boot-starter-validation
  - spring-boot-starter-data-jdbc
  - spring-boot-devtools
  - spring-boot-starter-test
  - spring-boot-starter-security
  - spring-boot-configuration-processor
* com.h2database
* org.flywaydb
* org.springdoc

## Quick Start

```bash
java -version
# openjdk version "17.0.8.1" 2023-08-22 LTS

mvn --version
# Apache Maven 3.9.4

mvn spring-boot:run

curl http://localhost:8080/todos
```

* http://localhost:8080/swagger-ui.html
* http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:file:./target/demodb`

## Spring Initializr

https://start.spring.io/

* Spring Web
* Spring Data JDBC
* H2 Database
* Flyway Migration
* Spring Boot DevTools

## Setup JDK Maven

* https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html
* https://maven.apache.org/download.cgi

```zsh
# MacOS .zshrc sample
export JAVA_HOME=`/usr/libexec/java_home -v 17`
export PATH=${JAVA_HOME}/bin:${PATH}
export PATH=~/Downloads/apache-maven-3.9.4/bin:${PATH}
```

## Test

* https://medium.com/@truongbui95/jacoco-code-coverage-with-spring-boot-835af8debc68
* https://sukkiri.jp/technologies/devtools/jacoco/jacoco-with-maven.html
* method naming convention is short form of `MethodName_StateUnderTest_ExpectedBehavior`
  - https://dzone.com/articles/7-popular-unit-test-naming

```bash
mvn clean test site

python -m http.server --directory ./target/site/
# unit tests report at http://localhost:8000/

python -m http.server --directory ./target/site/jacoco/ 3000
# coverage report at http://localhost:3000/

# verify if the coverage is above the minimum for CI
mvn clean verify
```

## DB Migration

* Note: 3 type of DB initialization
  - schema version: Flyway V files
  - reference data: Flyway R file
  - dev mock data: Spring built-in data.sql
    - test specific data: @Sql annotation
* in Dev: auto migration when server started
* in Prod: manual migration by Flyway plugin

```bash
export FLYWAY_URL="jdbc:h2:file:./target/demodb;MODE=MySQL;AUTO_SERVER=TRUE"
export FLYWAY_USER=sa
export FLYWAY_PASSWORD=

mvn flyway:info
mvn flyway:migrate
mvn flyway:validate
```

## Upgrade Maven Packages

* check outdated packages
  - https://medium.com/@mlvandijk/keeping-dependencies-up-to-date-with-maven-be8f7fb6441e
* upgrade packages
  - https://kazuhira-r.hatenablog.com/entry/20160528/1464444327

```bash
# display dependency updates
mvn versions:display-dependency-updates

# display plugin updates
mvn versions:display-plugin-updates

# update pom.xml as suggested
# c.f. VSCode Redhat Dependency Analytics extension

# show dependency-tree
mvn dependency:tree
mvn dependency:tree -Dincludes=org.slf4j:slf4j-api

# find unused dependencies
mvn dependency:analyze

# upgrade to release (latest stable non-SNAPSHOT)
mvn versions:use-latest-releases
# upgrade to newest possible (includes unstable SNAPSHOT)
mvn versions:use-latest-versions
```

## Reference

* https://spring.io/guides/gs/rest-service/
* https://spring.io/guides/tutorials/rest/
* https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/
