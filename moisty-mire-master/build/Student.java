package gradingtools;
import java.io.*;

/**
 * Student represents a tuple (First Name, Last Name)
 */
public class Student implements Comparable<Student>, java.io.Serializable{
    final String firstName;
    final String lastName;
  
    public Student(String firstName, String lastName){
      this.firstName = firstName;
      this.lastName = lastName; 
    }
  
    
    public boolean equals(String first, String last){
      return firstName.equals(first) && lastName.equals(last);
    }
  
    // Overriding equals method
    @Override
    public boolean equals(Object o){
      if(o == this) {
        return true;
      }
  
      if(!(o instanceof Student)){
        return false;
      }
  
      Student g = (Student) o;
  
      return equals(g.firstName, g.lastName);
    }
  
    public int compareTo(Student s){
      int firstComp = firstName.compareTo(s.firstName);
      if(firstComp == 0){
        return lastName.compareTo(s.lastName);
      }
      return firstComp;
    }


    //Might have to override hashcode for hashmap usage
    @Override
    public int hashCode(){
      return (firstName + " " + lastName).hashCode();
    }

    public String toString(){
        return (firstName + " " + lastName);
    }

    public String getFirstName(){
      return firstName;
    }

    public String getLastName(){
      return lastName;
    }

    public String getLF(){
      return lastName + ", " + firstName; 
    }
  }