/*

 * $Id: RadioButton.java,v 1.5 2002/06/12 18:28:23 laddi Exp $

 *

 * Copyright (C) 2001 Idega hf. All Rights Reserved.

 *

 * This software is the proprietary information of Idega hf.

 * Use is subject to license terms.

 *

 */

package com.idega.presentation.ui;



import java.io.IOException;

import com.idega.presentation.IWContext;



/**

 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

 * @version 1.2

 */

public class RadioButton extends InterfaceObject {

  public String _checked;



  /**

   *

   */

  public RadioButton() {

    this("untitled");

  }



  /**

   *

   */

  public RadioButton(String name) {

    this(name,"unspecified");

  }



  /**

   *

   */

  public RadioButton(String name, String value) {

    super();

    setName(name);

    setContent(value);

    _checked = "";

  }



  /**

   *

   */

  public void setSelected() {

    _checked = "checked";

  }



  public boolean getSelected() {

    if (_checked.equals("checked"))

      return(true);

    else

      return(false);

  }



  /**

   *

   */

  public void print(IWContext iwc) throws IOException {


    if (getLanguage().equals("HTML")){

      handleKeepStatus(iwc);



      println("<input type=\"radio\" name=\"" + getName() + "\" " + _checked + "  " + getAttributeString() + " >");

    }

  }



  /**

   * Sets the radio button to submit automatically.

   * Must add to a form before this function is used!!!!

   */

  public void setToSubmit() {

    setOnClick("this.form.submit()");

  }



  public synchronized Object clone() {

    RadioButton obj = null;

    try {

      obj = (RadioButton)super.clone();

      obj._checked = _checked;

    }

    catch(Exception ex) {

      ex.printStackTrace(System.err);

    }



    return obj;

  }



  public void handleKeepStatus(IWContext iwc) {

    if (statusKeptOnAction()) {

      String[] parameters = iwc.getParameterValues(getName());

      if (parameters != null) {

        for (int i = 0; i < parameters.length; i++) {

          if (parameters[i].equals(getValue())) {

            setSelected();

          }

        }

      }

    }

  }



  public void main(IWContext iwc) {

    handleKeepStatus(iwc);

  }

}
