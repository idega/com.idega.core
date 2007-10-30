/**
 * 
 */
package com.idega.util.encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import com.idega.idegaweb.IWMainApplication;

/**
 * <p>
 * Class that encrypts and decrypts a string with the AES/Rijndael algorithm
 * with a provided key. <br/> Note that the default key length is 32 bytes
 * (256-bits) but the default maximum key-length in the standard Java
 * distributions is 128 bits. This can be remedied by downloading the "Unlimited
 * Strength Jurisdiction Policy Files" on the Java Download pages:
 * http://java.sun.com/j2se/1.5.0/download.jsp or
 * http://java.sun.com/j2se/1.4.2/download.html and replacing two jar files in
 * the JDK. <br/> This encryption implementation is extended and used by
 * MentorEncryptionBean in module is.mentor.
 * </p>
 * Last modified: $Date: 2007/10/30 12:40:42 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.2.2.1 $
 */
public class RijndaelEncryptionBean {

	private String secretKey = null;
	protected BASE64Encoder base64Encoder = new BASE64Encoder();
	protected BASE64Decoder base64Decoder = new BASE64Decoder();
	private int keySize = 32;
	private byte[] IV = new byte[] { 0x43, 0x32, 0x11, 0x3a, 0x50, 0x37, 0x15, 0x74, 0x56, 0x63, 0x43, 0x56, 0x65, 0x64, 0x2a, 0x38 };

	public RijndaelEncryptionBean() {
	}

	/**
	 * Encrypts with the set secretKey and returns the resulting string in base64
	 * format.
	 */
	public String encrypt(String inputPlainText) {
		try {
			return encrypt(inputPlainText, getSecretKey());
		}
		catch (Exception e) {
			//e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * Encrypts the input text with key password and AES/Rijndael algorithm and
	 * returns the resulting string in base64 format.
	 */
	public String encrypt(String text, String password) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		// setup key (default keysize is 16 bytes so the password will 
		// be shortened if it is longer.
		byte[] keyBytes = new byte[getKeySize()];
		byte[] b = password.getBytes("UTF-8");
		int len = b.length;
		if (len > keyBytes.length) {
			len = keyBytes.length;
		}
		System.arraycopy(b, 0, keyBytes, 0, len);
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

		byte[] iv = getIV();
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

		byte[] results = cipher.doFinal(text.getBytes("UTF-8"));
		return this.base64Encoder.encode(results);
	}

	/**
	 * Decrypts the input text where text is in base64 format with the set secret
	 * key (password).
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
	 * Decrypts the input text where text is in base64 format with the key
	 * password.
	 */
	public String decrypt(String text, String password) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		// setup key
		byte[] keyBytes = new byte[getKeySize()];
		byte[] b = password.getBytes("UTF-8");
		int len = b.length;
		if (len > keyBytes.length) {
			len = keyBytes.length;
		}
		System.arraycopy(b, 0, keyBytes, 0, len);
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(getIV());
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

		byte[] results = cipher.doFinal(this.base64Decoder.decodeBuffer(text));
		return new String(results, "UTF-8");
	}

	public static void main(String[] args) {
		RijndaelEncryptionBean bean = new RijndaelEncryptionBean();
		String teststring = "0610703899";
		bean.setSecretKey("6r49twQhW3LMMYqG");
		bean.setIV("izyIRPoslwlSUZkZ".getBytes());
		bean.setKeySize(16);
		String encoded = bean.encrypt(teststring);
		System.out.println("inputString: '" + teststring + "' encrypts to: '" + encoded + "'");
		System.out.println("encryptedstring: '" + encoded + "' decrypts to: '" + bean.decrypt("wQVyxnWBgXCrF3volOqJAA=="));
	}

	protected IWMainApplication getIWMainApplication() {
		return IWMainApplication.getDefaultIWMainApplication();
	}

	/**
	 * @return Returns the iV.
	 */
	public byte[] getIV() {
		return this.IV;
	}

	/**
	 * @param iv
	 *          The iV to set.
	 */
	public void setIV(byte[] iv) {
		this.IV = iv;
	}

	/**
	 * Get the Key Size (length) in bytes
	 * 
	 * @return Returns the keySize.
	 */
	public int getKeySize() {
		return this.keySize;
	}

	/**
	 * Set the Key Size (length) in bytes
	 * 
	 * @param keySize
	 *          The keySize to set.
	 */
	public void setKeySize(int keySize) {
		this.keySize = keySize;
	}

	/**
	 * @return Returns the secretKey.
	 */
	private String getSecretKey() {
		if (this.secretKey == null) {
			throw new RuntimeException("Secret key is not set");
		}
		return this.secretKey;
	}

	/**
	 * @param secretKey
	 *          The secretKey to set.
	 */
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
}