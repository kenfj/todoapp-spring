#!/bin/bash

mvn package

java -jar target/demo-0.0.1-SNAPSHOT.jar

# java -jar target/demo-0.0.1-SNAPSHOT.jar --debug
# java -jar target/demo-0.0.1-SNAPSHOT.jar --server.port=8080
