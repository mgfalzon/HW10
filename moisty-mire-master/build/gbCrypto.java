/******************************************************************************************************************************************
* Author: Alexander Peralta
* Resources Used: 	https://www.geeksforgeeks.org/serialization-in-java/ 
* 					https://gist.github.com/praseodym/f2499b3e14d872fe5b4a
* 					https://stackoverflow.com/questions/2836646/java-serializable-object-to-byte-array
*******************************************************************************************************************************************/
package gradingtools;


import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;



public class gbCrypto {	

  // AES key size is 128 bits
  private static final int GCM_NONCE_LENGTH = 12; // in bytes
  private static final int GCM_TAG_LENGTH = 16; // in bytes
	
  private static byte[] encrypt(byte[] plainText) throws Exception {
    //SecureRandom random = SecureRandom.getInstanceStrong();
    Random r = new Random();
	SecretKey key = getSymmetricKey();   
    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "SunJCE");
    final byte[] nonce = new byte[GCM_NONCE_LENGTH];
    //random.nextBytes(nonce);
    r.nextBytes(nonce);
    GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce);
    cipher.init(Cipher.ENCRYPT_MODE, key, spec);
    byte[] aad = "something ".getBytes();;
    cipher.updateAAD(aad);
    byte[] cipherText = cipher.doFinal(plainText);
    
    
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
    outputStream.write( nonce );
    outputStream.write( cipherText );
    
    
    byte[] nonceCipherText = outputStream.toByteArray( );
    
    
    
    return nonceCipherText;
  }
  
  private static byte[] decrypt(byte[] nonceCipherText) throws Exception {
	  
    byte[] nonce = Arrays.copyOfRange(nonceCipherText, 0, GCM_NONCE_LENGTH);
    byte[] cipherText = Arrays.copyOfRange(nonceCipherText, GCM_NONCE_LENGTH, nonceCipherText.length);
    
  
    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "SunJCE");
    SecretKey key = getSymmetricKey();
    byte[] aad = "something ".getBytes();
    GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce);

    cipher.init(Cipher.DECRYPT_MODE, key, spec);
    cipher.updateAAD(aad);
    byte[] plainText = null;
	
    try {
	  plainText = cipher.doFinal(cipherText);
	} 
	catch (BadPaddingException e) {
		// TODO Auto-generated catch block
	  System.out.println("Gradebook file has been corrupted.");
      System.exit(1);
	}
      
	return plainText;

  }
  
  private static SecretKey getSymmetricKey() {
    String strKey = "600FFAA01AABD4E30DE96CEE99A5A951";
	int len = strKey.length();
    byte[] byteKey = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
    	byteKey[i / 2] = (byte) ((Character.digit(strKey.charAt(i), 16) << 4)
                             + Character.digit(strKey.charAt(i+1), 16));
    }
    
    SecretKey key = new SecretKeySpec(byteKey, 0, byteKey.length, "AES"); 
    return key;
  }
  
  private static Gradebook deserializeGradebook(byte[] plainText) throws Exception {
	ByteArrayInputStream bis = new ByteArrayInputStream(plainText);
    ObjectInput in = null;
    in = new ObjectInputStream(bis);
    Object o = in.readObject(); 
    
    return (Gradebook) o;
  }
  
  
  private static byte[] serializeGradeBook(Gradebook gb) throws Exception {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream out = null;
    out = new ObjectOutputStream(bos);   
    out.writeObject(gb);
    out.flush();
    byte[] serializedGB = bos.toByteArray();
    
    return serializedGB;
  }
  
  private static void writeCTGradebookToFile(byte[] nonceCipherText, String filename) throws Exception {
	FileOutputStream fos = new FileOutputStream(filename);
	ObjectOutputStream oos = new ObjectOutputStream(fos);
	// Write objects to file
	oos.writeObject(nonceCipherText);
	oos.close();
    fos.close();
  }
  
  private static byte[] readCTGradebookFromFile( String filename) throws Exception {
    FileInputStream file = new FileInputStream(filename);
    ObjectInputStream in = new ObjectInputStream(file);
    // Method for deserialization of object
    byte[] nonceCipherText = (byte[])in.readObject();
        
    in.close();
    file.close();
    
    return nonceCipherText;
  }
  
  public static boolean keyEquals(String userKey) {
    String strKey = "600FFAA01AABD4E30DE96CEE99A5A951";
    return userKey.equals(strKey);
  }
  
  public static Gradebook getGradebook(String filename) throws Exception {
	  return deserializeGradebook(decrypt(readCTGradebookFromFile(filename)));
  }
  
  public static void saveGradeBook(Gradebook gb , String filename) throws Exception {
	  writeCTGradebookToFile(encrypt(serializeGradeBook(gb)), filename);
  }
  
 
}
