//import ...

import java.util.ArrayList; 

/**
 * Allows the user to add a new student or assignment to a gradebook,
 * or add a grade for an existing student and existing assignment
 */
public class gradebookadd {

  /* parses the cmdline to keep main method simplified */
  private static String parse_cmdline(String[] args) {

    int counter;

    // just return a code/string for what is going to happen really
    String code = "ERRORa";

    if(args.length==1)
      System.out.println("\nNo Extra Command Line Argument Passed Other Than Program Name");
    if(args.length>=2) {
      System.out.println("\nNumber Of Arguments Passed: " + args.length);
      System.out.println("----Following Are The Command Line Arguments Passed----");
      for(counter=0; counter < args.length; counter++) {
        System.out.println("args[" + counter + "]: " + args[counter]);
        // Decide what is the setting we are in


        // check if the command at 0 is -N
        switch (counter)
        {
            case 0:
              if (args[counter].equals("-N") == false)
              {
                code = "ERROR1";
                break;
              }
            case 1:
                code = "name-1";
                break;
            case 2:
              if (args[counter].equals("-K") == false)
              {
                code = "ERROR2";
                break;
              }
            case 3:
                code = "KEY-3";
                break;
            // maybe throw error if this returns because there should be an argument after
            case 4:
              if (args[counter].equals("-AA"))
              {
                code = "ERROR3";
                break;
              }
              else if (args[counter].equals("-DA"))
              {
                code = "ERROR4";
                break;
              }
              else if (args[counter].equals("-AS"))
              {
                code = "ERROR5";
                break;
              }
              else if (args[counter].equals("-DS"))
              {
                code = "ERROR6";
                break;
              }
              else if (args[counter].equals("-AG"))
              {
                code = "ERROR7";
                break;
              }
              else
              {
                System.out.println(args[4]);
                code = "ERROR8";
                break;
              }
            case 5:
              if (args[counter - 1].equals("-AA") && args[counter].equals("-AN") && 
              args[counter + 2].equals("-P") && args[counter + 4].equals("-W"))
              {
                // check if <assignment-name> is alphanumeric and if there is already one with this name
                if (args[counter + 1].matches("([A-z0-9]+)"))
                {
                  // check if <assignment-points> is a non-negative
                  if (Integer.parseInt(args[counter + 3]) >= 0)
                  {
                    // check if <assignment-weight> is in between 0 and 1
                    code = "AA";
                  }
                }
                break;
              }
              else if (args[counter - 1].equals("-DA") && args[counter].equals("-AN"))
              {
                // check if <assignment-name> is alphanumeric and if there is already one with this name
                if (args[counter + 1].matches("([A-z0-9]+)"))
                {
                  code = "DA";
                }
                
                break;
              }
              else if (args[counter - 1].equals("-AS") && args[counter].equals("-FN")
              && args[counter + 2].equals("-LN"))
              {
                // check if <student-first-name> and <student-last-name>
                // is alphanumeric and if there is already one with this name
                if (args[counter + 1].matches("([A-z]+)") && args[counter + 3].matches("([A-z]+)"))
                {
                  code = "AS";
                }
                break;
              }
              else if (args[counter - 1].equals("-DS") && args[counter].equals("-FN") 
              && args[counter + 2].equals("-LN"))
              {
                // check if <student-first-name> and <student-last-name>
                // is alphanumeric and if there is already one with this name
                if (args[counter + 1].matches("([A-z]+)") && args[counter + 3].matches("([A-z]+)"))
                {
                  code = "DS";
                }
                break;
              }
              else if (args[counter - 1].equals("-AG") && args[counter].equals("-AN") && 
              args[counter + 2].equals("-FN") && args[counter + 4].equals("-LN") && 
              args[counter + 6].equals("-G"))
              {
                // check if <student-first-name> and <student-last-name> and <assignment-name>
                // is alphanumeric and if there is already one with this name
                if (args[counter + 3].matches("([A-z]+)") && args[counter + 5].matches("([A-z]+)") &&
                args[counter + 1].matches("([A-z0-9]+)"))
                {
                  // check if <grade> is a non-negative
                  if (Integer.parseInt(args[counter + 7]) >= 0)
                  {
                    code = "AG";
                  }
                }
                break;
              }

        }
      }
    }
    //TODO ...

    return code;
  }

  public static void main(String[] args) {

    String argument = parse_cmdline(args);

    // need to check if args[1] (the name of the gradebook is okay), call setup

    // need to check for "error" and throw error when calling parse method

    
    if(!argument.equals("ERROR")) {
      //read from gradebook
    	Gradebook currGradebook = new Gradebook(args[1] + ".txt"); //Reads ]
    	//TODO need to ensure it exists, if not then exit

      //TODO do things here.
    	switch(args[4]) {
    	case "-AA":
    		//Add assignment
    		String assignmentName = args[6];
    		int score = Integer.parseInt(args[8]);
    		double weight = Double.parseDouble(args[10]);
    		
    		if(weight < 0.0 || weight > 1.0) {
    			System.out.print("ERROR, weight is over or under allowed value.");
    			//TODO exit here...
    		}
    		
    		if(!currGradebook.addGradeKey(assignmentName, score, weight)) {
    			System.out.println("ERROR, weights greater than 1.0");
    			return;
    		}
    		break;
    	case "-DA":
    		String assignName = args[6];
    		if(!currGradebook.deleteAssignment(assignName)) {
    			System.out.print("ERROR, assignment does not exist");
    		}
    		break;
    	case "-AS":
    		String name = args[6] + " " + args[8]; //get first and last name 
    		currGradebook.addStudent(name);
    		break;
    	case "-DS":	
    		String currName = args[6] + " " + args[8];
    		currGradebook.deleteStudent(currName);
    		break;
    	case "-AG":
    		String assignName1 = args[6];
    		String name2 = args[8] + " " + args[10];
    		int currScore = Integer.parseInt(args[12]);
    		
    		currGradebook.updateGrade(name2, assignName1, currScore);
    	}

      //write the result back out to the gradebook
    	currGradebook.WriteToFile(); //Writes to file
    }


    System.out.println(argument);

    return;
  }
}
