# Makefile for etdportal project
# hussein suleman
# 20 august 2010

# variables

TOMCAT = tomcat7
export TOMCAT

# rules

.java.class:
	$(JAVA) $(JFLAGS) -o $*.class -c $<

# targets

COMPONENTS = harvester RSS OAI-PMH summary portal
	
all:
	for comp in $(COMPONENTS); do \
	   $(MAKE) -C $$comp; \
	done
	@echo Finis.
	
clean:
	for comp in $(COMPONENTS); do \
	   $(MAKE) -C $$comp clean; \
	done
	@echo Finis.
	
install:
	for comp in $(COMPONENTS); do \
	   $(MAKE) -C $$comp install; \
	done

	install -o $(TOMCAT) -v -m 755 installation/Harvest.sh /var/lib/$(TOMCAT)/webapps/harvester/
	install -o $(TOMCAT) -v -m 644 installation/etc_apache2_sites-available_etd /etc/apache2/sites-available/etd
	install -o $(TOMCAT) -v -m 644 installation/etc_tomcat5.5_policy.d_04webapps.policy /etc/$(TOMCAT)/policy.d/04webapps.policy
       
	install -o $(TOMCAT) -v -m 755 -d /etc/etdportal
	install -o $(TOMCAT) -v -m 755 -d /var/log/etdportal
	install -o $(TOMCAT) -v -m 644 installation/etc_etdportal_config.xml.orig /etc/etdportal/config.xml.orig
	
	@echo Finished.
