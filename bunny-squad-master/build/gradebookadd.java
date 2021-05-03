//import ...

/**
 * Allows the user to add a new student or assignment to a gradebook,
 * or add a grade for an existing student and existing assignment
 */

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class gradebookadd {

	private static boolean checkIfExists(JSONArray arr, String name) {
		for (Object a : arr) {
			JSONObject student = (JSONObject) a;
			if (student.get("name").equals(name)) {
				return true;
			}
		}

		return false;
	}

	private static boolean checkIfWeightsGTOne(JSONObject assignmentsList, Double weight) {
		Double totalWeight = 0.0;
		for (Object a : assignmentsList.keySet()) {
			String assignmentName = (String) a;
			totalWeight = Double.sum(totalWeight,
					(Double) ((JSONObject) assignmentsList.get(assignmentName)).get("weight"));
		}

		if ((totalWeight + weight) > 1.0) {
			return true;
		}

		return false;
	}

	private static byte[] hexStringToByteArray(String s) {
		byte[] b = new byte[s.length() / 2];
		for (int i = 0; i < b.length; i++) {
			int index = i * 2;
			int v = Integer.parseInt(s.substring(index, index + 2), 16);
			b[i] = (byte) v;
		}
		return b;
	}

	public static Object add_assignment(String args[], JSONObject gradebook_json) throws IOException {

		String assignment_name = "";
		JSONObject assignments = new JSONObject();
		JSONArray student_profiles = new JSONArray();
		JSONObject students = new JSONObject();
		JSONObject big_assignments = new JSONObject();
		HashMap<String, Integer> addgrade = new HashMap<>();

		addgrade.put("-AN", 0);
		addgrade.put("-P", 0);
		addgrade.put("-W", 0);
		int point = 0;
		double weight = 0.0;
		// For loop across arguments
		// If there are duplicates take the most recent one

		for (int i = 5; i < args.length - 1; i += 2) {
			// make sure arguments aren't null
			if (args[i] != null || args[i + 1] != null) {
				// check if FN/LN/AN/G or if that argument already happened
				if (addgrade.containsKey(args[i]) && addgrade.get(args[i]) == 0) {
					// update that it went through that argument
					addgrade.put(args[i], 1);
					if (args[i].equals("-P")) {
						try {
							point = Integer.parseInt(args[i + 1]);
						} catch (NumberFormatException nfe) {
							System.out.println("invalid");
							System.exit(255);
						}
					}
					if (args[i].equals("-W")) {
						try {
							weight = Double.parseDouble(args[i + 1]);
						} catch (NumberFormatException nfe) {
							System.out.println("invalid");
							System.exit(255);
						}
					}
					if (args[i].equals("-AN")) {
						if (args[i + 1].matches("^[a-zA-Z0-9]+$")) {
							assignment_name = args[i + 1];
						} else {
							System.out.println("invalid");
							System.exit(255);
						}
					}
				} else {
					System.out.println("invalid");
					System.exit(255);

				}
			}

		}

		// EXIT FOR LOOP CHECK IF EACH KEYS EQUALS ONE
		if (addgrade.get("-AN") == 1 && addgrade.get("-P") == 1 && addgrade.get("-W") == 1) {
			// If all keys equal one then we add add assignment

			// Get the assignment from the JSON
			JSONParser jsonParser = new JSONParser();

			big_assignments = (JSONObject) gradebook_json.get("assignments");
			student_profiles = (JSONArray) gradebook_json.get("student_profiles");

			if (checkIfWeightsGTOne(big_assignments, weight)) {
				System.out.println("invalid");
				System.exit(255);
			}

			// CHecks if assignment is empty or checks if doesn't exist
			if (assignments.isEmpty() || assignments.containsKey(assignment_name) == false) {
				// If empty
				if (assignments.isEmpty()) {
					assignments = new JSONObject();
					assignments.put("points", point);
					assignments.put("weight", weight);
					assignments.put("students", new JSONObject());
				}
//					System.out.println(assignments + " :assignmments2");
//					System.out.println(student_profiles + " :student_profiels");

				// check if students profiles is empty
				// Not need to go through
				if (student_profiles.isEmpty()) {
					// skip not students exist
					// Just add assignment in
					big_assignments.put(assignment_name, assignments);
					gradebook_json.put("assignments", big_assignments);
					JSONArray assignOrder = (JSONArray) gradebook_json.get("assignment_order");
					assignOrder.add(assignment_name);
					gradebook_json.put("assignment_order", assignOrder);

				} else {
					// for each loop across each student profiles
					// add each student into "Assignments"
					// Then add each assignment to that respetice student

					// Each object is a students

					students = (JSONObject) assignments.get("students");
					for (int i = 0; i < student_profiles.size(); i++) {

						// Initialize students if not already initialized
						if (students == null) {
							students = new JSONObject();
						}

						// Access the current student
						JSONObject currentStudentProfile = (JSONObject) student_profiles.get(i);

						// Add the assignment, initialized to 0, into their student profile
						JSONArray currentStudentProfileAssignments = (JSONArray) currentStudentProfile
								.get("assignments");
						JSONObject newAssignmentEntry = new JSONObject();
						newAssignmentEntry.put("name", assignment_name);
						newAssignmentEntry.put("grade", 0);
						currentStudentProfileAssignments.add(newAssignmentEntry);
						currentStudentProfile.put("assignments", currentStudentProfileAssignments);
						student_profiles.set(i, currentStudentProfile);
						gradebook_json.put("student_profiles", student_profiles);

						// Add the student into assignments -> students with a grade initialized to 0
						String studentName = (String) currentStudentProfile.get("name");
						students.put(studentName, 0);
					}

					JSONObject temp = new JSONObject();
					temp.put("points", point);
					temp.put("weight", weight);
					temp.put("students", students);
					big_assignments.put(assignment_name, temp);

					// Adding to order
					JSONArray assignOrder = (JSONArray) gradebook_json.get("assignment_order");
					assignOrder.add(assignment_name);
					gradebook_json.put("assignment_order", assignOrder);
					gradebook_json.put("assignments", big_assignments);

				}

			} else {
				System.out.println("invalid");
				System.exit(255);
			}

		} else {
			System.out.println("invalid");
			System.exit(255);
		}

		return gradebook_json;
	}

	public static Object delete_assignment(String args[], JSONObject gradebook_json) throws IOException {

		String assignment_name = "";
		JSONObject assignments = new JSONObject();
		JSONArray student_profiles = new JSONArray();
		HashMap<String, Integer> addgrade = new HashMap<>();

		addgrade.put("-AN", 0);

//		for (int i = 5; i < args.length - 1; i += 2) {
//			// check if FN/LN/
//			if (addgrade.containsKey(args[i])) {
//				// update that it went throguh that argument
//				addgrade.put(args[i], 1);
//				if (args[i].equals("-AN")) {
//					assignment_name = args[i + 1];
//				}
//			} else {
//				System.out.println("invalid");
//				System.exit(255);
//
//			}
//		}

		for (int i = 5; i < args.length; i += 2) {
			// check if FN/LN/
			if (addgrade.containsKey(args[i])) {
				// update that it went throguh that argument
				addgrade.put(args[i], 1);
				if (args[i].equals("-AN")) {
					if (args[i + 1].matches("^[a-zA-Z0-9]+$")) {
						assignment_name = args[i + 1];

					} else {
						System.out.println("invalid");
						System.exit(255);
					}
				} else {
					System.out.println("invalid");
					System.exit(255);
				}

			}
		}

		// Delete

		// Get the assignment from the JSON
		if (addgrade.get("-AN") == 1) {
			JSONParser jsonParser = new JSONParser();

			assignments = (JSONObject) gradebook_json.get("assignments");
			student_profiles = (JSONArray) gradebook_json.get("student_profiles");

			// CHecks if assignment is empty and checks if doesn't exist
			if (assignments == null || assignments.containsKey(assignment_name) == false) {
				// not there
				System.out.println("invalid");
				System.exit(255);

			} else {
				// delete assignment it exists
				assignments.remove(assignment_name);
				JSONArray assignOrder = (JSONArray) gradebook_json.get("assignment_order");
				// check student profiles and remove the assignments
				// TO DO
				if (!(student_profiles.isEmpty())) {
					// go through students and delete
					for (int i = 0; i < student_profiles.size(); i++) {
						JSONObject studentEntry = (JSONObject) student_profiles.get(i);
						JSONArray student_assignments = (JSONArray) studentEntry.get("assignments");
						for (int j = 0; j < student_assignments.size(); j++) {
							JSONObject individualAssignment = (JSONObject) student_assignments.get(j);
							if (individualAssignment.get("name").equals(assignment_name)) {
								student_assignments.remove(j);
							}
						}

						for (int j = 0; j < assignOrder.size(); j++) {
							if (assignOrder.get(j).equals(assignment_name)) {
								assignOrder.remove(j);
							}
						}

						studentEntry.put("assignments", student_assignments);
						student_profiles.set(i, studentEntry);

					}

				}
				gradebook_json.put("assignments", assignments);
				gradebook_json.put("assignment_order", assignOrder);
				gradebook_json.put("student_profiles", student_profiles);

			}
		}else {
			System.out.print("invalid");
			System.exit(255);
		}

		return gradebook_json;

	}

	public static Object add_student(String args[], JSONObject gradebook_json) {

		String First = "";
		String Last = "";
		JSONObject studentEntry = new JSONObject();
		HashMap<String, Integer> addgrade = new HashMap<>();
		addgrade.put("-FN", 0);
		addgrade.put("-LN", 0);

		for (int i = 5; i < args.length; i += 2) {

			// check if FN/LN/
			if (addgrade.containsKey(args[i])) {
				// update that it went throguh that argument
				addgrade.put(args[i], 1);
				if (args[i].equals("-FN")) {
					if (args[i + 1].matches("^[a-zA-Z]+$")) {
						First = args[i + 1];
					} else {
						System.out.println("invalid");
						System.exit(255);
					}

				}
				if (args[i].equals("-LN")) {
					if (args[i + 1].matches("^[a-zA-Z]+$")) {
						Last = args[i + 1];
					} else {
						System.out.println("invalid");
						System.exit(255);
					}
				}
			} else {
				System.out.println("invalid");
				System.exit(255);
			}

		}

		if (addgrade.get("-FN") == 1 && addgrade.get("-LN") == 1) {
			// Get the assignment from the JSON
			JSONParser jsonParser = new JSONParser();

			JSONArray students = (JSONArray) gradebook_json.get("student_profiles");
			String formattedName = First + "_" + Last;

			if (checkIfExists(students, formattedName)) {
				System.out.println("invalid");
				System.exit(255);
			} else {
				// just add more students in
				studentEntry.put("name", formattedName);

				// Get the list of all assignments
				JSONArray assignOrder = (JSONArray) gradebook_json.get("assignment_order");
				JSONObject assignments = (JSONObject) gradebook_json.get("assignments");
				JSONArray assignmentList = new JSONArray();

				for (int i = 0; i < assignOrder.size(); i++) {
					String assignmentName = (String) assignOrder.get(i);
					JSONObject individualAssignment = new JSONObject();
					individualAssignment.put("name", assignmentName);
					individualAssignment.put("grade", 0);
					assignmentList.add(individualAssignment);
					JSONObject a = (JSONObject) assignments.get(assignmentName);
					JSONObject studentList = (JSONObject) a.get("students");
					studentList.put(formattedName, 0);
					a.put("students", studentList);
					assignments.put(assignmentName, a);
				}

				// Putting updated assignments in
				gradebook_json.put("assignments", assignments);

				// Putting updated studentProfile in
				studentEntry.put("assignments", assignmentList);
				students.add(studentEntry);
				gradebook_json.put("student_profiles", students);

			}
		}else {
			System.out.println("invalid");
			System.exit(255);
		}
		return gradebook_json;
	}

	public static Object delete_student(String args[], JSONObject gradebook_json) {

		String First = "";
		String Last = "";
		JSONObject student = new JSONObject();
		HashMap<String, Integer> addgrade = new HashMap<>();
		addgrade.put("-FN", 0);
		addgrade.put("-LN", 0);

		for (int i = 5; i < args.length; i += 2) {
			// check if FN/LN/
			if (addgrade.containsKey(args[i])) {
				// update that it went throguh that argument
				addgrade.put(args[i], 1);
				if (args[i].equals("-FN")) {
					if (args[i + 1].matches("^[a-zA-Z]+$")) {
						First = args[i + 1];
					} else {
						System.out.println("invalid");
						System.exit(255);
					}

				}
				if (args[i].equals("-LN")) {
					if (args[i + 1].matches("^[a-zA-Z]+$")) {
						Last = args[i + 1];
					} else {
						System.out.println("invalid");
						System.exit(255);
					}
				}

			} else {
				System.out.println("invalid");
				System.exit(255);

			}

		}

		if (addgrade.get("-FN") == 1 && addgrade.get("-LN") == 1) {
			// Go through student profiles
			JSONArray student_profiles = new JSONArray();
			student_profiles = (JSONArray) gradebook_json.get("student_profiles");
			for (int i = 0; i < student_profiles.size(); i++) {
				student = (JSONObject) student_profiles.get(i);
				if (student.get("name").equals(First + "_" + Last)) {
					student_profiles.remove(i);

				}
			}

			gradebook_json.put("student_profiles", student_profiles);

			// Go through the Assignments.
			JSONObject outside_assignments = new JSONObject();
			JSONObject assignments = new JSONObject();

			outside_assignments = (JSONObject) gradebook_json.get("assignments");
			for (Object o : outside_assignments.keySet()) {
				assignments = (JSONObject) outside_assignments.get((String) o);
				student = (JSONObject) assignments.get("students");
				student.remove(First + "_" + Last);

			}
		} else {
			System.out.println("invalid");
			System.exit(255);
		}

		return gradebook_json;

	}

	public static Object add_grade(String args[], JSONObject gradebook_json) {
		String First = "";
		String Last = "";
		String assignment_name = "";
		int grade = 0;
		HashMap<String, Integer> addgrade = new HashMap<>();

		addgrade = new HashMap<>();
		addgrade.put("-FN", 0);
		addgrade.put("-LN", 0);
		addgrade.put("-AN", 0);
		addgrade.put("-G", 0);

//			String assignment_name
		// check if enough arguments
		// for loop across arguments
		for (int i = 5; i < args.length; i += 2) {
			// check if FN/LN/AN/G or if that argument already happened
			if (addgrade.containsKey(args[i]) && addgrade.get(args[i]) == 0) {
				// update that it went throguh that argument
				addgrade.put(args[i], 1);
				if (args[i].equals("-FN")) {
					if (args[i + 1].matches("^[a-zA-Z]+$")) {
						First = args[i + 1];
					} else {
						System.out.println("invalid");
						System.exit(255);
					}
				}
				if (args[i].equals("-LN")) {
					if (args[i + 1].matches("^[a-zA-Z]+$")) {
						Last = args[i + 1];
					} else {
						System.out.println("invalid");
						System.exit(255);
					}
				}
				if (args[i].equals("-G")) {
					try {
						grade = Integer.parseInt(args[i + 1]);
					} catch (NumberFormatException nfe) {
						// throw error
					}
				}
				if (args[i].equals("-AN")) {
					if (args[i].equals("-AN")) {
						if (args[i + 1].matches("^[a-zA-Z0-9]+$")) {
							assignment_name = args[i + 1];
						} else {
							System.out.println("invalid");
							System.exit(255);
						}
					} else {
						System.out.println("invalid");
						System.exit(255);
					}
				}
			} else {
				System.out.println("invalid");
				System.exit(255);
			}

		}
		// Exited Make sure each argument showed up once
		if (addgrade.get("-FN") == 1 && addgrade.get("-LN") == 1 && addgrade.get("-G") == 1
				&& addgrade.get("-AN") == 1) {

			String fullName = First + "_" + Last;

			JSONObject assignments = (JSONObject) gradebook_json.get("assignments");
			JSONArray studentProfiles = (JSONArray) gradebook_json.get("student_profiles");

			if (assignments.isEmpty()) {
				System.out.println("invalid");
				System.exit(255);
			}

			// Updating the student's grade in assignment
			if (assignments.get(assignment_name) != null) {

				JSONObject assignment = (JSONObject) assignments.get(assignment_name);
				JSONObject studentList = (JSONObject) assignment.get("students");

				if (studentList.get(fullName) != null) {
					studentList.put(fullName, grade);
					assignment.put("students", studentList);
					assignments.put(assignment_name, assignment);
					gradebook_json.put("assignments", assignments);
				} else {
					System.out.print("invalid");
					System.exit(255);
				}

				for (int i = 0; i < studentProfiles.size(); i++) {

					// Access the current student
					JSONObject currentStudentProfile = (JSONObject) studentProfiles.get(i);

					// Check for the name we are looking for
					if (currentStudentProfile.get("name").equals(fullName)) {
						JSONArray studentAssignments = (JSONArray) currentStudentProfile.get("assignments");

						for (int j = 0; j < studentAssignments.size(); j++) {
							JSONObject studentAssignment = (JSONObject) studentAssignments.get(j);
							if ((studentAssignment.get("name")).equals(assignment_name)) {
								studentAssignment.put("grade", grade);
								studentAssignments.set(j, studentAssignment);
							}
						}
						currentStudentProfile.put("assignments", studentAssignments);
						studentProfiles.set(i, currentStudentProfile);
						gradebook_json.put("student_profiles", studentProfiles);
					}
				}
			}
		}else {
			System.out.print("invalid");
			System.exit(255);
			
		}

		return gradebook_json;
	}

	public static void main(String[] args)
			throws FileNotFoundException, IOException, ParseException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException {

		String gradebook_name = "";
		String key = "";
		JSONObject new_gradebook = new JSONObject();

		if (args[0].equals("-N")) {
			// Check if name is good
			gradebook_name = args[1];
			// Check Keys
			if (args[2].equals("-K")) {
				key = args[3];
				String fileName = gradebook_name;

				Object gradebook = new JSONObject();
				JSONObject gradebook_json = (JSONObject) gradebook;

				// *************************DECODING**************************

				byte[] buffer = new byte[16];
				InputStream is = new FileInputStream("./" + fileName);
				is.read(buffer);

				IvParameterSpec ivSpec = new IvParameterSpec(buffer);

				byte[] decode = hexStringToByteArray(key);

				SecretKey originalKey = new SecretKeySpec(decode, 0, decode.length, "AES");
				Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				try {
					aesCipher.init(Cipher.DECRYPT_MODE, originalKey, ivSpec);
				} catch (Exception e) {
					System.out.println("invalid");
					System.exit(255);
				}

				CipherInputStream cipherInputStream = new CipherInputStream(new BufferedInputStream(is), aesCipher);
				ObjectInputStream ois = new ObjectInputStream(cipherInputStream);
				SealedObject sealed = (SealedObject) ois.readObject();

				Serializable unsealObject = (Serializable) sealed.getObject(aesCipher);
				gradebook_json = (JSONObject) unsealObject;

				// *****************************************************************

				if (args[4].equals("-AA")) {
					// Add assignment needs Name, Points, Weight
					new_gradebook = (JSONObject) add_assignment(args, gradebook_json);

				} else if (args[4].equals("-DA")) {
					// Add assignment needs Name, Points, Weight
					new_gradebook = (JSONObject) delete_assignment(args, gradebook_json);

					// Add Student
				} else if (args[4].equals("-AS")) {
					new_gradebook = (JSONObject) add_student(args, gradebook_json);

					// Delete Student
				} else if (args[4].equals("-DS")) {
					new_gradebook = (JSONObject) delete_student(args, gradebook_json);

				} else if (args[4].equals("-AG")) {
					new_gradebook = (JSONObject) add_grade(args, gradebook_json);

				} else {
					System.out.println("invalid");
					System.exit(255);
				}
			}
		} else {
			System.out.println("invalid");
			System.exit(255);
		}

		// Generate IV
		SecureRandom sRandom = new SecureRandom();
		byte[] iv = new byte[128 / 8];
		sRandom.nextBytes(iv);
		IvParameterSpec ivspec = new IvParameterSpec(iv);

		byte[] decode = hexStringToByteArray(key);
		SecretKey rencrypt_key = new SecretKeySpec(decode, 0, decode.length, "AES");

		// ENCODING:
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		try {
			cipher.init(Cipher.ENCRYPT_MODE, rencrypt_key, ivspec);
		} catch (Exception e) {
			System.out.println("invalid");
			System.exit(255);
		}

		SealedObject sealed_gradebook = new SealedObject(new_gradebook, cipher);

		FileOutputStream file_output = new FileOutputStream("./" + gradebook_name);
		file_output.write(iv);
		CipherOutputStream cipherOutputStream = new CipherOutputStream(new BufferedOutputStream(file_output), cipher);
		ObjectOutputStream outputStream = new ObjectOutputStream(cipherOutputStream);
		outputStream.writeObject(sealed_gradebook);
		outputStream.flush();
		outputStream.close();

		return;
	}
}
