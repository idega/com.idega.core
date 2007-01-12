package com.idega.presentation;


import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import java.util.Vector;
import java.util.Iterator;
import com.idega.presentation.ui.Window;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.CloseButton;

/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public abstract class ConfirmWindow extends Window{

  public Text question;
  public Form myForm;

  public SubmitButton confirm;
  public CloseButton close;
  public Table myTable = null;

  public static final String PARAMETER_CONFIRM = "confirm";

  public Vector parameters;

  public ConfirmWindow(){
    super("ConfirmWindow",300,130);
    super.setBackgroundColor("#d4d0c8");
    super.setScrollbar(false);
    super.setAllMargins(0);

    this.question = Text.getBreak();
    this.myForm = new Form();
    this.parameters = new Vector();
    this.confirm = new SubmitButton(ConfirmWindow.PARAMETER_CONFIRM,"   Yes   ");
    this.close = new CloseButton("   No    ");
    // close.setOnFocus();
    initialize();

  }


  public void lineUpElements(){
    this.myTable = new Table(2,2);
    this.myTable.setWidth("100%");
    this.myTable.setHeight("100%");
    this.myTable.setCellpadding(5);
    this.myTable.setCellspacing(5);
    //myTable.setBorder(1);


    this.myTable.mergeCells(1,1,2,1);

    this.myTable.add(this.question,1,1);

    this.myTable.add(this.confirm,1,2);

    this.myTable.add(this.close,2,2);

    this.myTable.setAlignment(1,1,"center");
//      myTable.setAlignment(2,1,"center");
    this.myTable.setAlignment(1,2,"right");
    this.myTable.setAlignment(2,2,"left");

    this.myTable.setVerticalAlignment(1,1,"middle");
    this.myTable.setVerticalAlignment(1,2,"middle");
    this.myTable.setVerticalAlignment(2,2,"middle");

    this.myTable.setHeight(2,"30%");

    this.myForm.add(this.myTable);

  }

  public void setQuestion(Text Question){
    this.question = Question;
  }


  /*abstract*/
  public abstract void initialize();
  public abstract void actionPerformed(IWContext iwc)throws Exception;



  public void maintainParameter(String parameter){
    this.parameters.add(parameter);
  }


  public void _main(IWContext iwc) throws Exception {
    Iterator iter = this.parameters.iterator();
    while (iter.hasNext()) {
      String item = (String)iter.next();
      this.myForm.maintainParameter(item);
    }

    String confirmThis = iwc.getParameter(ConfirmWindow.PARAMETER_CONFIRM);

    if(confirmThis != null){
      this.actionPerformed(iwc);
      this.setParentToReload();
      this.close();
    } else{
      this.empty();
      if(this.myTable == null){
        lineUpElements();
      }
      this.add(this.myForm);
    }
    super._main(iwc);
  }

}

