/**
 *
 */
package com.idega.util.encryption;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.idega.idegaweb.IWMainApplication;
import com.idega.util.CoreConstants;

/**
 * <p>
 * Class that encrypts and decrypts a string with the AES/Rijndael algorithm
 * with a provided key.
 * <br/>
 * Note that the default key length is 32 bytes (256-bits) but the default maximum
 * key-length in the standard Java distributions is 128 bits.
 * This can be remedied by downloading the "Unlimited Strength Jurisdiction Policy Files"
 * on the Java Download pages: http://java.sun.com/j2se/1.5.0/download.jsp or
 * http://java.sun.com/j2se/1.4.2/download.html and replacing two jar files in the JDK.
 * <br/>
 * This encryption implementation is extended and used by MentorEncryptionBean
 * in module is.mentor.
 * </p>
 * Last modified: $Date: 2007/10/17 15:09:36 $ by $Author: valdas $
 *
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.3 $
 */
public class RijndaelEncryptionBean {

	private String secretKey = null;
	private int keySize = 256;
	private byte[] IV = new byte[] {0x43, 0x32, 0x11, 0x3a, 0x50, 0x37,  0x15, 0x74, 0x56, 0x63, 0x43, 0x56, 0x65, 0x64, 0x2a, 0x38 };


	public RijndaelEncryptionBean() {
	}

	/**
	 * Encrypts with the set secretKey and returns the resulting string in base64 format.
	 */
	public String encrypt(String inputPlainText) {
		try {
			return encrypt(inputPlainText, getSecretKey());
		}
		catch (Exception e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
			return null;
		}
	}
	/**
	 * Encrypts the input text with key password and AES/Rijndael algorithm and returns the resulting string in base64 format.
	 */
	public String encrypt(String text, String password) throws Exception {
		return encrypt(text, password, "AES/CBC/PKCS5Padding");
	}

	public String encrypt(String text, String password, String algorithm) throws Exception {
		Cipher cipher = Cipher.getInstance(algorithm);
		byte[] keyBytes = new byte[getKeySize()];
		byte[] b = password.getBytes(CoreConstants.ENCODING_UTF8);
		int len = b.length;
		if (len > keyBytes.length) {
			len = keyBytes.length;
		}
		System.arraycopy(b, 0, keyBytes, 0, len);
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

		byte[] iv = getIV();
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
		byte[] results = cipher.doFinal(text.getBytes(CoreConstants.ENCODING_UTF8));
		return new String(Base64.encodeBase64(results), CoreConstants.ENCODING_UTF8);
	}

	/**
	 * Decrypts the input text where text is in base64 format with the set secret key (password).
	 */
	public String decrypt(String inputEncrypted) {
		try {
			return decrypt(inputEncrypted, getSecretKey());
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Decrypts the input text where text is in base64 format with the key password.
	 */
	public String decrypt(String text, String password) throws Exception {
		return decrypt(text, password, "AES/CBC/PKCS5Padding");
	}

	public String decrypt(String text, String password, String algorithm) throws Exception {
		Cipher cipher = Cipher.getInstance(algorithm);
		// setup key
		byte[] keyBytes = new byte[getKeySize()];
		byte[] b = password.getBytes(CoreConstants.ENCODING_UTF8);
		int len = b.length;
		if (len > keyBytes.length) {
			len = keyBytes.length;
		}
		System.arraycopy(b, 0, keyBytes, 0, len);
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(getIV());
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
		byte[] results = cipher.doFinal(Base64.decodeBase64(text.getBytes(CoreConstants.ENCODING_UTF8)));
		return new String(results, CoreConstants.ENCODING_UTF8);
	}

	public static void main(String[] args) {
		RijndaelEncryptionBean bean = new RijndaelEncryptionBean();
		String teststring = "1011783159";
		bean.setSecretKey("testlykill123");
		String encoded = bean.encrypt(teststring);
		System.out.println("inputString: '" + teststring + "' encrypts to: '" + encoded + "'");
	}

	protected IWMainApplication getIWMainApplication(){
		return IWMainApplication.getDefaultIWMainApplication();
	}

	/**
	 * @return Returns the iV.
	 */
	public byte[] getIV() {
		return this.IV;
	}

	/**
	 * @param iv The iV to set.
	 */
	public void setIV(byte[] iv) {
		this.IV = iv;
	}

	/**
	 * Get the Key Size (length) in bytes
	 * @return Returns the keySize.
	 */
	public int getKeySize() {
		return this.keySize;
	}

	/**
	 * Set the Key Size (length) in bytes
	 * @param keySize The keySize to set.
	 */
	public void setKeySize(int keySize) {
		this.keySize = keySize;
	}

	/**
	 * @return Returns the secretKey.
	 */
	private String getSecretKey() {
		if(this.secretKey==null){
			throw new RuntimeException("Secret key is not set");
		}
		return this.secretKey;
	}

	/**
	 * @param secretKey The secretKey to set.
	 */
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
}
