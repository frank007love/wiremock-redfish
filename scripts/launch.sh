#!/bin/sh

FULLPATH=`dirname "$0"`/`basename "$0"`
BASE=`readlink -f "$FULLPATH"`
BASEPATH=`dirname $BASE`

cd $BASEPATH

CLASSPATH=$BASEPATH/libs/*:

java -classpath $CLASSPATH org.tonylin.wiremock.redfish.Application

