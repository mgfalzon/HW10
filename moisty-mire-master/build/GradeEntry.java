package gradingtools;
import java.io.*;

/**
 * Student represents a tuple (First Name, Last Name)
 */
public class GradeEntry<T> implements java.io.Serializable{
    Student student;
    T studentPoints;
  
    public GradeEntry(Student s, T points){
        this.student = s;
        this.studentPoints = points;
    }
    
    public String toString(){
        return "(" + student.getLF() + ": " + studentPoints +")"; 
    }

    public Student getStudent(){
        return student;
    }

    public T getStudentPoints(){
        return studentPoints;
    }

    public void setStudentPoints(T x){
        studentPoints = x;
    }

  }