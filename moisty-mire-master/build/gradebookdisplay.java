//import ...
import java.lang.*;
import java.io.*;
import java.util.*;
import gradingtools.gbCrypto;
import gradingtools.Gradebook;
import gradingtools.Student;
import gradingtools.GradeEntry;

/**
 * Prints out a gradebook in a few ways
 * Some skeleton functions are included
 */
public class gradebookdisplay {
  static boolean verbose = false;

  static class NameComparator implements Comparator<GradeEntry>{
    public int compare(GradeEntry e1, GradeEntry e2){
      Student s1 = e1.getStudent();
      Student s2 = e2.getStudent();
      int lastComp = s1.getLastName().compareTo(s2.getLastName());
      if (lastComp== 0){
        return s1.getFirstName().compareTo(s2.getFirstName());
      }
      return lastComp;

    }
  }

  static class GradeComparator implements Comparator<GradeEntry>{
    public int compare(GradeEntry e1, GradeEntry e2){
      // do e2 - e1 to get it in descending order by score
      return (int)e2.getStudentPoints() - (int)e1.getStudentPoints();
    }
  }

  static class FinalGradeComparator implements Comparator<GradeEntry>{
    public int compare(GradeEntry e1, GradeEntry e2){
      // do e2 - e1 to get it in descending order by score
      double diff = (double)e2.getStudentPoints() - (double)e1.getStudentPoints();
      if(diff > 0){
        return 1;
      }
      if(diff < 0){
        return -1;
      }
      return 0;
    }
  }

  //Decrypts the gradebook file and returns a Gradebook object
  private static Gradebook decrypt(String filename) {
    Gradebook gradebook = null;
    try {
      gradebook = gbCrypto.getGradebook(filename);
      return gradebook;
    }
    catch (Exception e) {
      System.out.println("Invalid");
      System.exit(255);
    }
    return gradebook;
  }

  // Prints out grades of all students for a particular assignment. The print consists of tuples (LastName, FirstName, Grade), with one tuple per line.
  private static void print_Assignment(Gradebook gradebook, String assignment_name, boolean A, boolean G) {
    ArrayList<String> assignment_list = gradebook.getAssignmentList();
    HashMap<String, Integer> assignment_weights = gradebook.getAssignmentPoints();
    // TreeMap<String, ArrayList<GradeEntry>> studentPoints = gradebook.getStudentPoints();

    if(!assignment_list.contains(assignment_name)) {
      // Invalid because the assignment does not exist
      System.out.println("Invalid");
      System.exit(255);
    }

    ArrayList<GradeEntry> assignmentGrades = gradebook.getStudentPoints().get(assignment_name);
    // Print alphabetically
    if(A) {
      Collections.sort(assignmentGrades, new NameComparator());
      for(GradeEntry e: assignmentGrades) {
        System.out.println(e.toString());
      }
    }
    // Print by descending grade values
    else if(G) {
      Collections.sort(assignmentGrades, new GradeComparator());
      for(GradeEntry e: assignmentGrades) {
        System.out.println(e.toString());
      }
    }
    else {
      // This should never happen
      System.out.println("Invalid");
      System.exit(255);
    }
    return;
  }

  //Prints out all grades for a particular student. The print consists of pairs of (Assignment Name, Grade), with one tuple per line
  private static void print_Student(Gradebook gradebook, String first_name, String last_name) {
    Student student = new Student(first_name, last_name);
    ArrayList<Student> student_list = gradebook.getStudentList();
    TreeMap<String, ArrayList<GradeEntry>> studentPoints = gradebook.getStudentPoints();
    ArrayList<String> assignmentList = gradebook.getAssignmentList();
    
    if(!student_list.contains(student)) {
      // Invalid because the student does not exist
      System.out.println("Invalid");
      System.exit(255);
    }

    for (String assignment: assignmentList){
      ArrayList<GradeEntry> entries = studentPoints.get(assignment);
      for (GradeEntry e: entries){
        if (e.getStudent().equals(first_name, last_name)){
          System.out.println("(" + assignment + ", " + e.getStudentPoints()+")");
        }
      }
    }
    return;
  }

  // Prints out final grades for all students. The print consists of tuples (LastName, FirstName, Grade) with one tuple per line.
  private static void print_Final(Gradebook gradebook, boolean A, boolean G){
    ArrayList<GradeEntry> final_grades = gradebook.getAllFinalGrades();
    
    if(A) {
      Collections.sort(final_grades, new NameComparator());
      for(GradeEntry e: final_grades){
        System.out.println(e.toString());
      }
    }
    // Print by descending grade values
    else if(G) {
      Collections.sort(final_grades, new FinalGradeComparator());
      for(GradeEntry e: final_grades){
        System.out.println(e.toString());
      }  
    }
    else {
      // This should never happen
      System.out.println("Invalid");
      System.exit(255);
    }
    return;
  }

  public static void main(String[] args) {
    int opt,len;
    Gradebook gradebook = null;
    String gradebook_name = null;

    //TODO Code this
    if(args.length==1)
        System.out.println("\nNo Extra Command Line Argument Passed Other Than Program Name");
    if(args.length>=2)
    {
        // System.out.println("\nNumber Of Arguments Passed: %d" + args.length);
        // System.out.println("----Following Are The Command Line Arguments Passed----");
        // for(int counter = 0; counter < args.length; counter++) {
        //   System.out.println("args[" + counter + "]: " + args[counter]);
        // }
        
        // Decide what is the setting we are in

        //first catch-all for args
        if(args.length < 6) {
          // Invalid because not enough args are provided (bare minimum is 6 with "-N filename -K key -PF -A")
          System.out.println("Invalid");
          System.exit(255);
        }
        ////////////////COMMAND LINE SPECIFICATIONS///////////////////
        //args that MUST be included:
        //args[0] -> -N
        //args[1] -> <gradebook-name>, alphanumeric characters (including underscores and periods). If the file does not exist in current directoy raise error.
        if(args[0].equals("-N")) {
          if(args[1].matches("^[A-Za-z0-9_\\.]*$")) {
            gradebook_name = args[1];
            File f = new File(gradebook_name);
            if(!f.exists()) {
              // Invalid because the file does not exist
              System.out.println("Invalid");
              System.exit(255);
            }
          }
          else {
            // Invalid because gradebook name is not valid
            System.out.println("Invalid");
            System.exit(255);
          }
        } 
        else {
          //Invalid because "-N" was not provided
          System.out.println("Invalid");
          System.exit(255);
        }
        //args[2] -> -K
        //args[3] -> <key>, string of hex digits
        if(args[2].equals("-K")) {
          //should I check for key format validity here or is it handled in the encryption portion?
          //if(args[3].matches("[A-Za-z0-9]*")) {
            String key = args[3];
            if(gbCrypto.keyEquals(key)) {
              gradebook = gradebookdisplay.decrypt(gradebook_name);
            }
          //}
        } 
        else {
          // Invalid because "-K" was not provided
          System.out.println("Invalid");
          System.exit(255);
        }

        //args[4] -> action: {-PA, -PS, PF} representing print-assignment, print-student, and print-final

        //If args[4] is -PA the following must be specified:
        // -AN <assignment-name>, must be {A-Z, a-z, 0-9} cannot contain spaces and is case sensitive
        // -A indicating tuples are to be printed in alphabetical order by LastName, FirstName
        // -G indicating tuples are to be printed in grade order from highest to lowest
        // note exactly one of -A or -G must be specified, otherwise raise error
        if(args[4].equals("-PA")) { //print-assignment
          boolean print_alphabetical = false;
          boolean print_gradeorder = false;
          String assignment_name = null;
          if(args.length < 8) {
            // Invalid because there are not enough args
            // Get me out of here before we get a buffer overflow error
            System.out.println("Invalid");
            System.exit(255);
          }
          //args can be provided in any order so I use a for loop
          for(int i = 5; i < args.length; i++) {
            if(args[i].equals("-AN")) {
              i++; // increment i so that we pass the assignment name when looking for next arg
              if(args[i].matches("[A-Za-z0-9]*")) {
                assignment_name = args[i];
              }
              else {
                // Invalid because the assignment name is not valid
                System.out.println("Invalid");
                System.exit(255);
              }
            }
            else if(args[i].equals("-A")) {
              print_alphabetical = true;
            }
            else if(args[i].equals("-G")) {
              print_gradeorder = true;
            }
            else {
              // Invalid because none of "-AN, -A, or -G" were provided
              System.out.println("Invalid");
              System.exit(255);
            }
          }

          if((print_alphabetical == false && print_gradeorder == false) || 
              (print_alphabetical == true && print_gradeorder == true)) {
            // Invalid because no order was specified by -A or -G
            System.out.println("Invalid");
            System.exit(255);
          }
          if(assignment_name == null) {
            // Invalid because no assignment name was provided
            System.out.println("Invalid");
            System.exit(255);
          }
          try {
            print_Assignment(gradebook, assignment_name, print_alphabetical, print_gradeorder);       
          } catch (NullPointerException e) {
            // Invalid because gradebook is null (probably does not exist)
            System.out.println("Invalid");
            System.exit(255);
          }    
        }

        //if args[4] is -PS the following must be specified:
        // -FN <student-first-name>, must be {A-Z, a-z} cannot contain spaces and is case sensitive
        // -LN <student-last-name>, must be {A-Z, a-z} cannot contain spaces and is case sensitive
        else if(args[4].equals("-PS")) { //print-student
          boolean print_alphabetical = false;
          boolean print_gradeorder = false;
          String first_name = null;
          String last_name = null;

          if(args.length < 9) {
            // Invalid because there are not enough args
            // Get me out of here before we get a buffer overflow error
            System.out.println("Invalid");
            System.exit(255);
          }

          //args can be provided in any order so I use a for loop
          for (int i = 5; i < args.length; i++) {
            if(args[i].equals("-FN")) {
              i++; // increment i so that we pass the first name when looking for next arg 
              if(args[i].matches("[A-Za-z]*")) { 
                // this will only cause a buffer overflow in the case that "-FN" is provided last and comes after a provided FirstName 
                // which is incorrect syntax so fuck the user you deserve buffer overflow
                first_name = args[i];
              }
              else {
                // Invalid because the first name is not in a valid format
                System.out.println("Invalid");
                System.exit(255);
              }
            }
            else if(args[i].equals("-LN")) {
              i++; // increment i so that we pass the last name when looking for next arg
              if(args[i].matches("[A-Za-z]*")) { 
                // this will only cause a buffer overflow in the case that "-LN" is provided last and comes after a provided LastName, which is incorrect syntax
                last_name = args[i];
              }
              else {
                // Invalid because the last name is not in a valid format
                System.out.println("Invalid");
                System.exit(255);
              }
            }
            else {
              // Invalid because none of "-LN", "-A", or "-G" are specified
              System.out.println("Invalid");
              System.exit(255);
            }
          }

          if(last_name == null || first_name == null) {
            // Invalid because either no first name or no last name was provided
            System.out.println("Invalid");
            System.exit(255);
          }
          try {
            print_Student(gradebook, first_name, last_name);
          } catch (NullPointerException e) {
            // Invalid because gradebook is null (probably does not exist)
            System.out.println("Invalid");
            System.exit(255);
          }
        } 

        //if args[4] is -PF the following must be specified:
        // -A indicating tuples are to be printed in alphabetical order by Lastname, Firstname
        // -G indicating tuples are to be printed in grade order from highest to lowest
        // note exactly one of -A or -G must be specified, otherwise raise error
        else if(args[4].equals("-PF")) { //print-final
          boolean print_alphabetical = false;
          boolean print_gradeorder = false;

          if(args.length < 6) {
            // Invalid because there are not enough args
            // Get me out of here before we get a buffer overflow error
            System.out.println("Invalid");
            System.exit(255);
          }

          if(args[5].equals("-A")) {
            print_alphabetical = true;
          }
          else if(args[5].equals("-G")) {
            print_gradeorder = true;
          }
          else {
            // Invalid because no order is specified by -A or -G
            System.out.println("Invalid");
            System.exit(255);
          }
          try {
            print_Final(gradebook, print_alphabetical, print_gradeorder);
          } catch (NullPointerException e) {
            // Invalid because gradebook is null (probably does not exist)
            System.out.println("Invalid");
            System.exit(255);
          }      
        }
        else {
          // Invalid because none of "-PS", "-PA", or "-PF" were specified
          System.out.println("Invalid");
          System.exit(255);
        }
      }
  }
}



