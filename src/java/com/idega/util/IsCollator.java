package com.idega.util;
import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega multimedia
 * @author       <a href="mailto:aron@idega.is">aron@idega.is</a>
 * @version 1.0
 * @deprecated when ordering for specific locales one can use java.util.Collator collator = Collator.getInstance(java.util.Locale);

 */
public class IsCollator {
	private static final String isl =
		"< 1,1< 2,2< 3,3< 4,4< 5,5< 6,6< 7,7< 8,8< 9,9"
			+ "< a,A< á,Á< b,B< c,C< d,D< ð,Ð< e,E< é,É< f,F< g,G< h,H< i,I< í,Í< j,J"
			+ "< k,K< l,L< m,M< n,N< o,O< ó,Ó< p,P< q,Q< r,R< s,S< t,T"
			+ "< u,U< ú,Ú< v,V< w,W< x,X< y,Y< ý,Ý< z,Z< þ,Þ< æ,Æ< ö,Ö";
			
	public IsCollator() {
	}
	
	public static RuleBasedCollator getIsCollator() {
		RuleBasedCollator rbc;
		try {
			rbc = new RuleBasedCollator(isl);
		}
		catch (ParseException ex) {
			rbc = (RuleBasedCollator) Collator.getInstance();
		}
		return rbc;
	}
} // Class IsCollator
