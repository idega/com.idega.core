package com.idega.core.ldap.server.util;
/**
 * An EntrySet that contains a collection of Groups and converts a single Group object to an Entry object<br>
 * when the getNext() method is called
 *
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 */
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.ldapserver.server.Entry;
import org.codehaus.plexus.ldapserver.server.EntrySet;

import com.idega.core.ldap.server.backend.IWUserLDAPBackend;

public class GroupEntrySet implements EntrySet {
	
	private List entries = null;
	private Iterator entryIter = null;
	private IWUserLDAPBackend myBackend = null;
	
	/**
	 * This constuctor will only create an empty GroupEntrySet so getNext() will not return anything
	 *
	 */
	/*public GroupEntrySet()
	 {
	 super();
	 }*/
	
	/**
	 * This is the proper constructor to use
	 */
	public GroupEntrySet(IWUserLDAPBackend myBackend, List groups) {
		super();
		this.myBackend = myBackend;
		this.entries = groups;
	}
	
	public Entry getNext() {
		if (!hasMore()) {
			return null;
		}else {

			//create the directorystring path to the top node?
			//or do all the entries belong to the same directory string suffix?
			Entry current = (Entry) entryIter.next();
			return current;
		}
	}
	
	
	public boolean hasMore() {
		if(entryIter==null) {
			entryIter = entries.iterator();
		}
		return entryIter.hasNext();
	}
}