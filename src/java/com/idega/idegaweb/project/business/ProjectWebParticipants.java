package com.idega.idegaweb.project.business;

import com.idega.jmodule.object.*;
import com.idega.data.genericentity.Member;
import java.sql.SQLException;


/**
 * Title:        ProjectWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author idega - team 2001
 * @version 1.0
 */

public class ProjectWebParticipants extends JModuleObject {

  Table myTable;
  String[] Lang;
  String[] IS = {"iw","iw"};
  boolean first;
  String language;
  public ProjectWebProperties properties;

  public ProjectWebParticipants() {
  /*
    properties = new ProjectWebProperties();
    myTable = new Table(6,1);
      myTable.setCellspacing(0);
      myTable.setCellpadding(2);
   add(myTable);
   addHeader();
   Lang = IS;
   first = true;
   */
  }


  public void addHeader(){
/*            Text nafn = new Text(Lang[0]);
            nafn.setBold();
            nafn.setFontColor("#FFFFFF");
            Text stad = new Text(Lang[1]);  // Name
            stad.setBold();
            stad.setFontColor("#FFFFFF");
            Text simi = new Text(Lang[2]);  // Organisation
            simi.setBold();
            simi.setFontColor("#FFFFFF");
            Text fax  = new Text(Lang[3]);  // Phone
            fax.setBold();
            fax.setFontColor("#FFFFFF");
            Text emil = new Text(Lang[4]);  // Fax
            emil.setBold();
            emil.setFontColor("#FFFFFF");   // Email

            myTable.add(nafn,1,1);
            myTable.add(stad,2,1);
            myTable.add(simi,3,1);
            myTable.add(fax,5,1);
            myTable.add(emil,6,1);
  */
  }



  public void addMembers(Member[] members) throws SQLException{
/*
        int row = myTable.getRows();
        myTable.resize(myTable.getColumns(),row + members.length);

	for (int i = 0; i < members.length; i++){
          row++;
          myTable.add("Anna Sigríður Guðnadóttir",1,row);
          myTable.add("Landlæknir",2,row);
          myTable.add("5101900",3,row);
          myTable.add("5101919",5,row);
          myTable.add("<a href=\"mailto:anna@landlaeknir.is\">anna@landlaeknir.is</a>",6,row);

        }
*/
  }

  public void main(ModuleInfo modinfo) throws Exception {
    Lang = properties.getStringCollection(modinfo);

    if (first){
      myTable.setHorizontalZebraColored("#ABABAB","#EFEFEF");
      myTable.setRowColor(1,"#4D6476");
      first = false;
    }




  }



}