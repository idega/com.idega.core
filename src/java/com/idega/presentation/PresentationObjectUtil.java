/*
 * $Id: PresentationObjectUtil.java,v 1.2 2004/11/25 09:59:32 tryggvil Exp $
 * Created on 24.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation;

import javax.faces.component.UIComponent;


/**
 * This class contains utility methods moved from PresentationObjects to
 * be more easier accessible from classes not extending PresentationObject.
 * 
 *  Last modified: $Date: 2004/11/25 09:59:32 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class PresentationObjectUtil {
	
	
	public static Page getParentPage(UIComponent obj){
		Page returnPage = null;
		//UIComponent obj = null;
		if (obj instanceof IFrameContent)
		{
			obj = ((IFrameContent) obj).getOwnerInstance();
		}
		//else
		//{
			//try{
			//	obj = obj.getParent();
			//}
			//catch(ClassCastException cce){
			//	obj=null;
			//}
		//}
		while (obj != null)
		{
			if (obj instanceof Page)
			{
				returnPage = (Page) obj;
			}
			if (obj instanceof IFrameContent)
			{
				obj = ((IFrameContent) obj).getOwnerInstance();
			}
			else
			{
				obj = obj.getParent();
			}
		}
		if ((returnPage == null) && (obj instanceof Page))
		{
			return (Page) obj;
		}
		return returnPage;

	}
	
	
}
