#include "data.c"








int delete_assignment(Gradebook *gradebook, char *name) {
  Assignment *trv = gradebook->assignment;
  if (gradebook == NULL || name == NULL || gradebook->assignment == NULL) {
    DEBUG_PRINTF("Nothing to delete.\n");
    return -1;
  }
  /* Case of the first emelemnt */
  if (strcmp(trv->name, name) == 0) {
      DEBUGI_PRINTF("Popping assignment in delete_assignment.\n");
      pop_assignment(gradebook, NULL);
  }
  /* Case of the nth element */
  while(trv->next) {
    if (strcmp(trv->next->name, name) == 0) {
      DEBUGI_PRINTF("Freeing assignment in delete_assignment.\n");
      trv->next = free_assignment(trv->next);
      if (trv->next == NULL) {break;}
    }
    trv = trv->next;
  }
  DEBUG_PRINTF("Successfully deleted assignment \"%s\"\n", name);
  return 0;
}

int delete_student(Gradebook *gradebook, char *first_name, char *last_name) {
  Student *trv = gradebook->student;
  if (gradebook == NULL || first_name == NULL || last_name == NULL || gradebook->student == NULL) {
    DEBUG_PRINTF("Nothing to delete.\n");
    return -1;
  }
  /* Case of the first emelemnt */
  if ((strcmp(trv->first_name, first_name) == 0) && (strcmp(trv->last_name, last_name) == 0)) {
    DEBUGI_PRINTF("Popping student in delete_student.\n");
    pop_student(gradebook, NULL);
  }
  /* Case of the nth element */
  while(trv->next) {
    if ((strcmp(trv->next->first_name, first_name) == 0) && (strcmp(trv->next->last_name, last_name) == 0)) {
      DEBUGI_PRINTF("Freeing student in delete_student.\n");
      trv->next = free_student(trv->next);
      if (trv->next == NULL) {break;}
    }
    trv = trv->next;
  }
  DEBUG_PRINTF("Successfully deleted student \"%s %s\"\n", first_name, last_name);
  return 0;
}

int parse_add_assignment(int argc, char** argv, Gradebook *gradebook) {
  int counter;
  if (strcmp(argv[5], "-AA") == 0) {
      DEBUGI_PRINTF("Adding an assignment.\n");
      append(gradebook, instantiate_assignment(), NULL, NULL);
      counter = 6;
      while (counter + 1 < argc) {
        if (strcmp(argv[counter], "-AN") == 0) {
          gradebook->assignment->name = malloc((sizeof(char)) * (1 + strlen(argv[counter + 1])));
          strcpy(gradebook->assignment->name, argv[counter + 1]);
          if (strstr(gradebook->assignment->name, " ") != NULL) {
            DEBUG_PRINTF("Assignment name \"%s\" cannot contain a space.\n", gradebook->assignment->name);
            return 1;
          }
          if (find_assignment(gradebook->assignment->next, gradebook->assignment->name) != 0) {
            DEBUG_PRINTF("Assignment name \"%s\" already exists in the gradebook.\n", gradebook->assignment->name);
            return 1;
          }
        }
        if (strcmp(argv[counter], "-P") == 0) {
          sscanf(argv[counter + 1], " %f ", &(gradebook->assignment->max_points));
        }
        if (strcmp(argv[counter], "-W") == 0) {
          sscanf(argv[counter + 1], " %f ", &(gradebook->assignment->weight));
        }
        counter += 2;
      }
      if ((strlen(gradebook->assignment->name) < 1) 
         || (gradebook->assignment->max_points < 0) 
             || (gradebook->assignment->weight < 0)) {
        DEBUG_PRINTF("Not a valid assignment.  Please specify -AN <string> -P <float> -W <float>.\n");
        return 1;
      }
      DEBUG_PRINTF("Successfully added assignment \"%s\"\n", gradebook->assignment->name);
      return -1;
  }
  return 0;
}

int parse_delete_assignment(int argc, char** argv, Gradebook *gradebook) {
  if (strcmp(argv[5], "-DA") == 0) {
    if (argc < 6) {
      DEBUG_PRINTF("-DA option requires that you specify -AN.\n");
      return 1;
    }
    if (argc < 8 || strcmp(argv[6], "-AN") != 0) {
      DEBUG_PRINTF("-DA option requires that you imediately specify -AN <name>.\n");
      return 1;
    }
    if (find_assignment(gradebook->assignment, argv[7]) == 0) {
      DEBUG_PRINTF("Cannot delete nonexisting assignment \"%s\".\n", argv[7]);
      return 1;
    }
    delete_assignment(gradebook, argv[7]);
    return -1;
  }
  return 0;
}

int parse_add_student(int argc, char** argv, Gradebook *gradebook) {
  int counter = 0;
  if (strcmp(argv[5], "-AS") == 0) {
    /* ./gradebook -N <name> -K <key> -AS -FN <name> -LN <name>*/
    if (argc < 10) {
      DEBUG_PRINTF("Not enough arguments.  Expected: ./gradebook -N <name> -K <key> -AS -FN <name> -LN <name>.  Received %d arguments.\n", argc);
      return 1;
    }
    DEBUGI_PRINTF("Adding a student.\n");
    append(gradebook, NULL, instantiate_student(), NULL);
    counter = 6;
    while((counter + 1) < argc) {
      if (strcmp(argv[counter], "-FN") == 0) {
        gradebook->student->first_name = malloc((sizeof(char)) * (1 + strlen(argv[counter + 1])));
        strcpy(gradebook->student->first_name, argv[counter + 1]);
        if (strstr(gradebook->student->first_name, " ") != NULL) {
          DEBUG_PRINTF("Student first_name \"%s\" cannot contain a space.\n", gradebook->student->first_name);
          return 1;
        }
      }
      if (strcmp(argv[counter], "-LN") == 0) {
        gradebook->student->last_name = malloc((sizeof(char)) * (1 + strlen(argv[counter + 1])));
        strcpy(gradebook->student->last_name, argv[counter + 1]);
        if (strstr(gradebook->student->last_name, " ") != NULL) {
          DEBUG_PRINTF("Student last_name \"%s\" cannot contain a space.\n", gradebook->student->last_name);
          return 1;
        }
      }
      counter += 2;
    }
    if (find_student(gradebook->student->next, gradebook->student->first_name, gradebook->student->last_name) != 0) {
      DEBUG_PRINTF("Student \"%s %s\" already exists in the gradebook.\n"
         , gradebook->student->first_name, gradebook->student->last_name);
      return 1;
    }
    if ((gradebook->student->first_name == NULL) 
         || (gradebook->student->last_name == NULL)) {
      DEBUG_PRINTF("Not a valid student.  Please specify a first and last name.\n");
      return 1;
    }
    DEBUG_PRINTF("Successfully added student \"%s %s\"\n", gradebook->student->first_name, gradebook->student->last_name);
    return -1;
  }
  return 0;
}

int parse_add_record(int argc, char** argv, Gradebook *gradebook) {
  int counter = 0;
  Record *existing_record = NULL;
  if (strcmp(argv[5], "-AG") == 0) {
    /* ./gradebook -N <name> -K <key> -AG -FN <name> -LN <name> -AN <name> -G <grade>*/
    if (argc < 14) {
      DEBUG_PRINTF("Not enough arguments to add a grade.  Received %d arguments.\n", argc);
      return 1;
    }
    DEBUGI_PRINTF("Parsing a grade.\n");
    append(gradebook, NULL, NULL, instantiate_record());
    counter = 6;
    while((counter + 1) < argc) {
      if (strcmp(argv[counter], "-FN") == 0) {
        gradebook->record->first_name = malloc((sizeof(char)) * (1 + strlen(argv[counter + 1])));
        strcpy(gradebook->record->first_name, argv[counter + 1]);
        if (strstr(gradebook->record->first_name, " ") != NULL) {
          DEBUG_PRINTF("first_name \"%s\" cannot contain a space.\n", gradebook->record->first_name);
          return 1;
        }
      }
      if (strcmp(argv[counter], "-AN") == 0) {
        gradebook->record->assignment = malloc((sizeof(char)) * (1 + strlen(argv[counter + 1])));
        strcpy(gradebook->record->assignment, argv[counter + 1]);
        if (strstr(gradebook->record->assignment, " ") != NULL) {
          DEBUG_PRINTF("assignment \"%s\" cannot contain a space.\n", gradebook->record->assignment);
          return 1;
        }
      }
      if (strcmp(argv[counter], "-LN") == 0) {
        gradebook->record->last_name = malloc((sizeof(char)) * (1 + strlen(argv[counter + 1])));
        strcpy(gradebook->record->last_name, argv[counter + 1]);
        if (strstr(gradebook->record->last_name, " ") != NULL) {
          DEBUG_PRINTF("last_name \"%s\" cannot contain a space.\n", gradebook->record->last_name);
          return 1;
        }
      }
      if (strcmp(argv[counter], "-G") == 0) {
        sscanf(argv[counter + 1], " %f ", &(gradebook->record->grade));
      }
      counter += 2;
    }
    if ((gradebook->record->grade == -1) || (gradebook->record->first_name == NULL) || (gradebook->record->last_name == NULL) || (gradebook->record->assignment == NULL)) {
          DEBUG_PRINTF("Cannot add a grade of %f for assignment \"%s\" for student \"%s %s\".  Expected -G <grade> -FN <name> -LN <name> -AN <name> \n", gradebook->record->grade, gradebook->record->assignment, gradebook->record->first_name, gradebook->record->last_name);
          return 1;
    }
    if ((find_student(gradebook->student
                    , gradebook->student->first_name
                    , gradebook->student->last_name) == 0)
        || (find_assignment(gradebook->assignment, gradebook->record->assignment) == 0)) {
      DEBUG_PRINTF("Student \"%s %s\" or assignment \"%s\" does not exist.\n"
         , gradebook->record->first_name, gradebook->record->last_name, gradebook->record->assignment);
      return 1;
    }
    if ((existing_record = get_record(gradebook->record->next, gradebook->record->first_name, gradebook->record->last_name, gradebook->record->assignment))) {
      /* Record already exists, so update the old record and delete the new record. */
       existing_record->grade = gradebook->record->grade;
       pop_record(gradebook, NULL);
       DEBUG_PRINTF("Successfully updated the grade.\n");
       return -1;
    }
    gradebook->record->order = gradebook->number_of_records;
    gradebook->number_of_records = gradebook->number_of_records + 1;
    DEBUG_PRINTF("Successfully added the grade.\n");
    return -1;
  }
  return 0;
}

int parse_delete_student(int argc, char** argv, Gradebook *gradebook) {
  char *fn = NULL;
  char *ln = NULL;
  if (strcmp(argv[5], "-DS") == 0) {
    if (argc < 10) {
      DEBUG_PRINTF("-DS option requires that you specify a first and last name.  Received %d arguments.\n", argc);
      return 1;
    }
    fn = ((strcmp(argv[6], "-FN") == 0) ? argv[7] : ((strcmp(argv[8], "-FN") == 0) ? argv[9] : NULL));
    ln = ((strcmp(argv[6], "-LN") == 0) ? argv[7] : ((strcmp(argv[8], "-LN") == 0) ? argv[9] : NULL));
    if (fn && ln) {
      if (find_student(gradebook->student, fn, ln) == 0) {
        DEBUG_PRINTF("Cannot delete nonexisting student \"%s %s\".\n", fn, ln);
        return 1;
      }
      delete_student(gradebook, fn, ln);
      return -1;
    }
    DEBUG_PRINTF("-DS option requires that you specify -FN <naame> -LN <name>.\n");
    return 1;
  }
  return 0;
}




int is_character_valid(char c) {
/*printf("%c",c);*/
  return ((c > 64 && c < 91) || (c > 96 && c < 123));

}

int is_name_valid(char *name, int n) {
  
  char *c = NULL; 
  int i = 0;
/*printf("%s",name);*/
  for (c = name; c[i] != '\0' && i < n; i++) {
    if (!is_character_valid(c[i])) {return 0;}
  }
  return 1;
}

int are_all_names_valid(Assignment *assignment, Student *student, Record *record) {
  DEBUG_PRINTF("Checking if all of the names are valid.\n");
  if (assignment) {
    return (is_name_valid(assignment->name, 1000) 
            && are_all_names_valid(assignment->next, student, record));
  }
  if (student) {
    return (is_name_valid(student->first_name, 1000) 
            && is_name_valid(student->last_name, 1000) 
            && are_all_names_valid(assignment, student->next, record));
  }
  if (record) {
    return (is_name_valid(record->first_name, 1000) 
            && is_name_valid(record->last_name, 1000) 
            && is_name_valid(record->assignment, 1000) 
            && are_all_names_valid(assignment, student, record->next));
  }
  return 1;
}

int main(int argc, char** argv) {
  Gradebook *gradebook;
  int parsing[] = {0,0,0,0,0};
  int i = 0;
  int number_of_commands = 0;
  
  if (check_name_and_key(argc, argv, &gradebook) != 0) {return 255;}

  if (argc < 6) {
    DEBUG_PRINTF("Not enough arguments specified for gradebookadd.  Specify an action.\n");
    printf("invalid");return 255;
  }
  
  for (i = 0; i < argc; i ++) {
    if ((strcmp(argv[i], "-AS") == 0)
         || (strcmp(argv[i], "-DS") == 0)
         || (strcmp(argv[i], "-AA") == 0)
         || (strcmp(argv[i], "-DA") == 0)
         || (strcmp(argv[i], "-AG") == 0) ) {
      number_of_commands ++;
    }
  }

  if (number_of_commands > 1) {
      DEBUG_PRINTF("Conflicting command line arguments are given, for example both -AA and -AS.\n");
      printf("invalid");return 255;
  }
  
  parsing[0] = parse_add_assignment(argc, argv, gradebook);
  parsing[1] = parse_delete_assignment(argc, argv, gradebook);
  parsing[2] = parse_add_student(argc, argv, gradebook);
  parsing[3] = parse_delete_student(argc, argv, gradebook);
  parsing[4] = parse_add_record(argc, argv, gradebook);
  for (i = 0; i < 5; i ++) {
    if ((parsing[i]) > 0) {
      DEBUG_PRINTF("One of the parsing functions returned an error:\n");
      DEBUG_PRINTF("    %d %d %d %d %d\n", parsing[0], parsing[1], parsing[2], parsing[3], parsing[4]);
      printf("invalid");return 255;
    }
  }
  gradebook->record = purge_records(gradebook->assignment, gradebook->student, gradebook->record);
  if (!are_all_names_valid(gradebook->assignment, gradebook->student, gradebook->record)) {
    DEBUG_PRINTF("Invalid character in a name.\n");
    printf("invalid"); return 255;
  }
  DEBUG_PRINTF("Command was successful.  Writing gradebook to disk.\n");
  return write_gradebook(argv[2], gradebook);
  /*
  if (strcmp(argv[5], "-AA") == 0 || strcmp(argv[5], "-DA") == 0 || strcmp(argv[5], "-AA") == 0) {
    DEBUGI_PRINTF("gradebookadd with assignment named %s\n", gradebook->assignment->name);
    
  }*/
  DEBUG_PRINTF("Not a valid syntax for gradebookadd.\n");
  return 255;
  
  
}










