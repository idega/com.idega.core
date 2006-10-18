/*
 * $Id: CypherText.java,v 1.3.6.1 2006/10/18 13:53:58 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.util;

import com.idega.idegaweb.IWApplicationContext;

/**
 * 
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class CypherText {
//	private final String alphabet_ = "nk2cmqYgidw9spTuyDXFZt4HA8Ma3xEr75QClBV0IUPo6LezWNbOv1SjRfJGKh";
//	private String alphabet = "gidw9spTuyDXFZt4HA8Ma3xEr75QClBV0IUPo6LezWNbOv1SjRfJGKhnk2cmqY";
	private final static String ALPHABET_KEY = "alphabet_key";
	
	private String alphabet = null;

	private String cypherKey = null;

	public CypherText() {
		alphabet = "nk2cmqYgidw9spTuyDXFZt4HA8Ma3xEr75QClBV0IUPo6LezWNbOv1SjRfJGKh";
	}

	public CypherText(IWApplicationContext iwac) {
		alphabet = iwac.getApplicationSettings().getProperty(ALPHABET_KEY, "nk2cmqYgidw9spTuyDXFZt4HA8Ma3xEr75QClBV0IUPo6LezWNbOv1SjRfJGKh");
	}

	public String doCyper(String text, String cypherKey)
			throws NullPointerException {
		setKey(cypherKey);

		return cypher(text);
	}

	public String doDeCypher(String text, String cypherKey)
			throws NullPointerException {
		setKey(cypherKey);

		return deCypher(text);
	}

	/**
	 * Returns the current cypher key for this object. If the cypher key is null
	 * a new cypher key is created and returned.
	 * 
	 * @param length The Length of the key
	 * 
	 * @return
	 */
	public String getKey(int length) {
		if (cypherKey == null) {
			createKey(length);
		}

		if (cypherKey.length() != length) {
			createKey(length);
		}

		return cypherKey;
	}

	public void setKey(String key) throws NullPointerException {
		if (key == null) {
			throw (new NullPointerException("Cypher key is null"));
		}

		cypherKey = key;
	}

	private void createKey(int length) {
		StringBuffer key = new StringBuffer();
		for (int i = 0; i < length; i++) {
			char rnd = (char) (Math.random() * alphabet.length());
			key.append(alphabet.charAt(rnd));
		}

		cypherKey = key.toString();
	}

	private String cypher(String text) throws NullPointerException,
			IllegalArgumentException {
		if (text == null) {
			throw (new NullPointerException("No text to cypher"));
		}

		boolean isAlphabet = checkAlphabet(text);
		if (!isAlphabet) {
			throw (new IllegalArgumentException(
					"Text contains characters which are not cypherable. Please use only [a..z][A..Z][0..9]"));
		}

		StringBuffer cyphered = new StringBuffer();

		while (cypherKey.length() < text.length()) {
			cypherKey = cypherKey.concat(cypherKey);
		}

		for (int i = 0; i < text.length(); i++) {
			int a = getIntValue(text.charAt(i));
			int b = getIntValue(cypherKey.charAt(i));
			int c = (a + b) % alphabet.length();

			cyphered.append(alphabet.charAt(c));
		}

		String reversed = cyphered.toString();
		reversed = reverse(reversed);

		return reversed;
	}

	public String reverse(String text) {
		String ret = new String();
		for (int i = text.length() - 1; i >= 0; i--) {
			ret = ret.concat(String.valueOf(text.charAt(i)));
		}

		return ret;
	}

	private boolean checkAlphabet(String text) {
		for (int i = 0; i < alphabet.length(); i++) {
			text = text.replace(alphabet.charAt(i), ' ');
		}

		text = text.trim();
		if ((text == null) || (text.equalsIgnoreCase(""))) {
			return true;
		} else {
			return false;
		}
	}

	private String deCypher(String cypherText) throws NullPointerException {
		if (cypherText == null) {
			throw (new NullPointerException("No text to decypher"));
		}

		StringBuffer decyphered = new StringBuffer();
		cypherText = reverse(cypherText);

		while (cypherKey.length() < cypherText.length()) {
			cypherKey = cypherKey.concat(cypherKey);
		}

		for (int i = 0; i < cypherText.length(); i++) {
			int a = getIntValue(cypherText.charAt(i));
			int b = getIntValue(cypherKey.charAt(i));
			int c = (a - b) % alphabet.length();

			while (c < 0) {
				c += alphabet.length();
			}

			decyphered.append(alphabet.charAt(c));
		}

		return decyphered.toString();
	}

	private int getIntValue(char a) {
		return alphabet.indexOf(a);
	}
}