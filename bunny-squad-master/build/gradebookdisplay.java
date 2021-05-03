import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Prints out a gradebook in a few ways
 * Some skeleton functions are included
 */
public class gradebookdisplay {
	static boolean verbose = false;

	private static byte[] hexStringToByteArray(String s) {
		byte[] b = new byte[s.length() / 2];
		for (int i = 0; i < b.length; i++) {
			int index = i * 2;
			int v = Integer.parseInt(s.substring(index, index + 2), 16);
			b[i] = (byte) v;
		}
		return b;
	}

	private static void print_Assignment(String gradebook, String assignment, String option, JSONObject parsed) {

		// alphabetical order
		if (option.equals("-A")) {

			JSONObject assignments = (JSONObject) parsed.get("assignments");
			JSONObject thisAssignment = (JSONObject) assignments.get(assignment);

			int totalPoints = Integer.parseInt(thisAssignment.get("points").toString());
			JSONObject students = (JSONObject) thisAssignment.get("students");

			// Sort students by name
			ArrayList<String> allStudents = new ArrayList<>();
			String[] splitName = new String[2];
			ArrayList<String> studentList = new ArrayList<>();
			for (Object key: students.keySet()) {
				studentList.add(key.toString());
				splitName = key.toString().split("_");
				double grade = Double.parseDouble(students.get(key).toString())/totalPoints * 100;
				allStudents.add(splitName[1] + " " + splitName[0] + " " + (int)grade);
			}
			Collections.sort(allStudents);

			// Printing (Last Name, First Name, Grade)
			for (String element: allStudents) {
				String[] splitStudent = element.split(" ");
				System.out.println("(" +splitStudent[0] + ", " + splitStudent[1] + ", " + splitStudent[2] + ")");
			}
		}
		// sort by grade highest to lowest
		else if (option.equals("-G")) {

			JSONObject assignments = (JSONObject) parsed.get("assignments");
			JSONObject thisAssignment = (JSONObject) assignments.get(assignment);

			int totalPoints = Integer.parseInt(thisAssignment.get("points").toString());
			JSONObject students = (JSONObject) thisAssignment.get("students");

			// collecting students
			ArrayList<String> allStudents = new ArrayList<>();
			String[] splitName = new String[2];
			ArrayList<String> studentList = new ArrayList<>();
			for (Object key: students.keySet()) {
				studentList.add(key.toString());
				splitName = key.toString().split("_");
				int grade = Integer.parseInt(students.get(key).toString());
				allStudents.add(splitName[1] + " " + splitName[0] + " " + grade);
			}

			// Sorting by grade
			Collections.sort(allStudents, new Comparator<String>() {
				public int compare(String s1, String s2) {
					String[] split1 = s1.split(" ");
					String[] split2 = s2.split(" ");

					if (Integer.parseInt(split1[2]) > Integer.parseInt(split2[2])) {
						return -1;
					}
					else if (Integer.parseInt(split1[2]) < Integer.parseInt(split2[2])) {
						return 1;
					}
					else {
						if (split1[0].compareTo(split2[0]) == 0) 
							return split1[1].compareTo(split2[1]);
						else 
							return split1[0].compareTo(split2[0]);
					}
				}
			});  

			// Printing (Last Name, First Name, Grade)
			for (String element: allStudents) {
				String[] splitStudent = element.split(" ");
				System.out.println("(" +splitStudent[0] + ", " + splitStudent[1] + ", " + splitStudent[2] + ")");
			}
		}
		else {
			System.exit(255);
		}

	}

	private static void print_Student(String gradebook, String first, String last, JSONObject parsed) {
		JSONObject allAssignments = (JSONObject) parsed.get("assignments");
		JSONArray studentProfiles = (JSONArray) parsed.get("student_profiles");
		// print out (Assignment, Grade) for student
		for (Object s: studentProfiles) {
			JSONObject student = (JSONObject) s;
			if (student.get("name").equals(first + "_" + last)) {
				JSONArray studentAssignments = (JSONArray) student.get("assignments");
				for (Object as: studentAssignments) {
					JSONObject a = (JSONObject) as;
					System.out.println("(" + a.get("name") + ", " + a.get("grade") + ")");
				}
			} 
		}
	}

	private static void print_Final(String gradebook, String option, JSONObject parsed){

		if (option.equals("-A")) {
			JSONObject allAssignments = (JSONObject) parsed.get("assignments");
			JSONArray studentProfiles = (JSONArray) parsed.get("student_profiles");

			// Sort students by name
			ArrayList<String> allStudents = new ArrayList<>();
			String[] splitName = new String[2];
			ArrayList<String> studentList = new ArrayList<>();

			for (Object k: studentProfiles) {
				JSONObject key = (JSONObject) k;
//				studentList.add(key.toString());   
				splitName = key.get("name").toString().split("_");

				// Access students' assignments 
//				JSONObject thisStudent = (JSONObject) studentProfiles.get(splitName[0] + " " + splitName[1]);
				JSONArray studentAssignments = (JSONArray) key.get("assignments");

				Double totalGrade = 0.0;

				for (Object name: studentAssignments) {
					JSONObject assignment = (JSONObject) name;
					int grade = Integer.parseInt(assignment.get("grade").toString());
					JSONObject a = (JSONObject) allAssignments.get(assignment.get("name").toString());
					Double totalPoints = Double.parseDouble(a.get("points").toString());
					Double score = (grade/totalPoints);
					Double weight = Double.parseDouble(a.get("weight").toString());
					totalGrade += score * weight;
				}
				allStudents.add(splitName[1] + " " + splitName[0] + " " + totalGrade);
			}

			Collections.sort(allStudents);

			// Printing output
			for (String element: allStudents) {
				String[] splitStudent = element.split(" ");
				System.out.println("(" +splitStudent[0] + ", " + splitStudent[1] + ", " + splitStudent[2] + ")");
			}

		} else if (option.equals("-G")) {

			JSONObject allAssignments = (JSONObject) parsed.get("assignments");
			JSONArray studentProfiles = (JSONArray) parsed.get("student_profiles");

			// Sort students by name
			ArrayList<String> allStudents = new ArrayList<>();
			String[] splitName = new String[2];
			ArrayList<String> studentList = new ArrayList<>();

			for (Object k: studentProfiles) {
				JSONObject key = (JSONObject) k;
				splitName = key.get("name").toString().split("_");
 
				// Access students' assignments
				JSONArray studentAssignments = (JSONArray) key.get("assignments");

				Double totalGrade = 0.0;  

				for (Object name: studentAssignments) {
					JSONObject assignment = (JSONObject) name;
					int grade = Integer.parseInt(assignment.get("grade").toString());
					JSONObject a = (JSONObject) allAssignments.get(assignment.get("name").toString());
					Double totalPoints = Double.parseDouble(a.get("points").toString());
					Double score = (grade/totalPoints);
					Double weight = Double.parseDouble(a.get("weight").toString());
					totalGrade += score * weight;
				}
				allStudents.add(splitName[1] + " " + splitName[0] + " " + totalGrade);
			}

			// Sorting by grade
			Collections.sort(allStudents, new Comparator<String>() {
				public int compare(String s1, String s2) {
					String[] split1 = s1.split(" ");
					String[] split2 = s2.split(" ");

					if (Double.parseDouble(split1[2]) > Double.parseDouble(split2[2])) {
						return -1;
					}
					else if (Double.parseDouble(split1[2]) < Double.parseDouble(split2[2])) {
						return 1;
					}
					else {
						if (split1[0].compareTo(split2[0]) == 0) 
							return split1[1].compareTo(split2[1]);
						else 
							return split1[0].compareTo(split2[0]);
					}
				} 
			});

			// Printing output
			for (String element: allStudents) {
				String[] splitStudent = element.split(" ");
				System.out.println("(" + splitStudent[0] + ", " + splitStudent[1] + ", " + splitStudent[2] + ")");
			}  

		}
		return;
	}

	private static boolean file_test(String filename) {
		File file = new File(filename);
		return file.exists();
	}  

	public static void main(String[] args) {
		if(args.length <= 4) {
			System.out.println("invalid");
			System.exit(255);
		}
		else if (!args[0].equals("-N") || !args[2].equals("-K")) {
			System.out.println("invalid");
			System.exit(255);
		}
		else if (!file_test(args[1])) {
			System.out.println("invalid");
			System.exit(255);
		}
		else {
			System.out.println("\nNumber Of Arguments Passed: " + args.length);
			System.out.println("----Following Are The Command Line Arguments Passed----");
			for(int counter = 0; counter < args.length; counter++) {
				System.out.println("args[" + counter + "]: " + args[counter]);
			}
			String gradebook_name = args[1];
			String key = args[3];

			// *************************DECODING**************************

			String fileName = gradebook_name;


			JSONObject gradebook_json = new JSONObject();
			byte[] buffer = new byte[16];
			InputStream is;
			try {
				is = new FileInputStream("./" + fileName);
				is.read(buffer);

				IvParameterSpec ivSpec = new IvParameterSpec(buffer);

				byte[] decode = hexStringToByteArray(key);

				SecretKey originalKey = new SecretKeySpec(decode, 0, decode.length, "AES");
				Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

				aesCipher.init(Cipher.DECRYPT_MODE, originalKey, ivSpec);

				CipherInputStream cipherInputStream = new CipherInputStream(new BufferedInputStream(is), aesCipher);
				ObjectInputStream ois = new ObjectInputStream(cipherInputStream);
				SealedObject sealed = (SealedObject) ois.readObject();

				Serializable unsealObject = (Serializable) sealed.getObject(aesCipher);
				gradebook_json = (JSONObject) unsealObject;

				// print grades of all student for an assignment
				if (args[4].equals("-PA")) {
					if (args.length == 8) {
						if (args[5].equals("-AN") && (args[7].equals("-A") || args[7].equals("-G"))) {
							String assignment = args[6]; 
							print_Assignment(gradebook_name, assignment, args[7], gradebook_json);
						}
						else if ((args[5].equals("-A") || args[5].equals("-G")) && args[6].equals("-AN")) {
							String assignment = args[7];
							print_Assignment(gradebook_name, assignment, args[5], gradebook_json);
						} 
					}
					else {
						System.out.println("invalid");
						System.exit(255);
					}

				} // print all grades for a student
				else if (args[4].equals("-PS")) {
					if (args.length == 9) {
						if (args[5].equals("-FN") && args[7].equals("-LN")) {
							print_Student(gradebook_name, args[6], args[8], gradebook_json);
						}
						else if (args[5].equals("-LN") && args[7].equals("-FN")) {
							print_Student(gradebook_name, args[8], args[6], gradebook_json);
						}
					}
					else {
						System.out.println("invalid");
						System.exit(255);
					}

				} // print final grades for all students
				else if (args[4].equals("-PF")) {
					if (args.length == 6) {
						// alphabetical by last name then first name
						if (args[5].equals("-A")) {
							print_Final(gradebook_name,"-A", gradebook_json);
						} 
						// grade order highest to lowest
						else if (args[5].equals("-G")) {
							print_Final(gradebook_name,"-G", gradebook_json);
						}
						else if (args.length >= 6) {
							System.exit(255);
						}
					}
					else {
						System.out.println("invalid");
						System.exit(255);
					}
				}
				else {
					System.out.println("invalid");
					System.exit(255);
				}
			} catch (Exception e) {
				System.out.println("invalid");
				e.printStackTrace();
				System.exit(255);
			}
		}
	}
}