/*
 * $Id: GenderBusinessBean.java,v 1.1 2005/05/19 11:15:54 laddi Exp $
 * Created on May 17, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.business;

import java.util.ArrayList;
import java.util.Collection;
import javax.ejb.FinderException;
import com.idega.business.IBORuntimeException;
import com.idega.business.IBOServiceBean;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.user.data.Gender;
import com.idega.user.data.GenderHome;


/**
 * Last modified: $Date: 2005/05/19 11:15:54 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class GenderBusinessBean extends IBOServiceBean  implements GenderBusiness{
	
	private GenderHome getHome() {
		try {
			return (GenderHome) IDOLookup.getHome(Gender.class);
		}
		catch (IDOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}
	
	public Gender getGender(Integer primaryKey) throws FinderException {
		return getHome().findByPrimaryKey(primaryKey);
	}
	
	public Collection getAllGenders() {
		try {
			return getHome().findAllGenders();
		}
		catch (FinderException fe) {
			fe.printStackTrace();
			return new ArrayList();
		}
	}

	public Gender getMaleGender() throws FinderException {
		return getHome().getMaleGender();
	}
	
	public Gender getFemaleGender() throws FinderException {
		return getHome().getFemaleGender();
	}
}