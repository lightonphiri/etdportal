# Makefile for etdportal project
# hussein suleman
# 7 october 2010

# variables

SERVLETAPI = /usr/share/$(TOMCAT)/lib

JFLAGS = -classpath WEB-INF/lib/mysql-connector-java.jar:$(SERVLETAPI)/servlet-api.jar:. \
         -d WEB-INF/classes \
         -sourcepath WEB-INF/classes

JAVAC = javac

WEBAPPS = /var/lib/$(TOMCAT)/webapps
APPNAME = RSS

# rules

%.class: %.java
	$(JAVAC) $(JFLAGS) $<

# targets

TARGETS = WEB-INF/classes/RSS.class
          
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

	install -o $(TOMCAT) -v -d $(WEBAPPS)/$(APPNAME)/WEB-INF/lib
	install -o $(TOMCAT) -v -m 644 WEB-INF/lib/mysql-connector-java.jar $(WEBAPPS)/$(APPNAME)/WEB-INF/lib/

	install -o $(TOMCAT) -v -d $(WEBAPPS)/$(APPNAME)/WEB-INF/classes
	for comp in $(TARGETS); do \
	   install -o $(TOMCAT) -v -m 644 $$comp $(WEBAPPS)/$(APPNAME)/WEB-INF/classes/; \
	done
	@echo Finis.

