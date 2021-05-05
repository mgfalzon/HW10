all: gradebookadd gradebookdisplay setup

JFLAGS=-g
JC = javac

gradebookadd:
	$(JC) $(JFLAGS) gradebookadd.java Gradebook.java

gradebookdisplay:
	$(JC) $(JFLAGS) gradebookdisplay.java Gradebook.java

setup:
	$(JC) $(JFLAGS) setup.java Gradebook.java


clean:
	rm -f *.class
	rm -f gradebookadd gradebookdisplay setup
