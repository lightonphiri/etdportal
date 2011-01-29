#!/bin/sh
cd /var/lib/tomcat6/webapps
java -cp harvester/WEB-INF/classes:harvester/WEB-INF/lib/mysql-connector-java.jar OAIHarvest

java -cp portal/WEB-INF/classes:portal/WEB-INF/lib/mysql-connector-java.jar:portal/WEB-INF/lib/lucene-analyzers.jar:portal/WEB-INF/lib/lucene-core.jar  HarvestingMain

