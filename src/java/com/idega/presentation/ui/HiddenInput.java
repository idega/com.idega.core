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
public class HiddenInput extends Parameter{

public HiddenInput(){
	this("untitled");
}

public HiddenInput(String name){
	this(name,"unspecified");
}

public HiddenInput(String name,String value){
        super(name,value);
}




}

