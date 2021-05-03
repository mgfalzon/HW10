all: setup gradebookadd gradebookdisplay

JFLAGS=-g
JC = javac

#Use to incorporate libraries at run-time
CPFLAGS = # -cp .:./library.jar

gradebookadd:
	$(JC) -d . Gradebook.java Student.java GradeEntry.java gbCrypto.java
	$(JC) $(JFLAGS) gradebookadd.java 
	echo "#!/bin/bash\n java $(CPFLAGS) gradebookadd \$$@" > gradebookadd
	chmod +x gradebookadd

gradebookdisplay:
	$(JC) -d . Gradebook.java Student.java GradeEntry.java gbCrypto.java
	$(JC) $(JFLAGS) gradebookdisplay.java 
	echo "#!/bin/bash\n java $(CPFLAGS) gradebookdisplay \$$@" > gradebookdisplay
	chmod +x gradebookdisplay

setup:
	$(JC) -d . Gradebook.java Student.java GradeEntry.java gbCrypto.java
	$(JC) $(JFLAGS) setup.java 
	echo "#!/bin/bash\n java $(CPFLAGS) setup \$$@" > setup
	chmod +x setup

clean:
	rm -rf gradingtools
	rm -f *.class
	rm -rf gradebookadd gradebookdisplay setup
