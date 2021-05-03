//import ...
import gradingtools.Gradebook;
import gradingtools.gbCrypto;
import java.util.*;
import java.lang.*;
import java.io.*;

/**
 * Allows the user to add a new student or assignment to a gradebook,
 * or add a grade for an existing student and existing assignment
 */
class Parameters {
  public Gradebook gradebook;
  public String filename, action;
  public Parameters(Gradebook gradebook, String filename, String action) {
    this.gradebook = gradebook;
    this.filename = filename;
    this.action = action;
  }
}

public class gradebookadd {

  

    // is alphanumeric (including periods and underscores) and same directory that the program is running in

  private static boolean isValidAssignmentName(String name) { 
    if (name.matches("[A-Za-z0-9_.]+")) {
      return true;
    } else return false;
  }

  private static boolean isValidStudentName(String name) { 
    if (name.matches("[A-Za-z]+")) {
      return true;
    } else return false;
  }

  private static boolean isAction(String arg) {
    String[] actions = {"-AA", "-DA", "-AS", "-DS", "-AG"};

    for (String action : actions) {
      if (arg.equals(action)) return true; 
    } 
    System.out.println("Action invalid.");
    return false;
  }

  /* parses the cmdline to keep main method simplified */
  private static Parameters parse_cmdline(String[] args) {

    if(args.length==1)
      System.out.println("\nNo Extra Command Line Argument Passed Other Than Program Name");
    if(args.length>=2) {
      // System.out.println("\nNumber Of Arguments Passed: " + args.length);
      // System.out.println("----Following Are The Command Line Arguments Passed----");
      // for(int counter=0; counter < args.length; counter++) {
      //   System.out.println("args[" + counter + "]: " + args[counter]); 
      // }

      /**
     * Command line arguments must be:
     * arg[0] -> "-N", arg[1] -> exists()
     * arg[2] -> "-K" arg[3] -> key
     * arg[4] -> exactly ONE of {-AA, -DA, -AS, -DS, -AG}
     * options arg[5...]-> {-AN, -FN, -LN, -P, -W, -G} in any order and number following
     */
        // Decide what is the setting we are in
      if (args.length >= 5) {
        File f = new File(args[1]);
        if (args[0].equals("-N") && f.exists() && args[2].equals("-K") && args[3] != null && isAction(args[4])) {
          Gradebook gradebook;
          String key = args[3];
          String filename = args[1];
          String action = args[4];

          //CRYPTO HERE
          if(gbCrypto.keyEquals(key)) {
            try {
              gradebook = gbCrypto.getGradebook(filename);
              return new Parameters(gradebook, filename, action);
            }
            catch (Exception e){
              System.out.println("Could not decrypt/deserialize input file.");
              System.out.println("invalid");
              System.exit(255);
            }
          }
          
        }
      }  
    }
     // Catch all for invalid args
     System.out.println("Something went wrong with parsing the command line.");
     System.out.println("invalid");
     System.exit(255);
     return null;
  }

  /* Helper functions for the various actions*/

  // Adds assignment to the gradebook
  // must have AT LEAST 6 args following "-AA" (at least 11 args in entire command)
  // unsuccessful operation return false
  private static boolean addAssignment(Parameters params, String[] args) { 
    // init all parameters as null to check if any are missing after parsing and to ony take the last values 
    String name = null, pointsString = null, weightString = null;
    int points;
    float weight;

    if (args.length >= 11) {
      // parse the options
      for (int i = 5; i < args.length; i += 2) {
        if (args[i].equals("-AN") && args[i + 1] != null) {
            name = args[i + 1]; 
        } else if (args[i].equals("-P") && args[i + 1] != null) {
            pointsString = args[i + 1];
        } else if (args[i].equals("-W") && args[i + 1] != null) {
            weightString = args[i + 1];
        } else {
          System.out.println("Add Assignment Error: Incorrect formatting or lacking a value.");
          return false;
        }
      }
      
      if (name != null && pointsString != null && weightString != null) {
        if (isValidAssignmentName(name)){
          try {
            points = Integer.parseInt(pointsString);
          } catch (NumberFormatException e) {
            System.out.println("Add Assignment Error: Points value must be a non negative integer.");
            return false;
          }

          try{
            weight = Float.parseFloat(weightString);
          } catch (NumberFormatException e) {
            System.out.println("Add Assignment Error: Weight value must be a real number [0,1].");
            return false;
          }

          if (points < 0) {
            System.out.println("Add Grade Error: Points value must be a non negative integer.");
            return false;
          } 

          // Call Gradebook class function 
          params.gradebook.addAssignment(name, points, weight);
          return true;

        } else {
          System.out.println("Add Assignment Error: Assignment name must be alphanumeric 0-9 and not contain spaces.");
          return false;
        }
      }
    }
    return false;
  }

  /**
   * Deletes an assignment for all students
   * Must have at least 2 arguments following "-DA" (at least 7 arguments in entire command)
   */
  private static boolean deleteAssignment(Parameters params, String[] args) {
    String name = null;
    
    if (args.length >= 7) {
      
      for (int i = 5; i < args.length; i += 2) {
        if (args[i].equals("-AN") && args[i + 1] != null) {
          name = args[i + 1];
        } else {
          System.out.println("Delete Assignment Error: Incorrect formatting or lacking a value.");
          return false;
        }
      }

      if (name != null && isValidAssignmentName(name)) {
        params.gradebook.deleteAssignment(name);
        return true;
      } else {
        System.out.println("Delete Assignment Error: Assignment name must be alphanumeric 0-9 and not contain spaces.");
        return false;
      }
    }
    return false;
  }

  /**
   * Add a new student
   * Must have at least 4 arguments following "-AS" (at least 9 arguments in entire command)
   */
  private static boolean addStudent(Parameters params, String[] args) {
    String firstName = null, lastName = null;

    if (args.length >= 9) {

      for (int i = 5; i < args.length; i += 2) {
        if (args[i].equals("-FN") && args[i + 1] != null) {
          firstName = args[i + 1];
        } else if (args[i].equals("-LN") && args[i + 1] != null) {
          lastName = args[i + 1]; 
        } else {
          System.out.println("Add Student Error: Incorrect formatting or lacking a value.");
        }
      }

      if (firstName != null && lastName != null && isValidStudentName(firstName) && isValidStudentName(lastName)) {
        params.gradebook.addStudent(firstName, lastName);
        return true;
      } else {
        System.out.println("Add Student Error: First and Last must be alphabetic and not contain spaces.");
        return false;
      }
    } else {
      return false;
    }
  }

   /**
   * Deletes a student
   * Must have at least 4 arguments following "-AS" (at least 9 arguments in entire command)
   */
  private static boolean deleteStudent(Parameters params, String[] args) {
    String firstName = null, lastName = null;

    if (args.length >= 9) {

      for (int i = 5; i < args.length; i += 2) {
        if (args[i].equals("-FN") && args[i + 1] != null) {
          firstName = args[i + 1];
        } else if (args[i].equals("-LN") && args[i + 1] != null) {
          lastName = args[i + 1]; 
        } else {
          System.out.println("Delete Student Error: Incorrect formatting or lacking a value.");
        }
      }

      if (firstName != null && lastName != null && isValidStudentName(firstName) && isValidStudentName(lastName)) {
        params.gradebook.deleteStudent(firstName, lastName);
        return true;
      } else {
        System.out.println("Delete Student Error: First and Last must be alphabetic and not contain spaces.");
        return false;
      }
    } else {
      return false;
    }
  }

  /**
   * Add a new grade for an existing student or assignment
   * Must have at least 8 arguments following "-AG" (at least 13 arguments in the entire command)
   */
  private static boolean addGrade(Parameters params, String[] args) {
    String firstName = null, lastName = null, assignmentName = null, pointsString = null;
    int points;

    if (args.length >= 13) {
      for (int i = 5; i < args.length; i += 2) {
        if (args[i].equals("-FN")) {
          firstName = args[i + 1];
        } else if (args[i].equals("-LN")) {
          lastName = args[i + 1];
        } else if (args[i].equals("-AN")) {
          assignmentName = args[i + 1];
        } else if (args[i].equals("-G")) {
          pointsString = args[i + 1];
        } else {
          System.out.println("Add Grade Error: Incorrect Formatting or lacking a value.");
          return false;
        }
      }  

      if (firstName != null && lastName != null && assignmentName != null && pointsString != null) {
        if  (isValidStudentName(firstName) && isValidStudentName(lastName)) {
          if (isValidAssignmentName(assignmentName)) {
            try {
                points = Integer.parseInt(pointsString); 
            } catch (NumberFormatException e) {
              System.out.println("Add Grade Error: Points value must be a non negative integer.");
              return false;
            }

            if (points < 0) {
              System.out.println("Add Grade Error: Points value must be a non negative integer.");
              return false;
            } 
            params.gradebook.addGrade(firstName, lastName, assignmentName, points);
            return true;
          } else {
            System.out.println("Add Grade Error: Invalid Assignment Name.");
            return false;
          }
        } else {
          System.out.println("Add Grade Error: Invalid Student Names.");
          return false;
        }
      } else {
        System.out.println("Add Grade Error: Null parameters.");
        return false;
      }    
    } else {
      System.out.println("Add Grade Error: Not enough arguments.");
      return false;
    }
  }

  public static void main(String[] args) {
    
    Parameters params = parse_cmdline(args);

    if (params.action.equals("-AA")) {
      if (addAssignment(params, args)) {
        ;
      } else {
        System.out.println("invalid");
        System.exit(255);
      }
    } else if (params.action.equals("-DA")) {
      if (deleteAssignment(params, args)) {
        ;
      } else {
        System.out.println("invalid");
        System.exit(255);
      }
    } else if (params.action.equals("-AS")) {
      if (addStudent(params, args)) {
        ;
      } else {
        System.out.println("invalid");
        System.exit(255);
      }
    } else if (params.action.equals("-DS")) {
      if (deleteStudent(params, args)) {
        ;
      } else {
        System.out.println("invalid");
        System.exit(255);
      }
    } else if (params.action.equals("-AG")) {
      if (addGrade(params, args)) {
        ;
      } else {
        System.out.println("invalid");
        System.exit(255);
      }
    } else {
      System.out.println("invalid");
      System.exit(255);
    }

    // Save the gradbook here
    try {
      gbCrypto.saveGradeBook(params.gradebook, params.filename);
    }
    catch (Exception e){
      System.out.println("Error trying to save gradebook. Any changes currently made to gradebook will not be saved");
      System.out.println("invalid");
      System.exit(255);
    }
    

    return;
  }
}
