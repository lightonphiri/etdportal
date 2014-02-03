# Makefile for etdportal project
# hussein suleman
# 20 august 2010

# variables

TOMCAT = tomcat7
SERVLETAPI = /usr/share/$(TOMCAT)/lib

JFLAGS = -classpath WEB-INF/lib/mysql-connector-java.jar:$(SERVLETAPI)/servlet-api.jar:. \
         -d WEB-INF/classes \
         -sourcepath WEB-INF/classes

JAVAC = javac

WEBAPPS = /var/lib/$(TOMCAT)/webapps
APPNAME = harvester

# rules

%.class: %.java
	$(JAVAC) $(JFLAGS) $<

# targets

TARGETS = WEB-INF/classes/OAIHarvest.class \
          WEB-INF/classes/HarvesterControlPanel.class \
          WEB-INF/classes/Config.class \
          WEB-INF/classes/Database.class \
          WEB-INF/classes/Repository.class \
          WEB-INF/classes/HTMLWriter.class \
          WEB-INF/classes/OAIRecord.class \
          WEB-INF/classes/OAIRequest.class \
          WEB-INF/classes/OAIResponseHandler.class \
          WEB-INF/classes/Log.class \
          WEB-INF/classes/MetadataFormat.class \
          
all: $(TARGETS)

clean:
	for comp in $(TARGETS); do \
	   rm $$comp; \
	done
	@echo Finis.
	
install:
	install -o $(TOMCAT) -v -d $(WEBAPPS)/$(APPNAME)/style
	install -o $(TOMCAT) -v -m 644 style/style.css $(WEBAPPS)/$(APPNAME)/style/

	install -o $(TOMCAT) -v -d $(WEBAPPS)/$(APPNAME)/WEB-INF/db
	install -o $(TOMCAT) -v -m 644 WEB-INF/web.xml $(WEBAPPS)/$(APPNAME)/WEB-INF/
	install -o $(TOMCAT) -v -m 644 WEB-INF/db/create_db.sql $(WEBAPPS)/$(APPNAME)/WEB-INF/db/
	install -o $(TOMCAT) -v -m 644 WEB-INF/db/clean_db.sql $(WEBAPPS)/$(APPNAME)/WEB-INF/db/

	install -o $(TOMCAT) -v -d $(WEBAPPS)/$(APPNAME)/WEB-INF/lib
	install -o $(TOMCAT) -v -m 644 WEB-INF/lib/mysql-connector-java.jar $(WEBAPPS)/$(APPNAME)/WEB-INF/lib/

	install -o $(TOMCAT) -v -d $(WEBAPPS)/$(APPNAME)/WEB-INF/classes
	for comp in $(TARGETS); do \
	   install -o $(TOMCAT) -v -m 644 $$comp $(WEBAPPS)/$(APPNAME)/WEB-INF/classes/; \
	done
	@echo Finis.

