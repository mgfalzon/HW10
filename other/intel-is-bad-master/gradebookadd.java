//import ...
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.lang.IllegalArgumentException;
/**
 * Allows the user to add a new student or assignment to a gradebook,
 * or add a grade for an existing student and existing assignment
 */
public class gradebookadd {

  /* parses the cmdline to keep main method simplified */
  private String parse_cmdline(String[] args) {

    // Holds the data gathered from the cmdline
    String something = null;

    // Set to "aa", "da", "as", "ds", or "ag" with respect to the cdmline args.
    String action=null;

    // The rest of the options which are needed to fulfill desired action.
    String assignmentName=null;
    int assignmentPoints=-1;
    double assignmentWeight=-1;
    String studentFirstName=null;
    String studentLastName=null;
    int grade=-1;

    String filename=null;
    String key=null;

    // Keep track of which flags have been used so far to verify that none are missing
    Set<String> usedFlags = new HashSet<>();
    Set<String> requiredFlags = null;

    if(args.length==1)
      System.out.println("\nNo Extra Command Line Argument Passed Other Than Program Name");
    if(args.length>=2) {
      // Checks first arguments
      if(!args[0].equals("-N")){
        exit();
      } else if (!args[2].equals("-K")){
        exit();
      } else {
        filename = args[1];
        if (!filename.matches("^[a-zA-Z0-9_.]*$")) {
>>>>>>> 8e3cefa4edfbd219c53a774f64c596c4a2029a07
          exit();
        }
        key = args[3];
        if (!key.matches("[a-fA-F0-9]*$")) { //verify that the key is in hexadecimal
          exit();
        }

        // Handles Add Assignment
        if(args[4].equals("-AA")){
          action = "aa";
          requiredFlags = new HashSet<>(Arrays.asList("-AN","-P","-W"));
          if (args.length != 11)
            exit(); // invalid number of arguments
          for(int counter=5; counter < args.length; counter++){
            switch (args[counter]) {
              case "-AN":
                usedFlags.add("-AN");
                assignmentName = args[counter+1];
                if (!isAlphanumeric(assignmentName))
                  exit();
                break;
              case "-P":
                usedFlags.add("-P");
                try { 
                  assignmentPoints = Integer.parseInt(args[counter+1]);
                  if (assignmentPoints < 0)
                    exit();
                } catch (NumberFormatException e) {
                    exit();
                }
                break;
              case "-W":
                usedFlags.add("-W");
                try {
                  assignmentWeight = Double.parseDouble(args[counter+1]);
                  if (assignmentWeight < 0 || assignmentWeight > 1) 
                    exit();
                } catch (NumberFormatException e) {
                    exit();
                }
                break;
              default:
                break;
            }
          }
          something = filename+" "+key+" "+action+" "+assignmentName+" "+
            assignmentPoints+" "+assignmentWeight;

        //Handles Delete Assignment
        } else if (args[4].equals("-DA")) {
          action = "da";
          requiredFlags = new HashSet<>(Arrays.asList("-AN"));
          if (args.length != 7)
            exit(); // invalid number of arguments
          for(int counter=5; counter < args.length; counter++){
            switch (args[counter]) {
              case "-AN":
                usedFlags.add("-AN");
                assignmentName = args[counter+1];
                if (!isAlphanumeric(assignmentName))
                  exit();
                break;
              default:
                break;
            }
          }
          something = filename+" "+key+" "+action+" "+assignmentName;

        // Handles Add Student
        } else if (args[4].equals("-AS")) {
          action = "as";
          requiredFlags = new HashSet<>(Arrays.asList("-FN","-LN"));
          if (args.length != 9)
            exit(); // invalid number of arguments
          for(int counter=5; counter < args.length; counter++){
            switch (args[counter]) {
              case "-FN":
                usedFlags.add("-FN");
                studentFirstName = args[counter+1];
                if (!isAlphabetical(studentFirstName))
                  exit();
                break;
              case "-LN":
                usedFlags.add("-LN");
                studentLastName = args[counter+1];
                if (!isAlphabetical(studentLastName))
                  exit();
                break;
              default:
                break;
            }
          }
          something = filename+" "+key+" "+action+" "+studentFirstName+" "+
            studentLastName;

        // Handles Delete Student
        } else if (args[4].equals("-DS")) {
          action = "ds";
          requiredFlags = new HashSet<>(Arrays.asList("-FN","-LN"));
          if (args.length != 9)
            exit(); // invalid number of arguments
          for(int counter=5; counter < args.length; counter++){
            switch (args[counter]) {
              case "-FN":
                usedFlags.add("-FN");
                studentFirstName = args[counter+1];
                if (!isAlphabetical(studentFirstName))
                  exit();
                break;
              case "-LN":
                usedFlags.add("-LN");
                studentLastName = args[counter+1];
                if (!isAlphabetical(studentLastName))
                  exit();
                break;
              default:
                break;
            }
          }
          something = filename+" "+key+" "+action+" "+studentFirstName+" "+
            studentLastName;

        // Handles Add Grade
        } else if (args[4].equals("-AG")) {
          action = "ag";
          requiredFlags = new HashSet<>(Arrays.asList("-FN","-LN","-AN","-G"));
          if (args.length != 13)
            exit(); // invalid number of arguments
          for(int counter=5; counter < args.length; counter++){
            switch (args[counter]) {
              case "-FN":
                usedFlags.add("-FN");
                studentFirstName = args[counter+1];
                if (!isAlphabetical(studentFirstName))
                  exit();
                break;
              case "-LN":
                usedFlags.add("-LN");
                studentLastName = args[counter+1];
                if (!isAlphabetical(studentLastName))
                  exit();
                break;
              case "-AN":
                usedFlags.add("-AN");
                assignmentName = args[counter+1];
                if (!isAlphanumeric(assignmentName))
                  exit();
                break;
              case "-G":
                usedFlags.add("-G");
                try {
                  grade = Integer.parseInt(args[counter+1]);
                  if (grade < 0)
                    exit();
                } catch (NumberFormatException e) {
                  exit();
                }
                break;
              default:
                break;
            }
          }
          something = filename+" "+key+" "+action+" "+studentFirstName+" "+
            studentLastName+" "+assignmentName+" "+grade;
        } else {
          exit(); // action is invalid
        }
      }

    }
    // Check if any flags are missing
    if (!usedFlags.equals(requiredFlags))
      exit();
      
    return something;
  }

  //helper method to verify that a string only contains alphabetical characters
  private static boolean isAlphabetical(String s) {
    return s != null && s.matches("^[a-zA-Z]*$");
  }

  //helper method to verify that a string only contains alphanumeric characters
  private static boolean isAlphanumeric(String s) {
    return s != null && s.matches("^[a-zA-Z0-9]*$");
  }

  private static void exit() {
    System.out.println("invalid");
    System.exit(255);
  }

  public static void main(String[] args) {
    gradebookadd myGradeBookAdd = new gradebookadd();
    String something = myGradeBookAdd.parse_cmdline(args);

    String[] data = something.split(" ");
    
    try {
    	//create a Gradebook object to modify the gradebook file
    	// data[0] = filename, data[1] = key
    	Gradebook gb = new Gradebook(data[0], data[1]); 

    	//modify the gradebook based on the specified action
    	boolean updated = true;
    	try {
    		switch (data[2]) {
    		case "aa": 
    			// addAssignment(assignmentName, assignmentPoints, assignmentWeight);
    			gb.addAssignment(data[3], Integer.parseInt(data[4]), Double.parseDouble(data[5]));
    			break;
    		case "da":
    			// deleteAssignment(assignmentName);
    			gb.deleteAssignment(data[3]);
    			break;
    		case "as": 
    			// addStudent(studentFirstName, studentLastName);
    			gb.addStudent(data[3], data[4]);
    			break;
    		case "ds":
    			// deleteStudent(studentFirstName, studentLastName);
    			gb.deleteStudent(data[3], data[4]);
    			break; 
    		case "ag":
    			// addGrade(studentFirstName, studentLastName, assignmentName, grade);
    			gb.addGrade(data[3], data[4], data[5], Integer.parseInt(data[6]));
    			break; 
    		}
    	} catch (IllegalArgumentException e) {
    		updated = false;
    	}

    	String key = data[1];
    	gb.saveGradebook(data[0], key);
	
    } catch (Exception e) {
    	System.out.println("invalid");
    	System.exit(255);
    }
    
    return;
  }
}
