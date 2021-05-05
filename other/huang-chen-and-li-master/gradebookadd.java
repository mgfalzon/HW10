
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Allows the user to add a new student or assignment to a gradebook,
 * or add a grade for an existing student and existing assignment
 */
public class gradebookadd {

  /* parses the cmdline to keep main method simplified */
  private static void parse_cmdline(String[] args) throws ClassNotFoundException, IOException {
    String filename = "";
    String key = "";
    Gradebook gb = null;
    HashMap<String, Object> helpMap = new HashMap<>();

    if(args.length==1)
      System.out.println("\nNo Extra Command Line Argument Passed Other Than Program Name");
    if(args.length>=5) {
      //System.out.println("\nNumber Of Arguments Passed: " + args.length);
      //System.out.println("----Following Are The Command Line Arguments Passed----");

      for(int counter=0; counter < args.length; counter++) {
        //System.out.println("args[" + counter + "]: " + args[counter]);
        
        // Check the validity of the inputs.
        if (counter == 0 && !args[0].equals("-N")) {
          err();
        } 
        if (counter == 1) {
          String regex = "[0-9A-Za-z_.]+";
          Pattern p = Pattern.compile(regex);
          Matcher m = p.matcher(args[1]);
          if (!m.matches())
            err();

          filename = args[1];
        }
        if (counter == 2 && !args[2].equals("-K"))
          err();
        if (counter == 3) {
          key = args[counter];
          try{
            gb = new Gradebook(filename, key);
        } catch (Exception e){
            err();
        }
        }

        if (counter == 4 && !args[4].equals("-AA") && !args[4].equals("-DA") && !args[4].equals("-AS") && !args[4].equals("-DS") && !args[4].equals("-AG"))
          err();

        if (counter >= 5 && counter % 2 == 1 &&
        ((args[4].equals("-AA") && (!args[counter].equals("-AN") && !args[counter].equals("-P") && !args[counter].equals("-W")))
          || (args[4].equals("-DA") && (!args[counter].equals("-AN")))
          || (args[4].equals("-AS") && (!args[counter].equals("-FN") && !args[counter].equals("-LN")))
          || (args[4].equals("-DS") && (!args[counter].equals("-FN") && !args[counter].equals("-LN")))
          || (args[4].equals("-AG") && (!args[counter].equals("-FN") && !args[counter].equals("-LN") && !args[counter].equals("-AN") && !args[counter].equals("-G"))))) {
          err();
        }

        // Check parameters.
        if (counter >= 5 && counter % 2 == 0 && args[4].equals("-AA")) {
          if (args[counter - 1].equals("-AN")) {
            checkAssignmentName(args[counter]);
            helpMap.put("-AN", args[counter]);
          }
          else if (args[counter - 1].equals("-P")) {
            if (Float.parseFloat(args[counter]) < 0 || isStringInt(args[counter])) {
              err();
            }
            helpMap.put("-P", args[counter]);
          }
          else if (args[counter - 1].equals("-W")) {
            if (Float.valueOf(args[counter]) < 0 || Float.valueOf(args[counter]) > 1) {
              err();
            }
            helpMap.put("-W", args[counter]);
          }
        } else if (counter >= 5 && counter % 2 == 0 && args[4].equals("-DA")) {
          checkAssignmentName(args[counter]);
          helpMap.put("-AN", args[counter]);
        } else if (counter >= 5 && counter % 2 == 0 && (args[4].equals("-AS") || args[4].equals("-DS"))) {
          if (args[counter - 1].equals("-FN")) {
            checkStudentName(args[counter]);
            helpMap.put("-FN", args[counter]);
          }
          else if (args[counter - 1].equals("-LN")) {
            checkStudentName(args[counter]);
            helpMap.put("-LN", args[counter]);
          }
        } else if (counter >= 5 && counter % 2 == 0 && args[4].equals("-AG")) {
          if (args[counter - 1].equals("-FN")) {
            checkStudentName(args[counter]);
            helpMap.put("-FN", args[counter]);
          }
          else if (args[counter - 1].equals("-LN")) {
            checkStudentName(args[counter]);
            helpMap.put("-LN", args[counter]);
          }
          else if (args[counter - 1].equals("-AN")) {
            checkAssignmentName(args[counter]);
            helpMap.put("-AN", args[counter]);
          }
          else if (args[counter - 1].equals("-G")) {
            if (Float.parseFloat(args[counter]) < 0 || isStringInt(args[counter])) {
              err();
            }
            helpMap.put("-G", args[counter]);
          }
        }
      }
    } else err();

    // Make changes to the gradebook.
    if (args[4].equals("-AA")) {
      if (!helpMap.containsKey("-AN") || !helpMap.containsKey("-P") || !helpMap.containsKey("-W"))
        err();
      gb.addAssignment((String)helpMap.get("-AN"), Integer.valueOf((String)helpMap.get("-P")), Float.valueOf((String)helpMap.get("-W")));
    } else if (args[4].equals("-DA")) {
      if (!helpMap.containsKey("-AN"))
        err();
      gb.deleteAssignment((String)helpMap.get("-AN"));
    } else if (args[4].equals("-AS")) {
      if (!helpMap.containsKey("-FN") || !helpMap.containsKey("-LN"))
        err();
      gb.addStudent((String)helpMap.get("-FN"), (String)helpMap.get("-LN"));
    } else if (args[4].equals("-DS")) {
      if (!helpMap.containsKey("-FN") || !helpMap.containsKey("-LN"))
        err();
      gb.deleteStudent((String)helpMap.get("-FN"), (String)helpMap.get("-LN"));
    } else if (args[4].equals("-AG")) {
      if (!helpMap.containsKey("-FN") || !helpMap.containsKey("-LN") || !helpMap.containsKey("-AN") || !helpMap.containsKey("-G"))
        err();
      gb.addGrade((String)helpMap.get("-FN"), (String)helpMap.get("-LN"), (String)helpMap.get("-AN"), Integer.valueOf((String)helpMap.get("-G")));
    }

    // Encrypt gradebook again.
    try {
      FileOutputStream fileOut = new FileOutputStream(args[1]);
      final Cipher c = Cipher.getInstance("AES");
      
      byte[] decodedKey = Base64.getDecoder().decode(key);
      SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 

      c.init(Cipher.ENCRYPT_MODE, originalKey);
      CipherOutputStream output = new CipherOutputStream(fileOut, c);
      ObjectOutputStream out = new ObjectOutputStream(output);
      out.writeObject(gb);
      out.close();
      output.close();
      fileOut.close();
    }
    catch (Exception e) {
        System.out.println("failed to write gradebook object to file");
        System.exit(255);
    }
  }

  public static void main(String[] args) throws ClassNotFoundException, IOException {
    parse_cmdline(args);
  }

  //--- Helper methods ---

  // Print invalid and exit
  private static void err() {
    System.out.println("invalid");
    System.exit(255);
  }

  // Check whether the string parse into a integer of float.
  public static boolean isStringInt(String s) {
    try
    {
      Integer.parseInt(s);
      return false;
    } catch (NumberFormatException ex)
    {
      return true;
    }
  }

  // Print invalid and exit
  private static void checkStudentName(String s) {
    String regex = "[A-Za-z]+";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(s);
    if (!m.matches())
      err();
  }

  // Print invalid and exit
  private static void checkAssignmentName(String s) {
    String regex = "[0-9A-Za-z]+";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(s);
    if (!m.matches())
      err();
  }
}
