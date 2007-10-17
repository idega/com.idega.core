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
	protected BASE64Encoder base64Encoder = new BASE64Encoder();
	protected BASE64Decoder base64Decoder = new BASE64Decoder();
	private int keySize = 32;
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
			//e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	/**
	 * Encrypts the input text with key password and AES/Rijndael algorithm and returns the resulting string in base64 format.
	 */
	public String encrypt(String text, String password) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		// setup key (default keysize is 16 bytes so the password will 
		// be shortened if it is longer.
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
		//BASE64Encoder encoder = new BASE64Encoder();
		return this.base64Encoder.encode(results);
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
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
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
		//BASE64Decoder decoder = new BASE64Decoder();
		byte[] results = cipher.doFinal(this.base64Decoder.decodeBuffer(text));
		return new String(results, CoreConstants.ENCODING_UTF8);
	}
/*
	public String encryptJCEOld(String sInputPlaintext) throws Exception {
		// byte[] keyBytes = "Kui29Sie46K7seTk".getBytes();
		byte[] keyBytes = secretKey.getBytes();
		char[] keyChars = secretKey.toCharArray();
		byte[] bInputPlainText = sInputPlaintext.getBytes(CoreConstants.ENCODING_UTF8);
		byte[] plainText = new byte[bInputPlainText.length * 2];
		byte[] salt = Integer.toString(secretKey.length()).getBytes("ASCII");
		// byte[] salt = keyBytes;
		for (int i = 0; i < bInputPlainText.length; i++) {
			// Enlarge the byte array to get a two-byte representation per
			// character
			plainText[i * 2] = bInputPlainText[i];
			plainText[(i * 2) + 1] = 0;
		}
		Provider provider = null;
		Provider[] providers = Security.getProviders();
		for (int i = 0; i < providers.length; i++) {
			Provider prov = providers[i];
			Set entries = prov.keySet();
			Iterator iter = entries.iterator();
			while (iter.hasNext()) {
				Object element = (Object) iter.next();
				String s = element.toString();
				if ((s.indexOf("AES") != -1)) {// ||s.indexOf("Rijndael")!=-1){
					provider = prov;
					System.out.println("Found algorithm: " + s + " in provider " + prov.getInfo());
				}
			}
			// if(provider!=null){
			// break;
			// }
		}
		// provider = new BouncyCastleProvider();
		// Cipher AesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding",
		// provider);
		Cipher AesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding", provider);
		SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
		// create ivspec
		
//		 int count = 1024; String MYPBEALG = "PBEWithSHA1AndDESede" ;
//		 PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, count);
//		 String password = secretKey;//"ssshh! a difficult secret" ;
//		 PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
//		 SecretKeyFactory keyFac = SecretKeyFactory.getInstance(MYPBEALG);
//		 SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec); Cipher
//		 pbeCipher = Cipher.getInstance(MYPBEALG);
//		 pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec); byte[] IV =
//		 pbeCipher.getIV() ; //actual internally generated IV byte[] key =
//		 pbeKey.getEncoded(); //this just returns the password as a byte[]
		 
		// SecretKeySpec KeySpec = new SecretKeySpec(pbeKey.getEncoded(),
		// "AES");
		IvParameterSpec ivspec = new IvParameterSpec("testvector123456".getBytes(CoreConstants.ENCODING_UTF8));
		// PBEKeySpec KeySpec = new PBEKeySpec(keyChars,salt,1);
		// SecretKey secretKey =
		// SecretKeyFactory.getInstance("PBEWithMD5AndDES",provider).generateSecret(KeySpec);
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(secretKey.getEncoded());
		md.update(salt);
		int iterationCount = 2;
		for (int i = 1; i < iterationCount; i++) {
			md.update(md.digest());
		}
		byte[] newKeyBytes = md.digest();
		SecretKeySpec newSecretKey = new SecretKeySpec(newKeyBytes, "AES");
		AesCipher.init(Cipher.ENCRYPT_MODE, newSecretKey, ivspec);
		byte[] ciphertext = AesCipher.doFinal(plainText);
		String returnString = base64Encoder.encode(ciphertext);
		return returnString;
	}

	public String encryptBouncy(String sInputPlaintext) throws Exception {
		SecureRandom mSecureRandom = new SecureRandom();
		byte[] salt = Integer.toString(secretKey.length()).getBytes("ASCII");
		AESLightEngine blockCipher = new AESLightEngine();
		// BlockCipher cipher = blockCipher;
		int blockSizeBits = 8;
		int blockSize = blockCipher.getBlockSize();
		// Create an IV of random data.
		byte[] iv = new byte[blockSize];
		mSecureRandom.nextBytes(iv);
		byte[] bSecretKey = secretKey.getBytes();
		CipherParameters key = new KeyParameter(bSecretKey);
		// ParametersWithSalt parametersWithSalt = new
		// ParametersWithSalt(key,salt);
		ParametersWithIV parametersWithIV = new ParametersWithIV(key, iv);
		// ParametersWithSalt parametersWithSalt = new
		// ParametersWithSalt(parametersWithIV,salt);
		CipherParameters parameters = parametersWithIV;
		CFBBlockCipher cfbCipher = new CFBBlockCipher(blockCipher, blockSizeBits);
		StreamCipher streamCipher = new StreamBlockCipher(cfbCipher);
		StreamCipher cipher = streamCipher;
		// Concatenate the IV and the message.
		byte[] messageBytes1 = sInputPlaintext.getBytes(CoreConstants.ENCODING_UTF8);
		byte[] messageBytes = new byte[messageBytes1.length * 2];
		for (int i = 0; i < messageBytes1.length; i++) {
			// Enlarge the byte array to get a two-byte representation per
			// character
			messageBytes[i * 2] = messageBytes1[i];
			messageBytes[(i * 2) + 1] = 0;
		}
		
//		 int length = messageBytes.length + blockCipher.getBlockSize(); byte[]
//		 plaintext = new byte[length]; System.arraycopy(iv, 0, plaintext, 0,
//		 iv.length); System.arraycopy(messageBytes, 0, plaintext, iv.length,
//		 messageBytes.length);
//		 
		byte[] plaintext = messageBytes;
		// Encrypt the plaintext.
		byte[] ciphertext = new byte[plaintext.length];
		cipher.init(true, parameters);
		cipher.processBytes(plaintext, 0, plaintext.length, ciphertext, 0);
		// cipher.processBlock(plaintext, 0, ciphertext, 0);
		String returnString = base64Encoder.encode(ciphertext);
		return returnString;
	}

	public String encryptRijndael1(String sInputPlaintext) throws Exception {
		Rijndael_Algorithm algoritm = new Rijndael_Algorithm();
		byte[] bInputPlaintext = null;
		try {
			bInputPlaintext = sInputPlaintext.getBytes("UTF-16");
		}
		catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		byte[] key = null;
		key = secretKey.getBytes();
		int keyLengthBytes = key.length;
		int keyLengthBits = keyLengthBytes * 8;
		Object sessionKey = null;
		int blockSizeBytes = 16;
		if (bInputPlaintext.length < 16) {
			// make the block size shorter for a shorter inputString
			blockSizeBytes = bInputPlaintext.length;
		}
		try {
			sessionKey = algoritm.makeKey(key, blockSizeBytes);
		}
		catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		
//		 //Initialization Vector: String sInitializationVector =
//		 "testvector123456"; byte[] bInitializationVector=null;
//		 bInitializationVector = sInitializationVector.getBytes();
//		  
//		  //Concatenate the IV and the bInputPlaintext. int length =
//		  bInputPlaintext.length + bInitializationVector.length; byte[]
//		  bInputEncryptionText = new byte[length];
//		  System.arraycopy(bInitializationVector, 0, bInputEncryptionText, 0,
//		  bInitializationVector.length); System.arraycopy(bInputPlaintext, 0,
//		  bInputEncryptionText, bInitializationVector.length,
//		  bInputPlaintext.length);
		 
		int offset = 0;// bInitializationVector.length;
		byte[] encrypted = algoritm.blockEncrypt(bInputPlaintext, offset, sessionKey, blockSizeBytes);
		String returnString = base64Encoder.encode(encrypted);
		// String returnString = new String(encrypted);
		return returnString;
	}
*/
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
