/*
 * Copyright (C) 2004 Idega software. All Rights Reserved.
 *
 * This software is the proprietary information of Idega software.
 * Use is subject to license terms.
 *
 */

package com.idega.presentation.ui;

/**
 * @author palli
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DoubleInput extends TextInput {
 
    public DoubleInput() {
        this("untitled");
        setAsDouble();
    }

    public DoubleInput(String name) {
        super(name);
        setAsDouble();
    }

    public DoubleInput(String name, String errorWarning) {
        super(name);
        setAsDouble(errorWarning);
    }

    public DoubleInput(String name, double value) {
        super(name, Double.toString(value));
        setAsDouble();
    }

    public DoubleInput(String name, double value, String errorWarning) {
        super(name, Double.toString(value));
        setAsDouble(errorWarning);
    }

    public void setValue(double value) {
        setValue(Double.toString(value));
    }

    public void setValue(Double value) {
        setValue(value.toString());
    }
}