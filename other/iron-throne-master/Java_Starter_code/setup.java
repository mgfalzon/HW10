//import ...
import java.io.File;
import java.io.IOException;
/**
 * Initialize gradebook with specified name and generate a key.
 */
public class setup {

  /* test whether the file exists */
  private boolean file_test(String filename) {
    File f = new File(filename);
    if(f.isFile()) {
      return true;
    }
    else {
      return false;
    }
  }

  public static void main(String[] args) {
    String key;

    if (args.length < 2) {
      System.out.println("Usage: setup <logfile pathname>");
      System.exit(1);
    }
  /* add your code here */
    if(args[0] != "-N") {
      return;
    }
    try {
      if(file_test(args[1])) {
        System.out.println("Invalid");
        System.exit(255);
      }
      else {
        Gradebook gradebook = new Gradebook(args[1]);
      }
    } catch (Exception e) {
        System.out.println("An error occurred.");
    }
    System.out.println("Key is: " + key);

    return;
  }
}
