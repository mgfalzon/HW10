all: setup gradebookadd gradebookdisplay

JFLAGS=-g 
JC = javac

#Use to incorporate libraries at run-time
CPFLAGS = -cp .:./json-simple-1.1.1.jar

gradebookadd:
	$(JC) $(JFLAGS) $(CPFLAGS) gradebookadd.java 
	echo "#!/bin/bash\n java $(CPFLAGS) gradebookadd \$$@" > gradebookadd
	chmod +x gradebookadd

gradebookdisplay:
	$(JC) $(JFLAGS) $(CPFLAGS) gradebookdisplay.java 
	echo "#!/bin/bash\n java $(CPFLAGS) gradebookdisplay \$$@" > gradebookdisplay
	chmod +x gradebookdisplay

setup:
	$(JC) $(JFLAGS) $(CPFLAGS) setup.java 
	echo "#!/bin/bash\n java $(CPFLAGS) setup \$$@" > setup
	chmod +x setup

clean:
	rm -f *.class
	rm -rf gradebookadd gradebookdisplay setup
