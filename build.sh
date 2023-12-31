#!/bin/sh

javac -d ./bin ./src/*.java && java -classpath ./bin Main "$@"
