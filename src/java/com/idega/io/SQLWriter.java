/*
 * @(#)StringWriter.java	1.18 00/02/02
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the proprietary information of Sun Microsystems, Inc.
 * Use is subject to license terms.
 *
 */

package com.idega.io;


/**
 * A character stream that collects its output in a string buffer, which can
 * then be used to construct a string.
 *
  * Title:
  * Description:
  * Copyright:    Copyright (c) 2001
  * Company:      idega.is
  * @author 2000 - idega team - <br><a href="mailto:aron@idega.is">Aron Birkir</a><br>
  * @version 1.0
  */

public class SQLWriter extends java.io.StringWriter {

  public SQLWriter(){
    super();
    init();
  }

  public SQLWriter(int initialSize){
    super(initialSize);
    init();
  }

  public void init(){
    write("select");
  }

}
