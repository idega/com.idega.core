package com.idega.util;



import com.idega.presentation.*;

//import com.idega.projects.golf.entity.*;

import java.sql.*;

import java.io.*;



/**

 * Title:        idegaUtil

 * Description:

 * Copyright:    Copyright (c) 2001

 * Company:      idega margmiðlun hf.

 * @author

 * @version 1.0

 */



public class Fix extends Block {





  boolean copy;

  IWContext iwc;

//  int min;

//  int max;



  public Fix() {

//  min = 0;

//  max = 1000;

  }

/*

  public void copy(boolean cp){

    copy = cp;

  }

*/

/*

  public void setRange(int selection_min, int selection_max){

  min = selection_min;

  max = selection_max;

  }

*/

/*

  public void copy_from_Member_To_UnionMemberInfo(int min, int max) throws SQLException, IOException {

    PrintStream ut = System.out;

    Connection conn= null;

    Statement Stmt= null;



    ut.print("inni\n<br>");



    conn = getConnection();



    ResultSet RS = null;

    String type = null;





    Member member = new Member();

//    Member[] members = (Member[])member.findAll();

    Member[] members = (Member[])member.findAll("select * from member where member_id >= " + min + " And member_id <= " + max );



    ut.print("findAll");

    UnionMemberInfo info = null;

    boolean next;



    for (int i = 0; i < members.length; i++) {

        Stmt = conn.createStatement();

        //ut.print("for: " + i);

        RS = Stmt.executeQuery("select * from union_member where member_id = "+members[i].getID());

        //ut.print("member: " + members[i].getID());

        next = RS.next();

        if (next){

          //ut.print(" <if>\n<br>");

          while(next){

           // ut.print(" <while>\n<br>");

            info = new UnionMemberInfo();

            if (members[i].getCardId() > 0)

              info.setCardId(members[i].getCardId());

            info.setComment(members[i].getComment());

            if (members[i].getFamilyId() > 0)

              info.setFamilyId(members[i].getFamilyId());

            info.setFirstInstallmentDate(members[i].getFirstInstallmentDate());

            info.setLockerNumber(members[i].getLockerNumber());

            info.setMemberID(members[i].getID());

            info.setMemberNumber(members[i].getMemberNumber());

            type = RS.getString("membership_type");

            if (type != null){

              info.setMembershipType(RS.getString("membership_type"));

            } else {

              info.setMembershipType("sub");

            }

            info.setMemberStatus("A");

            if (members[i].getPaymentTypeID() > 0)

              info.setPaymentTypeID(members[i].getPaymentTypeID());

            info.setPreferredInstallmentNr(members[i].getPreferredInstallmentNr());

            info.setUnionID(new Integer(RS.getString("union_id")));

            info.setVisible(true);

            info.insert();

            next = RS.next();

          }

        } else {

       //     ut.print("member: " + members[i].getID() + " >>else<<\n<br>");

            info = new UnionMemberInfo();

            if (members[i].getCardId() > 0)

              info.setCardId(members[i].getCardId());

            info.setComment(members[i].getComment());

            if (members[i].getFamilyId() > 0)

              info.setFamilyId(members[i].getFamilyId());

            info.setFirstInstallmentDate(members[i].getFirstInstallmentDate());

            info.setLockerNumber(members[i].getLockerNumber());

            info.setMemberID(members[i].getID());

            info.setMemberNumber(members[i].getMemberNumber());

            info.setMembershipType("main");

            info.setMemberStatus("A");

            if (members[i].getPaymentTypeID() > 0)

              info.setPaymentTypeID(members[i].getPaymentTypeID());

            info.setPreferredInstallmentNr(members[i].getPreferredInstallmentNr());

            info.setUnionID(new Integer(members[i].getMainUnionID()));

            info.setVisible(true);

            info.insert();

        }

    }



    ut.print("lokid");



    RS.close();

    Stmt.close();



    if (conn != null){

      freeConnection(conn);

    }





  }

  */

/*

  public void main(IWContext iwc2)throws Exception{

  this.iwc = iwc2;

  if(copy)

    copy_from_Member_To_UnionMemberInfo();

  }

*/

} // Class Fix
