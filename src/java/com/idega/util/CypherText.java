/*
 * $Id: CypherText.java,v 1.1 2001/07/13 09:31:59 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.util;

import java.util.Vector;
import java.util.Collections;

/**
 *
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class CypherText {
  private StringBuffer cypherKey = null;

  public CypherText() {
  }

  public static void main(String[] args) {
    CypherText cypherText1 = new CypherText();
    cypherText1.doCypher("Palli var einn í heiminum");
  }

  public void doCypher(String text) {
    if (cypherKey == null)
      createKey();
    System.out.println("Before : " + text);
    System.out.println("CypherKey = " + cypherKey);

  }

  private void createKey() {
    cypherKey = new StringBuffer();
    for (int i = 0; i < 4000; i++) {
      char rnd = (char)(Math.random() * 256);
      cypherKey.append(rnd);
    }
  }

  private String cypher(String text) {
    return(null);
  }

  private String deCypher(String cypherText) {
    return(null);
  }
}