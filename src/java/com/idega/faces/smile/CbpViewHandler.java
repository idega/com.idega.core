/*
 * Created on 21.6.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.idega.faces.smile;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.faces.application.ViewHandler;

import net.sourceforge.smile.application.CbpViewHandlerImpl;

/**
 * @author tryggvil
 *
 */
public class CbpViewHandler extends CbpViewHandlerImpl{

	private ViewHandler parentViewHandler;
	
	/**
	 * @return Returns the defaultViewHandler.
	 */
	public ViewHandler getParentViewHandler() {
		
		return parentViewHandler;
	}
	/**
	 * @param defaultViewHandler The defaultViewHandler to set.
	 */
	public void setParentViewHandler(ViewHandler parentViewHandler) {
		this.parentViewHandler = parentViewHandler;
	}
	
	/**
	 * Breaks down the URL string separated by the '/' charachter to an array.<br>
	 * For instance it breaks down the URL "/component/78909" to {"component","78909"}
	 * @return
	 */
	protected String[] breakDownURL(String urlString) {
		//Performance optimization to handle the first 4 parameters without having to construct the Vector
		String s1=null;
		String s2=null;
		String s3=null;
		String s4=null;
		List list = null;
		StringTokenizer st = new StringTokenizer(urlString,"/");
		int index=0;
		while(st.hasMoreTokens()){
			index++;
			if(index==1){
				s1=st.nextToken();
			}
			else if(index==2){
				s2=st.nextToken();
			}
			else if(index==3){
				s3=st.nextToken();
			}
			else if(index==4){
				s4=st.nextToken();
			}
			else if(index==5){
				list=new ArrayList();
				list.add(s1);
				list.add(s2);
				list.add(s3);
				list.add(s4);
				list.add(st.nextToken());
			}
			else if(index>5){
				st.nextToken();
			}	
		}
		if(index==1){
			String[] theReturn={s1};
			return theReturn;
		}
		else if(index==2){
			String[] theReturn={s1,s2};
			return theReturn;
		}
		else if(index==3){
			String[] theReturn={s1,s2,s3};
			return theReturn;
		}
		else if(index==4){
			String[] theReturn={s1,s2,s3,s4};
			return theReturn;
		}
		else if(index>4){
			String[] theReturn = (String[])list.toArray(new String[0]);
			return theReturn;
		}
		return null;
	}
	
}
