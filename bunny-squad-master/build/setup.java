//import ...

/**
 * Initialize gradebook with specified name and generate a key.
 */

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.*;

public class setup {

	/* test whether the file exists */
	private static boolean file_test(String filename) {
		File check = new File("./" + filename);
		return check.exists();
	}

	private static String bytesToHex(byte[] in) {
		final StringBuilder builder = new StringBuilder();
		for (byte b : in) {
			builder.append(String.format("%02x", b));
		}
		return builder.toString();
	}

	public static void main(String[] args) {
		String gradebook_name;
		if (args.length < 2) {
			System.out.println("Usage: setup <logfile pathname>");
			System.exit(1);
		}

		// Creating a KeyGenerator object

		if (args[0].equals("-N")) {
			gradebook_name = args[1];

			if (file_test(gradebook_name)) {
				System.out.println("invalid");
				System.exit(255);
			}

			try {
				KeyGenerator keyGen = KeyGenerator.getInstance("AES");

				// Creating a SecureRandom object
				SecureRandom sRandom = new SecureRandom();

				// Initializing the KeyGenerator
				keyGen.init(128);

				// Creating/Generating a key
				SecretKey key = keyGen.generateKey();

				// Generate IV
				byte[] iv = new byte[128 / 8];
				sRandom.nextBytes(iv);
				IvParameterSpec ivspec = new IvParameterSpec(iv);

				byte[] key2 = key.getEncoded();

				System.out.println("My key is: " + bytesToHex(key2));

				JSONObject obj = new JSONObject();
				obj.put("name", gradebook_name);
				obj.put("assignments", new JSONObject());
				obj.put("student_profiles", new JSONArray());
				obj.put("assignment_order", new JSONArray());

				// ENCODING:
				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				try {
					cipher.init(Cipher.ENCRYPT_MODE, key, ivspec);
				} catch (Exception e) {
					System.out.println("invalid");
					System.exit(255);
				}

				SealedObject sealed_gradebook = new SealedObject(obj, cipher);

				FileOutputStream file_output = new FileOutputStream("./" + gradebook_name);
				file_output.write(iv);
				CipherOutputStream cipherOutputStream = new CipherOutputStream(new BufferedOutputStream(file_output), cipher);
				ObjectOutputStream outputStream = new ObjectOutputStream(cipherOutputStream);
				outputStream.writeObject(sealed_gradebook);
				outputStream.flush();
				outputStream.close();

			} catch (Exception e) {
				System.out.println("invalid");
				System.exit(255);
			}

		} else {
			System.out.println("invalid");
			System.exit(255);
		}

		return;
	}

}
