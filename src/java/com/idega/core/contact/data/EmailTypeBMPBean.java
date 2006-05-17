package com.idega.core.contact.data;

import java.sql.SQLException;
import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.data.IDOException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDOQuery;

/**
 * Title:        IW Core
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class EmailTypeBMPBean extends com.idega.core.data.GenericTypeBMPBean implements  EmailType{
	
	public static final String MAIN_EMAIL = "main";

	public EmailTypeBMPBean(){
    super();
  }

  public EmailTypeBMPBean(int id)throws SQLException{
    super(id);
  }

  public String getEntityName() {
    return "ic_email_type";
  }
  
	public Object ejbFindMainEmailType() throws FinderException
	{
		return ejbFindEmailTypeByUniqueName(MAIN_EMAIL);
	}
	
	public Object ejbFindEmailTypeByUniqueName(String uniqueName) throws FinderException
	{
		IDOQuery query = idoQueryGetSelect().appendWhereEqualsQuoted(getColumnNameUniqueName(), uniqueName);
		return idoFindOnePKByQuery(query);
	}

  
  public boolean ejbHomeUpdateStartData() throws IDOException, IDOLookupException, CreateException {
	  IDOQuery query = idoQueryGetSelectCount();
	  query.appendWhereEqualsQuoted(getColumnNameUniqueName(), EmailTypeBMPBean.MAIN_EMAIL);
	  int i = idoGetNumberOfRecords(query);
	  if (i <= 0) {
		EmailTypeHome home = (EmailTypeHome) IDOLookup.getHome(EmailType.class);
		EmailType email = home.create();
		email.setUniqueName(EmailTypeBMPBean.MAIN_EMAIL);
		email.setName(EmailTypeBMPBean.MAIN_EMAIL);
		email.setDescription("Main email");
		email.store();
		return true;
	  }
	  return true; 
  }
}
