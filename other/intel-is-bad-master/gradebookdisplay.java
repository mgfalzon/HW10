//import ...
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.security.InvalidKeyException;
/**
 * Prints out a gradebook in a few ways
 * Some skeleton functions are included
 */
public class gradebookdisplay {
  static boolean verbose = false;
  
  //TODO: Need to implement key checks to make sure no unauthorized users can access the gradebooks
  private String parse_cmdline(String[] args) {

    String data = null;

    // Gets set to "pa", "ps", or "pf"
    String action = null;


    // Possible remaining options used to fullfil request.
    String assignmentName = null;
    String studentFirstName = null;
    String studentLastName = null;
    String mode = null;
    int args_size = args.length;
    
    String filename = null;
    String key = null;
    if (args_size==0)
      System.out.println("\nNo Extra Command Line Argument Passed Other Than Program Name");
    
    else if (args_size < 5) {
      errorMsg();
    } else if (args_size >= 5) {
      // Check first arguments
      if (!args[0].equals("-N")) {
        // Problem
    	errorMsg();
        
      } else if (!args[2].equals("-K")) {
        // Problem
    	errorMsg();
    	
      } else {  
        filename = args[1];
        key = args[3];
        
        if(!filename.matches("^[a-zA-Z0-9_.]*$")) {
        	errorMsg();
        }
        if(!key.matches("[a-fA-F0-9]*$")) {
        	errorMsg();
        }
        // Checks for Print-Assignment option
        if(args[4].equals("-PA")){
          action = "pa";
          for(int counter=5; counter < args_size; counter++){
            switch (args[counter]) {
              // alphabetical order
              case "-A":
            	if (mode == null) {
            		mode = "a";
            	} else if (mode == "g") {
            		errorMsg();
            	}
                break;
              // order by highest grade
              case "-G":
            	if (mode == null) {
            		mode = "g";
            	} else if (mode == "a") {
            		errorMsg();
            	}
                break;
              // print assignment names
              case "-AN":
            	// Don't know if we need to check to make sure if the following argument is
            	// a valid assignmentName
                assignmentName = args[counter+1];
                counter += 1;
                break;
              default:
            	errorMsg();
                break;
            }
          }
          if (assignmentName == null || mode == null) {
        	  errorMsg();
          } 
          data = filename+" "+key+" "+action+" "+assignmentName+" "+mode;
          print_Assignment(filename, assignmentName, mode, key);
          
        // Checks for Print-Student option
        } else if (args[4].equals("-PS")){
          action = "ps";
          for(int counter=5; counter < args_size; counter++){
            switch (args[counter]){
              case "-FN":
                studentFirstName = args[counter+1];
                counter += 1;
                break;
              case "-LN":
                studentLastName = args[counter+1];
                counter += 1;
                break;
              default:
                errorMsg();
                break;
            }
          }
          if (studentFirstName == null || studentLastName == null) {
        	  errorMsg();
          }
          print_Student(filename, studentFirstName, studentLastName, key);
          data = filename+" "+key+" "+action+" "+studentFirstName+" "+studentLastName;

        // Checks for Print-Final option
        } else if (args[4].equals("-PF")){
          action = "pf";
          for(int counter=5; counter < args_size; counter++){
            switch (args[counter]){
              case "-A":
            	if (mode == null) {
            		mode = "a";
            	} else if (mode == "g") {
            		errorMsg();
            	}
                break;
              case "-G":
            	if (mode == null) {
            		mode = "g";
            	} else if (mode == "a") {
            		errorMsg();
            	}
                break;
              default:
                errorMsg();
                break;
            }
          }
          if (mode == null) {
        	  errorMsg();
          }
          print_Final(filename, mode, key);
          data = filename+" "+key+" "+action+" "+mode;
        } else {
          // Action Not Specified
          errorMsg();
        }
        
      }
    }
    return data;
  }
  
  private void errorMsg() {
	  System.out.println("Invalid");
      System.out.println("Usage: gradebookdisplay.java -N <file name> -K <key> [-PA -AN <assignment name> <-A|-G> | -PS -FN <student-first-name> -LN <student-last-name> | -PF <-A | -G>]");
      System.exit(255);
  }
  
  private void print_Gradebook() {
	int num_assignment = 1;
    for(int i = 0; i < num_assignment; i++) {
      dump_assignment();
      System.out.println("----------------\n");
    }

    return;
  }
  
  private void dump_assignment() {
	  return;
  }
  
  
  private void print_Assignment(String fileName, String assignmentName, String mode, String s_key) {
	  // TODO: File must be located in the same location gradebookAdd is running in. Currently the 
	  // the program does not check this
	  try {
		  Gradebook gb = new Gradebook(fileName, s_key);
		  HashMap<String, Integer> grades = gb.retrieveAssignmentGrades(assignmentName);
		  if (mode == "a") {
			  List<String> keys = new ArrayList<String>(grades.keySet());
			  Collections.sort(keys);
			  Iterator<String> item = keys.iterator();
			  
			  while(item.hasNext()) {
				 String cur_key = item.next();
				 System.out.println("(" + cur_key + ", " + grades.get(cur_key) + ")");
			  }
			  
		  } else if (mode == "g") {
			  ArrayList<String> sorter = new ArrayList<String>();
			  grades.forEach((k, v) -> {
				  String element = v + "  " + k;
				  sorter.add(element);
			  });
			  Collections.sort(sorter);
			  Collections.reverse(sorter);
			  Iterator<String> item = sorter.iterator();
			  
			  while(item.hasNext()) {
				  String cur_key = item.next();
				  String[] valAndKey = cur_key.split("  ");
				  System.out.println("(" + valAndKey[1] + ", " + valAndKey[0] + ")");
			  }
		  } else {
			  System.out.println("Incorrect mode detected");
			  System.exit(255); 
		  }
	  } catch(Exception e) {
		  errorMsg();
		  System.exit(255);
	  } 
	  
	  return;
  }

  
  private void print_Student(String fileName, String firstName, String lastName, String s_key) {
	  try {
		  Gradebook gb = new Gradebook(fileName, s_key);
		  
		  String key = firstName + " " + lastName;
		  
		  if (gb.containsStudent(key)) {
			LinkedHashMap<String, String> assignments = gb.retrieveAssignmentsNames();
			assignments.forEach((k,v) -> {
				Integer grade = gb.retrieveStudentGrades(key, k);
				if(grade != null) {
					System.out.println("(" + k + ", " + grade + ")");
				}
			});
		  } else {
			  System.out.println("No such student exists in the database");
			  System.exit(255);
		  }
		  
	  } catch (Exception e) {
		  errorMsg();
	  } 
	  
	  return;
  }

  
  private void print_Final(String fileName, String mode, String s_key){
	  try {
		Gradebook gb = new Gradebook(fileName, s_key);
		Object[] students = gb.retrieveStudentNames();
		HashMap<String, String> assignments = gb.retrieveAssignmentsNames();
		Object[] listOfAssign = assignments.keySet().toArray();
		HashMap<String, String> final_grades = new HashMap<String, String>();
		
		for(int i = 0; i < students.length; i++) {
			String curr_student = students[i].toString();
			String[] names = curr_student.split(" ");
			double grade = 0.0;
			
			for (int j = 0; j < listOfAssign.length; j++) {
				String curr_assign = listOfAssign[j].toString();
				String[] parameters = assignments.get(curr_assign).split(" ");
				
				Integer assign_grade = gb.retrieveStudentGrades(curr_student, curr_assign);
				if (assign_grade != null) {
					grade += ((double)assign_grade / Double.valueOf(parameters[1])) * Double.valueOf(parameters[2]);
				}
			}
			
			String val = "(" + names[1] + ", " + names[0] + ", " + (float)grade + ")";
			if (mode == "g") {
				String key = grade + " " + names[1] + " " + names[0];
				final_grades.put(key, val);
				
			} else if (mode == "a") {
				String key = names[1] +  " " + names[0];
				final_grades.put(key, val);
				
			}
		}
		
		List<String> sorter = new ArrayList<String>(final_grades.keySet());
		Collections.sort(sorter);
		if (mode == "g") {
			Collections.reverse(sorter);
		}
		Iterator item = sorter.iterator();
		while(item.hasNext()) {
			System.out.println(final_grades.get(item.next()));
		}
	  } catch (Exception e) {
		  errorMsg();
	  }
	  
    return;
  }

  
  public static void main(String[] args) {
    int opt,len;
    //char *logpath = null;

    gradebookdisplay myGradeBookDisplay = new gradebookdisplay();
    String data = myGradeBookDisplay.parse_cmdline(args);



  }
}
