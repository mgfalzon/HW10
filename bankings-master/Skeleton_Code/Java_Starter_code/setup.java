//import ...
import java.util.*;
import java.security.*;
import javax.crypto.*;
import java.io.*;
import java.util.regex.*;

/**
 * Initialize gradebook with specified name and generate a key.
 */
public class setup {

  private static final String FILENAME_REGEX = "[^\\w\\.]";
  /* test whether the file exists */
  private static boolean file_test(String filename) {
    Pattern pattern = Pattern.compile(FILENAME_REGEX);
    Matcher matcher = pattern.matcher(filename);
    if(!matcher.find()){
      File f = new File(filename);
      if(!f.exists()){
        return true;
      }
    }
    return false;
  }

  public static void main(String[] args) {
    SecretKey key;
    String hexKey, filename;
    byte[] encryptedBook;
    Gradebook gradebook;
    FileOutputStream fileStream;
    BufferedOutputStream buffStream;
    CipherOutputStream ciphStream;
    ObjectOutputStream objStream;
    SealedObject sealedBook;

    if (args.length != 2) {
      System.out.println("Usage: setup <logfile pathname>");
      System.exit(1);
    }

    if (args[0].equals("-N")){
      filename = args[1];

      if (!file_test(filename)){
        System.out.println("invalid filename");
        System.exit(255);
      }

      gradebook = new Gradebook();
      //Generate Gradebook file using arg[1] as the filename
      //Generate key
      try{
        SecureRandom secRand = new SecureRandom();
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128, secRand);
        key = keyGen.generateKey();
        hexKey = "";
        for (byte b : key.getEncoded()) {
          hexKey += String.format("%02X", b);
        }

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        sealedBook = new SealedObject(gradebook,cipher);
        fileStream = new FileOutputStream(filename);
        buffStream = new BufferedOutputStream(fileStream);
        ciphStream = new CipherOutputStream(buffStream,cipher);
        objStream = new ObjectOutputStream(ciphStream);
        objStream.writeObject(sealedBook);
        objStream.close();


        System.out.println("Key is: " + hexKey);
      }catch(Exception e){
        System.out.println("invalid");
        System.exit(255);
      }
    }
    else{
      System.out.println("invalid");
      System.exit(255);
    }
    return;
  }
}
