//import ...
import java.util.*;
import java.io.*;
import java.security.*;
import java.io.Serializable;

/**
 * A helper class for your gradebook
 * Some of these methods may be useful for your program
 * You can remove methods you do not need
 * If you do not wish to use a Gradebook object, don't
 */
public class Gradebook implements Serializable{
  private String studentsHash, assignmentsHash, gradebookHash;
  private List<Student> students;
  private List<Assignment> assignments;
  private Map<Student, Map<Assignment, Integer>> gradebook;

  /* Create a new gradebook */
  public Gradebook() {
    students = new ArrayList<Student>();
    assignments = new ArrayList<Assignment>();
    gradebook = new HashMap<Student, Map<Assignment, Integer>>();
    studentsHash = calculateHash(students);
    assignmentsHash = calculateHash(assignments);
    gradebookHash = calculateHash(bookToString());
    //gradebook.put(some student, new HashMap<Assignment, Integer>())
  }

  /* return the size of the gradebook */
  public int size() {
    return students.size();
  }

  public List<Student> getAllStudents () {
    return students;
  }

  public Map<Assignment, Integer> getStudentGrades(Student stud){

    return gradebook.get(stud);
  }

  public List<Assignment> getAllAssignments(){

    return assignments;
  }
  public Map<Student, Map<Assignment, Integer>> getGradebook(){

    return gradebook;
  }

  /* Adds a student to the gradebook */
  public void addStudent(String first, String last) {
    Student stud = new Student(first, last);
    students.add(stud);
    Map<Assignment, Integer> gradeMap = new HashMap<Assignment, Integer>();

    for(Assignment a : assignments){
      gradeMap.put(a, 0);
    }

    gradebook.put(stud, gradeMap);
  }

  public Student removeStudent(String first, String last) {
    Student stud = getStudent(first, last);
    if(stud == null) {
      return null;
    }

    students.remove(stud);
    gradebook.remove(stud);
    return stud;
  }

  /* Adds an assinment to the gradebook */
  public void addAssignment(String name, int points, double weight) {

    Assignment a = new Assignment(name, points, weight);
    assignments.add(a);
    for (Student s: students) {
      addGrade(name, s.getFirst(), s.getLast(), 0);
    }

  }

  /* remove assignment, returns Assignment if it exists, null if it doesn't */
  public Assignment removeAssignment(String assignmentName) {
    Assignment a = getAssignment(assignmentName);

    if(a == null) {
      return null;
    }

    assignments.remove(a);

    for(Student s : gradebook.keySet()){

        Map<Assignment, Integer> gb = gradebook.get(s);
        gb.remove(a);
    }

    return a;
  }

  public double getTotalWeights() {
    double total = 0;
    for(Assignment a : assignments) {
      total += a.getWeight();
    }

    return total;
  }

  public boolean valueExceedsMax(String assignment, int grade){

    Assignment a = getAssignment(assignment);

    if (a == null){

      return false;
    }

    if (a.getPoints() < grade){

      return true;
    }

    return false;
  }

  /* Adds a grade to the gradebook */
  public void addGrade(String assignmentName, String firstName, String lastName, int grade) {
    Student student = getStudent(firstName, lastName) ;
    Assignment assignment = getAssignment(assignmentName);

    // Ensure that student and assignment exist
    if (student != null && assignment != null) {
      Map<Assignment, Integer> grades = gradebook.get(student);
      grades.put(assignment, grade);
      gradebook.put(student, grades);
    } else {
      System.out.println("Student or Assignment not found in addGrade");
    }

  }

  public Student getStudent(String firstName, String lastName) {
    for (Student s : students) {
      if (s.matchStudent(firstName, lastName)){
        return s;
      }
    }

    return null;
  }

  public Assignment getAssignment(String assignmentName) {
    for (Assignment a : assignments) {
      if (a.getName().equals(assignmentName)) {
        return a;
      }
    }

    return null;
  }

  public int getPointsAssignment(Student student, Assignment assignment) {
    return gradebook.get(student).get(assignment);
  }

  public double getGradeStudent(Student student) {
    Map<Assignment, Integer> assignmentMap = gradebook.get(student);
    Set<Assignment> assignmentSet = assignmentMap.keySet();
    double total = 0;
    for (Assignment a : assignmentSet) {
      total += (((double) assignmentMap.get(a)) / a.getPoints()) * (a.getWeight());
    }

    return total;
  }


  private static String calculateHash(Object obj) {
    String hash = "";
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream out = null;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      out = new ObjectOutputStream(bos);
      out.writeObject(obj);
      out.close();
      byte[] yourBytes = bos.toByteArray();
      md.update(yourBytes);
      for (byte b : md.digest()) {
        hash += String.format("%02X", b);
      }
    } catch(Exception e) {
      System.out.println("invalid -- hashing failed");
      System.exit(255);
    } finally {
      try {
        bos.close();
      } catch (IOException ex) {
        // ignore close exception
      }
    }


    return hash;
  }

  private String bookToString(){
    String book = "{";
    for (Map.Entry<Student, Map<Assignment, Integer>> s : new TreeMap<Student, Map<Assignment, Integer>>(gradebook).entrySet()){
      book += "{" + s.getKey().toString() + " ";
      for (Map.Entry<Assignment, Integer> a : new TreeMap<Assignment, Integer>(s.getValue()).entrySet()){
        book += a.getKey().toString();
        book += String.format("-Grade: %d",a.getValue());
      }
      book += "}";
    }
    book += "}";
    return book;
  }
  public boolean checkHashes() {

    return studentsHash.equals(calculateHash(students))
    && assignmentsHash.equals(calculateHash(assignments))
    && gradebookHash.equals(calculateHash(bookToString()));
  }

  public void recalculateHashes() {

    studentsHash = calculateHash(students);
    assignmentsHash = calculateHash(assignments);
    gradebookHash = calculateHash(bookToString());
  }
}
