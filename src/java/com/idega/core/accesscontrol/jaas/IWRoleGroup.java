/*
 * $Id: IWRoleGroup.java,v 1.1.2.1 2007/01/12 19:31:30 idegaweb Exp $
 * Created on 8.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.accesscontrol.jaas;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;


/**
 * 
 *  Last modified: $Date: 2007/01/12 19:31:30 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.1.2.1 $
 */
public class IWRoleGroup extends HashSet implements Group {
	    
	    public boolean addMember(Principal user) {
	        return this.add(user);
	    }
	    
	    public boolean isMember(Principal member) {
	        return this.contains(member);
	    }
	    
	    public Enumeration members() {
	        class MembersEnumeration implements Enumeration {
	            private Iterator m_iter;
	            public MembersEnumeration(Iterator iter) {
	                this.m_iter = iter;
	            }
	            public boolean hasMoreElements () {
	                return this.m_iter.hasNext();
	            }
	            public Object nextElement () {
	                return this.m_iter.next();
	            }
	        }

	        return new MembersEnumeration(this.iterator());
	    }

	    public boolean removeMember(Principal user) {
	        return this.remove(user);
	    }
	    
	    public String getName() {
	        return "roles";
	    }
}
