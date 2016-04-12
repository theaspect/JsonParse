#!/bin/bash
OUT=build/libs
LIBS=$OUT/groovy-all-2.4.4.jar:$OUT/log4j-api-2.5.jar:$OUT/log4j-core-2.5.jar:$OUT/JsonParse-1.0-SNAPSHOT.jar
MAIN=ParseJson

java -cp $LIBS $MAIN