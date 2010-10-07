# Makefile for etdportal project
# hussein suleman
# 7 october 2010

# variables

TOMCAT = tomcat6
SERVLETAPI = /usr/share/tomcat6/lib

JFLAGS = -classpath WEB-INF/lib/mysql-connector-java.jar:$(SERVLETAPI)/servlet-api.jar:WEB-INF/lib/lucene-analyzers.jar:WEB-INF/lib/lucene-core.jar:. \
         -d WEB-INF/classes \
         -sourcepath WEB-INF/classes

JAVAC = javac

WEBAPPS = /var/lib/$(TOMCAT)/webapps
APPNAME = ROOT

# rules

%.class: %.java
	$(JAVAC) $(JFLAGS) $<

# targets

TARGETS = WEB-INF/classes/ConfigurationManager.class \
          WEB-INF/classes/DatabaseBrowser.class \
          WEB-INF/classes/DatabaseUpdater.class \
          WEB-INF/classes/FileDocument.class \
          WEB-INF/classes/HarvestingMain.class \
          WEB-INF/classes/HarvestRequest.class \
          WEB-INF/classes/Index.class \
          WEB-INF/classes/IndexFiles.class \
          WEB-INF/classes/Record.class \
          WEB-INF/classes/ResponseParser.class \
          WEB-INF/classes/ResultFormat.class \
          WEB-INF/classes/ResumptionToken.class \
          WEB-INF/classes/SearchEngine.class
          
CONFIGFILES = viewsnippets.xsl config.xml dc_index.xsl dc_snippet.xsl \
          dc.xsl etdms_index.xsl etdms_snippet.xsl etdms.xsl \
          index.xsl install_db.sql marc_index.xsl marc_snippet.xsl \
          marc.xsl rfc1807_index.xsl rfc1807_snippet.xsl rfc1807.xsl \
          viewfull.xsl viewsnippets.xsl

all: $(TARGETS)

clean:
	for comp in $(TARGETS); do \
	   rm $$comp; \
	done
	@echo Finis.
	
install:
	install -o $(TOMCAT) -v -d $(WEBAPPS)/$(APPNAME)/META-INF
	install -o $(TOMCAT) -v -m 644 META-INF/context.xml $(WEBAPPS)/$(APPNAME)/META-INF/

	install -o $(TOMCAT) -v -d $(WEBAPPS)/$(APPNAME)/style
	install -o $(TOMCAT) -v -m 644 style/style.css $(WEBAPPS)/$(APPNAME)/style/

	install -o $(TOMCAT) -v -d $(WEBAPPS)/$(APPNAME)/images
	install -o $(TOMCAT) -v -m 644 images/page.png $(WEBAPPS)/$(APPNAME)/images/
	install -o $(TOMCAT) -v -m 644 images/header.png $(WEBAPPS)/$(APPNAME)/images/
	install -o $(TOMCAT) -v -m 644 images/footer.png $(WEBAPPS)/$(APPNAME)/images/

	install -o $(TOMCAT) -v -d $(WEBAPPS)/$(APPNAME)/WEB-INF/config
	install -o $(TOMCAT) -v -m 644 WEB-INF/web.xml $(WEBAPPS)/$(APPNAME)/WEB-INF/
	for comp in $(CONFIGFILES); do \
	   install -o $(TOMCAT) -v -m 644 WEB-INF/config/$$comp $(WEBAPPS)/$(APPNAME)/WEB-INF/config/; \
	done

	install -o $(TOMCAT) -v -d $(WEBAPPS)/$(APPNAME)/WEB-INF/lib
	install -o $(TOMCAT) -v -m 644 WEB-INF/lib/mysql-connector-java.jar $(WEBAPPS)/$(APPNAME)/WEB-INF/lib/
	install -o $(TOMCAT) -v -m 644 WEB-INF/lib/lucene-analyzers.jar $(WEBAPPS)/$(APPNAME)/WEB-INF/lib/
	install -o $(TOMCAT) -v -m 644 WEB-INF/lib/lucene-core.jar $(WEBAPPS)/$(APPNAME)/WEB-INF/lib/
	install -o $(TOMCAT) -v -m 644 WEB-INF/lib/xalan.jar $(WEBAPPS)/$(APPNAME)/WEB-INF/lib/

	install -o $(TOMCAT) -v -d $(WEBAPPS)/$(APPNAME)/WEB-INF/classes
	for comp in $(TARGETS); do \
	   install -o $(TOMCAT) -v -m 644 $$comp $(WEBAPPS)/$(APPNAME)/WEB-INF/classes/; \
	done
	@echo Finis.

