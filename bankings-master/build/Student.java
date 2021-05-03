import java.io.Serializable;
public class Student implements Serializable, Comparable<Student>{
  private String firstName;
  private String lastName;

  //calculate grade at end

  public Student(String first, String last) {
    this.firstName = first;
    this.lastName = last;
  }

  public String getFirst() {
    return firstName;
  }

  public String getLast() {
    return lastName;
  }

  public boolean matchStudent(String fn, String ln){
    return fn.equals(firstName) && ln.equals(lastName);
  }

  public String toString(){
    return firstName + " " + lastName;
  }

  public int compareTo(Student student) {
      if ( student.getLast().compareToIgnoreCase(this.getLast()) == 0) {
        return this.getFirst().compareToIgnoreCase(student.getFirst());
      } else {
        return this.getLast().compareToIgnoreCase(student.getLast());
      }
  }
}
