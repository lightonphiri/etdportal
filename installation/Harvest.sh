#!/bin/sh
cd /var/lib/tomcat7/webapps/harvester
java -Dhttp.proxyHost=localhost -Dhttp.proxyPort=3128 -Xms512m -Xmx512m -cp WEB-INF/classes:WEB-INF/lib/mysql-connector-java.jar OAIHarvest $1

java -cp portal/WEB-INF/classes:portal/WEB-INF/lib/mysql-connector-java.jar:portal/WEB-INF/lib/lucene-analyzers.jar:portal/WEB-INF/lib/lucene-core.jar  HarvestingMain

