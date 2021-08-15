@echo off
set BASE=%~dp0

cd /d %BASE%

set CLASSPATH=%BASE%libs\*;

java -classpath "%CLASSPATH%" org.tonylin.wiremock.redfish.Application

