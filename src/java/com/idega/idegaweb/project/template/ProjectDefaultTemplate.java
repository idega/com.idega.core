//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.idegaweb.project.template;

import com.idega.jmodule.object.*;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/
public class ProjectDefaultTemplate extends Page{

  private Table table;

    public ProjectDefaultTemplate(){
        table = new Table(3,3);
        table.setBorder(2);
        table.setRowColor(1,"black");
        table.setWidth("100%");
        table.setHeight("100%");
        super.add("header");
        super.addBreak();
        super.add(new Image("/pics/bull"));

        super.add(table);

    }

    public void add(ModuleObject obj){
      table.add(obj,2,2);
    }

/*
    protected void prepareClone(ModuleObject newObjToCreate){
      super.prepareClone(newObjToCreate);
      //DefaultTemplate newPage = (DefaultTemplate)newObjToCreate;
      //newPage.table=this.table.clone();
    }

*/

} // class DefaultTemplate
