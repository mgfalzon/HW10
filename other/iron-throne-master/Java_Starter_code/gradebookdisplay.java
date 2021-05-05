//import ...

/**
 * Prints out a gradebook in a few ways
 * Some skeleton functions are included
 */
public class gradebookdisplay {
  static boolean verbose = false;

  private void print_Gradebook(...) {

    for(int i = 0; i < num_assigment; i++) {
      dump_assignment();
      System.out.println("----------------\n");
    }

    return;
  }

  private void print_Assignment(...) {

    return;
  }

  private void print_Student(...) {

    return;
  }

  private void print_Final(...){

    return;
  }

  public static void main(String[] args) {
    int   opt,len;
    char  *logpath = NULL;

    //TODO Code this
    if(args.length==1)
        System.out.println("\nNo Extra Command Line Argument Passed Other Than Program Name");
    if(args.length>=2)
    {
        System.out.println("\nNumber Of Arguments Passed: %d" + args.length);
        System.out.println("----Following Are The Command Line Arguments Passed----");
        for(int counter = 0; counter < args.length; counter++)
          System.out.println("args[" + counter + "]: " + args[counter]);
          // Decide what is the setting we are in
    }

  }
}
