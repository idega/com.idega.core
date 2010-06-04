package com.idega.util;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
 import java.security.MessageDigest;
 import java.security.NoSuchAlgorithmException;
/*
import java.io.*;
import java.security.*;
import java.math.*;
import cryptix.util.core.BI;
import cryptix.util.core.ArrayUtil;
import cryptix.util.core.Hex;
import cryptix.provider.key.*;
*/

public class Encrypter {

  //private final static String encryptionType = "SHA-1";
  private final static String encryptionType = "MD5";


  private Encrypter() {
  }

  public static String encryptOneWay(String textToEncrypt){

	  //System.out.println("textToEncrypt = " + textToEncrypt);
	  
        MessageDigest digest = null;
        try {
          /*
           * Create a instance of the MessageDigest class.
           * We will be using SHA-1 as encryption algorithm.
           */
          digest = MessageDigest.getInstance(encryptionType);
        } catch (NoSuchAlgorithmException nsex) {
          /* do nothing, only print debug info */
          nsex.printStackTrace();
        }

        /*
         * Reset MessageDigest object
         */
        digest.reset();

        /*
         * Convert String to array of bytes
         */
        byte[] b = textToEncrypt.getBytes();

        /*
         * And "feed" this array of bytes to the MessageDigest object
         */
        digest.update(b);

        /*
         * Get the resulting bytes after the encryption process
         */
        byte[] digestedBytes = digest.digest();
        char[] digestedChars = new char[digestedBytes.length];
        int maxvalue=128;
        for (int i = 0; i < digestedBytes.length; i++) {
          int myByte = digestedBytes[i]+maxvalue;
          digestedChars[i]=(char)myByte;
        }


        String encryptedString = String.valueOf(digestedChars);
        
        //System.out.println("encryptedString " + encryptedString);
        
        return encryptedString;

  }



  public static boolean verifyOneWayEncrypted(String encryptedString,String unEncryptedString){
//    return(true);
    return encryptOneWay(unEncryptedString).equals(encryptedString);

    //String secondEncrypted = encryptOneWay(unEncryptedString);
    //return secondEncrypted.equals(encryptedString);
  }

  /*
  public static String encryptTwoWay(String textToEncrypt){
    return null;
  }

  public static String decryptTwoWay(String encryptedText){
    return null;
  }*/


/*
public static void main (String[] args) {

	try {
	FileOutputStream outFile1 = new FileOutputStream("DES.out");
	// Note: PrintStream is deprecated, but still works fine in jdk1.1.7b
	PrintStream output1 = new PrintStream(outFile1);

	// convert a string to a DES key and print out the result
	RawSecretKey key2 = new RawSecretKey("DES",Hex.fromString("3812A419C63BE771"));
	RawKey rkey = (RawKey) key2;
	byte[] yval = rkey.getEncoded();
	BigInteger Bkey = new BigInteger(yval);
	String w = cryptix.util.core.BI.dumpString(Bkey);
	output1.println("The Encryption Key = " + w);

	// use the DES key to encrypt a string
	Cipher des=Cipher.getInstance("DES/ECB/NONE","Cryptix");
	des.initEncrypt(key2);
	byte[] ciphertext = des.crypt(Hex.fromString("01010101010101010102030405060708090A0B0C0D0E0F101112131415161718"));

	// print out length and representation of ciphertext
	output1.print("\n");
	output1.println("ciphertext.length = " + ciphertext.length);

	BigInteger Bciph = new BigInteger(ciphertext);
	w = cryptix.util.core.BI.dumpString(Bciph);
	output1.println("Ciphertext for DES encryption = " + w);

	// decrypt ciphertext
	des.initDecrypt(key2);
	ciphertext = des.crypt(ciphertext);
	output1.print("\n");
	output1.println("plaintext.length = " + ciphertext.length);

	// print out representation of decrypted ciphertext
	Bciph = new BigInteger(ciphertext);
	w = cryptix.util.core.BI.dumpString(Bciph);
	output1.println("Plaintext for DES encryption = " + w);

	output1.println(" ");
	output1.close();

      } catch (Exception e) {
            System.err.println("Caught exception " + e.toString());
      }

    }*/




}
