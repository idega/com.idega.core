/*
 *Copyright 2000 idega.is All Rights Reserved.
 */

package com.idega.presentation.ui;

/**
 * 
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson </a>
 * 
 * @version 1.2
 *  
 */
public class FloatInput extends TextInput {

    public FloatInput() {
        this("untitled");
        setAsFloat();
    }

    public FloatInput(String name) {
        super(name);
        setAsFloat();
    }

    public FloatInput(String name, String errorWarning) {
        super(name);
        setAsFloat(errorWarning);
    }

    public FloatInput(String name, float value) {
        super(name, Float.toString(value));
        setAsFloat();
    }

    public FloatInput(String name, float value, String errorWarning) {
        super(name, Float.toString(value));
        setAsFloat(errorWarning);
    }

    public void setValue(float value) {
        setValue(Float.toString(value));
    }

    public void setValue(Float value) {
        setValue(value.toString());
    }
}