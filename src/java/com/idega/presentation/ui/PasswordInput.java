//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/
package com.idega.presentation.ui;




/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class PasswordInput extends TextInput{

	/**
	 * Constructs a new <code>PasswordInput</code> with the name "untitled".
	 */
  public PasswordInput(){
	  this("untitled");
  }

	/**
	 * Constructs a new <code>PasswordInput</code> with the given name.
	 */
  public PasswordInput(String name){
	  super(name);
		setInputType(INPUT_TYPE_PASSWORD);
  }

	/**
	 * Constructs a new <code>PasswordInput</code> with the given name and sets the given
	 * content.
	 */
  public PasswordInput(String name,String content){
	  super(name,content);
		setInputType(INPUT_TYPE_PASSWORD);
  }

}