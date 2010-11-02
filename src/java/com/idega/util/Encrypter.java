package com.idega.util;

/**
 * Title: idega Framework Description: Copyright: Copyright (c) 2001 Company:
 * idega
 * 
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



 	public static void main(String[] args) {
		System.out.println(encryptOneWay("idega"));
	}

	public static boolean verifyOneWayEncrypted(String encryptedString, String unEncryptedString) {
		return encryptOneWay(unEncryptedString).equals(encryptedString);
	}

	public static String hexToAscii(String str) {
		char[] pass = new char[str.length() / 2];
		try {
			for (int i = 0; i < pass.length; i++) {
				pass[i] = (char) Integer.decode("0x" + str.charAt(i * 2) + str.charAt((i * 2) + 1)).intValue();
			}
			return String.valueOf(pass);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return str;
		}
	}
	
	public static String asciiToHex(String str) {
		char[] pass = str.toCharArray();
		
		String hexString = "";
		for (int i = 0; i < pass.length; i++) {
			String hex = Integer.toHexString(pass[i]);
			while (hex.length() < 2) {
				String s = "0";
				s += hex;
				hex = s;
			}
			hexString += hex;
		}
		if (hexString.equals("")) {
			hexString = null;
		}
		
		return hexString;
	}
}