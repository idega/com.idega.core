package com.idega.util;

import java.text.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega multimedia
 * @author       <a href="mailto:aron@idega.is">aron@idega.is</a>
 * @version 1.0
 */

public class IsCollator {

  private static final String isl = "< a,A< á,Á< b,B< c,C< d,D< ð,Ð< e,E< é,É< f,F< g,G< h,H< i,I< í,Í< j,J" +
                             "< k,K< l,L< m,M< n,N< o,O< ó,Ó< p,P< q,Q< r,R< s,S< t,T" +
                             "< u,U< ú,Ú< v,V< w,W< x,X< y,Y< ý,Ý< z,Z< þ,Þ< æ,Æ< ö,Ö";

  private RuleBasedCollator rbc;

  public IsCollator() {
    try {
      rbc = new RuleBasedCollator(isl);
    }
    catch (ParseException ex) {
      rbc = (RuleBasedCollator)Collator.getInstance();
    }
  }

  public static RuleBasedCollator getIsCollator(){
    RuleBasedCollator rbc;
    try {
      rbc = new RuleBasedCollator(isl);
    }
    catch (ParseException ex) {
      rbc = (RuleBasedCollator)Collator.getInstance();
    }
    return rbc;
  }

} // Class IsCollator