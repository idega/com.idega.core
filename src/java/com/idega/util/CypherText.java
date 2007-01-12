/*
 * $Id: CypherText.java,v 1.3.6.2 2007/01/12 19:31:41 idegaweb Exp $
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
		this.alphabet = "nk2cmqYgidw9spTuyDXFZt4HA8Ma3xEr75QClBV0IUPo6LezWNbOv1SjRfJGKh";
	}

	public CypherText(IWApplicationContext iwac) {
		this.alphabet = iwac.getApplicationSettings().getProperty(ALPHABET_KEY, "nk2cmqYgidw9spTuyDXFZt4HA8Ma3xEr75QClBV0IUPo6LezWNbOv1SjRfJGKh");
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
		if (this.cypherKey == null) {
			createKey(length);
		}

		if (this.cypherKey.length() != length) {
			createKey(length);
		}

		return this.cypherKey;
	}

	public void setKey(String key) throws NullPointerException {
		if (key == null) {
			throw (new NullPointerException("Cypher key is null"));
		}

		this.cypherKey = key;
	}

	private void createKey(int length) {
		StringBuffer key = new StringBuffer();
		for (int i = 0; i < length; i++) {
			char rnd = (char) (Math.random() * this.alphabet.length());
			key.append(this.alphabet.charAt(rnd));
		}

		this.cypherKey = key.toString();
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

		while (this.cypherKey.length() < text.length()) {
			this.cypherKey = this.cypherKey.concat(this.cypherKey);
		}

		for (int i = 0; i < text.length(); i++) {
			int a = getIntValue(text.charAt(i));
			int b = getIntValue(this.cypherKey.charAt(i));
			int c = (a + b) % this.alphabet.length();

			cyphered.append(this.alphabet.charAt(c));
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
		for (int i = 0; i < this.alphabet.length(); i++) {
			text = text.replace(this.alphabet.charAt(i), ' ');
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

		while (this.cypherKey.length() < cypherText.length()) {
			this.cypherKey = this.cypherKey.concat(this.cypherKey);
		}

		for (int i = 0; i < cypherText.length(); i++) {
			int a = getIntValue(cypherText.charAt(i));
			int b = getIntValue(this.cypherKey.charAt(i));
			int c = (a - b) % this.alphabet.length();

			while (c < 0) {
				c += this.alphabet.length();
			}

			decyphered.append(this.alphabet.charAt(c));
		}

		return decyphered.toString();
	}

	private int getIntValue(char a) {
		return this.alphabet.indexOf(a);
	}
}