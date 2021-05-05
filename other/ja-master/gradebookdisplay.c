#include "data.c"

typedef enum _SORTING {G, A, N, ORDER} SORTING;

Record *shallow_copy_single_record(Record *r) {
  Record *new_record = NULL;
  if (r == NULL) {return NULL;}
  new_record = malloc(sizeof(Record));
  *new_record = *r;
  new_record -> next = NULL;
  return new_record;
}

void record_shallow_delete(Record *r);
void record_shallow_delete(Record *r) {
  if (r) { 
    record_shallow_delete(r->next);
    free(r);
  }
}

Record *insert_sorted_record(Record *R, Record *new_record, SORTING sorting) {
  int sort_condition = 0;
  if (R == NULL) { return new_record; }
  if (sorting == G) {
    DEBUGI_PRINTF("Sorting by grade.\n");
    sort_condition = R->grade < new_record->grade;
  } else if (sorting == ORDER) {
    DEBUGI_PRINTF("Sorting by order in which the record was added.\n");
    sort_condition = R->order > new_record->order;
  } else if (sorting == A) {
    DEBUGI_PRINTF("Sorting by assignment.\n");
    sort_condition = (strcmp(R->assignment, new_record->assignment) > 0);
  } else {
    DEBUGI_PRINTF("Sorting by name.\n");
    sort_condition = 
        (((strcmp(R->last_name, new_record->last_name) == 0) ? 
          strcmp(R->first_name, new_record->first_name):
           strcmp(R->last_name, new_record->last_name)) > 0);
  }

  if (sort_condition) {
      new_record->next = R; return new_record;
    } else {
      R->next = insert_sorted_record(R->next, new_record, sorting);
    }

  return R;
}

Record *sorted_record_copy(Record *records, SORTING sorting) {
  Record *trv = records;
  Record *R = NULL;
  Record *new_record = NULL;
  /*if (records == NULL) {return NULL;}*/
  for (trv = records; trv != NULL; trv = trv->next) {
    new_record = shallow_copy_single_record(trv);
    R = insert_sorted_record(R, new_record, sorting);
    DEBUGI_PRINTF("Inserting record into sorted list\n");
  }
  return R;
}

int print_assignment(Gradebook *gradebook, char *assignment_name, int is_Alphabetical) {
  Record *trv = NULL;
  Record *sorted = NULL;
  int i = 0;
  DEBUG_PRINTF("Print_assignment for assignment \"%s\"\n", assignment_name);
  sorted = sorted_record_copy(gradebook->record, is_Alphabetical ? N : G);
  if (!find_assignment(gradebook->assignment, assignment_name)) { return 1; }
  for (trv = sorted; trv != NULL; trv = trv->next) {
    DEBUGI_PRINTF("Comparing record %d to print.\n", i);i++;
    if ( (trv->first_name) && (trv->last_name) && (trv->assignment)/*
        && (strcmp(trv->first_name, first_name) == 0)
        && (strcmp(trv->last_name, last_name) == 0)*/
        && (strcmp(trv->assignment, assignment_name) == 0)) {
        printf("(%s, %s, %d)\n", trv->last_name, trv->first_name, (int)(trv->grade));
      /* "(Tyler, Russell, 190)\n(Smith, John, 180)\n(Mason, Ted, 150)\n" */
    }
  }
  record_shallow_delete(sorted);
  return 0;
}

int print_student(Gradebook *gradebook, char *first_name, char *last_name) {
  Record *trv = NULL;
  Record *sorted = NULL;
  int i = 0;
  DEBUG_PRINTF("Print_student for student \"%s %s\"\n", first_name, last_name);
  sorted = sorted_record_copy(gradebook->record, ORDER);
  if (!find_student(gradebook->student, first_name, last_name)) { return 1; }
  for (trv = sorted; trv != NULL; trv = trv->next) {
    DEBUGI_PRINTF("Comparing record %d to print.\n", i);i++;
    if ( (trv->first_name) && (trv->last_name) && (trv->assignment)
        && (strcmp(trv->first_name, first_name) == 0)
        && (strcmp(trv->last_name, last_name) == 0)
        /*&& (strcmp(trv->assignment, assignment_name) == 0)*/) {
        printf("(%s, %d)\n", trv->assignment, (int)(trv->grade));
      /*'(Midterm, 95)\n(Final, 180)\n(Project, 48)\n'*/
    }
  }
  record_shallow_delete(sorted);
  return 0;
}

int print_final(Gradebook *gradebook, int is_Alphabetical) {
  Record *trv = NULL;
  Student *stu = NULL;
  Record *sorted = NULL;
  Record *unsorted = NULL;
  Record *new_tmp_record = NULL;
  Assignment *assn = NULL;
  int i = 0;
  int num_grades = 0;
  float total_grade = 0;
  DEBUG_PRINTF("print_final\n");
  for (stu = gradebook->student; stu != NULL; stu = stu->next) {
    num_grades = 0;
    total_grade = 0.0f;
    i = 0;
    for (trv = gradebook->record; trv != NULL; trv = trv->next) {
      DEBUGI_PRINTF("Looking at %dth record for %s %s.\n", i, stu->first_name, stu->last_name);
      i++;
      if ( (trv->first_name) && (trv->last_name) && (trv->assignment) 
          && (stu->first_name) && (stu->last_name)
          && (strcmp(trv->first_name, stu->first_name) == 0)
          && (strcmp(trv->last_name, stu->last_name) == 0)
          /*&& (strcmp(trv->assignment, assignment_name) == 0)*/) {
          num_grades++;
          assn = find_assignment(gradebook->assignment, trv->assignment);
          if (assn != NULL && (assn->max_points != 0)) {
            total_grade += (1.0f * (trv->grade / assn->max_points) * assn->weight);
DEBUGI_PRINTF("print_final grade is %f %f %f\n", trv->grade, assn->max_points, assn->weight);
          }
      }
    }
    new_tmp_record = instantiate_record();
    new_tmp_record->last_name = stu->last_name;
    new_tmp_record->first_name = stu->first_name;
    new_tmp_record->grade = total_grade;
DEBUGI_PRINTF("        grade is %f\n", new_tmp_record->grade);
    new_tmp_record->next = unsorted;
    unsorted = new_tmp_record;
  }

  sorted = sorted_record_copy(unsorted, (is_Alphabetical ? N : G));
  
  for (trv = sorted; trv != NULL; trv = trv->next) {
    /* '(Smith, John, 0.9275)\n(Tyler, Russell, 0.92)\n(Mason, Ted, 0.85)\n' */
    printf("(%s, %s, %f)\n", trv->last_name, trv->first_name
          , trv->grade);
  }
  record_shallow_delete(sorted);
  return 0;
}


int parse_print_assignment(int argc, char** argv, Gradebook *gradebook) {
  int counter = 0;
  char *assignment_name = NULL;
  int is_Grade = 0;
  int is_Alphabetical = 0;
  if (strcmp(argv[5], "-PA") == 0) {
    /* ./gradebookdisplay -N mygradebook -K $key -PA -G -AN Final */
    if (argc < 9) {
      DEBUG_PRINTF("-PA requires more arguments.  Received %d arguments.\n", argc);
      return 1;
    }
    counter = 6;
    while((counter) < argc) {
      if ((strcmp(argv[counter], "-AN") == 0) && ((counter + 1) < argc)) {
        assignment_name = argv[counter + 1];
        counter += 1;
        DEBUGI_PRINTF("Assignment name \"%s\"\n", assignment_name);
      }
      if (strcmp(argv[counter], "-G") == 0) {
        is_Grade++;
      }
      if (strcmp(argv[counter], "-A") == 0) {
        is_Alphabetical++;
      }
      counter += 1;
    }
    if ((is_Alphabetical + is_Grade) != 1) {
      DEBUG_PRINTF("Exactly one of -A, -G should be specified.");
      return 1;
    }
    print_assignment(gradebook, assignment_name, is_Alphabetical);
  }
  return 0;
}

int parse_print_final(int argc, char** argv, Gradebook *gradebook) {
  int counter = 0;
  int is_Grade = 0;
  int is_Alphabetical = 0;
  if (strcmp(argv[5], "-PF") == 0) {
    /* gradebookdisplay -N mygradebook -K $key -PF -A */
    if (argc < 7) {
      DEBUG_PRINTF("Not enough arguments for -PF.  Received %d arguments.\n", argc);
      return 1;
    }
    counter = 6;
    while((counter) < argc) {
      if (strcmp(argv[counter], "-G") == 0) {
        is_Grade++;
      }
      if (strcmp(argv[counter], "-A") == 0) {
        is_Alphabetical++;
      }
      counter += 1;
    }
    if ((is_Alphabetical + is_Grade) != 1) {
      DEBUG_PRINTF("Exactly one of -A, -G should be specified.");
      return 1;
    }
    print_final(gradebook, is_Alphabetical);
  }
  return 0;
}

int parse_print_student(int argc, char** argv, Gradebook *gradebook) {
  int counter = 0;
  char *fn = NULL;
  char *ln = NULL;

  if (strcmp(argv[5], "-PS") == 0) {
    /* ./gradebookdisplay -N mygradebook -K $key -PS -FN John -LN Smith */
    if (argc < 10) {
      DEBUG_PRINTF("-PS option requires that you specify a first and last name.  Received %d arguments.\n", argc);
      return 1;
    }
    counter = 6;
    while((counter) < argc) {
      if ((strcmp(argv[counter], "-FN") == 0) && ((counter + 1) < argc)) {
        fn = argv[counter + 1];
        counter += 1;
        DEBUGI_PRINTF("Student first name \"%s\"\n", fn);
      }
      if ((strcmp(argv[counter], "-LN") == 0) && ((counter + 1) < argc)) {
        ln = argv[counter + 1];
        counter += 1;
        DEBUGI_PRINTF("Student last name \"%s\"\n", ln);
      }
      counter += 1;
    }
    print_student(gradebook, fn, ln);
  }
  return 0;
}

int main(int argc, char** argv) {
  Gradebook *gradebook = NULL;
  int parsing[] = {0,0,0};
  int i = 0;
  int number_of_commands = 0;

  if (check_name_and_key(argc, argv, &gradebook) != 0) {printf("invalid");return 255;}
  if (gradebook == NULL) {printf("invalid");return 255;}
  if (argc < 6) {
    DEBUG_PRINTF("Not enough arguments specified for gradebookdisplay.  Specify an action.\n");
    printf("invalid");return 255;
  }
  
  for (i = 0; i < argc; i ++) {
    if ((strcmp(argv[i], "-PA") == 0)
         || (strcmp(argv[i], "-PS") == 0)
         || (strcmp(argv[i], "-PF") == 0) ) {
      number_of_commands ++;
    }
  }
  if (number_of_commands > 1) {
      DEBUG_PRINTF("Conflicting command line arguments are given.\n");
      printf("invalid");return 255;
  }

  parsing[0] = parse_print_assignment(argc, argv, gradebook);
  parsing[1] = parse_print_final(argc, argv, gradebook);
  parsing[2] = parse_print_student(argc, argv, gradebook);
  
  for (i = 0; i < 3; i ++) {
    if ((parsing[i]) > 0) {
      DEBUG_PRINTF("One of the parsing functions returned an error:\n");
      DEBUG_PRINTF("    %d %d %d\n", parsing[0], parsing[1], parsing[2]);
      printf("invalid");return 255;
    }
  }

  DEBUG_PRINTF("Command was successful.\n");
  return 0;
}






