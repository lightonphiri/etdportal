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
APPNAME = OAI-PMH

# rules

%.class: %.java
	$(JAVAC) $(JFLAGS) $<

# targets

TARGETS = WEB-INF/classes/CheckParams.class \
          WEB-INF/classes/Config.class \
          WEB-INF/classes/DatabaseConnection.class \
          WEB-INF/classes/GetRecord.class \
          WEB-INF/classes/Identify.class \
          WEB-INF/classes/ListIdentifiers.class \
          WEB-INF/classes/ListMetadataFormats.class \
          WEB-INF/classes/ListRecords.class \
          WEB-INF/classes/ListSets.class \
          WEB-INF/classes/MetadataFormat.class \
          WEB-INF/classes/OAI_servlet.class \
          WEB-INF/classes/Response.class \
          WEB-INF/classes/ResponseFormatter.class \
          WEB-INF/classes/StorageFormat.class \
          WEB-INF/classes/Log.class \

all: $(TARGETS)

clean:
	for comp in $(TARGETS); do \
	   rm $$comp; \
	done
	@echo Finis.
	
install:
	install -o $(TOMCAT) -v -d $(WEBAPPS)/$(APPNAME)/WEB-INF
	install -o $(TOMCAT) -v -m 644 WEB-INF/web.xml $(WEBAPPS)/$(APPNAME)/WEB-INF/
#	install -o $(TOMCAT) -v -m 644 WEB-INF/config/config.xml $(WEBAPPS)/$(APPNAME)/WEB-INF/config/
#	install -o $(TOMCAT) -v -m 644 WEB-INF/config/metadataFormats.xml $(WEBAPPS)/$(APPNAME)/WEB-INF/config/

	install -o $(TOMCAT) -v -d $(WEBAPPS)/$(APPNAME)/WEB-INF/lib
	install -o $(TOMCAT) -v -m 644 WEB-INF/lib/mysql-connector-java.jar $(WEBAPPS)/$(APPNAME)/WEB-INF/lib/

	install -o $(TOMCAT) -v -d $(WEBAPPS)/$(APPNAME)/WEB-INF/classes
	for comp in $(TARGETS); do \
	   install -o $(TOMCAT) -v -m 644 $$comp $(WEBAPPS)/$(APPNAME)/WEB-INF/classes/; \
	done
	@echo Finis.

