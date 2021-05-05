//import ...
import java.io.File;

/**
 * Initialize gradebook with specified name and generate a key.
 */
public class setup {

  /* test whether the file exists */
  private static boolean file_test(String filename) {
		File testFile = new File(filename); //Open it as a File object
		
		//Make sure it is actually a file and not a directory
		if (testFile.isFile()) {
			return true;
		}
		
		return false;
  }

  public static void main(String[] args) {
    //String key = "";

    if (args.length < 2) {
      System.out.println("Usage: setup -N <logfile pathname>");
      System.exit(1);
    }

    /* add your code here */
    
    //Make sure the file doesn't already exist - exit if so
    if (file_test(args[1])) {
    	System.out.println("invalid");
    	System.exit(255);
    }
    
    //Otherwise, create a new Gradebook and save it to a file
    try {
    	Gradebook gb = new Gradebook(args[1]);
    }
    catch (Exception e) {
    	System.out.println("invalid");
    	System.exit(255);
    }
    return;
  }
}
