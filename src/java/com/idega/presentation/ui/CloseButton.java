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
public class CloseButton extends GenericButton {

	public CloseButton() {
		setAsImageButton(true);
		setOnClick("top.window.close()");
		setValue("close");
	}

	public CloseButton(String displayString) {
		super();
		setName("");
		setValue(displayString);
		setInputType(INPUT_TYPE_BUTTON);
		setOnClick("top.window.close()");
	}

	public CloseButton(Image image) {
		super();
		setButtonImage(image);
		setOnClick("top.window.close()");
	}

}
