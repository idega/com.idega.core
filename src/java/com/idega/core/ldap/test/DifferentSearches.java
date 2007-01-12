package com.idega.core.ldap.test;

import java.util.Hashtable;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;


/**
  *  This example shows how different searches are done on the ldap server
  * Usage :java DifferentSearches [Lookup |Basic |Filter |ContorlAttributes |ControlResults|Compare
  */

public class DifferentSearches{

public static void main(String args[]) throws Exception{

	if(args.length==0){
		System.out.println("Usage :java DifferentSearches [Lookup |Basic |Filter |ContorlAttributes |ControlResults|Compare]");
		System.exit(0);
	}

	DifferentSearches dc = new DifferentSearches();
	if(args[0].equalsIgnoreCase("Lookup")) {
		dc.doLookup();
	}
	else if(args[0].equalsIgnoreCase("Basic")) {
		dc.doBasicSearch();
	}
	else if(args[0].equalsIgnoreCase("Filter")) {
		dc.doFilterSearch();
	}
	else if(args[0].equalsIgnoreCase("ControlAttributes")) {
		dc.controlArrtibutesInResults();
	}
	else if(args[0].equalsIgnoreCase("ControlResults")) {
		dc.controlResults();
	}
	else if(args[0].equalsIgnoreCase("Compare")) {
		dc.doCompareSearch();
	}
	else {
		System.out.println("Usage :java DifferentSearches [Lookup |Basic|Filter|ControlAttributes|ControlResults|Compare]");
	}


}



/**
 * This method does a simple lookup for DN=cn=vectorid-2,ou=JavaObjects,o=myserver.com
 */
public void doLookup() throws Exception{
	Context ctx=  getContext();
	Object vec =ctx.lookup("cn=vectorid-2,ou=JavaObjects,o=myserver.com");
	System.out.println("Object returned for DN cn=vectorid-2,ou=JavaObjects,o=myserver.com is instance of vector =" + (vec instanceof Vector));
	ctx.close();
}

/**
 * This method does a basic search on the subtree for any entry that has the uid=kevink attribute
 */
public void doBasicSearch() throws Exception{
	DirContext ctx=  getDirContext();
	Attributes matchAttrs = new BasicAttributes(true); // ignore attribute name case
	matchAttrs.put(new BasicAttribute("uid", "kevink"));

	// Search for objects with those matching attributes
	NamingEnumeration answer = ctx.search("ou=People,o=myserver.com", matchAttrs);
	formatResults(answer);
	ctx.close();
}





/**
 * Search for objects using filter
 * Create search controls and allow java objects to be returned in the results.
 * Specify the search filter as entries attribute cn=vectorid-1 and the attribute
 * lastlogin has any value
 */
public void doFilterSearch() throws Exception{
	DirContext ctx=  getDirContext();
	SearchControls ctls = new SearchControls();
	ctls. setReturningObjFlag (true);
	ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	String filter = "(&(cn=vectorid-1)( lastlogin=*))";
	NamingEnumeration answer = ctx.search("o=myserver.com", filter, ctls);
	formatResults(answer);
	ctx.close();
}


/**
 * This methos shows how to search and control arrtibutes returned in the result.
 * Create search controls and allow java objects to be returned in the results.
 * Specify the search filter as entries attribute cn=vectorid-1 and the attribute
 * lastlogin has any value.
 * Only the attributes cn and lastlogin should  be returned in the results
 */
public void controlArrtibutesInResults() throws Exception{
	    DirContext ctx=  getDirContext();

	    // Specify the ids of the attributes to return
	    String[] attrIDs = {"cn", "lastlogin"};
		SearchControls ctls = new SearchControls();
		ctls. setReturningAttributes (attrIDs);
		ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String filter = "(&(cn=vectorid-1)( lastlogin=*))";
		NamingEnumeration answer = ctx.search("o=myserver.com", filter, ctls);
	    formatResults(answer);
		ctx.close();
}

/**
 * This methos shows how to search and control the actual results returned.
 * Create search controls and allow java objects to be returned in the results.
 * Specify the search filter as entries attribute cn=vectorid-1 and the attribute
 * lastlogin has any value.
 * Specify that a max of 2 results should be returned.
 */
public void controlResults()throws Exception{
		DirContext ctx=  getDirContext();
		SearchControls ctls = new SearchControls();
		ctls.setCountLimit(2);
		ctls.setReturningObjFlag(true);
		ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String filter = "(&(cn=*)(lastlogin=*))";
		NamingEnumeration answer = ctx.search("o=myserver.com", filter, ctls);
		formatResults(answer);
		ctx.close();
}


/**
 * Ask the server to do a compare and tell us if the DN vectorid-1,ou=JavaObjects,o=myserver.com
 * has an attribute called lastlogin and that attribute has 200 as one of its values.
 * If yes, the results would contain 1 unique entry since there is no way to return a boolean
 * If not, the results would be empty.
 */
public void doCompareSearch() throws Exception{
	DirContext ctx=  getDirContext();
	SearchControls ctls = new SearchControls();
	ctls. setReturningObjFlag (true);
	ctls.setReturningAttributes(new String[0]);
	ctls.setSearchScope(SearchControls.OBJECT_SCOPE);
	String filter = "(lastlogin=200)";
	NamingEnumeration answer = ctx.search("cn=vectorid-1,ou=JavaObjects,o=myserver.com", filter, ctls);
	formatResults(answer);
	ctx.close();


}


/*
* Generic method to format the NamingEnumeration returned from a search.
*/
public  void formatResults(NamingEnumeration enumer) throws Exception{
	int count=0;
	try {
	    while (enumer.hasMore()) {
		SearchResult sr = (SearchResult)enumer.next();
		System.out.println("SEARCH RESULT:" + sr.getName());
		formatAttributes(sr.getAttributes());
		System.out.println("====================================================");
		count++;
		   }

	   System.out.println("Search returned "+ count+ " results");

	} catch (NamingException e) {
	    e.printStackTrace();
	}
    }

/*
* Generic method to format the Attributes .Displays all the multiple values of
* each Attribute in the Attributes
*/
 public  void formatAttributes(Attributes attrs) throws Exception{
	if (attrs == null) {
	    System.out.println("This result has no attributes");
	} else {
	    try {
		for (NamingEnumeration enumer = attrs.getAll(); enumer.hasMore();) {
		    Attribute attrib = (Attribute)enumer.next();
		    System.out.println("ATTRIBUTE :" + attrib.getID());
		    for (NamingEnumeration e = attrib.getAll();e.hasMore();) {
				System.out.println("\t\t        = " + e.next());
			}
		}

	    } catch (NamingException e) {
		e.printStackTrace();
	    }

	}
}


/** Generic method to obtain a reference to a DirContext
*/
public DirContext getDirContext() throws Exception{
	   Hashtable env = new Hashtable(11);
	   env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
	   env.put(Context.PROVIDER_URL, "ldap://localhost:389");
	   // Create the initial context
	   DirContext ctx = new InitialDirContext(env);
	   return ctx;

    }

/** Generic method to obtain a reference to a Context object.
*/
public Context getContext() throws Exception{
	   Hashtable env = new Hashtable(11);
	   env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
	   env.put(Context.PROVIDER_URL, "ldap://localhost:389");
	   // Create the initial context
	   Context ctx = new InitialContext(env);
	   return ctx;

    }

}