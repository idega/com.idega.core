package com.idega.core.ldap.client.jndi;

import javax.naming.*;
import javax.naming.directory.*;
import java.util.Properties;

/**
 * <p>JNDI Ops is a bare-bones utility class that takes up some
 * of the over-head of making jndi calls.  It is used by
 * BasicOps which adds validation, error handling and logging.<p>
 *
 * <p>This utility class assumes you will be using ldap v3 with
 * the environment defaults:
 * "java.naming.ldap.deleteRDN" = "false"
 * Context.REFERRAL (java.naming.referral), "follow");
 * java.naming.ldap.attributes.binary", "photo jpegphoto jpegPhoto");
 * java.naming.ldap.derefAliases", "finding");
 *
 */

public class JNDIOps
{
    private static final String DEFAULT_CTX = "com.sun.jndi.ldap.LdapCtxFactory";

    // we may wish to extend this class to opening DSML connections in future.
    //private static final String DEFAULT_DSML_CTX = "com.sun.jndi.dsmlv2.soap.DsmlSoapCtxFactory";

    protected DirContext ctx = null;

    protected static int ldapVersion = -1;  // ldap version of the current connection (-1 = not connected)

   /**
	*   Initialise a Basic Operation object with a context.
	*/

    public JNDIOps(DirContext c)
    {
        this.ctx = c;
    }

    /**
     * This creates a jndi connection with a particular set of environment properties.
     * Often this will be obtained by using one of the set...Properties methods to
     * create a base list of properties and then modifing it before calling this
     * constructor.
     */

    public JNDIOps(Properties env)
        throws NamingException
    {
        this.ctx = openContext(env);                         // create the connection!
    }

    /**
     *   This creates a simple, unauthenticated jndi connection to an ldap url.
     *    Note that this ftn may take some time to return...
     *
     *   @param url                a url of the form ldap://hostname:portnumber.
     */

    public JNDIOps(String url)
            throws NamingException
    {
        Properties env = new Properties();  // an environment for jndi context parameters

        setupBasicProperties(env, url);       // set up the bare minimum parameters

        this.ctx = openContext(env);                         // create the connection!
    }

    /**
     *    <p>This creates a JNDIOps object using simple username + password authentication.</p>
     *
     *    <p>This constructor opens an initial context.  Note that this ftn may take some
     *    time to return...</p>
     *
     *   @param url                a url of the form ldap://hostname:portnumber.
     *   @param userDN		       the Manager User's distinguished name (optionally null if not used).
     *   @param pwd                the Manager User's password - (is null if user is not manager).
     */

    public JNDIOps (String url, String userDN, char[] pwd)
            throws NamingException
    {

        Properties env = new Properties();              // an environment for jndi context parameters

        setupBasicProperties(env, url);                   // set up the bare minimum parameters

        setupSimpleSecurityProperties(env, userDN, pwd);  // add the username + password parameters

        this.ctx = openContext(env);             // create the connection !
    }

    /**
     *    This creates a JNDIOps object with an SSL or SASL Connection.
     *
     *   If only SSL is desired, the clientcerts, clientKeystorePwd and clientKeystoreType
     * variables may be set to null.
     *
     *   @param url                a url of the form ldap://hostname:portnumber.
     *   @param cacerts            the file containing the trusted server certificates (no keys).
     *   @param clientcerts        the file containing client certificates.
     *   @param caKeystorePwd      the password to the ca's keystore (may be null for non-client authenticated ssl).
     *   @param clientKeystorePwd  the password to the client's keystore (may be null for non-client authenticated ssl).
     *   @param caKeystoreType     the type of keystore file; e.g. 'JKS', or 'PKCS12'.
     *   @param clientKeystoreType the type of keystore file; e.g. 'JKS', or 'PKCS12'.
     *   @param tracing            whether to set BER tracing on or not.
     *   @param sslTracing         whether to set SSL tracing on or not.
     */

    public JNDIOps(String url,
                   String cacerts, String clientcerts,
                   char[] caKeystorePwd, char[] clientKeystorePwd,
                   String caKeystoreType, String clientKeystoreType,
                   boolean tracing, boolean sslTracing)
        throws Exception
    {

        Properties env = new Properties();  // an environment for jndi context parameters

        setupBasicProperties(env, url);       // set up the bare minimum parameters


        setupSSLProperties(env, cacerts,      // add the SSL ('ca...') and possible SASL ('client...') parameters
                    clientcerts, caKeystorePwd, clientKeystorePwd, caKeystoreType, clientKeystoreType, tracing, sslTracing);

        this.ctx = openContext(env);            // create the connection !
    }

    /**
     * This sets the basic environment properties needed for a simple,
     * unauthenticated jndi connection.  It is used by openBasicContext().
     *
     * This method is provided as a convenience for people wishing to append
     * or modify the jndi environment, without setting it up entirely from
     * scratch.
     *
     * @param url
     * @param env
     * @throws NamingException
     */
    public static void setupBasicProperties(Properties env, String url) throws NamingException
    {
        // sanity check
        if (url == null) {
			throw new NamingException("URL not specified in openContext()!");
		}

        env.put("java.naming.ldap.version", "3");               // always use ldap v3

        env.put(Context.INITIAL_CONTEXT_FACTORY, DEFAULT_CTX);  // use jndi provider

        env.put("java.naming.ldap.deleteRDN", "false");         // usually what we want

        env.put(Context.REFERRAL, "follow");                    //could be: follow, ignore, throw

        env.put("java.naming.ldap.attributes.binary", "photo jpegphoto jpegPhoto");  // special hack to handle non-standard binary atts

        env.put("java.naming.ldap.derefAliases", "finding");    // could be: finding, searching, etc.

        env.put(Context.SECURITY_AUTHENTICATION, "none");       // no authentication (may be modified by other code)

        env.put(Context.PROVIDER_URL, url);                     // the ldap url to connect to; e.g. "ldap://ca.com:389"
    }

    /**
     * This sets the environment properties needed for a simple username +
     * password authenticated jndi connection.  It is used by openSimpleSecurityContext().
     *
     * This method is provided as a convenience for people wishing to append
     * or modify the jndi environment, without setting it up entirely from
     * scratch.
     *
     * @param env
     * @param userDN
     * @param pwd
     */
    public static void setupSimpleSecurityProperties(Properties env, String userDN, char[] pwd)
    {
        env.put(Context.SECURITY_AUTHENTICATION, "simple");         // 'simple' = username + password

        env.put(Context.SECURITY_PRINCIPAL, userDN);                // add the full user dn

        env.put(Context.SECURITY_CREDENTIALS, new String(pwd));     // stupid jndi requires us to cast this to a string-
                                                                    // this opens a security weakness with swapped memory etc.
    }


    /*    This static ftn. sets the environment used to open an SSL or SASL context.
    *   It is used by openSSLContext.
    *
    *   If only SSL is desired, the clientcerts, clientKeystorePwd and clientKeystoreType
    * variables may be set to null.
    *
    * This method is provided as a convenience for people wishing to append
    * or modify the jndi environment, without setting it up entirely from
    * scratch.
    *
    *   @param url                a url of the form ldap://hostname:portnumber.
    *   @param tracing            whether to set BER tracing on or not.
    *   @param cacerts            the file containing the trusted server certificates (no keys).
    *   @param clientcerts        the file containing client certificates.
    *   @param caKeystorePwd      the password to the ca's keystore (may be null for non-client authenticated ssl).
    *   @param clientKeystorePwd  the password to the client's keystore (may be null for non-client authenticated ssl).
    *   @param caKeystoreType     the type of keystore file; e.g. 'JKS', or 'PKCS12'.
    *   @param clientKeystoreType the type of keystore file; e.g. 'JKS', or 'PKCS12'.
    *
    *   @return                   The created context.
    */

    public static void setupSSLProperties(Properties env,
                                        String cacerts, String clientcerts,
                                        char[] caKeystorePwd, char[] clientKeystorePwd,
                                        String caKeystoreType, String clientKeystoreType,
                                        boolean tracing, boolean sslTracing)
            throws Exception
    {

        // sanity check
        if (cacerts == null) {
			throw new NamingException("Cannot use SSL without a trusted CA certificates JKS file.");
		}

        // the exact protocol (e.g. "TLS") set in JndiSocketFactory
        env.put(Context.SECURITY_PROTOCOL, "ssl");

        // Initialise the SSL Socket Factory.  Due to architectural wierdnesses, this is
        // a separate, static method in our own separate SSL Factory class.
        JndiSocketFactory.init(cacerts, clientcerts,
                caKeystorePwd, clientKeystorePwd,
                caKeystoreType, clientKeystoreType);

        // Tell JNDI to use our own, separate SSL Factory class with the keystores set as previously
        env.put("java.naming.ldap.factory.socket", "com.ca.commons.jndi.JndiSocketFactory");

        // try to use client authentication (SASL) if a clientcert keystore and pwd supplied
        if (clientcerts != null && (clientKeystorePwd != null && clientKeystorePwd.length > 0))
        {
            env.put(Context.SECURITY_AUTHENTICATION, "EXTERNAL");  // Use sasl external (i.e., certificate) auth
        }

        // set the tracing level now, since (wierdly) it can't be set once the connection is open.
        if (tracing) {
			env.put("com.sun.jndi.ldap.trace.ber", System.err);
		}

        if (sslTracing) {
			System.setProperty("javax.net.debug=all", "all");
		}
    }


    /**
     * 	This is a raw interface to javax.naming.directory.InitialDirContext, that allows
     *  	an arbitrary environment string to be passed through.  Often it will be
     * convenient to create that environment list using aset...Properties call (or just
     * use one of the constructors to create a JNDIOps object.
     *  	@param env a list of environment variables for the context
     *  	@return a newly created DirContext.
     */

    public static DirContext openContext(Properties env)
            throws NamingException
    {
        DirContext ctx = new InitialDirContext(env);

        if (ctx == null) {
			throw new NamingException("Internal Error with jndi connection: No Context was returned, however no exception was reported by jndi.");
		}

        return ctx;
    }


    /**
     *   <p>A wrapper for context.rename... changes the
     *   distinguished name of an object, checks for error.
     *   !! Only changes the final RDN.</p>
     *
     *   <p>WARNING! this will fail for single valued manditory attributes.
     *since using 'deleteRDN = false' - use renameEntry(old, new, deleteOldRdn)
     * method instead - 30 May 2002.</p>
     *
     *   @param oldDN current distinguished name of an object.
     *   @param newDN the name it is to be changed to.
     */

     public void renameEntry (Name oldDN, Name newDN)
        throws NamingException
     {
         Name rdn = newDN.getSuffix(newDN.size()-1);
         Name oldRdn = oldDN.getSuffix(oldDN.size()-1);

         if (oldRdn.toString().equals(rdn.toString()) == false) {
			this.ctx.rename (oldDN, rdn);
		}
     }



    /**
     *    Copies an object to a new DN by the simple expedient of adding
     *    an object with the new DN, and the attributes of the old object.
     *    @param fromDN the original object being copied
     *    @param toDN the new object being created
     */

     public void copyEntry(Name fromDN, Name toDN)
         throws NamingException
     {
         addEntry(toDN, read(fromDN));
     }



     /**
      *   creates a new object (subcontext) with the given
      *   dn and attributes.
      *
      *   @param dn the distinguished name of the new object
      *   @param atts attributes for the new object
      */

     public void addEntry (Name dn, Attributes atts)
        throws NamingException
     {
         this.ctx.createSubcontext (dn, atts);
     }

     /**
      *    deletes a leaf entry (subcontext).  It is
      *    an error to attempt to delete an entry which is not a leaf
      *    entry, i.e. which has children.
      *
      */

     public void deleteEntry (Name dn)
        throws NamingException
     {
         this.ctx.destroySubcontext (dn);
     }




    /**
     *    Checks the existence of a particular DN, without (necessarily)
     *    reading any attributes.
     *    @param nodeDN the DN to check
     *    @return the existence of the nodeDN (or false if an error occurs).
     */

     public boolean exists(Name nodeDN)
        throws NamingException
     {
         return (this.ctx.lookup(nodeDN)!=null);

         /* FUTURE - DSML bug
         catch (NullPointerException e)          //TE: thrown by sun at com.sun.jndi.dsmlv2.soap.DsmlSoapCtx.c_lookup(DsmlSoapCtx.java:571)
             return false;
         */
     }


     /**
      * Reads all the attribute type and values for the given entry.
      * @param dn the ldap string distinguished name of entry to be read
      * @return an 'Attributes' object containing a list of all Attribute
      *         objects.
      */

     public synchronized Attributes read(Name dn)
        throws NamingException
     {
         return read(dn, null);
     }


     /**
      * Reads all the attribute type and values for the given entry.
      * @param dn the ldap string distinguished name of entry to be read
      * @param returnAttributes a list of specific attributes to return.
      * @return an 'Attributes' object containing a list of all Attribute
      *         objects.
      */

     public synchronized Attributes read(Name dn, String[] returnAttributes)
        throws NamingException
     {
         return this.ctx.getAttributes(dn, returnAttributes);
     }

     /**
      *   Modifies an object's attributes, either adding, replacing or
      *   deleting the passed attributes.
      *
      *   @param dn  distinguished name of object to modify
      *   @param mod_type the modification type to be performed; one of
      *          DirContext.REPLACE_ATTRIBUTE, DirContext.DELETE_ATTRIBUTE, or
      *          DirContext.ADD_ATTRIBUTE.
      *   @param attr the new attributes to update the object with.
      */

     public void modifyAttributes(Name dn, int mod_type, Attributes attr)
        throws NamingException
     {
         this.ctx.modifyAttributes(dn, mod_type, attr);
     }


     /**
      *   Modifies an object's attributes, either adding, replacing or
      *   deleting the passed attributes.
      *
      *   @param dn  distinguished name of object to modify
      *   @param modList a list of ModificationItems
      */

     public void modifyAttributes(Name dn, ModificationItem[] modList)
        throws NamingException
     {
             this.ctx.modifyAttributes(dn, modList);
     }

     /**
      *   Updates an object with a new set of attributes
      *
      *   @param dn  distinguished name of object to update
      *   @param atts the new attributes to update the object with.
      */

     public void updateEntry (Name dn, Attributes atts)
        throws NamingException
     {
         modifyAttributes(dn, DirContext.REPLACE_ATTRIBUTE, atts);
     }


     /**
      *   deletes an attribute from an object
      *
      *   @param dn         distinguished name of object
      *   @param a       the attribute to delete
      */

     public void deleteAttribute(Name dn, Attribute a)
        throws NamingException
     {
         BasicAttributes atts = new BasicAttributes();
         atts.put(a);
         modifyAttributes(dn, DirContext.REMOVE_ATTRIBUTE, atts);
     }

     /**
      *   deletes a set of attribute-s from an object
      *
      *   @param dn         distinguished name of object
      *   @param a          the Attributes object containing the
      *                     list of attribute-s to delete
      */


     public void deleteAttributes(Name dn, Attributes a)
             throws NamingException
     {
         modifyAttributes(dn, DirContext.REMOVE_ATTRIBUTE, a);
     }

     /**
      *   updates an Attribute with a new value set
      *
      *   @param dn         distinguished name of object
      *   @param a       the attribute to modify
      */

     public void updateAttribute(Name dn, Attribute a)
             throws NamingException
     {
         BasicAttributes atts = new BasicAttributes();
         atts.put(a);
         modifyAttributes(dn, DirContext.REPLACE_ATTRIBUTE, atts);
     }

     /**
      *   updates a set of Attribute-s.
      *
      *   @param dn         distinguished name of object
      *   @param a          an Attributes object containing the attribute-s to modify
      */

     public void updateAttributes(Name dn, Attributes a)
             throws NamingException
     {
         modifyAttributes(dn, DirContext.REPLACE_ATTRIBUTE, a);
     }

     /**
      *   Adds a new attribute to a particular dn.
      *
      *   @param dn         distinguished name of object
      *   @param a       the attribute to modify
      */

     public void addAttribute(Name dn, Attribute a)
             throws NamingException
     {
         BasicAttributes atts = new BasicAttributes();
         atts.put(a);
         modifyAttributes(dn, DirContext.ADD_ATTRIBUTE, atts);
     }

     /**
      *   Adds a set of attributes to a particular dn.
      *
      *   @param dn         distinguished name of object
      *   @param a          the Attributes (set of attribute-s) to add
      */

     public void addAttributes(Name dn, Attributes a)
             throws NamingException
     {
         modifyAttributes(dn, DirContext.ADD_ATTRIBUTE, a);
     }



     /**
      *   returns the next level of a directory tree, returning
      *   a Enumeration of the results, *relative* to the SearchBase (i.e. not as
      *   absolute DNs), along with their object classes if possible.
      *
      *   @param Searchbase the node in the tree to expand
      *   @return list of results (NameClassPair); the next layer of the tree...
      */

     public NamingEnumeration list(Name Searchbase)
        throws NamingException
     {
         //    Attempt to read the names of the next level of subentries along with their object
         //    classes.  Failing that, try to just read their names.

         return rawOneLevelSearch(Searchbase, "(objectclass=*)", 0, 0, new String[] {"1.1"} );
     }


     /**
      *   Performs a one-level directory search (i.e. a search of immediate children), returning
      *   object classes if possible, otherwise just the names.
      *
      *   @param searchbase the domain name (relative to initial context in ldap) to seach from.
      *   @param filter the non-null filter to use for the search
      *   @param limit the maximum number of results to return
      *   @param timeout the maximum time to wait before abandoning the search
      *
      *   @return list of search results ('SearchResult's); entries matching the search filter.
      */

     public NamingEnumeration searchOneLevel(Name searchbase, String filter, int limit, int timeout)
        throws NamingException
     {
          return searchOneLevel(searchbase, filter, limit, timeout, new String[] {"1.1"});
     }

     /**
      *   Performs a one-level directory search (i.e. a search of immediate children)
      *
      *   @param searchbase the domain name (relative to initial context in ldap) to seach from.
      *   @param filter the non-null filter to use for the search
      *   @param limit the maximum number of results to return
      *   @param timeout the maximum time to wait before abandoning the search
      *   @param returnAttributes an array of strings containing the names of attributes to search. (null = all, empty array = none)
      *
      *   @return list of search results ('SearchResult's); entries matching the search filter.
      */


     public NamingEnumeration searchOneLevel(Name searchbase, String filter, int limit,
                                                     int timeout, String[] returnAttributes)
        throws NamingException
     {
             return rawOneLevelSearch(searchbase, filter, limit, timeout, returnAttributes);
     }

    /**
     * Method that calls the actual search on the jndi context.
     *
     *   @param searchbase the domain name (relative to initial context in ldap) to seach from.
     *   @param filter the non-null filter to use for the search
     *   @param limit the maximum number of results to return
     *   @param timeout the maximum time to wait before abandoning the search
     *   @param returnAttributes an array of strings containing the names of attributes to search. (null = all, empty array = none)
     * @return
     * @throws NamingException
     */
     private NamingEnumeration rawOneLevelSearch(Name searchbase, String filter, int limit,
                 int timeout, String[] returnAttributes ) throws NamingException
     {
         /* specify search constraints to search one level */
         SearchControls constraints = new SearchControls();

         constraints.setSearchScope(SearchControls.ONELEVEL_SCOPE);
         constraints.setCountLimit(limit);
         constraints.setTimeLimit(timeout);

         constraints.setReturningAttributes(returnAttributes);

         NamingEnumeration results = this.ctx.search(searchbase, filter, null);

         return results;

     }

     /**
      *   Performs a directory sub tree search (i.e. of the next level and all subsequent levels below),
      *   returning just dns);
      *
      *   @param searchbase the domain name (relative to initial context in ldap) to seach from.
      *   @param filter the non-null filter to use for the search
      *   @param limit the maximum number of results to return
      *   @param timeout the maximum time to wait before abandoning the search
      *   @return list of search results ('SearchResult's); entries matching the search filter.
      */
     public NamingEnumeration searchSubTree(Name searchbase, String filter, int limit, int timeout)
        throws NamingException
     {
           return searchSubTree(searchbase, filter,  limit, timeout, new String[] {"1.1"});
     }
     /**
      *   Performs a directory sub tree search (i.e. of the next level and all subsequent levels below).
      *
      *   @param searchbase the domain name (relative to initial context in ldap) to seach from.
      *   @param filter the non-null filter to use for the search
      *   @param limit the maximum number of results to return
      *   @param timeout the maximum time to wait before abandoning the search
      *   @param returnAttributes an array of strings containing the names of attributes to search. (null = all, empty array = none)
      *   @return list of search results ('SearchResult's); entries matching the search filter.
      */

     public NamingEnumeration searchSubTree(Name searchbase, String filter, int limit,
                                     int timeout, String[] returnAttributes)
        throws NamingException
     {
         NamingEnumeration result = null;

         if (returnAttributes != null  &&  returnAttributes.length == 0) {
			returnAttributes = new String[] {"objectClass"};
		}

         /* specify search constraints to search subtree */
         SearchControls constraints = new SearchControls();

         constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
         constraints.setCountLimit(limit);
         constraints.setTimeLimit(timeout);

         constraints.setReturningAttributes(returnAttributes);

         result = this.ctx.search(searchbase, filter, constraints);

         return result;

     }



    /**
     *   Performs a base object search (i.e. just a search of the current entry, nothing below it),
     *   returning no attributes (i.e. just DNs);
     *
     *   @param searchbase the domain name (relative to initial context in ldap) to seach from.
     *   @param filter the non-null filter to use for the search
     *   @param limit the maximum number of results to return
     *   @param timeout the maximum time to wait before abandoning the search
     *   @return list of search results ('SearchResult's); entries matching the search filter.
     */

     public NamingEnumeration searchBaseEntry(Name searchbase, String filter, int limit, int timeout)
        throws NamingException
     {
         return searchBaseEntry(searchbase, filter,  limit, timeout, new String[] {"objectClass"});
     }



    /**
     *   Performs a base object search (i.e. just a search of the current entry, nothing below it).
     *
     *   @param searchbase the domain name (relative to initial context in ldap) to seach from.
     *   @param filter the non-null filter to use for the search
     *   @param limit the maximum number of results to return
     *   @param timeout the maximum time to wait before abandoning the search
     *   @param returnAttributes an array of strings containing the names of attributes to search. (null = all, empty array = none)
     *   @return list of search results ('SearchResult's); entries matching the search filter.
     */

     public NamingEnumeration searchBaseEntry(Name searchbase, String filter, int limit,
                                     int timeout, String[] returnAttributes)
        throws NamingException
     {
         NamingEnumeration result = null;

         if (returnAttributes != null  &&  returnAttributes.length == 0) {
			returnAttributes = new String[] {"objectClass"};
		}

         /* specify search constraints to search subtree */
         SearchControls constraints = new SearchControls();

         constraints.setSearchScope(SearchControls.OBJECT_SCOPE);
         constraints.setCountLimit(limit);
         constraints.setTimeLimit(timeout);

         constraints.setReturningAttributes(returnAttributes);

         result = this.ctx.search(searchbase, filter, constraints);

         return result;
     }


    /**
     * This method allows an object to be renamed, while also specifying
     * the exact fate of the old name.
     * @param OldDN the original name to be changed
     * @param NewDN the new name
     * @param deleteOldRDN whether the rdn of the old name should be removed,
     * or retained as a second attribute value.
     */

    public void renameEntry (Name OldDN, Name NewDN, boolean deleteOldRDN)
        throws NamingException
    {
        String value = (deleteOldRDN) ? "true" : "false" ;
        try
        {
            this.ctx.addToEnvironment("java.naming.ldap.deleteRDN", value);

            renameEntry(OldDN, NewDN);

            this.ctx.addToEnvironment("java.naming.ldap.deleteRDN", "false");  // reset to default of 'false' afterwards.
        }
        catch (NamingException e)
        {
            this.ctx.addToEnvironment("java.naming.ldap.deleteRDN", "false");  // reset to default of 'false' afterwards.
            throw e;
        }
    }





// *********************************


    /**
     *   <p>A wrapper for context.rename... changes the
     *   distinguished name of an object.</p>
     *
     *   <p>WARNING! this will fail for single valued manditory attributes.
     *since using 'deleteRDN = false' - use renameEntry(old, new, deleteOldRdn)
     * method instead - 30 May 2002.</p>
     *
     *   @param oldDN current distinguished name of an object.
     *   @param newDN the name it is to be changed to.
     */

     public void renameEntry (String oldDN, String newDN)
        throws NamingException
     {
          this.ctx.rename (oldDN, newDN);
     }


    /**
     *    Copies an object to a new DN by the simple expedient of adding
     *    an object with the new DN, and the attributes of the old object.
     *    @param fromDN the original object being copied
     *    @param toDN the new object being created
     */

     public void copyEntry(String fromDN, String toDN)
         throws NamingException
     {
         addEntry(toDN, read(fromDN));
     }



     /**
      *   creates a new object (subcontext) with the given
      *   dn and attributes.
      *
      *   @param dn the distinguished name of the new object
      *   @param atts attributes for the new object
      */

     public void addEntry (String dn, Attributes atts)
        throws NamingException
     {
         this.ctx.createSubcontext (dn, atts);
     }

     /**
      *    deletes a leaf entry (subcontext).  It is
      *    an error to attempt to delete an entry which is not a leaf
      *    entry, i.e. which has children.
      *
      */

     public void deleteEntry (String dn)
        throws NamingException
     {
         this.ctx.destroySubcontext (dn);
     }




    /**
     *    Checks the existence of a particular DN, without (necessarily)
     *    reading any attributes.
     *    @param nodeDN the DN to check
     *    @return the existence of the nodeDN (or false if an error occurs).
     */

     public boolean exists(String nodeDN)
        throws NamingException
     {
         return (this.ctx.lookup(nodeDN)!=null);

         /* FUTURE - DSML bug
         catch (NullPointerException e)          //TE: thrown by sun at com.sun.jndi.dsmlv2.soap.DsmlSoapCtx.c_lookup(DsmlSoapCtx.java:571)
             return false;
         */
     }


     /**
      * Reads all the attribute type and values for the given entry.
      * @param dn the ldap string distinguished name of entry to be read
      * @return an 'Attributes' object containing a list of all Attribute
      *         objects.
      */

     public synchronized Attributes read(String dn)
        throws NamingException
     {
         return read(dn, null);
     }


     /**
      * Reads all the attribute type and values for the given entry.
      * @param dn the ldap string distinguished name of entry to be read
      * @param returnAttributes a list of specific attributes to return.
      * @return an 'Attributes' object containing a list of all Attribute
      *         objects.
      */

     public synchronized Attributes read(String dn, String[] returnAttributes)
        throws NamingException
     {
     	
     	Name name = getNameFromDNString(dn);
         return this.ctx.getAttributes(name, returnAttributes);
     }

     /**
      *   Modifies an object's attributes, either adding, replacing or
      *   deleting the passed attributes.
      *
      *   @param dn  distinguished name of object to modify
      *   @param mod_type the modification type to be performed; one of
      *          DirContext.REPLACE_ATTRIBUTE, DirContext.DELETE_ATTRIBUTE, or
      *          DirContext.ADD_ATTRIBUTE.
      *   @param attr the new attributes to update the object with.
      */

     public void modifyAttributes(String dn, int mod_type, Attributes attr)
        throws NamingException
     {
         this.ctx.modifyAttributes(dn, mod_type, attr);
     }


     /**
      *   Modifies an object's attributes, either adding, replacing or
      *   deleting the passed attributes.
      *
      *   @param dn  distinguished name of object to modify
      *   @param modList a list of ModificationItems
      */

     public void modifyAttributes(String dn, ModificationItem[] modList)
        throws NamingException
     {
             this.ctx.modifyAttributes(dn, modList);
     }

     /**
      *   Updates an object with a new set of attributes
      *
      *   @param dn  distinguished name of object to update
      *   @param atts the new attributes to update the object with.
      */

     public void updateEntry (String dn, Attributes atts)
        throws NamingException
     {
         modifyAttributes(dn, DirContext.REPLACE_ATTRIBUTE, atts);
     }


     /**
      *   deletes an attribute from an object
      *
      *   @param dn         distinguished name of object
      *   @param a       the attribute to delete
      */

     public void deleteAttribute(String dn, Attribute a)
        throws NamingException
     {
         BasicAttributes atts = new BasicAttributes();
         atts.put(a);
         modifyAttributes(dn, DirContext.REMOVE_ATTRIBUTE, atts);
     }

     /**
      *   deletes a set of attribute-s from an object
      *
      *   @param dn         distinguished name of object
      *   @param a          the Attributes object containing the
      *                     list of attribute-s to delete
      */


     public void deleteAttributes(String dn, Attributes a)
             throws NamingException
     {
         modifyAttributes(dn, DirContext.REMOVE_ATTRIBUTE, a);
     }

     /**
      *   updates an Attribute with a new value set
      *
      *   @param dn         distinguished name of object
      *   @param a       the attribute to modify
      */

     public void updateAttribute(String dn, Attribute a)
             throws NamingException
     {
         BasicAttributes atts = new BasicAttributes();
         atts.put(a);
         modifyAttributes(dn, DirContext.REPLACE_ATTRIBUTE, atts);
     }

     /**
      *   updates a set of Attribute-s.
      *
      *   @param dn         distinguished name of object
      *   @param a          an Attributes object containing the attribute-s to modify
      */

     public void updateAttributes(String dn, Attributes a)
             throws NamingException
     {
         modifyAttributes(dn, DirContext.REPLACE_ATTRIBUTE, a);
     }

     /**
      *   Adds a new attribute to a particular dn.
      *
      *   @param dn         distinguished name of object
      *   @param a       the attribute to modify
      */

     public void addAttribute(String dn, Attribute a)
             throws NamingException
     {
         BasicAttributes atts = new BasicAttributes();
         atts.put(a);
         modifyAttributes(dn, DirContext.ADD_ATTRIBUTE, atts);
     }

     /**
      *   Adds a set of attributes to a particular dn.
      *
      *   @param dn         distinguished name of object
      *   @param a          the Attributes (set of attribute-s) to add
      */

     public void addAttributes(String dn, Attributes a)
             throws NamingException
     {
         modifyAttributes(dn, DirContext.ADD_ATTRIBUTE, a);
     }



     /**
      *   returns the next level of a directory tree, returning
      *   a Enumeration of the results, *relative* to the SearchBase (i.e. not as
      *   absolute DNs), along with their object classes if possible.
      *
      *   @param Searchbase the node in the tree to expand
      *   @return list of results (NameClassPair); the next layer of the tree...
      */

     public NamingEnumeration list(String Searchbase)
        throws NamingException
     {
         //    Attempt to read the names of the next level of subentries along with their object
         //    classes.  Failing that, try to just read their names.

         return rawOneLevelSearch(Searchbase, "(objectclass=*)", 0, 0, new String[] {"1.1"} );
     }


     /**
      *   Performs a one-level directory search (i.e. a search of immediate children), returning
      *   object classes if possible, otherwise just the names.
      *
      *   @param searchbase the domain name (relative to initial context in ldap) to seach from.
      *   @param filter the non-null filter to use for the search
      *   @param limit the maximum number of results to return
      *   @param timeout the maximum time to wait before abandoning the search
      *
      *   @return list of search results ('SearchResult's); entries matching the search filter.
      */

     public NamingEnumeration searchOneLevel(String searchbase, String filter, int limit, int timeout)
        throws NamingException
     {
          return searchOneLevel(searchbase, filter, limit, timeout, new String[] {"1.1"});
     }

     /**
      *   Performs a one-level directory search (i.e. a search of immediate children)
      *
      *   @param searchbase the domain name (relative to initial context in ldap) to seach from.
      *   @param filter the non-null filter to use for the search
      *   @param limit the maximum number of results to return
      *   @param timeout the maximum time to wait before abandoning the search
      *   @param returnAttributes an array of strings containing the names of attributes to search. (null = all, empty array = none)
      *
      *   @return list of search results ('SearchResult's); entries matching the search filter.
      */


     public NamingEnumeration searchOneLevel(String searchbase, String filter, int limit,
                                                     int timeout, String[] returnAttributes)
        throws NamingException
     {
             return rawOneLevelSearch(searchbase, filter, limit, timeout, returnAttributes);
     }

    /**
     * Method that calls the actual search on the jndi context.
     *
     *   @param searchbase the domain name (relative to initial context in ldap) to seach from.
     *   @param filter the non-null filter to use for the search
     *   @param limit the maximum number of results to return
     *   @param timeout the maximum time to wait before abandoning the search
     *   @param returnAttributes an array of strings containing the names of attributes to search. (null = all, empty array = none)
     * @return
     * @throws NamingException
     */
     private NamingEnumeration rawOneLevelSearch(String searchbase, String filter, int limit,
                 int timeout, String[] returnAttributes ) throws NamingException
     {
         
         Name name = getNameFromDNString(searchbase);

         return rawOneLevelSearch(name,filter,limit,timeout,returnAttributes);

     }

     private Name getNameFromDNString(String searchbase) throws NamingException {
		// Get the parser for this namespace
 	    NameParser ldapParser = this.ctx.getNameParser("");
 	    // Parse name
 	    Name compound = ldapParser.parse(searchbase);
		return compound;
	}

	/**
      *   Performs a directory sub tree search (i.e. of the next level and all subsequent levels below),
      *   returning just dns);
      *
      *   @param searchbase the domain name (relative to initial context in ldap) to seach from.
      *   @param filter the non-null filter to use for the search
      *   @param limit the maximum number of results to return
      *   @param timeout the maximum time to wait before abandoning the search
      *   @return list of search results ('SearchResult's); entries matching the search filter.
      */
     public NamingEnumeration searchSubTree(String searchbase, String filter, int limit, int timeout)
        throws NamingException
     {
           return searchSubTree(searchbase, filter,  limit, timeout, new String[] {"1.1"});
     }
     /**
      *   Performs a directory sub tree search (i.e. of the next level and all subsequent levels below).
      *
      *   @param searchbase the domain name (relative to initial context in ldap) to seach from.
      *   @param filter the non-null filter to use for the search
      *   @param limit the maximum number of results to return
      *   @param timeout the maximum time to wait before abandoning the search
      *   @param returnAttributes an array of strings containing the names of attributes to search. (null = all, empty array = none)
      *   @return list of search results ('SearchResult's); entries matching the search filter.
      */

     public NamingEnumeration searchSubTree(String searchbase, String filter, int limit,
                                     int timeout, String[] returnAttributes)
        throws NamingException
     {
         NamingEnumeration result = null;

         if (returnAttributes != null  &&  returnAttributes.length == 0) {
			returnAttributes = new String[] {"objectClass"};
		}

         /* specify search constraints to search subtree */
         SearchControls constraints = new SearchControls();

         constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
         constraints.setCountLimit(limit);
         constraints.setTimeLimit(timeout);

         constraints.setReturningAttributes(returnAttributes);

         result = this.ctx.search(searchbase, filter, constraints);

         return result;

     }



    /**
     *   Performs a base object search (i.e. just a search of the current entry, nothing below it),
     *   returning no attributes (i.e. just DNs);
     *
     *   @param searchbase the domain name (relative to initial context in ldap) to seach from.
     *   @param filter the non-null filter to use for the search
     *   @param limit the maximum number of results to return
     *   @param timeout the maximum time to wait before abandoning the search
     *   @return list of search results ('SearchResult's); entries matching the search filter.
     */

     public NamingEnumeration searchBaseEntry(String searchbase, String filter, int limit, int timeout)
        throws NamingException
     {
         return searchBaseEntry(searchbase, filter,  limit, timeout, new String[] {"objectClass"});
     }



    /**
     *   Performs a base object search (i.e. just a search of the current entry, nothing below it).
     *
     *   @param searchbase the domain name (relative to initial context in ldap) to seach from.
     *   @param filter the non-null filter to use for the search
     *   @param limit the maximum number of results to return
     *   @param timeout the maximum time to wait before abandoning the search
     *   @param returnAttributes an array of strings containing the names of attributes to search. (null = all, empty array = none)
     *   @return list of search results ('SearchResult's); entries matching the search filter.
     */

     public NamingEnumeration searchBaseEntry(String searchbase, String filter, int limit,
                                     int timeout, String[] returnAttributes)
        throws NamingException
     {
         NamingEnumeration result = null;

         if (returnAttributes != null  &&  returnAttributes.length == 0) {
			returnAttributes = new String[] {"objectClass"};
		}

         /* specify search constraints to search subtree */
         SearchControls constraints = new SearchControls();

         constraints.setSearchScope(SearchControls.OBJECT_SCOPE);
         constraints.setCountLimit(limit);
         constraints.setTimeLimit(timeout);

         constraints.setReturningAttributes(returnAttributes);

         result = this.ctx.search(searchbase, filter, constraints);

         return result;
     }



     /**
      *    Shuts down the current context.<p>
      *    nb. It is not an error to call this method multiple times.
      */

     public void close()
        throws NamingException
     {
         if (this.ctx == null) {
			return;  // it is not an error to multiply disconnect.
		}

         this.ctx.close();
     }

    /**
     * This method allows an object to be renamed, while also specifying
     * the exact fate of the old name.
     * @param OldDN the original name to be changed
     * @param NewDN the new name
     * @param deleteOldRDN whether the rdn of the old name should be removed,
     * or retained as a second attribute value.
     */

    public void renameEntry (String OldDN, String NewDN, boolean deleteOldRDN)
        throws NamingException
    {
        String value = (deleteOldRDN) ? "true" : "false" ;
        try
        {
            this.ctx.addToEnvironment("java.naming.ldap.deleteRDN", value);

            renameEntry(OldDN, NewDN);

            this.ctx.addToEnvironment("java.naming.ldap.deleteRDN", "false");  // reset to default of 'false' afterwards.
        }
        catch (NamingException e)
        {
            this.ctx.addToEnvironment("java.naming.ldap.deleteRDN", "false");  // reset to default of 'false' afterwards.
            throw e;  // rethrow exception...
        }
    }

}
