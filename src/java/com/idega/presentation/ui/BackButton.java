//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/
package com.idega.presentation.ui;

import java.io.*;
import java.util.*;
import com.idega.presentation.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class BackButton extends GenericButton {

	private Image defaultImage;
	private String howFarBackOrForward = "-1";

	public BackButton() {
		this("<=");
	}

	public BackButton(String displayString) {
		super();
		setName("");
		setValue(displayString);
		setInputType(INPUT_TYPE_BUTTON);
		setOnClick("history.go(" + this.howFarBackOrForward + ")");
	}

	public BackButton(Image defaultImage) {
		super();
		setOnClick("history.go(" + this.howFarBackOrForward + ")");
		this.defaultImage = defaultImage;
	}

	public void setHistoryMove(String howFarBackOrForward) {
		this.howFarBackOrForward = howFarBackOrForward;
	}

	public void setHistoryMove(int howFarBackOrForward) {
		setHistoryMove(String.valueOf(howFarBackOrForward));
	}
}