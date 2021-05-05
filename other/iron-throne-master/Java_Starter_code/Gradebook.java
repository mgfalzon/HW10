import java.util.Hashtable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
//import ...

/**
 * A helper class for your gradebook
 * Some of these methods may be useful for your program
 * You can remove methods you do not need
 * If you do not wiish to use a Gradebook object, don't
 */
public class Gradebook {
	private String name;
	private int len;
	private Hashtable<String, Grades> students;
	private Hashtable<String, GradeKey> gradeKey;
	
	
  /* Read a Gradebook from a file */
  public Gradebook(String filename) {
	this.name = filename;  	  
    this.students = new Hashtable<String, Grades>();
    this.gradeKey = new Hashtable<String, GradeKey>();
    BufferedReader reader;
    try {
    	reader = new BufferedReader(new FileReader("./" + filename));
    	String line = reader.readLine();
    	line = reader.readLine(); //Get to first Student/grade key     	
    	while(line != null) {
    		if(line.equals("GradeKey")) {
    			line = reader.readLine();
    			
    			while(line != null && !line.equals("Student")) {
    				String[] parsedLine = line.split(",");
        			String[] nameLine = parsedLine[0].split(":");
        			String weight = parsedLine[1].split(":")[1];
        			GradeKey temp = new GradeKey(Double.parseDouble(weight), Integer.parseInt(nameLine[1]));
        			gradeKey.put(nameLine[0], temp);
        			line = reader.readLine();
    			}			
    		} else if(line.equals("Student")) {
    			line = reader.readLine(); //Next line to get student name
    			String curr_name = line;
    			//students.put(curr_name, null); //Put student name in
    			line = reader.readLine();
    			//Assignments will be read from text file
    			if(line.equals("Assignment")) {
    				line = reader.readLine();
    				Grades temp = new Grades();
    				
    				//Read line until another student is read
    				while(line != null && !line.equals("Student")) {
    					String[] parsed = line.split(":");
    					temp.addAssignment(parsed[0], Integer.parseInt(parsed[1])); //Add assignment to gradebook 
    					line = reader.readLine();
    				}
    				students.put(curr_name, temp); //Add grades to student 
    			} else {
    				line = reader.readLine();
    			}
    		} else {
    			line = reader.readLine();
    		}
    	}
    } catch(IOException e) {
    	System.out.print("Error Code 255");
    }
    
  }

  /* Create a new gradebook */
  public Gradebook() {
	  
  }

  /* return the size of the gradebook */
  public int size() {
    return len;
  }

  /* Adds a student to the gradebook */
  public void addStudent(String name) {
	  //No assignment added yet
	  if(!students.containsKey(name)) {
		  Grades temp = new Grades();
		  for(String x: gradeKey.keySet()) {
			  temp.addAssignment(x, 0);
		  }
		  students.put(name, temp);
		  this.len += 1;
	  } else {
		  //TODO fix error print here
		  System.out.println("ERROR, student already exists");
	  }
  }
  
  public void deleteStudent(String name) {
	  //No assignment added yet
	  if(!students.containsKey(name)) {
		  students.remove(name);
		  this.len -= 1;
	  } else {
		  //TODO fix error print here
		  System.out.println("ERROR, student does not exist");
	  }
  }

  /* Adds an assignment to the gradebook */
  public void addAssignment(String assignmentName) {
	  //Iterate through to add assignment to all students
	  for(String x: students.keySet()) {
		  Grades temp = students.get(x);
		  temp.addAssignment(assignmentName, 0);
		  students.put(x, temp);
	  }
  }
  
  public boolean deleteAssignment(String assignmentName) {
	  if(gradeKey.containsKey(assignmentName)) {
		  for(String x: students.keySet()) {
			  Grades temp = students.get(x);
			  temp.deleteAssignment(assignmentName);
			  students.put(x, temp);
		  }
		  gradeKey.remove(assignmentName);
		  return true;
	  }
	  return false;
  }
  
  public void updateGrade(String studentName, String assignmentName, int score) {
	  //Iterate through to add assignment to all students
	  if(!students.containsKey(studentName)) {
		  System.out.print("Error, cannot add grade to student that does not exist.");
	  }
	  
	  
	  Grades temp = students.get(studentName);
	  temp.addAssignment(assignmentName, score);
	  students.put(studentName, temp);
	  
  }
  
  public boolean addGradeKey(String name, int score, double weight) {
	  double totalWeight = weight;
	  
	  for(String x: gradeKey.keySet()) {
		  totalWeight += gradeKey.get(x).getWeight();
	  }
	  
	  
	  if(totalWeight > 1.0) {
		  return false;
	  } else {
		  gradeKey.put(name, new GradeKey(weight, score));
		  addAssignment(name); //Add assignment to all students 
		  return true;
	  }
  }

  /* Adds a grade to the gradebook */
  public void addGrade(String student, String assignmentName, int grade) {
	  Grades temp = students.get(student);
	  temp.addAssignment(assignmentName, grade);
	  students.put(student, temp);
  }

  public String toString() {
	String toReturn = "";
	
	for(String x: students.keySet()) {
		toReturn += x + "\n";
		toReturn += students.get(x).toString() + "\n";
	}
	
	
    return toReturn;
  }
  
  public void WriteToFile() {
	  try {
		  System.out.println("In Write to File");
		  FileWriter myWriter = new FileWriter(this.name);
		  //System.out.print(this.name);
		  myWriter.write(this.name + "\n");
		  for(String x: gradeKey.keySet()) {
			  myWriter.write(x + gradeKey.get(x).getGradeVal() + "\n");
			  System.out.print(x + gradeKey.get(x).getGradeVal() + "\n");
		  }
		  
		  for(String x: students.keySet()) {
			  myWriter.write("Student\n");
			  myWriter.write(x + "\n");
			  myWriter.write("Assignment\n");
			  myWriter.write(students.get(x).getAssignmentVal());
			  System.out.print("Student\n"+ x + "\nAssignment\n" + students.get(x).getAssignmentVal());
		  }
		 
		  myWriter.close();
	  } catch(IOException e) {
		  System.out.print("Error writing to file");
	  } 
  }
}






