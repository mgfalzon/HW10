import java.io.*;
import javax.crypto.*;
import java.util.*;
import java.security.*;
import java.util.regex.Pattern;
/**
 * Initialize gradebook with specified name and generate a key.
 */
public class setup {

  /* test whether the file exists */
  private static boolean file_test(String filename) {
    String regex = "[0-9A-Za-z_.]+";
    Pattern p = Pattern.compile(regex);
    if(!p.matcher(filename).matches()){
      return true;
    }
    File f = new File(filename);
    return f.exists() && !f.isDirectory();
  }

  public static void main(String[] args) {
    String key = null;

    if (args.length < 2) {
      System.out.println("Usage: setup -N <logfile pathname>");
      System.exit(1);
    }

    if(file_test(args[1])){
    	System.out.println("invalid");
    	System.exit(255);
    }
    
    KeyGenerator keyGenerator = null;
    try{
        keyGenerator = KeyGenerator.getInstance("AES");
    }catch(Exception e){
        System.out.println("failed to create KeyGenerator");
        System.exit(255);
    }
    SecureRandom secureRandom = new SecureRandom();
    int keyBitSize = 256;
    keyGenerator.init(keyBitSize, secureRandom);
    SecretKey secretKey = keyGenerator.generateKey();
    key = Base64.getEncoder().encodeToString(secretKey.getEncoded());

    Gradebook g = new Gradebook();
    try {
        g.save(args[1], key);
    }
    catch (Exception e) {
        System.out.println("failed to write gradebook object to file");
        System.exit(255);
    }
    
    System.out.println("Key is: " + key);

    return;
  }
}
