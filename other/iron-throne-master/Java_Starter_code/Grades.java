import java.util.Hashtable;

public class Grades {	
	private Hashtable<String, Integer> grades;
	private int len;
	
	public Grades() {
		grades = new Hashtable<String, Integer>();
	}
	
	public Grades(String assignmentName, int grade) {
		grades = new Hashtable<String, Integer>();
		grades.put(assignmentName, grade);
	}
	
	public void addAssignment(String assignmentName, int grade) {
		grades.put(assignmentName, grade); //-1 if no grade is put in 
		len += 1;
	}
	
	public void deleteAssignment(String assignmentName) {
		grades.remove(assignmentName); //-1 if no grade is put in 
		len -= 1;
	}
	
	public String toString() {
		String toReturn = "";
		
		for(String x: grades.keySet()) {
			toReturn += "\t" + x + " " + grades.get(x);
			
		}
		
		return toReturn;
	}
	
	public String getAssignmentVal() {
		String toReturn = "";
		for(String x: grades.keySet()) {
			toReturn += x + ":" + grades.get(x) + "\n";
			
		}
		return toReturn;
	}
}
