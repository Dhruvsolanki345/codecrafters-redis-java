#!/bin/bash
set -e

export JAVA_HOME="/usr/lib/jvm/java-1.21.0-openjdk-amd64/"

mvn -B --quiet package -Ddir=/tmp/codecrafters-redis-target
exec java -jar /tmp/codecrafters-redis-target/java_redis.jar "$@"