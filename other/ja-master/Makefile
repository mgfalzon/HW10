all: gradebookadd gradebookdisplay setup

CC = gcc
CFLAGS = -g -ansi -pedantic-errors -Wall -Werror -Wshadow -fstack-protector-all
LFLAGS = -lssl -lcrypto 

gradebookadd: gradebookadd.c data.c
	$(CC) $(CFLAGS) -o gradebookadd gradebookadd.c $(LFLAGS)

gradebookdisplay: gradebookdisplay.c data.c
	$(CC) $(CFLAGS) -o gradebookdisplay gradebookdisplay.c $(LFLAGS)

setup: setup.c data.c
	$(CC) $(CFLAGS) -o setup setup.c $(LFLAGS)

clean:
	rm -f *.o
	rm -rf gradebookadd gradebookdisplay setup