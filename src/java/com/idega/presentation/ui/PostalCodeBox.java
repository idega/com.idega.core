/*
 * $Id: PostalCodeBox.java,v 1.1.2.1 2007/01/26 05:47:28 idegaweb Exp $
 * Created on 26.1.2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.ui;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import javax.ejb.FinderException;
import com.idega.business.IBOLookup;
import com.idega.business.IBORuntimeException;
import com.idega.business.InputHandler;
import com.idega.core.location.business.AddressBusiness;
import com.idega.core.location.data.Country;
import com.idega.core.location.data.PostalCode;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;

/**
 * @author laddi
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PostalCodeBox extends SelectionBox implements InputHandler {

	public void main(IWContext iwc) {
		try {
			Country country = getAddressBusiness(iwc).getCountryHome().findByCountryName("Iceland");			
			
			if( country!=null ){
				Collection postals = getAddressBusiness(iwc).getPostalCodeHome().findAllByCountryIdOrderedByPostalCode(((Integer)country.getPrimaryKey()).intValue());
				Iterator iter = postals.iterator();
				while (iter.hasNext()) {
					PostalCode element = (PostalCode) iter.next();
					String id = element.getPostalCode();
					String code = element.getPostalAddress();
					if( code!=null ) {
						addMenuElement(id,code);
					}						
				}
			}
			else {
				addMenuElement("-1", "No country selected");
			}
		}
		catch (RemoteException re) {
			re.printStackTrace();
		}
		catch (FinderException fe) {
			fe.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.idega.business.InputHandler#getDisplayNameOfValue(java.lang.Object, com.idega.presentation.IWContext)
	 */
	public String getDisplayForResultingObject(Object value, IWContext iwc) {
		if (value != null) {
			Iterator iter = ((Collection) value).iterator();
			StringBuffer names = new StringBuffer();

			while (iter.hasNext()) {
				names.append((String) iter.next());
				if (iter.hasNext()) {
					names.append(", ");
				}
			}

			return names.toString();
		}
		return "";
	}
	
	/* (non-Javadoc)
	 * @see com.idega.business.InputHandler#getHandlerObject(java.lang.String, java.lang.String, com.idega.presentation.IWContext)
	 */
	public PresentationObject getHandlerObject(String name, String stringValue, IWContext iwc) {
		setName(name);
		return this;
	}
	
	/* (non-Javadoc)
	 * @see com.idega.business.InputHandler#getResultingObject(java.lang.String[], com.idega.presentation.IWContext)
	 */
	public Object getResultingObject(String[] values, IWContext iwc) throws Exception {
		Collection codes = new ArrayList();
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				codes.add(values[i]);
			}
		}
		return codes;
	}

	private AddressBusiness getAddressBusiness(IWApplicationContext iwc) {
		try {
			return (AddressBusiness) IBOLookup.getServiceInstance(iwc,AddressBusiness.class);
		}
		catch (RemoteException e) {
			throw new IBORuntimeException(e);
		}
	}

	public PresentationObject getHandlerObject(String name, Collection values, IWContext iwc) {
		String value = (String) Collections.min(values);
		return getHandlerObject(name, value, iwc);
	}


	/* (non-Javadoc)
	 * @see com.idega.business.InputHandler#convertResultingObjectToType(java.lang.Object, java.lang.String)
	 */
	public Object convertSingleResultingObjectToType(Object value, String className) {
		return value;
	}
}