#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/stat.h>
/*#define DEBUG*/
/*#define DEBUG_INFORMATION*/


#if defined(DEBUG)
 #define DEBUG_PRINTF printf("    DEBUG: ");printf
#else
 #define DEBUG_PRINTF empty_printf /* Will not print anything. */
#endif

#if defined(DEBUG_INFORMATION)
 #define DEBUGI_PRINTF printf("    DEBUG INFORMATION: ");printf
#else
 #define DEBUGI_PRINTF empty_printf /* Will not print anything. */
#endif

void empty_printf(const char *fmt, ...) { /* Prints nothing */ }


typedef struct _Gradebook {
  int number_of_records;
  float weights;
  char *key;
  struct _Assignment *assignment;
  struct _Student *student;
  struct _Record *record;
} Gradebook;

typedef struct _Assignment {
  char *name;
  float max_points;
  float weight;
  struct _Assignment *next;
} Assignment;

typedef struct _Student {
  char *first_name;
  char *last_name;
  struct _Student *next;
} Student;

typedef struct _Record {
  float grade;
  int order;
  char *assignment;
  char *first_name;
  char *last_name;
  struct _Record *next;
} Record;

/* test whether the file exists */
int file_exists(char* filename) {
  struct stat buffer;
  return (stat(filename, &buffer) == 0);
}

Gradebook *instantiate_gradebook() {
  Gradebook *gradebook = malloc(sizeof(Gradebook));
  gradebook->weights = 0;
  gradebook->key = NULL;
  gradebook->assignment = NULL;
  gradebook->student = NULL;
  gradebook->record = NULL;
  gradebook->number_of_records = 0;
  return gradebook;
}

Assignment *free_assignment(Assignment *a) {
  Assignment *next = NULL;
  if (a) {
    next = a->next;
    if (a->name) {
      DEBUGI_PRINTF("Freeing an assignment \"%s\".\n", a->name);
      free(a->name);
    }
    free(a);
  }
  return next;
}

Student *free_student(Student *s) {
  Student *next = NULL;
  if (s) {
    next = s->next;
    if (s->first_name && s->last_name) {
      DEBUGI_PRINTF("Freeing a student first_name \"%s\" last_name \"%s\".\n", s->first_name, s->last_name);
      free(s->first_name);
      free(s->last_name);
    }
    free(s);
  }
  return next;
}

Record *free_record(Record *r) {
  Record *next = NULL;
  if (r) {
    next = r->next;
    if (r->first_name && r->last_name && r->assignment) {
      DEBUGI_PRINTF("Freeing a record for first_name \"%s\" last_name \"%s\" assignment \"%s\".\n"
          , r->first_name, r->last_name, r->assignment);
      if (r->first_name) {free(r->first_name);}
      if (r->last_name) {free(r->last_name);}
      if (r->assignment) {free(r->assignment);}
    }
    free(r);
  }
  return next;
}

int pop_assignment(Gradebook *gradebook, char *buff) {
  Assignment *a;
  char line[1001];
  DEBUGI_PRINTF("Popping assignment\n");
  if (gradebook->assignment != NULL) {
    sprintf(line, "Assignment: %s %f %f \n", gradebook->assignment->name, gradebook->assignment->weight, gradebook->assignment->max_points);
    DEBUGI_PRINTF("Successfully condensed assignment as %s", line);
    a = gradebook->assignment;
    gradebook->assignment = gradebook->assignment->next;
    DEBUGI_PRINTF("Is there another assignment? %p\n", (void *)(gradebook->assignment));
    free_assignment(a);
    if (buff) {strcpy(buff, line);}
    return 1;
  }
  if (buff) {strcpy(buff, "");}
  return 0;
}

int pop_student(Gradebook *gradebook, char *buff) {
  Student *s;
  char line[1001];
  DEBUGI_PRINTF("Popping student\n");
  if (gradebook->student != NULL) {
    sprintf(line, "Student: %s %s \n", gradebook->student->first_name, gradebook->student->last_name);
    DEBUGI_PRINTF("Successfully condensed student as %s", line);
    s = gradebook->student;
    gradebook->student = gradebook->student->next;
    DEBUGI_PRINTF("Is there another student? %p\n", (void *)(gradebook->student));
    free_student(s);
    if (buff) {strcpy(buff, line);}
    return 1;
  }
  if (buff) {strcpy(buff, "");}
  return 0;
}

int pop_record(Gradebook *gradebook, char *buff) {
  Record *r;
  char line[1001];
  DEBUGI_PRINTF("Popping record\n");
  if (gradebook->record != NULL) {
    sprintf(line, "Record: %s %s %s %f %d \n"
       , gradebook->record->first_name
       , gradebook->record->last_name
       , gradebook->record->assignment
       , gradebook->record->grade
       , gradebook->record->order);
    DEBUGI_PRINTF("Successfully condensed record as %s", line);
    r = gradebook->record;
    gradebook->record = gradebook->record->next;
    DEBUGI_PRINTF("Is there another record? %p\n", (void *)(gradebook->record));
    free_record(r);
    if (buff) {strcpy(buff, line);}
    return 1;
  }
  if (buff) {strcpy(buff, "");}
  return 0;
}

Assignment *instantiate_assignment() {
  Assignment *assignment = malloc(sizeof(Assignment));
  assignment->weight = -1.0f;
  assignment->max_points = -1.0f;
  assignment->name = NULL;
  assignment->next = NULL;
  return assignment;
}

Student *instantiate_student() {
  Student *student = malloc(sizeof(Student));
  student->next = NULL;
  student->first_name = NULL;
  student-> last_name = NULL;
  return student;
}

Record *instantiate_record() {
  Record *record = malloc(sizeof(Record));
  record->grade = -1.0f;
  record->assignment = NULL;
  record->first_name = NULL;
  record->last_name = NULL;
  record->next = NULL;
  record->order = 0;
  return record;
}


Assignment *read_assignment(char *buff) {
  Assignment *assignment = instantiate_assignment();
  assignment->name = malloc(1000 * sizeof(char));
  strcpy(assignment->name, "");
  if (sscanf(buff, "Assignment: %s %f %f "
        , assignment->name
        , &(assignment->weight)
        , &(assignment->max_points)) < 3) {
    /*DEBUG_PRINTF("Not an assignment.  %s\n", buff);*/
    return free_assignment(assignment); /* NULL */
  }
  return assignment;
}

Student *read_student(char *buff) {
  Student *student = instantiate_student();
  student->first_name = malloc(1000 * sizeof(char));
  strcpy(student->first_name, "");
  student->last_name = malloc(1000 * sizeof(char));
  strcpy(student->last_name, "");
  if (sscanf(buff, "Student: %s %s ", student->first_name, student->last_name) < 2) {
    /*DEBUG_PRINTF("Not a student.  %s\n", buff);*/
    return free_student(student);
  }
  return student;
}

Record *read_record(char *buff) {
  Record *record = instantiate_record();
  record->first_name = malloc(1000 * sizeof(char));
  strcpy(record->first_name, "");
  record->last_name = malloc(1000 * sizeof(char));
  strcpy(record->last_name, "");
  record->assignment = malloc(1000 * sizeof(char));
  strcpy(record->assignment, "");
  if (sscanf(buff, "Record: %s %s %s %f %d "
       , record->first_name
       , record->last_name
       , record->assignment
       , &(record->grade)
       , &(record->order)) < 5) {
    /*DEBUG_PRINTF("Not a record.  %s\n", buff);*/
    return free_record(record);
  }
  return record;
}


Assignment *find_assignment(Assignment *assignment, char *name) {
  Assignment *trv;
  if (!(name)) {
    DEBUG_PRINTF("NULL name in find_assignment.\n");
    return 0;
  }
  for (trv = assignment; trv != NULL; trv = trv->next) {
    DEBUGI_PRINTF("Comparing received assignment \"%s\" with found \"%s\"\n", name, trv->name);
    if (trv->name && strcmp(trv->name, name) == 0) {
      return trv;
    }
  }
  return 0;
}

Record *get_record(Record *record, char *first_name, char *last_name, char *assignment) {
  Record *trv;
  if (!(record && first_name && last_name && assignment)) {
    DEBUG_PRINTF("Cannot search for a record with a NULL field.\n");
    return NULL;
  }
  for (trv = record; trv != NULL; trv = trv->next) {
    DEBUGI_PRINTF("Comparing record.\n");
    if (trv->first_name == NULL || trv->last_name == NULL || trv->assignment == NULL) {
      DEBUG_PRINTF("NULL field found in one of the records.\n");
      return NULL;
    }
    if (   (strcmp(trv->first_name, first_name) == 0)
        && (strcmp(trv->last_name, last_name) == 0)
        && (strcmp(trv->assignment, assignment) == 0)) {
      return trv;
    }
  }
  return NULL;
}

int find_student(Student *student, char *first_name, char *last_name) {
  Student *trv;
  if (!(first_name && last_name)) {
    DEBUG_PRINTF("NULL name in find_student.\n");
    return 0;
  }
  for (trv = student; trv != NULL; trv = trv->next) {
    DEBUGI_PRINTF("Comparing received student \"%s %s\" with found \"%s %s\"\n"
        , first_name, last_name, trv->first_name, trv->last_name);
    if (trv->first_name && trv->last_name 
         && strcmp(trv->first_name, first_name) == 0 && strcmp(trv->last_name, last_name) == 0) {
      return 1;
    }
  }
  return 0;
}
Record *purge_records(Assignment *assignment, Student *student, Record *r);
Record *purge_records(Assignment *assignment, Student *student, Record *r) {
  if ((assignment == NULL) || (student == NULL) || (r == NULL)) {
    return r;
  }
  if ((find_student(student, r->first_name, r->last_name) == 0)
          || (find_assignment(assignment, r->assignment) == NULL)) {
    /* Purge this record */
    /* Delete r, so just return r->next */
    r->next = purge_records(assignment, student, r->next);
    /*free_record(r);*/
    DEBUGI_PRINTF("Purging this record.\n");
    return r->next;
  }
  /* Keep r, so return r */
  DEBUGI_PRINTF("Not purging this record.\n");
  r->next = (purge_records(assignment, student, r->next));
  return r;

}

int append(Gradebook *gradebook, Assignment *assignment, Student *student, Record *record) {
  if (assignment) {
    assignment->next = gradebook->assignment;
    gradebook->assignment = assignment;
  }
  if (student) {
    student->next = gradebook->student;
    gradebook->student = student;
  }
  if (record) {
    record->next = gradebook->record;
    gradebook->record = record;
  }
  return (assignment || student || record);
}

void special_encrypt(char *c) {
  int i = 0;
  for (i = 0; i<(strlen(c) - 1); i++) {
    c[i] = c[i] + 1;
  }
}

void special_decrypt(char *c) {
  int i = 0;
  for (i = 0; i<(strlen(c) - 1); i++) {
    c[i] = c[i] - 1;
  }
}

Gradebook *read_gradebook(char *filename) {
  FILE *fp;
  Gradebook *gradebook;
  char *buff;
  char key[200] = "";

  fp = fopen(filename, "r");
  if (fp == NULL) {
    DEBUG_PRINTF("Error opening file \"%s\"\n", filename);
    printf("invalid\n");
    return NULL;
  }

/* Decrypt */
  

  buff = malloc(1000 * sizeof(char));
  strcpy(buff, "");
  gradebook = instantiate_gradebook();
  fgets(buff, 200, fp);
  special_decrypt(buff);
  sscanf(buff, "Key: %s %d ", key, &(gradebook->number_of_records));
  gradebook->key = malloc((strlen(key) + 1) * sizeof(char));
  strcpy(gradebook -> key, key);
  while(!feof(fp)) {
    strcpy(buff, "");
    fgets(buff, 1000, fp);
    special_decrypt(buff);
    if (strstr(buff, "\n")) {
      DEBUGI_PRINTF("Reading line from file %s", buff);
    } else {
      DEBUGI_PRINTF("Reading line from file %s\n", buff);
    }
    if ( (append(gradebook
           , read_assignment(buff)
           , read_student(buff)
           , read_record(buff)) == 0)
        && (strcmp(buff, "\n") != 0)
        && (strcmp(buff, "") != 0)
        && (strcmp(buff, " \n") != 0) ) {
      DEBUG_PRINTF("Improperly formatted line.  \"%s\"\n", buff);
    }
  }
  fclose(fp);
  return gradebook;
}

int write_gradebook(char *filename, Gradebook *gradebook) {
  FILE *fp = fopen(filename, "w");
  char line[1001];


  fp = fopen(filename, "w");
  if (fp == NULL) {
    DEBUG_PRINTF("Error opening file \"%s\"\n", filename);
    printf("invalid\n");
    return(255);
  }
  if (file_exists(filename)) {
    DEBUGI_PRINTF("created file named %s\n", filename);
  }

  sprintf(line, "Key: %s %d \n", gradebook->key, gradebook->number_of_records);
  special_encrypt(line);
  fputs(line, fp);
  while (pop_assignment(gradebook, line)) {
    DEBUGI_PRINTF("Adding assignment as %s", line);
    special_encrypt(line);
    fputs(line, fp);
  }
  while (pop_student(gradebook, line)) {
    DEBUGI_PRINTF("Adding student as %s", line);
    special_encrypt(line);
    fputs(line, fp);
  }
  while (pop_record(gradebook, line)) {
    DEBUGI_PRINTF("Adding record as %s", line);
    special_encrypt(line);
    fputs(line, fp);
  }

  
  fclose(fp);
  return 0;
}



int check_name_and_key(int argc, char** argv, Gradebook **g) {
  Gradebook *gradebook = NULL;
  if (argc < 3 || strcmp(argv[1], "-N") != 0) {
    DEBUG_PRINTF("-N <name> is required.  Received %s.\n", argv[1]);
    return 1;
  }
  gradebook = read_gradebook(argv[2]);
  if (argc < 5 || (strcmp(argv[3], "-K") != 0) ) {
    DEBUG_PRINTF("Need to specify a key with -K <key>.\n");
    return 1;
  }
  if ( gradebook == NULL || gradebook->key == NULL) {
    DEBUG_PRINTF("Could not read the key inside gradebook file.\"%s\"\n", argv[2]);
    return 255;
  }  
  if (strcmp(argv[4], gradebook->key) != 0) {
      DEBUG_PRINTF("Wrong key was provided.  Recieved \"%s\" but expected \"%s\".\n", argv[4], gradebook->key);
      return 1;
  }
  /* valid key and name */
  if(g) { *g = gradebook; }
  return 0;
}

void generate_key(char **key) {
  char new_key[1000] = "KeyKeyKeyKey";
  sprintf(new_key, "%d%d%d%d%d%d", rand(),rand(),rand(),rand(),rand(),rand());
  *key = malloc((strlen(new_key) + 1) * sizeof(char));
  strcpy(*key, new_key);
}




