# Makefile for etdportal project
# hussein suleman
# 20 august 2010

# variables

# rules

.java.class:
	$(JAVA) $(JFLAGS) -o $*.class -c $<

# targets

COMPONENTS = harvester OAI-PMH
	
all:
	for comp in $(COMPONENTS); do \
	   $(MAKE) -C $$comp; \
	done
	@Echo Finis.
	
clean:
	for comp in $(COMPONENTS); do \
	   $(MAKE) -C $$comp; clean \
	done
	@Echo Finis.
	
install:
	for comp in $(COMPONENTS); do \
	   $(MAKE) -C $$comp; install \
	done
	@Echo Finis.

