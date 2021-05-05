#include "data.c"







int main(int argc, char** argv) {
  Gradebook *gradebook = instantiate_gradebook();
  int err_number = 0;

  if (argc < 3 || !(strcmp(argv[1], "-N") == 0 || strcmp(argv[1], "N") == 0)) {
    printf("Usage: setup -N <logfile pathname>\n");
    return(255);
  }
  


  generate_key(&(gradebook->key));
  err_number = write_gradebook(argv[2], gradebook);
  printf("Key is: %s\n", gradebook->key);
  return err_number;
}
