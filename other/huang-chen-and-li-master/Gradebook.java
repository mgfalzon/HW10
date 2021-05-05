
import java.io.*;
import java.util.*;
import javax.crypto.*;
import java.security.*;
import javax.crypto.spec.*;

/**
 * A helper class for your gradebook
 * Some of these methods may be useful for your program
 * You can remove methods you do not need
 * If you do not wiish to use a Gradebook object, don't
 */
public class Gradebook implements Serializable {
    ArrayList<Student> students;
    ArrayList<Assignment> assignments;

    /* Read a Gradebook from a file */
    public Gradebook(String fileName, String key) throws IOException, GeneralSecurityException, ClassNotFoundException{
      FileInputStream fileIn = null;
      CipherInputStream cipherIn = null;
      ObjectInputStream objectIn = null;
      try {
          // decode the base64 encoded string
        byte[] decodedKey = Base64.getDecoder().decode(key);
          // rebuild key using SecretKeySpec
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
        fileIn = new FileInputStream(fileName);
        final Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE, originalKey);
        cipherIn = new CipherInputStream(fileIn, c); 
        objectIn = new ObjectInputStream(cipherIn);
        Gradebook gb = (Gradebook) objectIn.readObject();
        this.students = gb.students;
        this.assignments = gb.assignments;
        objectIn.close();
      } catch (Exception e) {
        if(fileIn != null)
            fileIn.close();
        if(cipherIn != null)
            cipherIn.close();
        if(objectIn != null)
            objectIn.close();
        throw e;
      }
    }

    /* Create a new gradebook */
    public Gradebook() {
        students = new ArrayList<>();
        assignments = new ArrayList<>();
    }
    
    public void save(String filename, String key) throws IOException, GeneralSecurityException{
        FileOutputStream fileOut = null;
        CipherOutputStream cipherOut = null;
        ObjectOutputStream objectOut = null;
        try {
            // decode the base64 encoded string
            byte[] decodedKey = Base64.getDecoder().decode(key);
            // rebuild key using SecretKeySpec
            SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
            fileOut = new FileOutputStream(filename);
            final Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, secretKey);
            cipherOut = new CipherOutputStream(fileOut, c);
            objectOut = new ObjectOutputStream(cipherOut);
            objectOut.writeObject(this);
            objectOut.close();
            cipherOut.close();
            fileOut.close();
        }
        catch (Exception e) {
            if(fileOut != null)
                fileOut.close();
            if(cipherOut != null)
                cipherOut.close();
            if(objectOut != null)
                objectOut.close(); 
            throw e;
        }
    }


    /* Adds a student to the gradebook */
    public void addStudent(String firstname, String lastname) {
      int index = students.indexOf(new Student(firstname, lastname));
        if (index != -1){
          err();
        } else {
          students.add(new Student(firstname, lastname));
        }
    }

    /* Deletes a student to the gradebook */
    public void deleteStudent(String firstname, String lastname) {
      int index = students.indexOf(new Student(firstname, lastname));
        if (index == -1){
            err();
        } else {
            students.remove(students.get(index));
        }
    }

    /* Adds an assignment to the gradebook */
    public void addAssignment(String name, int point, float weight) {
        Assignment a = new Assignment(name, point, weight);
        if(assignments.contains(a)){
            err();
        }
        assignments.add(a);
    }

    /* Deletes an assinment to the gradebook */
    public void deleteAssignment(String name) {
        Assignment a = new Assignment(name, 0, 0);
        if(!assignments.remove(a)){
            err();
        } 
    }

    /* Adds a grade to the gradebook */
    public void addGrade(String firstname, String lastname, String assignment, int grade) {
        int index = students.indexOf(new Student(firstname, lastname));
        if (index == -1){
            err();
        } else {
            students.get(index).grades.put(assignment, grade);
        }
    }

    void printAssignment(String assignment_name, String order) {
        if(!assignments.contains(new Assignment(assignment_name, 0, 0))){
            err();
        }

        if (order.equals("a")){
            Collections.sort(students, new Comparator<Gradebook.Student>() {
                @Override
                public int compare(Gradebook.Student o1, Gradebook.Student o2) {
                    return o1.lastname.compareTo(o2.lastname);
                }
            });
        } else {
            Collections.sort(students, new Comparator<Gradebook.Student>() {
                @Override
                public int compare(Gradebook.Student o1, Gradebook.Student o2) {
                    return o2.grades.get(assignment_name) - o1.grades.get(assignment_name);
                }
            });
        }
        for (Gradebook.Student s : students){
            System.out.println("("+s.lastname+", " + s.firstname+", " + s.grades.get(assignment_name)+")");
        }
    }

    void printStudent(String firstname, String lastname) {
        int index = students.indexOf(new Gradebook.Student(firstname,lastname));
        if (index != -1){
            for (Assignment i: assignments){
                System.out.println("(" + i.name + ", " + students.get(index).grades.get(i.name) + ")");
            }
        } else {
            err();
        }
    }

    void printFinal(String order) {
        if (order.equals("a")){
            Collections.sort(students, new Comparator<Gradebook.Student>() {
                @Override
                public int compare(Gradebook.Student o1, Gradebook.Student o2) {
                    return o1.lastname.compareTo(o2.lastname);
                }
            });
        } else {
            Collections.sort(students, new Comparator<Gradebook.Student>() {
                @Override
                public int compare(Gradebook.Student o1, Gradebook.Student o2) {
                    if(o1.getFinal(assignments)>o2.getFinal(assignments)){
                        return -1;
                    } else if(o1.getFinal(assignments)<o2.getFinal(assignments)) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
        }

        for (Gradebook.Student s : students){
            System.out.println("("+s.lastname+", " + s.firstname+", " + s.getFinal(assignments)+")");
        }
    }

    static class Assignment implements Serializable {
        String name;
        int point;
        float weight;
        public Assignment(String n, int p, float w){
            name = n;
            point = p;
            weight = w;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Assignment assignment = (Assignment) o;
            return Objects.equals(name, assignment.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(point, weight);
        }
    }

    static class Student implements Comparable<Student>, Serializable {
        String firstname;
        String lastname;

        HashMap<String, Integer> grades;

        public Student(String fn,String ln){
            firstname = fn;
            lastname = ln;
            grades = new HashMap<>();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Student student = (Student) o;
            return Objects.equals(firstname, student.firstname) && Objects.equals(lastname, student.lastname);
        }

        @Override
        public int hashCode() {
            return Objects.hash(firstname, lastname);
        }

        public int compareTo(Student o) {
            if(firstname.equals(o.firstname) && lastname.equals(o.lastname)){
                return 0;
            } else {
                return 1;
            }
        }

        public float getFinal(ArrayList<Assignment> assignments){
            float fgrade = 0;
            for (Assignment i : assignments){
                if(grades.containsKey(i.name)){
                    fgrade += ((float)grades.get(i.name)/i.point)*i.weight;

                } else {

                }
            }
            return fgrade;
        }
    }

    static void err(){
        System.out.println("invalid");
        System.exit(255);
    }
}
