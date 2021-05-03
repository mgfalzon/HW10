//import ...
import java.util.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;

/**
* Allows the user to add a new student or assignment to a gradebook,
* or add a grade for an existing student and existing assignment
*/
public class gradebookadd {

  /* parses the cmdline to keep main method simplified */
  private static void parse_cmdline(String[] args) {
    Gradebook gradebook = new Gradebook();

    try {
      if(args.length<2)
        System.out.println("\nNo Extra Command Line Argument Passed Other Than Program Name");
      else if(args.length % 2 == 0) {
        //if there's an even number of arguments, we have a flag without a specified variable.
        throw new Exception("even number of arguments");
      } else if(args.length>=2) {
        // System.out.println("\nNumber Of Arguments Passed: " + args.length);
        // System.out.println("----Following Are The Command Line Arguments Passed----");
        // for(int counter=0; counter < args.length; counter++)
        // System.out.println("args[" + counter + "]: " + args[counter]);

        if(args[0].equals("-N")) {
          if (!allowedChars("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_.", args[1])) {
            throw new IllegalArgumentException("Disallowed character presented in file name.");
          }

          if(args[2].equals("-K")){

            if(allowedChars("abcdefABCDEF0123456789", args[3])){
               gradebook = retrieveGradebook(args[1], args[3]);
            } else {
              throw new IllegalArgumentException("Disallowed characters in key.");
            }

            if(!gradebook.checkHashes()) {
              throw new Exception("Something has been tampered with.");
            }

            String [] flags = parse_remainder(args); //Flag 0: -AN, Flag 1: -FN, Flag 2: -LN, Flag 3: -P, Flag 4: -W, Flag 5: -G
            String assignment, firstName, lastName, allowed;
            switch(args[4]){

              case "-AA":
                //If an assignment already exist, then we error. If sum of weights of new assignment and all current assignments adds up to more than 1, then error.
                assignment = flags[0];
                int points;
                double weight;

                if(!allowedChars("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", assignment)) {
                  throw new IllegalArgumentException("Disallowed characters in assignment name.");
                }

                //Exception can be handled by external try catch
                points = Integer.parseInt(flags[3]);
                weight = Double.parseDouble(flags[4]);

                if (points <  0 || weight < 0 || weight > 1) {
                  throw new IllegalArgumentException("Incorrect points or weight.");
                }

                if(gradebook.getAssignment(assignment) != null) {
                  throw new IllegalArgumentException("Assignment already exists.");
                }

                if(gradebook.getTotalWeights() + weight > 1) {
                  throw new Exception("Total weight exceeds one.");
                }

                gradebook.addAssignment(assignment, points, weight);
                break;
              case "-DA":
                assignment = flags[0];

                //check if any flags exist that shouldn't
                for(int i = 1; i < flags.length; i++) {
                  if(flags[i] != null) {
                    throw new IllegalArgumentException("Additional argument present for -DA.");
                  }
                }

                //check for allowed characters
                if(!allowedChars("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", assignment)) {
                  throw new IllegalArgumentException("Disallowed characters in assignment name.");
                }

                //delete assignment
                if(gradebook.removeAssignment(assignment) == null) {
                  throw new IllegalArgumentException("Assignment does not exist.");
                }
                break;
              case "-AS":
                firstName = flags[1];
                lastName = flags[2];
                allowed = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

                //check if any flags exist that shouldn't
                for(int i = 0; i < flags.length; i++) {
                  if(i != 1 && i != 2 && flags[i] != null) {
                    throw new IllegalArgumentException("Additional argument present -AS.");
                  }
                }

                if(!(allowedChars(allowed, firstName) && allowedChars(allowed, lastName))) {
                  throw new IllegalArgumentException("Disallowed characters in student name.");
                }

                if(gradebook.getStudent(firstName, lastName) != null) {
                  throw new IllegalArgumentException("Student already exists");
                }

                gradebook.addStudent(firstName, lastName);

                break;
              case "-DS":
                firstName = flags[1];
                lastName = flags[2];
                allowed = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"  ;

                //check if any flags exist that shouldn't
                for(int i = 0; i < flags.length; i++) {
                  if(i != 1 && i != 2 && flags[i] != null) {
                    throw new IllegalArgumentException("Additional argument present in -DS.");
                  }
                }

                if(!(allowedChars(allowed, firstName) && allowedChars(allowed, lastName))) {
                  throw new IllegalArgumentException("Disallowed characters in student name.");
                }

                if(gradebook.removeStudent(firstName, lastName) == null) {
                  throw new IllegalArgumentException("Student doesn't exist.");
                }

                break;
              case "-AG":
                String fn = flags[1];
                String ln = flags[2];
                assignment = flags[0];
                int grade = 0;
                allowed = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
                if(!allowedChars("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", assignment)) {
                  throw new IllegalArgumentException("Disallowed characters in assignment name.");
                }

                try{
                  grade = Integer.parseInt(flags[5]);
                } catch(Exception e) {
                  throw new IllegalArgumentException("Grade is not a valid integer.");
                }

                if (grade < 0) {
                  throw new IllegalArgumentException("Grade is negative. Non-negative grades only.");
                }

                if(!(allowedChars(allowed, fn) && allowedChars(allowed, ln))) {
                  throw new IllegalArgumentException("Disallowed characters in student name.");
                }

                Object student = gradebook.getStudent(fn, ln);
                Object assign = gradebook.getAssignment(assignment);
                if (student == null || assign == null){
                  throw new IllegalArgumentException("Either Student or Assignment were not found.");
                }

                if(gradebook.valueExceedsMax(assignment, grade)){
                  throw new IllegalArgumentException("Exceeds maximum of the assignment.");
                }

                gradebook.addGrade(assignment, fn, ln, grade);
                break;
              default:
                throw new Exception("Non-recognized argument.");
            }

            //Write gradebook back to file
            gradebook.recalculateHashes();
            writeGradebook(gradebook, args[1], args[3]);

          } else{
            throw new IllegalArgumentException("Wrong arg 1.");
          }
        } else {
          throw new IllegalArgumentException("Wrong arg 0.");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(255);
    }

    return;
  }

  private static boolean allowedChars(String allowed, String check) {
    for(int i = 0; i < check.length(); i++) {
      if(!allowed.contains("" + check.charAt(i))) {
        return false;
      }
    }

    return true;
  }

  //pass in allowed flags
  //returns the values following last occurence of each flags
  private static String [] parse_remainder(String [] args) {
    String [] ret = new String [6];

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
        case "-P":
        ret[3] = args[i+1];
        break;
        case "-W":
        ret[4] = args[i+1];
        break;
        case "-G":
        ret[5] = args[i+1];
        break;
        default:
        throw new IllegalArgumentException("Invalid flag found");
      }
    }

    return ret;
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

private static void writeGradebook(Gradebook gradebook, String filename, String key) throws Exception {
  //Recalculate the hashes

  Cipher cipher = Cipher.getInstance("AES");
  byte[] keyBytes = new byte[16];
  for (int i=0;i < 32; i+=2){
    keyBytes[i/2] = (byte) ((Character.digit(key.charAt(i), 16) << 4) + (Character.digit(key.charAt(i+1), 16)));
  }
  SecretKey secret = new SecretKeySpec(keyBytes, "AES");
  cipher.init(Cipher.ENCRYPT_MODE, secret);

  SealedObject sealedBook = new SealedObject(gradebook,cipher);
  FileOutputStream fileStream = new FileOutputStream(filename);
  BufferedOutputStream buffStream = new BufferedOutputStream(fileStream);
  CipherOutputStream ciphStream = new CipherOutputStream(buffStream,cipher);
  ObjectOutputStream objStream = new ObjectOutputStream(ciphStream);
  objStream.writeObject(sealedBook);
  objStream.close();
}

  public static void main(String[] args) {

    parse_cmdline(args);
    return;
  }
}
