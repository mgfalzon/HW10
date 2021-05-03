//import ...
package gradingtools;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.io.*;

/**
 * A helper class for your gradebook
 * Some of these methods may be useful for your program
 * You can remove methods you do not need
 * If you do not wiish to use a Gradebook object, don't
 */
public class Gradebook implements java.io.Serializable{
  ArrayList<Student> studentList;
  ArrayList<String> assignmentList;
  TreeMap<String, ArrayList<GradeEntry>> studentPoints; // Map<Assignment Names, ArrayList< (Student, points) > 
  
  HashMap<String, Double> assignmentWeights;           // Map<Assignment Names, Weight>
  HashMap<String, Integer> assignmentPoints;

  double cumulativeWeight; 
  int numStudents;
  int numAssignments;

  /* Create a new gradebook */
  public Gradebook() {
    studentList = new ArrayList<Student>();
    assignmentList = new ArrayList<String>();
    studentPoints = new TreeMap<String, ArrayList<GradeEntry>>();   // Map<Assignment Names, Map<Student, Grade>>
    assignmentWeights = new HashMap<String, Double>();           // Map<Assignment Names, Weight>
    assignmentPoints = new HashMap<String, Integer>();

    cumulativeWeight = 0; 
    numStudents = 0;
    numAssignments = 0;
  }

  /* return the size of the gradebook */
  public int size() {

    return numAssignments;
  }

  /* Adds a student to the gradebook */
  public void addStudent(String firstName, String lastName) {
    // Deal with student already exists
    for(Student s: studentList){
      if(s.equals(firstName, lastName)){
        System.out.println("invalid");
        System.exit(255);
      }
    }

    // System.out.println("Student added: " + firstName + ", " + lastName);

    // Create new student, add to student List
    Student newStudent = new Student(firstName, lastName);
    studentList.add(newStudent);
    numStudents ++;
    ArrayList<GradeEntry> studentGrades;

    // Iterate through all assignments in grades, set this new student's grade for each assignment to be 0
    for(String assignmentName: studentPoints.keySet()){
      studentGrades = studentPoints.get(assignmentName);
      studentGrades.add(new GradeEntry(newStudent, 0));
      studentPoints.put(assignmentName, studentGrades);
    }

  }

  /* Adds an assinment to the gradebook */
  public void addAssignment(String name, int points, double weight) {
    // Check if assignment name already exists
    for(String assignmentName: assignmentList){
      if (assignmentName.equals(name)){
        System.out.println("invalid");
        System.exit(255);
      }
    }

    // Check if input weight is invalid
    if(cumulativeWeight + weight > 1){
      System.out.println("invalid");
      System.exit(255);
    }

    
    // Add name, weight, points of assignment
    assignmentList.add(name);
    assignmentWeights.put(name, weight);
    assignmentPoints.put(name, points);
    cumulativeWeight += weight;
    numAssignments ++;

    //Create student grades for this assignment (they should all be 0) 
    ArrayList<GradeEntry> studentGrades = new ArrayList<GradeEntry>();
    for (Student s: studentList){
      studentGrades.add(new GradeEntry(s, 0));
    }
    studentPoints.put(name, studentGrades);
    // System.out.println("Assignment added: " + name);
  }

  /* Adds a grade to the gradebook */
  public void addGrade(String firstName, String lastName, String assignmentName, int points) {
    // Check if student exists
    boolean exists = false;
    Student currentStudent = null; 
    for(Student s: studentList){
      if(s.equals(firstName, lastName)){
        exists = true;
        currentStudent = s;
        break;
      }
    }

    // If student exists, stop
    if(!exists){
      // System.out.println("Student does not exist");
      System.out.println("invalid");
      System.exit(255);
    }

    // Check if assignment exists
    exists = false;
    for(String assignment: assignmentList){
      if (assignment.equals(assignmentName)){
        exists = true;
        break;
      }
    }

    if(!exists){
      // System.out.println("Assignment does not exist");
      System.out.println("invalid");
      System.exit(255);
    }


    // Check if 0 <= input points <= assignment total points
    if (points > assignmentPoints.get(assignmentName) || points < 0){
      // System.out.println("Input points is not <= total points and >= 0");
      System.out.println("invalid");
      System.exit(255);
    }

    // Update the student's grade
    ArrayList<GradeEntry> studentGrades = studentPoints.get(assignmentName);
    for(GradeEntry e: studentGrades){
      if(e.getStudent().equals(firstName, lastName)){
        e.setStudentPoints(points);
        break;
      }
    }
    // System.out.println("Updating student's grade");
    studentPoints.put(assignmentName, studentGrades);
  }

  // Delete assignment
  public void deleteAssignment(String assignmentName){
    //Check if valid assignment name to remove
    if(!assignmentList.contains(assignmentName)){
      System.out.println("invalid");
      System.exit(255);
    }

    //Remove from assignment list, grades list, points + weight, remove from cumulative weight, decrement num assignments
    assignmentList.remove(assignmentName);
    studentPoints.remove(assignmentName);
    assignmentPoints.remove(assignmentName);
    cumulativeWeight -= assignmentWeights.get(assignmentName);
    assignmentWeights.remove(assignmentName);
    numAssignments --;
  }

  // Delete student 
  public void deleteStudent(String firstName, String lastName){
    boolean exists = false;
    Student currentStudent = null; 
    // Check if student exists
    for(Student s: studentList){
      if(s.equals(firstName, lastName)){
        exists = true;
        currentStudent = s;
        break;
      }
    }

    //If student doesn't exist, stop
    if(!exists){
      System.out.println("invalid");
      System.exit(255);
    }

    //If student exists remove from list and...
    studentList.remove(currentStudent);

    // remove any grades associated with this student
   ArrayList<GradeEntry> studentGrades;
    for(String assignmentName: studentPoints.keySet()){
      studentGrades = studentPoints.get(assignmentName);

      //Iterate through all grade entries, find matching student, remove them
      for(int x =0; x < studentGrades.size(); x++){
        if(studentGrades.get(x).getStudent().equals(firstName, lastName)){
          studentGrades.remove(x);
          break;
        }
      }

      studentPoints.put(assignmentName, studentGrades);
    }

    numStudents --;
  }

  
  public String toString() {

    return "";
  }

  // Helper method to get all student grades
  // Gets singular student's grade
  private double getFinalStudentGrade(Student s){
    //Check if student exists. Shouldn't need this
    // if(!studentList.contains(s)){
    //   System.out.println("Student does not exist in list for grade calculation");
    //   System.exit(255);
    // }

    double grade = 0;
    int points = 0;
    ArrayList<GradeEntry> studentGrades;
    for(String assignmentName: studentPoints.keySet()){
      studentGrades = studentPoints.get(assignmentName);
      for (GradeEntry e: studentGrades){
        if (e.getStudent().equals(s)){
          points = (int) e.getStudentPoints();
          break;
        }
      }
      
      // grade += (weight * studentPoints)/totalPoints; 
      grade += (assignmentWeights.get(assignmentName) * points)/assignmentPoints.get(assignmentName);
    }

    return grade;
  }

 
  public ArrayList<GradeEntry> getAllFinalGrades(){
    ArrayList<GradeEntry> finalGrades = new ArrayList<GradeEntry>();

    for(Student s: studentList){
      finalGrades.add(new GradeEntry(s, getFinalStudentGrade(s)));
    }

    return finalGrades;
  }
  
  public ArrayList<Student> getStudentList(){
    return studentList;
  }

  public ArrayList<String> getAssignmentList(){
    return assignmentList;
  }

  public TreeMap<String, ArrayList<GradeEntry>> getStudentPoints(){
    return studentPoints;
  }
  
  public HashMap<String, Double> getAssignmentWeights(){
    return assignmentWeights;
  }

  public HashMap<String, Integer> getAssignmentPoints(){
    return assignmentPoints;
  }
 
  public double getCumulativeWeight(){
    return cumulativeWeight;
  }

  public int getNumStudents(){
    return numStudents;
  }

  public int getNumAssignments(){
    return numAssignments;
  }


}
