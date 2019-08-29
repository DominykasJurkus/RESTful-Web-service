package lt.viko.eif.groupwork;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

class FileEncryption {
	
	public static void encryptFile(String password) throws Exception {
		
		File fileIn = new File("C:/Users/Dominykas Jurkus/Desktop/temp/FileToEncrypt.txt");
		File fileOut = new File("C:/Users/Dominykas Jurkus/Desktop/temp/EncryptedFile.aes");
		
		// file to be encrypted
		FileInputStream inFile = new FileInputStream(fileIn);

		// encrypted file
		FileOutputStream outFile = new FileOutputStream(fileOut);

		byte[] salt = new byte[8];
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextBytes(salt);
		FileOutputStream saltOutFile = new FileOutputStream("salt.enc");
		saltOutFile.write(salt);
		saltOutFile.close();

		SecretKeyFactory factory = SecretKeyFactory
				.getInstance("PBKDF2WithHmacSHA1");
		KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536,
				256);
		SecretKey secretKey = factory.generateSecret(keySpec);
		SecretKey secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secret);
		AlgorithmParameters params = cipher.getParameters();

		FileOutputStream ivOutFile = new FileOutputStream("iv.enc");
		byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
		ivOutFile.write(iv);
		ivOutFile.close();

		byte[] input = new byte[64];
		int bytesRead;

		while ((bytesRead = inFile.read(input)) != -1) {
			byte[] output = cipher.update(input, 0, bytesRead);
			if (output != null)
				outFile.write(output);
		}

		byte[] output = cipher.doFinal();
		if (output != null)
			outFile.write(output);
		
		inFile.close();
		outFile.flush();
		outFile.close();
	}
	
	public static String decryptFile(String fileName, String password) throws Exception {

		if(fileName == null)
		{
			return null;
		}
		else 
		{				
			FileInputStream saltFis = new FileInputStream("salt.enc");
			byte[] salt = new byte[8];
			saltFis.read(salt);
			saltFis.close();
	
			FileInputStream ivFis = new FileInputStream("iv.enc");
			byte[] iv = new byte[16];
			ivFis.read(iv);
			ivFis.close();
	
			SecretKeyFactory factory = SecretKeyFactory
					.getInstance("PBKDF2WithHmacSHA1");
			KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536,
					256);
			SecretKey tmp = factory.generateSecret(keySpec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
	
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
			FileInputStream fis = new FileInputStream("C:/Users/Dominykas Jurkus/Desktop/temp/" + fileName);
			FileOutputStream fos = new FileOutputStream("C:/Users/Dominykas Jurkus/Desktop/temp/downloaded - " + fileName);
			byte[] in = new byte[64];
			int read;
			while ((read = fis.read(in)) != -1) {
				byte[] output = cipher.update(in, 0, read);
				if (output != null)
					fos.write(output);
			}
	
			byte[] output = cipher.doFinal();
			if (output != null)
				fos.write(output);
			fis.close();
			fos.flush();
			fos.close();
			
			return fileName;
		}
	}
}
