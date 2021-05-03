//import ...
import gradingtools.Gradebook;
import gradingtools.gbCrypto;


import java.io.File;

/**
 * Initialize gradebook with specified name and generate a key.
 */
public class setup {

  /* test whether the file exists */
  private boolean file_test(String filename) {
    //TODO complete
    return true;
  }

  public static void main(String[] args) throws Exception {
    String key = "600FFAA01AABD4E30DE96CEE99A5A951";

    if (args.length != 2 || !(args[0].equals("-N")) || !(args[1].matches("^[A-Za-z0-9_\\.]*$"))) {
      System.out.println("Error with user input");
      System.exit(1);
    }
    // else arg.length is 2, first arguemnet is -N and second arguemnt is filename 
    
    // TODO: whtielist filename 
    String filename = args[1];    
    File f = new File(filename);
    if (f.exists()) {
    	System.out.println("Invalid");
        System.exit(1);
    }
    
    Gradebook gb = new Gradebook();
    gbCrypto.saveGradeBook(gb, filename);   
    System.out.println("Key is: " + key);

    return;
  }
}

