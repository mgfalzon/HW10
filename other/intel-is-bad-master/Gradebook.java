import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * A helper class for your gradebook
 * Some of these methods may be useful for your program
 * You can remove methods you do not need
 * If you do not wiish to use a Gradebook object, don't
 */

 // NOTE: KEY GENERATION MAY TAKE A LONG TIME, TYPICALLY 3 - 5 minutes
public class Gradebook {
	
	private static final String CIPHER_ALGO = "AES/GCM/NoPadding"; //The algorithm used for encryption/decryption
	private static final String KEY_ALGO = "AES";				   //Algorithm for generating the key
	
	private static final int IV_LENGTH = 12;
	private static final int TAG_LENGTH = 16;
	
	private HashMap<String, Student> studentMap;
	private LinkedHashMap<String, Assignment> assignMap;

	/* Read a Gradebook from a file */
	public Gradebook(String filename, String key) throws Exception {
		studentMap = new HashMap<String, Student>();
  		assignMap = new LinkedHashMap<String, Assignment>();
  		
		//Decode the key from the given string
		SecretKey skey = this.recodeKey(key);
		
	  	//create a gradebook object from the given file
		loadGradebook(filename, skey);
  	}

  	/* Create a new gradebook */
  	public Gradebook(String filename) throws Exception {
  		studentMap = new HashMap<String, Student>();
  		assignMap = new LinkedHashMap<String, Assignment>();
  		
  		SecretKey key = this.generateKey(); //Create a new secure random key
  		
  		saveGradebook(filename, key); //Save the gradebook
  		byte[] sKey = key.getEncoded();
  		System.out.println("The key is: " + bytesToHexString(sKey));
  	}
  	
  	
  	/* return the size of the gradebook */
  	public int size() {
  		return studentMap.size(); //Just students? Or combination of both?
  	}

  	/* Adds a student to the gradebook */
  	public void addStudent(String firstName, String lastName) throws IllegalArgumentException {
  		//Check if the student exists already. If so, throw an error
  		
  		//Note: A space is placed between the two names to delineate the names.
  		//      Because, technically, these two names are different and valid, if not realisitic:
  		//                   John Smith          -       JohnS mith
  		//      having no delineate would then have the above two names be considered the same name.
  		String hashkey = firstName + " " + lastName; //Get what will be the key to the hashmap for the given student
  		
  		if (studentMap.containsKey(hashkey)) {
  			throw new IllegalArgumentException();
  		}

  		//Otherwise, the student is valid, so add it to the gradebook
  		studentMap.put(hashkey, new Student(firstName, lastName));
  	}

  	/* Adds an assignment to the gradebook */
  	public void addAssignment(String assignName, int points, double weight) throws IllegalArgumentException {
  		
  		//Check if the assignment already exists. If so, throw an error
  		if (assignMap.containsKey(assignName)) {
  			throw new IllegalArgumentException();
  		}
  		
  		//Check if the weights are valid when adding the new assignment ( total =< 1). If not, throw an error.
  		if (!isValidWeight(weight)) {
  			throw new IllegalArgumentException();
  		}
  		
  		//Otherwise, the assignment is valid, so add it to the gradebook
  		assignMap.put(assignName, new Assignment(assignName, points, weight));
  	}

  	/* Adds a grade to the gradebook */
  	public void addGrade(String firstName, String lastName, String assignName, int grade) throws IllegalArgumentException {
  		//Check that both the student and the assignment exist
  		String studentkey = firstName + " " + lastName;
  		
  		if (!studentMap.containsKey(studentkey) || !assignMap.containsKey(assignName)) {
  			throw new IllegalArgumentException();
  		}
  		
  		Student s = studentMap.get(studentkey);		//Get the student object associated with the given name
  		s.addGrade(assignName, grade);				//Add the grade (and overwrite any old grade for the same assignment)
  		studentMap.put(studentkey, s);				//Save the object back again (just for safety)
  	}
  	
  	/* Delete a student from the gradebook */
  	public void deleteStudent(String firstName, String lastName) throws IllegalArgumentException {
  		//Check that the student exists
  		String studentkey = firstName + " " + lastName;
  		
  		if (!studentMap.containsKey(studentkey)) {
  			throw new IllegalArgumentException();
  		}
  		
  		//If they do, remove them from the gradebook
  		studentMap.remove(studentkey);
  	}
  	
  	/* Delete an assignment from the gradebook */	
  	public void deleteAssignment(String assignName) throws IllegalArgumentException {
  		
  		//Check that the assignment exists. If not, raise an error
  		if (!assignMap.containsKey(assignName)) {
  			throw new IllegalArgumentException();
  		}
  		
  		//Otherwise, remove the assignment from the gradebook
  		assignMap.remove(assignName);
  		
  		//Remove all grades from the students associated with the assignment
  		removeAllGrades(assignName);
  	}
  	
  	/**
  	 * Returns if the gradebook contains a given student.
  	 */
  	public boolean containsStudent(String studentName) {
  		return studentMap.containsKey(studentName);
  	}
  	
  	/**
  	 * Returns if the gradebook contains a given assignment
  	 */
  	public boolean containsAssignment(String assignmentName) {
  		return assignMap.containsKey(assignmentName);
  	}
  	
  	/**
  	 * Retrieve a specific grade from a specified student
  	 */
    public Integer retrieveStudentGrades(String studentName, String assignment) {
    	return studentMap.get(studentName).getGrade(assignment);
    }
  	
    /**
     * Return a hashmap containing the student grades for a given assignment
     * 
     * @param assignment the assignment to get the grades for
     * @return the hashmap for all the student grades
     */
    public HashMap<String, Integer> retrieveAssignmentGrades(String assignment) {
    	HashMap<String, Integer> assignmentGrades = new HashMap<String, Integer>();
    	
    	studentMap.forEach((k,v) -> {
    		String[] name = k.split(" ");
    		Integer grade = v.getGrade(assignment);
    		assignmentGrades.put(name[1] + ", " + name[0], grade);
    	});
    	return assignmentGrades;
    }
    
    public LinkedHashMap<String, String> retrieveAssignmentsNames() {
    	LinkedHashMap<String, String> assignments = new LinkedHashMap<String, String>();
    	assignMap.forEach((k,v) -> {
    		assignments.put(k, v.toString());
    		
    	});
    	return assignments;
    }
    
    public Object[] retrieveStudentNames() {
    	return studentMap.keySet().toArray();
    }
    
  	public String toString() {
  		return "";
  	}
  	
  	//************************//
  	// Private helper methods //
  	//************************//
  	
  	/**
  	 * Determines if the new weight is valid for the gradebook (that is, it doesn't put the total weight above 1).
  	 * @param newWeight
  	 * @return
  	 */
  	private boolean isValidWeight(double newWeight) {
  		double total = totalWeight();
  		
  		if (newWeight + total > 1) {
  			return false;
  		}
  		
  		return true;
  	}
  	
  	/**
  	 * Returns the total weights of all the assignments in the gradebook.
  	 * @return
  	 */
  	private double totalWeight() {
  		double total = 0.0;
  		
  		for(Assignment a: assignMap.values()) {
  			total += a.weight;
  		}
  		
  		return total;
  	}
  	
  	/**
  	 * Removes the grades from every student that are for the given assignment
  	 * @param assignment
  	 */
  	private void removeAllGrades(String assignment) {
  		for (Student s: studentMap.values()) {
  			s.removeGrade(assignment);
  		}
  	}
  	
  	/**
  	 * Returns the gradebook's data as a string
  	 * @return
  	 */
  	private String getGradebookString() {
  		String data = "";
  		
  		data += "Assignments\n";
  		for (String a: assignMap.keySet()) {
  			data += assignMap.get(a) + "\n";
  		}
  		
  		data += "Students\n";
  		for (String s: studentMap.keySet()) {
  			Student st = studentMap.get(s);
  			
  			data += st.getFullName() + " ";
  			
  			for (String g: st.grades.keySet()) {
  				data += g + " " + st.grades.get(g) + " ";
  			}
  			
  			data += "\n";
  		}
  		
  		return data;
  	}
  	
  	/**
  	 * Parses a string of data and adds to the gradebook from that.
  	 * @param data
  	 */
  	private void getDataFromString(String data) throws IllegalArgumentException {
  		
  		int assignIndex = data.indexOf("Assignments\n");
  		int studentIndex = data.indexOf("Students\n");
  		
  		//Make sure the two strings actually show up; otherwise throw an exception
  		if (assignIndex == -1 || studentIndex == -1) {
  			throw new IllegalArgumentException();
  		}
  		
  		String aData = data.substring(assignIndex, studentIndex);
  		String sData = data.substring(studentIndex);
  		
  		//Split on the newlines
  		String[] aList = aData.split("\n");
  		String[] sList = sData.split("\n");
  		
  		//Add the assignments first
  		for (int i = 1; i < aList.length; i++) {
  			
  			//Split up the assignment data into the individual fields
  			String[] fields = aList[i].split(" ");
  			
  			//Make sure there are three AND ONLY THREE values in the fields array
  			if (fields.length != 3) {
  				throw new IllegalArgumentException();
  			}
  			
  			String name = fields[0];
  			
  			//Convert the points and weight of the assignment to the proper integer and double representation
  			try {
  				int points = Integer.valueOf(fields[1]);
  				double weight = Double.valueOf(fields[2]);
  				
  				addAssignment(name, points, weight); //Use the built-in method to make sure the data is still valid
  				
  			} catch(NumberFormatException e) {
  				throw new IllegalArgumentException();
  			}
  		}
  		
  		//Add the students and then the grades next
  		for (int i = 1; i < sList.length; i++) {
  			
  			//Split up the assignment data into the individual fields
  			String[] fields = sList[i].split(" ");
  			
  			String firstName = fields[0];
  			String lastName = fields[1];
  			
  			addStudent(firstName, lastName); //Use the built-in method to make sure the data is still valid
  			
  			//Loop through the student's grade data
  			for (int j = 2; j < fields.length; j+=2) {
  				String gradeAssign = fields[j];
  				
  				//Try converting the grade data to a string, and throw an exception if something goes wrong
  				try {
  					int grade = Integer.valueOf(fields[j+1]); //Convert the string version into an integer
  					
  					addGrade(firstName, lastName, gradeAssign, grade);
  				} catch (NumberFormatException e) {
  					throw new IllegalArgumentException();
  				}
  			}
  		}
  	}
  	
  	//*********************************//
  	// Encryption / Decryption methods //
  	//*********************************//
  	
  	/**
  	 * This method generates a new random key
  	 */
  	private SecretKey generateKey() throws NoSuchAlgorithmException {
  		//Create salt for generating the key
  		final byte[] salt = new byte[64];
  		System.out.println("Generating a key, this may take a while...");
        SecureRandom rand = SecureRandom.getInstanceStrong();
        rand.nextBytes(salt);
        //Initialize the key generator
        KeyGenerator keygen = KeyGenerator.getInstance(KEY_ALGO);
        keygen.init(128, rand);
        SecretKey key = keygen.generateKey();
        return key;
  	}
  	
  	// Credit to: https://www.programiz.com/java-programming/examples/convert-byte-array-hexadecimal
  	// for each byte we convert it to hexadecimal using String.format(). "%02X" specifies two things.
  	// the X part states that we're converting to hexadecimal. the 02 part specifies that we must print
  	// at least two digits and if there's less than two digits, we add in a zero to compensate.
  	private static String bytesToHexString(byte[] bytes) {
  		String result = "";
  		int size = bytes.length;
  		for (int i = 0; i < size; i++) {
  			result += String.format("%02X", bytes[i]);
  		}
  		return String.valueOf(result);
  	}
  	
  	// Credit to: https://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
  	// Hex digits comes in pairs, so two digits in a hex is equivalent to one byte. So for each byte, we
  	// look at the next two digits and 
  	private static byte[] hexStringToByteArray(String s) {
  	    int len = s.length();
  	    byte[] data = new byte[len / 2];
  	    if ( len % 2 == 0) {
	  	    for (int i = 0; i < len; i += 2) {
	  	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	  	                             + Character.digit(s.charAt(i+1), 16));
	  	    }
  	    }
  	    return data;
  	}
  	
  	/**
  	 * This method generates a SecreKey object from a given string
  	 * 
  	 */
  	private SecretKey recodeKey(String key) {
 
  		//Decode the String key back into a byte array and convert that back into a SecretKey object
  		byte[] decodedKey = hexStringToByteArray(key);
  		SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, KEY_ALGO);
  		return originalKey;
  	}
  	
  	/**
  	 * This method generates an Initialization vector for encryption/decryption
  	 */
  	private byte[] createIV() {
  		byte[] initVector = new byte[IV_LENGTH]; //Use 12 for GCM
  		SecureRandom rand = new SecureRandom();
  		rand.nextBytes(initVector);
  		return initVector;
  	}
  	
  	
  	/**
  	 * Save the Gradebook object to a file, after encrypting it
  	 */
  	public void saveGradebook(String filename, String key) throws Exception {
  		//-------------------------------------------------------//
  		// First, encrypt the gradebook data using the given key //
  		//-------------------------------------------------------//------------------------------------------------------------//
  		// The following sources were consulted during the creation of this code:
  		//    	https://www.novixys.com/blog/java-aes-example/
  		// 		https://www.tutorialspoint.com/java_cryptography/java_cryptography_encrypting_data.htm
  		//		https://wiki.sei.cmu.edu/confluence/display/java/MSC61-J.+Do+not+use+insecure+or+weak+cryptographic+algorithms
  		//		https://docs.oracle.com/javase/7/docs/technotes/guides/security/crypto/CryptoSpec.html
  		//
  		//	Along with the Java 8 API on the cryptography library.
  		//--------------------------------------------------------------------------------------------------------------------//
  		
  		//Get the gradebook data as a string
  		String gb_data = getGradebookString();
  	
  		//Get the key as a SecreKey object
  		SecretKey skey = this.recodeKey(key);
  		
  		Cipher c = Cipher.getInstance(CIPHER_ALGO);
  		byte[] iVector = createIV(); //Create an initialization vector
  		GCMParameterSpec gcmParam = new GCMParameterSpec(TAG_LENGTH * java.lang.Byte.SIZE, iVector); //Use it as a class for use with the Cipher class
  		
  		c.init(Cipher.ENCRYPT_MODE, skey, gcmParam); //Initialize the cipher
  		
  		byte[] encoded = gb_data.getBytes(java.nio.charset.StandardCharsets.UTF_8);
  		byte[] ciphertext = new byte[iVector.length + c.getOutputSize(encoded.length)];
  		
  		for (int i = 0; i < iVector.length; i++) {
  			ciphertext[i] = iVector[i];
  		}
  		
  		//Finally, encrypt the data
  		c.doFinal(encoded, 0, encoded.length, ciphertext, iVector.length);
  		
  		//------------------------------//
  		// Now, save the encrypted data //
  		//------------------------------//
  		FileOutputStream output = new FileOutputStream(filename);
  		output.write(ciphertext);
  		output.flush();
  		output.close();
  	}
  	
  	/**
  	 * Overloaded method, takes a SecretKey object for the key instead of a String object
  	 */
  	public void saveGradebook(String filename, SecretKey key) throws Exception {
  		
  		//-------------------------------------------------------//
  		// First, encrypt the gradebook data using the given key //
  		//-------------------------------------------------------//------------------------------------------------------------//
  		// The following sources were consulted during the creation of this code:
  		//    	https://www.novixys.com/blog/java-aes-example/
  		// 		https://www.tutorialspoint.com/java_cryptography/java_cryptography_encrypting_data.htm
  		//		https://wiki.sei.cmu.edu/confluence/display/java/MSC61-J.+Do+not+use+insecure+or+weak+cryptographic+algorithms
  		//		https://docs.oracle.com/javase/7/docs/technotes/guides/security/crypto/CryptoSpec.html
  		//
  		//	Along with the Java 8 API on the cryptography library.
  		//--------------------------------------------------------------------------------------------------------------------//
  		
  		//Get the gradebook data as a string
  		String gb_data = getGradebookString();
  
  		Cipher c = Cipher.getInstance(CIPHER_ALGO);
  		byte[] iVector = createIV(); //Create an initialization vector
  		GCMParameterSpec gcmParam = new GCMParameterSpec(TAG_LENGTH * java.lang.Byte.SIZE, iVector); //Use it as a class for use with the Cipher class
  		
  		c.init(Cipher.ENCRYPT_MODE, key, gcmParam); //Initialize the cipher
  		
  		byte[] encoded = gb_data.getBytes(java.nio.charset.StandardCharsets.UTF_8);
  		byte[] ciphertext = new byte[iVector.length + c.getOutputSize(encoded.length)];
  		
  		for (int i = 0; i < iVector.length; i++) {
  			ciphertext[i] = iVector[i];
  		}
  		
  		//Finally, encrypt the data
  		c.doFinal(encoded, 0, encoded.length, ciphertext, iVector.length);
  		
  		//------------------------------//
  		// Now, save the encrypted data //
  		//------------------------------//
  		FileOutputStream output = new FileOutputStream(filename);
  		output.write(ciphertext);
  		output.flush();
  		output.close();
  	}
  	
  	
  	/**
  	 * Load gradebook data from a given encrypted file using a given SecretKey object
  	 */
  	public void loadGradebook(String filename, SecretKey key) throws Exception {
  		
  		//---------------------------------------------------------------------------------------------------------------------//
  		// The following sources were consulted during the creation of this code:
  		//    	https://www.novixys.com/blog/java-aes-example/
  		// 		https://www.tutorialspoint.com/java_cryptography/java_cryptography_encrypting_data.htm
  		//		https://wiki.sei.cmu.edu/confluence/display/java/MSC61-J.+Do+not+use+insecure+or+weak+cryptographic+algorithms
  		//		https://docs.oracle.com/javase/7/docs/technotes/guides/security/crypto/CryptoSpec.html
  		//
  		//	Along with the Java 8 API on the cryptography library.
  		//--------------------------------------------------------------------------------------------------------------------//
  		
  		//-----------------------------//
  		// Read the data from the file //
  		//-----------------------------//
  		File file = new File(filename);
  		byte[] bytes = Files.readAllBytes(file.toPath());
  		
  		//----------------------------//
  		// Decrypt the gradebook data //
  		//----------------------------//
  		
  		Cipher c = Cipher.getInstance(CIPHER_ALGO);
  		
  		//seperate the initialization vector from the actual ciphertext
  		byte[] iVector = Arrays.copyOfRange(bytes, 0, IV_LENGTH);
  		GCMParameterSpec gcmParam = new GCMParameterSpec(TAG_LENGTH * java.lang.Byte.SIZE, iVector);
  		
  		
  		try {
  			c.init(Cipher.DECRYPT_MODE, key, gcmParam);
  		} catch (InvalidKeyException e) {
  			System.out.println("invalid"); //Either change to "invalid" or throw the exception
  			System.exit(255);
  		}

  		byte[] decrypted = c.doFinal(bytes, IV_LENGTH, bytes.length - IV_LENGTH);
  		
  		//--------------------------------------------------//
  		// Use the decrypted data to populate the gradebook //
  		// and make sure that the data is read in correctly //
  		//--------------------------------------------------//
  		try {
  			this.getDataFromString(new String(decrypted));
  		}
  		catch (IllegalArgumentException e) {
  			//Make sure the gradebook is not used (erased) with an error
  			studentMap = null;
  			assignMap = null;
  			System.out.println("Gradebook may have been modified");
  			throw new IllegalArgumentException(); //Alert the calling method/object to an error happening
  		}
  	}
  	
  	
  	/*------------------------------------------------------------------*/
  	/* The nested classes for implementing the students and assignments */
  	/*------------------------------------------------------------------*/
  	
  	class Student{
  		
  		private String firstName;
  		private String lastName;
  		private HashMap<String, Integer> grades;
  		
  		Student(String f, String l) {
  			firstName = f;
  			lastName = l;
  			grades = new HashMap<String, Integer>();
  		}
  		
  		/**
  		 * Adds a grade to the grade hashmap. Doesn't do any checking, as the grade should be validated by this point,
  		 * and if the grade hashmap already contains a grade for the assignment, it should be overriden.
  		 * @param assignment
  		 * @param score
  		 */
  		void addGrade(String assignment, int score) {
  			grades.put(assignment, score);
  		}
  		
  		/**
  		 * Removes a grade from the grade hashmap associated with the given assignment.
  		 * @param assignment
  		 * @return if a grade was removed or not.
  		 */
  		boolean removeGrade(String assignment) {
  			if (grades.containsKey(assignment)) {
  				grades.remove(assignment);
  				return true;
  			}
  			
  			return false;
  		}
  		
  		/**
  		 * Return the grade for the student for the given assignment
  		 * @param assignment
  		 * @return
  		 */
  		Integer getGrade(String assignment) {
  			if (grades.containsKey(assignment)) {
  				return grades.get(assignment);
  			}
  			
  			return null; //No grade, return null
  		}
  		
  		String getFullName() {
  			return firstName + " " + lastName;
  		}
  		
  		/**
  		 * Override inherited equals method to check if the first and last name are the same
  		 */
  		public boolean equals(Object o) {
  			if (o == this) {
  				return true;
  			}
  			
  			if (o instanceof Student) {
  				Student oS = (Student) o;
  				return (firstName.equals(oS.firstName) && lastName.equals(oS.lastName));
  			}
  			
  			return false;
  		}
  	}
  	
  
  	class Assignment {
  		private String name;   //The name of the assignment
  		private int points;    //The total points the assignment is worth
  		private double weight; //The weight of the assignment
  		
  		/**
  		 * Basic constructor: Pass in all wanted values 
  		 */
  		Assignment(String n, int p, double w) {
  			name = n;
  			points = p;
  			weight = w;
  		}
  		
  		/**
  		 * Takes in a string with format: "name points weight" and extracts the fields from this
  		 * @param a
  		 */
  		Assignment(String a) {
  			String[] fields = a.split(" ");
  			
  			name = fields[0];
  			points = Integer.valueOf(fields[1]);
  			weight = Double.valueOf(fields[2]);
  		}
  		
  		/**
  		 * Override the inherited equals method to check if the names are the same.
  		 */
  		public boolean equals(Object o) {
  			if (o == this) {
  				return true;
  			}
  			
  			if (o instanceof String) {
  				Assignment oAssign = (Assignment) o;
  				return name.equals(oAssign.name);
  			}
  			
  			return false;
  		}
  		
  		/**
  		 * Display the name followed by the points and weight inside of parenthesis
  		 */
  		public String toString() {
  			return name + " " + points + " " + weight;
  		}
  	}

}
