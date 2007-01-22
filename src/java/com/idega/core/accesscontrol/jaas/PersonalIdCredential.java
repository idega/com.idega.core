/**
 * 
 */
package com.idega.core.accesscontrol.jaas;

import javax.security.auth.DestroyFailedException;


/**
 * <p>
 * Credential implementing information about PersonalId
 * </p>
 *  Last modified: $Date: 2007/01/22 08:10:28 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1.2.1 $
 */
public class PersonalIdCredential implements IWCredential {

	String credentialName="personalId";
	String personalId;
	
	public PersonalIdCredential(String personalId){
		this.personalId=personalId;
	}
	
	/* (non-Javadoc)
	 * @see com.idega.core.accesscontrol.jaas.IWCredential#getKey()
	 */
	public Object getKey() {
		return getPersonalId();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.accesscontrol.jaas.IWCredential#getName()
	 */
	public String getName() {
		return credentialName;
	}

	/* (non-Javadoc)
	 * @see javax.security.auth.Destroyable#destroy()
	 */
	public void destroy() throws DestroyFailedException {
		personalId=null;
	}

	/* (non-Javadoc)
	 * @see javax.security.auth.Destroyable#isDestroyed()
	 */
	public boolean isDestroyed() {
		// TODO Auto-generated method stub
		return false;
	}

	
	/**
	 * @return the personalId
	 */
	public String getPersonalId() {
		return personalId;
	}

	
	/**
	 * @param personalId the personalId to set
	 */
	public void setPersonalId(String personalId) {
		this.personalId = personalId;
	}
}
