//import ...
import java.util.*;
import java.util.Comparator;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.text.DecimalFormat;
import java.math.RoundingMode;
/**
 * Prints out a gradebook in a few ways
 * Some skeleton functions are included
 */
public class gradebookdisplay {
  static boolean verbose = false;

//Size 5 Flags: 0 = "-AN", 1 = "-FN", 2 = "-LN", 3 = "-G", 4 = "-A"
//NOTE: for -G and -A, they will be "G" and "A" since these do not have args.
//When checking if G and A are present, see if it equals "G" and "A" respectively.

 //assignment name valid and present checked prior to this method
  private static void print_Assignment(Gradebook gradebook, String[] flags) {
    //sort grades highest to lowest
    if (flags[3] != null && flags[3].equals("G")) {
      List<Student> studentList = gradebook.getAllStudents();
      Map<Integer, Set<Student>> gradeToName = new HashMap<Integer, Set<Student>>();
      Set<Integer> grades = new HashSet<Integer>();

      for(Student s : studentList){
        Assignment assign = gradebook.getAssignment(flags[0]);
        Map<Assignment, Integer> gradeMap = gradebook.getStudentGrades(s);
        Integer grade = gradeMap.get(assign);

        Set<Student> studs = gradeToName.get(grade);
        if(studs == null){

          studs = new HashSet<Student>();
        }

        studs.add(s);
        gradeToName.put(grade, studs);
        grades.add(grade);
      }

      List<Integer> sortedList = new ArrayList<Integer>(grades);
      Collections.sort(sortedList, Collections.reverseOrder());

      for(Integer i : sortedList){
        Set<Student> studs = gradeToName.get(i);
        for(Student s : studs)
          System.out.println("(" + s.getLast() + ", " + s.getFirst() + ", " + i + ")");
      }

      // sort display aplhabetically
    } else if (flags[4] != null && flags[4].equals("A")){
      //compare override class at bottom of file
      List<Student> studentsAlpha = gradebook.getAllStudents();
      Collections.sort(studentsAlpha, new Comparator<Student>() {
        @Override
        public int compare(Student o1, Student o2) {
            if ( o2.getLast().compareToIgnoreCase(o1.getLast()) == 0) {
              return o1.getFirst().compareToIgnoreCase(o2.getFirst());
            } else {
              return o1.getLast().compareToIgnoreCase(o2.getLast());
            }
        }
      });

      for (Student stud: studentsAlpha) {
                                                                                              // flags[0] should be assign name
        System.out.println("(" + stud.getLast() + ", " + stud.getFirst() + ", " + gradebook.getPointsAssignment(stud, gradebook.getAssignment(flags[0])) + ")");
      }

    } else {
      throw new IllegalArgumentException("Please specify if you want the display to be alphabetical or by grades highest to lowest.");
    }
    return;
  }

  //firstName/lastName are already checked for bad characters and existence prior to this method
  private static void print_Student(Gradebook gradebook, String firstName, String lastName) {

    Student stud = gradebook.getStudent(firstName, lastName);

    if (stud == null){

      System.out.println("Invalid student.");
      System.exit(255);
    }

    Map<Assignment, Integer> assignments = gradebook.getStudentGrades(stud);
    List<Assignment> assignList = gradebook.getAllAssignments();

    for(Assignment a : assignList){
      System.out.println("(" + a.getName() + ", " + assignments.get(a) + ")");
    }

    return;
  }

  //nothing is checked prior to this method (besides flags[3] and flags[4] are both not null)
  private static void print_Final(Gradebook gradebook, String[] flags){
    if (flags[3] != null && flags[3].equals("G")){
      List<Student> studentsList = gradebook.getAllStudents();
      //Map<Student, Double>
      Map<Double, List<Student>> grades = new HashMap<Double, List<Student>>();
      Set<Double> gradeSet = new HashSet<Double>();

      for(Student s : studentsList){
        double grade = gradebook.getGradeStudent(s);
        List<Student> studs = grades.get(grade);

        if(studs == null){
          studs = new ArrayList<Student>();
        }

        studs.add(s);
        grades.put(grade, studs);
        gradeSet.add(grade);
      }

      List<Double> gradeList = new ArrayList<Double>(gradeSet);
      Collections.sort(gradeList, Collections.reverseOrder());

      DecimalFormat df = new DecimalFormat("#.####"); //Round to 4 decimal places
      df.setRoundingMode(RoundingMode.HALF_UP);
      for(Double d : gradeList){

        List<Student> studs = grades.get(d);
        for(Student s : studs){

          System.out.println("(" + s.getLast() + ", " + s.getFirst() + ", " + df.format(d) + ")");
        }
      }
    } else if (flags[4] != null && flags[4].equals("A")){
      //compare override class at bottom of file
      List<Student> studentsAlpha = gradebook.getAllStudents();
      Collections.sort(studentsAlpha, new Comparator<Student>() {
        @Override
        public int compare(Student o1, Student o2) {
            if ( o2.getLast().compareToIgnoreCase(o1.getLast()) == 0) {
              return o1.getFirst().compareToIgnoreCase(o2.getFirst());
            } else {
              return o1.getLast().compareToIgnoreCase(o2.getLast());
            }
        }
      });

      DecimalFormat df = new DecimalFormat("#.####"); //Round to 4 decimal places
      df.setRoundingMode(RoundingMode.HALF_UP);
      for (Student stud: studentsAlpha) {
        System.out.println("(" + stud.getLast() + ", " + stud.getFirst() + ", " + df.format(gradebook.getGradeStudent(stud)) + ")");
      }

    }
    else{
      throw new IllegalArgumentException("Grade or Alphabetical flag missing.");
    }
    return;
  }

  public static void main(String[] args){

    parse_cmd(args);
    return;
  }

  private static void parse_cmd(String[] args) {

    //TODO Code this
    try {
      if(args.length < 2)
          throw new IllegalArgumentException("Not enough arguments.");
      if(args.length >= 2)
      {
          // System.out.println("\nNumber Of Arguments Passed: %d" + args.length);
          // System.out.println("----Following Are The Command Line Arguments Passed----");
          // for(int counter = 0; counter < args.length; counter++)
          //   System.out.println("args[" + counter + "]: " + args[counter]);
            // Decide what is the setting we are in

          if (args[0].equals("-N")){

            if (!allowedChars("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_.", args[1])) {
              throw new IllegalArgumentException("Disallowed characters present in file name.");
            }

            if(args[2].equals("-K")){

              Gradebook gradebook = null;
              if(allowedChars("abcdefABCDEF0123456789", args[3])){
                 gradebook = retrieveGradebook(args[1], args[3]);
              } else {
                throw new IllegalArgumentException("Disallowed characters in key.");
              }

              if(!gradebook.checkHashes()) {
                throw new Exception("Something has been tampered with.");
              }

              String[] flags = parse_remainder(args);
              switch(args[4]){

                case "-PA":
                if(flags[0] == null || gradebook.getAssignment(flags[0]) == null || !allowedChars("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", flags[0])) {
                  throw new IllegalArgumentException("Assignment name missing or invalid.");
                }

                print_Assignment(gradebook, flags);
                break;
                case "-PS":
                if(flags[1] == null || flags[2] == null || !allowedChars("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", flags[1]) || !allowedChars("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", flags[2])) {
                  throw new IllegalArgumentException("One of the names are either missing or invalid.");
                }

                print_Student(gradebook, flags[1], flags[2]);
                break;
                case "-PF":
                print_Final(gradebook, flags);
                break;
                default:
                throw new IllegalArgumentException("Invalid command.");
              }
            } else { throw new IllegalArgumentException("The third argument is not -K."); }
          } else { throw new IllegalArgumentException("The first argument is not -N."); }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(255);
    }

  }

  private static String [] parse_remainder(String [] args) {
    String [] ret = new String [5];

    for(int i = 5; i < args.length; i += 2) {
      switch (args[i]) {
        case "-AN":
        ret[0] = args[i+1];
        break;
        case "-FN":
        ret[1] = args[i+1];
        break;
        case "-LN":
        ret[2] = args[i+1];
        break;
        case "-G":
        ret[3] = "G";
        i = i-1; //Need to do this since -G is not followed by an arg
        break;
        case "-A":
        ret[4] = "A";
        i = i-1; //Need to do this since -A is not followed by an arg
        break;
        default:
        throw new IllegalArgumentException("Invalid flag found");
      }
    }

    if(ret[3] != null && ret[4] != null) {
      throw new IllegalArgumentException("Can't have both G and A flags simulatenously.");
    }

    return ret;
  }

  private static boolean allowedChars(String allowed, String check) {
    for(int i = 0; i < check.length(); i++) {
      if(!allowed.contains("" + check.charAt(i))) {
        return false;
      }
    }

    return true;
  }

  // compares by last name, and if the last names are the same it compares first name as well
  private class NameSorter implements Comparator<Student> {
      @Override
      public int compare(Student o1, Student o2) {
          if ( o2.getLast().compareToIgnoreCase(o1.getLast()) == 0) {
            return o2.getFirst().compareToIgnoreCase(o1.getFirst());
          } else {
            return o2.getLast().compareToIgnoreCase(o1.getLast());
          }
      }
  }

  private static Gradebook retrieveGradebook(String filename, String key) throws Exception {
    Cipher cipher = Cipher.getInstance("AES");
    byte[] keyBytes = new byte[16];
    for (int i=0;i < 32; i+=2){
      keyBytes[i/2] = (byte) ((Character.digit(key.charAt(i), 16) << 4) + (Character.digit(key.charAt(i+1), 16)));
    }
    SecretKey secret = new SecretKeySpec(keyBytes, "AES");
    cipher.init(Cipher.DECRYPT_MODE, secret);
    FileInputStream fileStream = new FileInputStream(filename);
    BufferedInputStream buffStream = new BufferedInputStream(fileStream);
    CipherInputStream ciphStream = new CipherInputStream(buffStream, cipher);
    ObjectInputStream objStream = new ObjectInputStream(ciphStream);
    SealedObject sealedBook = (SealedObject) objStream.readObject();
    Gradebook gradebook = (Gradebook) sealedBook.getObject(cipher);
    return gradebook;
  }
}
