package com.idega.core.ldap.client.jndi;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.log4j.Logger;

/**
*    <p>The BasicOps class contains methods for performing basic
*    directory operations.  Errors are generally caught and handled locally,
*    although return codes usually indicate the general success status of
*    operations.  </p>
*
*    <p>Two methods, error() and log() are defined.  These are intended to be
*    over-ridden by programs wishing application specific handling of these
*    (i.e. for more sensible user output than System.out.println()...).</p
*/

public class BasicOps
{
    private static final String DEFAULT_CTX = "com.sun.jndi.ldap.LdapCtxFactory";

    private static final String DEFAULT_DSML_CTX = "com.sun.jndi.dsmlv2.soap.DsmlSoapCtxFactory";

    private final static Logger log = Logger.getLogger("com.ca.commons.jndi.BasicOps");


    private Attributes schema = null;

    protected DirContext ctx = null;
	protected ConnectionData connectionData = null;

    protected static int ldapVersion = -1;  // ldap version of the current connection (-1 = not connected)

    String errorMsg;                     //TE: a record of the last error msg.
    Exception errorException = null;     //TE: a record of the last exception.



   /**
	*   Initialise a Basic Operation object with a context.
	*/

    public BasicOps(DirContext c)
    {
        ctx = c;
    }

   /**
	* 	Factory Method to create BasicOps objects, initialised
	* 	with an ldap context created from the connectionData,
	* 	and maintaining a reference to that connectionData.
	*
	* 	@param cData the details of the directory to connect to
	* 	@return a BasicOps object.
	*/

	public static BasicOps getInstance(ConnectionData cData)
		throws NamingException
	{
		BasicOps newObject = new BasicOps(openContext(cData));
		newObject.setConnectionData(cData);
		return newObject;
	}



   /**
	*  Sets the details of the connection Data used to make
	*  the ldap context.
	*
	*  @param cData the ldap connection details
	*/

	public void setConnectionData(ConnectionData cData)
	{
		connectionData = cData;
	}



   /**
    *   Open an initial context.
    *   Will open an initial context which can then be used to construct a
    *   BasicOps object. Note that this method may take some time to return.
    *
	*	@param connectionData a data object contain all the connection details.
	*/

	public static DirContext openContext(ConnectionData connectionData)
        throws NamingException
    {

        // sanity check
        if (connectionData.url == null)
            throw new NamingException("URL not specified in openContext()!");

        if (connectionData.version <2 || connectionData.version>3)
            throw new NamingException("Incorrect ldap Version! (was " + connectionData.version + ")");

        if (connectionData.useSSL && (connectionData.cacerts == null))
            throw new NamingException("Cannot use SSL without a trusted CA certificates JKS file.");

        if (connectionData.referralType == null) connectionData.referralType = "follow";  // not an error not to specify this.

        if (connectionData.aliasType == null) connectionData.aliasType = "finding"; // not an error not to specify this

        if ("followthrowignore".indexOf(connectionData.referralType) == -1)
            throw new NamingException("unknown referral type: " + connectionData.referralType + " (setting to 'follow')");

        Properties env = new Properties();

        // ldap version
        env.put("java.naming.ldap.version", String.valueOf(connectionData.version) );   // ignored for DSML

        // general default parameters
        log.debug("connection protocol = " + connectionData.protocol);
        if (connectionData.protocol == connectionData.LDAP)
        {
            env.put(Context.INITIAL_CONTEXT_FACTORY, DEFAULT_CTX);
            //TE: Set the property to keep RDN
            env.put("java.naming.ldap.deleteRDN", "false");
        }
        else if (connectionData.protocol == connectionData.DSML)
        {
            //TE: Set the property to keep RDN
            env.put("java.naming.ldap.deleteRDN", "false");
            env.put(Context.INITIAL_CONTEXT_FACTORY, DEFAULT_DSML_CTX);
//            env.put(Context.PROVIDER_URL, "http://betch01:6666/axis/services/DSML");  // could add a baseDN here if desired
        }
        else
            throw new NamingException("Unknown protocol '" + connectionData.protocol + "' encountered in com.ca.commons.jndi.BasicOps");

        env.put(Context.PROVIDER_URL, connectionData.url);  // could add a baseDN here if desired

        //TE: was 'follow' by default, could be: follow, ignore, throw....
        //TE: this method should also throw a ReferralException which is what happens if the referral is 'throw'...
        env.put(Context.REFERRAL, connectionData.referralType);

        env.put("java.naming.ldap.attributes.binary", "photo jpegphoto jpegPhoto");

        // host name and port
//System.out.println("ignoring provider url: '" + connectionData.url + "'");
        // alias handling
        env.put("java.naming.ldap.derefAliases", connectionData.aliasType);


        if (connectionData.tracing)                             // nb. Changing this during a connection
            env.put("com.sun.jndi.ldap.trace.ber", System.err); // would be attractive, but doesn't seem to work...

        if (connectionData.userDN != null && connectionData.pwd != null)               // Set up for simple authentication
        {                                                       // if the user is a Manager
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, connectionData.userDN);
            env.put(Context.SECURITY_CREDENTIALS, new String(connectionData.pwd));
        }
        else
        {
            env.put(Context.SECURITY_AUTHENTICATION, "none");    // no authentication
        }

        if (connectionData.useSSL) // Specify SSL as the security protocol (others are possible)
        {
            env.put(Context.SECURITY_PROTOCOL, "ssl");

            if (connectionData.cacerts != null)
            {
                try
                {
                    // Initialise the SSL Socket Factory
                    JndiSocketFactory.init(connectionData.cacerts, connectionData.clientcerts,
                                           connectionData.caKeystorePwd, connectionData.clientKeystorePwd,
                                           connectionData.caKeystoreType, connectionData.clientKeystoreType);

                    // try to use client authentication if a clientcert keystore and pwd supplied
                    if (connectionData.clientcerts != null && (connectionData.clientKeystorePwd != null && connectionData.clientKeystorePwd.length > 0))
                    {
                        env.put(Context.SECURITY_AUTHENTICATION, "EXTERNAL");  // Use sasl external (i.e., certificate) auth
                    }

                    env.put("java.naming.ldap.factory.socket", "com.ca.commons.jndi.JndiSocketFactory"); // Specify SSL socket factory
                }
                catch (Exception e)
                {
                    String msg = "Error opening SSL connection: ";
                    if (e.getMessage() != null) msg += e.getMessage();
                    e.printStackTrace();
                    throw new NamingException(msg);
                }
            }
       }

        // create the connection !
        return openContext(env);
	}



   /**
	*    This static ftn. can be used to open an initial context (which can then
	*    be used to construct a BasicOps object).  Note that this ftn may take some
	*    time to return...
	*
	*   @param version         the LDAP Version (2 or 3) being used.
	*   @param host            the LDAP server name.
	*   @param port            the LDAP server port (default 389) being used.
	*   @param user            the Manager User's DN - (is null if user is not manager)
	*   @param pwd             the Manager User's password - (is null if user is not manager)
	*   @param tracing         whether to set BER tracing on or not
	*   @param referralType    the jndi ldap referral type: [follow:ignore:throw]
	*   @param aliasHandling   how aliases should be handled in searches ('always'|'never'|'find'|'search')
	*   @deprecated            use getInstance() instead
	*   @return                The created context.
	*/

    public static DirContext openContext(int version, String host, int port, String user, char[] pwd,
                        boolean tracing, String referralType, String aliasHandling)
        throws NamingException
    {
        if (host == null)
            throw new NamingException("Host not specified in openContext()!");

        if (port == 0) port = 389;

        return openContext(version, ("ldap://" + host + ":" + port), user, pwd, tracing, referralType, aliasHandling);
    }



   /**
	* 	Opens a simple default initial context, with no authentication, using version 3 ldap.
	*   @deprecated use getInstance() instead.
	*/

    public static DirContext openContext(String url)
        throws NamingException
    {
		ConnectionData myData = new ConnectionData();
		myData.url = url;

        return openContext(3, url, "", null, false, null, null, false, null, null, null, null, null, null);
    }



   /**
	*  	Opens an initial context with (optional) authentication and configurable ldap version.
	*   @param version         the LDAP Version (2 or 3) being used.
	*   @param url             a url of the form ldap://hostname:portnumber
	*   @param managerUserDN   the Manager User's distinguished name (optionally null if not used)
	*   @param pwd             the Manager User's password - (is null if user is not manager)
	*/
/*
    public static DirContext openContext(int version, String url, String managerUserDN, char[] pwd)
        throws NamingException
    {
        return openContext(version, url, managerUserDN, pwd, false, null, null, false, null, null, null, null, null);
    }
*/



   /**
	*  	This static ftn. can be used to open an initial context (which can then
	*   be used to construct a BasicOps object).  Note that this ftn may take some
	*   time to return...
	*
	*   @param version         the LDAP Version (2 or 3) being used.
	*   @param url             a url of the form ldap://hostname:portnumber
	*   @param userDN   		the Manager User's distinguished name (optionally null if not used)
	*   @param pwd             the Manager User's password - (is null if user is not manager)
	*   @param tracing         whether to set BER tracing on or not
	*   @param referralType    the jndi ldap referral type: [follow:ignore:throw] (may be null - defaults to 'follow')
	*   @param aliasHandling
	*   @deprecated            use getInstance() instead
	*   @return                The created context.
	*/

    public static DirContext openContext(int version, String url, String userDN, char[] pwd, boolean tracing, String referralType, String aliasHandling)
        throws NamingException
    {
        return openContext(version, url, userDN, pwd, tracing, referralType, aliasHandling, false, null, null, null, null, null, null);
    }



   /**
	*    This static ftn. can be used to open an initial context (which can then
	*    be used to construct a BasicOps object).  Note that this ftn may take some
	*    time to return...
	*
	*   @param version            the LDAP Version (2 or 3) being used.
	*   @param url                a url of the form ldap://hostname:portnumber.
	*   @param userDN		       the Manager User's distinguished name (optionally null if not used).
	*   @param pwd                the Manager User's password - (is null if user is not manager).
	*   @param tracing            whether to set BER tracing on or not.
	*   @param referralType       the jndi ldap referral type: [follow:ignore:throw] (may be null - defaults to 'follow').
	*   @param aliasType	       how aliases should be handled in searches ('always'|'never'|'find'|'search').
	*   @param useSSL             whether to use SSL (either simple or client-authenticated).
	*   @param cacerts            the file containing the trusted server certificates (no keys).
	*   @param clientcerts        the file containing client certificates.
	*   @param caKeystorePwd      the password to the ca's keystore (may be null for non-client authenticated ssl).
	*   @param clientKeystorePwd  the password to the client's keystore (may be null for non-client authenticated ssl).
	*   @param caKeystoreType     the type of keystore file; e.g. 'JKS', or 'PKCS12'.
	*   @param clientKeystoreType the type of keystore file; e.g. 'JKS', or 'PKCS12'.
	*
	*   @return                   The created context.
	*/

    public static DirContext openContext(int version, String url, String userDN,
                                         char[] pwd, boolean tracing,
										 String referralType, String aliasType,
										 boolean useSSL, String cacerts, String clientcerts,
                                         char[] caKeystorePwd, char[] clientKeystorePwd,
                                         String caKeystoreType, String clientKeystoreType )
		throws NamingException
    {
		ConnectionData connectionData = new ConnectionData();

		connectionData.version 				= version;
		connectionData.url					= url;
		connectionData.userDN				= userDN;
		connectionData.pwd					= pwd;
		connectionData.referralType 		= referralType;
		connectionData.aliasType 			= aliasType;
		connectionData.useSSL				= useSSL;
		connectionData.cacerts				= cacerts;
		connectionData.clientcerts			= clientcerts;
		connectionData.caKeystorePwd		= caKeystorePwd;
		connectionData.clientKeystorePwd	= clientKeystorePwd;
		connectionData.caKeystoreType		= caKeystoreType;
		connectionData.clientKeystoreType	= clientKeystoreType;
		connectionData.tracing				= tracing;

		return openContext(connectionData);
    }



   /**
	* 	This is a raw interface to javax.naming.directory.InitialDirContext, that allows
	*  	an arbitrary environment string to be passed through.  Often the other version
	* 	of openContext() above will prove more convenient.
	*  	@param env a list of environment variables for the context
	*  	@return a newly created DirContext.
	*/

    public static DirContext openContext(Properties env)
        throws NamingException
    {
        log.debug("opening Directory Context to " + env.get(Context.PROVIDER_URL) + "\n using: " + env.get(Context.INITIAL_CONTEXT_FACTORY));

        DirContext ctx = new InitialDirContext(env);

        log.debug("context successfully opened " + (ctx != null));

        if (ctx != null)
        {
            try
            {
                ldapVersion = Integer.parseInt(env.get("java.naming.ldap.version").toString());
            }
            catch (Exception e)
            {
                throw new NamingException("BasicOps.openContext(): unable to determine ldap version of connection.");
            }
        }
        return ctx;
    }



   /**
	* 	A simple wrapper for a ctx.getSchema("") call.
	*   @deprecated - jndi's 'getSchema' may not always be available (e.g. not implemented in dsml).
    *    use 'getSchemaAttributes()' instead
	*/

    public DirContext getSchema() throws NamingException
    {
        if (ctx == null)
            throw new NamingException("No context open to retrieve Schema from");

        log.debug("getSchema() call");

        return ctx.getSchema("");
    }

    public void setSchemaAttributes(Attributes newSchema)
    {
        schema = newSchema;
    }

    /**
     *  This returns the schema as an Attributes object.  Each multi-valued Attribute within
     *  that Attributes object represents a different aspect of schema: e.g. 'objectClasses',
     * 'ldapSyntaxes' etc.  Convenienct getObjectClasses, getLdapSyntaxes and getAttributeTypes
     *  @return a collection of schema 'Attribute' entries.
     */

    public Attributes getSchemaAttributes() throws NamingException
    {
        // every v3 ldap directory *should* have a special 'schema entry', that is named by the 'subschemaSubentry'
        // attribute of the directory.

        if (schema != null) return schema;  // 'cache' schema.

        String subschemaSubentry = "cn=schema";  // default - it usually *is* this though...

        Attributes atts = ctx.getAttributes("", new String[] {"subschemaSubentry"});

        if (atts != null)
        {
            Attribute subschema = atts.get("subschemaSubentry");
            if (subschema != null && subschema.get() != null)
            {
                subschemaSubentry = subschema.get().toString();
                if (subschemaSubentry.length() == 0)                // probably not a necessary check, but you never know...
                    subschemaSubentry = "cn=schema";
            }
        }

        schema = ctx.getAttributes(subschemaSubentry);             // sets object variable 'schema'

        return schema;

    }

    /**
     *   Convenience method to get the objectClasses Attribute, which represents the syntax of
     *   object classes, what attributes each object class has, and what it is derived from.
     *   @return an Attribute with multiple values, each representing a particular object class
     *     represented as complex string of values that must be parsed: e.g. the country value might be:
     * ' 2.5.6.2 NAME 'country' SUP ( top ) STRUCTURAL MUST ( c ) MAY ( description $ searchGuide ) '
     */
    public Attribute getObjectClasses()
        throws NamingException
    {
        if (schema == null)
            schema = getSchemaAttributes();

        return (schema == null)?null:schema.get("objectClasses");
    }

    /**
     *   Convenience method to get the Syntax Attribute, which represents the ldap attribute syntaxes available.
     *   @return an Attribute with multiple values, each representing a particular Syntax,
     *     represented as complex string of values that must be parsed: e.g. the DirectoryString value might be:
     *    '1.3.6.1.4.1.1466.115.121.1.15 DESC 'Directory String'
     */

    public Attribute getLdapSyntaxes()
            throws NamingException
    {
        if (schema == null)
            schema = getSchemaAttributes();

        return (schema == null)?null:schema.get("ldapSyntaxes");
    }

    /**
     *   Convenience method to get the objectClasses Attribute, which represents the syntax of
     *   attributeTypes.
     *   @return an Attribute with multiple values, each representing a particular attributeType
     *     represented as complex string of values that must be parsed: e.g. the surname value might be:
     *       2.5.4.4 NAME ( 'sn' 'surname' ) SYNTAX 1.3.6.1.4.1.1466.115.121.1.15
     */

    public Attribute getAttributeTypes()
            throws NamingException
    {
        if (schema == null)
            schema = getSchemaAttributes();

        return (schema == null)?null:schema.get("attributeTypes");
    }




   /**
	*   basically a wrapper for context.rename... changes the
	*   distinguished name of an object, checks for error.
	*
	*   @param oldDN current distinguished name of an object.
	*   @param newDN the name it is to be changed to.
	*   @return the success status of the operation
	*/

//XXXX - this will fail for single valued manditory attributes.
//XXXX - since using 'deleteRDN = false' - 30 May 2002.

    public boolean renameObject (Name OldDN, Name NewDN)
    {
        log.debug("renaming object " + OldDN.toString() + " to " + NewDN.toString());

        Name oldDN = preParse(OldDN);
        Name newDN = preParse(NewDN);

        if (ctx == null) return error("Null Directory Context\n  in BasicOps.renameObject()\n  (so can't do anything!)", null);

        Name rdn = newDN.getSuffix(newDN.size()-1);
        Name oldRdn = oldDN.getSuffix(oldDN.size()-1);

        if (oldRdn.toString().equals(rdn.toString()))
            return true;                                // nothing to do, rdns are identical.

        try
        {
            ctx.rename (oldDN, rdn);
        }
        catch (NamingException e)
        {
            return error("Failed to rename entry " + oldDN + " to " + newDN + "\n  in BasicOps.renameObject()", e );
        }

        return true;
    }



   /**
	*    Copies an object to a new DN by the simple expedient of adding
	*    an object with the new DN, and the attributes of the old object.
	*    @param FromDN the original object being copied
	*    @param ToDN the new object being created
	*    @return the success status of the operation
	*/

    public boolean copyObject(Name FromDN, Name ToDN)
    {
        log.debug("copying object " + FromDN.toString() + " to " + ToDN.toString());

        Name fromDN = preParse(FromDN);
        Name toDN = preParse(ToDN);

        if (ctx == null) return error("Null Directory Context\n  in BasicOps.copyObject\n  (so can't do anything!)", null);

        return (addObject(toDN, read(fromDN)));
    }



    /**
     *   creates a new object (subcontext) with the given
     *   dn and attributes.
     *
     *   @param Dn the distinguished name of the new object
     *   @param atts attributes for the new object
     *   @return the success status of the operation
     */

    public boolean addObject (Name Dn, Attributes atts)
    {
        log.debug("add object " + Dn.toString());
        Name dn = preParse(Dn);

        if (ctx == null) return error("Null Directory Context\n  in BasicOps.addObject()\n  (so can't do anything!)", null);

        // Sanity check
        if (dn==null) return error("null DN in BasicOps.addObject()", null);
        if (atts==null) return error("null atts in BasicOps.addObject()", null);
        if (dn.size()==0) return error("DN has no elements in BasicOps.addObject()", null);

        try
        {
            ctx.createSubcontext (dn, atts);
        }
        catch (NamingException e)
        {
            e.printStackTrace();
            return error("Unable to add object '" + dn + "' in BasicOps.addObject()", e);
        }

        return true;
    }

    /**
     *    deletes a leaf entry (subcontext).  It is
     *    an error to attempt to delete an entry which is not a leaf
     *    entry, i.e. which has children.
     *
     */

    public boolean deleteObject (Name Dn)
    {
        log.debug("delete object " + Dn.toString());

        Name dn = preParse(Dn);

        if (ctx == null) return error("Null Directory Context\n  in BasicOps.deleteObject()\n  (so can't do anything!)", null);

        // Sanity check
        if (dn==null) return error("null DN in BasicOps.deleteObject()", null);
        if (dn.size()==0) return error("DN has no elements in BasicOps.deleteObject()", null);

        try
        {
            ctx.destroySubcontext (dn);
        }
        catch (NamingException e)
        {
            return error("Unable to delete object " + dn + "\n  in BasicOps.deleteObject()", e);
        }

        return true;
    }




   /**
    *    Checks the existence of a particular DN, without (necessarily)
    *    reading any attributes.
    *    @param NodeDN the DN to check
    *    @return the existence of the nodeDN (or false if an error occurs).
    */

    public boolean exists(Name NodeDN)
    {
        log.debug("checking existance of: " + NodeDN.toString());

        Name nodeDN = preParse(NodeDN);
        try
        {
            Object o = ctx.lookup(nodeDN);
            return (o!=null);
        }
        catch (NamingException e)    // this is normal - implies object not found.
        {
            return false;
        }
        catch (NullPointerException e)          //TE: thrown by sun at com.sun.jndi.dsmlv2.soap.DsmlSoapCtx.c_lookup(DsmlSoapCtx.java:571)
        {
            return false;
        }
    }


    /**
     * Reads all the attribute type and values for the given entry.
     * @param Dn the ldap string distinguished name of entry to be read
     * @return an 'Attributes' object containing a list of all Attribute
     *         objects.
     */

    public synchronized Attributes read(Name Dn)
    {
        return read(Dn, null);
    }


    /**
     * Reads all the attribute type and values for the given entry.
     * @param Dn the ldap string distinguished name of entry to be read
     * @param returnAttributes a list of specific attributes to return.
     * @return an 'Attributes' object containing a list of all Attribute
     *         objects.
     */

    public synchronized Attributes read(Name Dn, String[] returnAttributes)
    {
        log.debug("reading object " + Dn.toString());

        Name dn = preParse(Dn);

        if (ctx == null) {error("Null Directory Context\n  in BasicOps.read()\n  (so can't do anything!)", null); return null; }

        if (dn==null) {error("Null DN in BasicOps.read()",null);return null;}

        try
        {
            Attributes atts = ctx.getAttributes(dn, returnAttributes);

            return atts;
        }
        catch(NamingException e)
        {
            error("Failed to read attributes for " + dn + "\n  in BasicOps.read()\n", e);
        }
        return null;
    }

    /**
     *   Modifies an object's attributes, either adding, replacing or
     *   deleting the passed attributes.
     *
     *   @param Dn  distinguished name of object to modify
     *   @param mod_type the modification type to be performed; one of
     *          DirContext.REPLACE_ATTRIBUTE, DirContext.DELETE_ATTRIBUTE, or
     *          DirContext.ADD_ATTRIBUTE.
     *   @param attr the new attributes to update the object with.
     *   @return the success status of the operation
     */

    public boolean modifyAttributes(Name Dn, int mod_type, Attributes attr)
    {
        log.debug("modifying object " + Dn.toString() + " mod type is: " + mod_type);

        Name dn = preParse(Dn);

        if (ctx == null) return error("Null Directory Context\n  in BasicOps.modifyAttributes\n  (so can't do anything!)", null);

        try
        {
            ctx.modifyAttributes(dn, mod_type, attr);
        }
        catch (NamingException e)
        {
            return error("Failed to modify entry " + dn + "\n  in BasicOps.modifyAttributes()", e);
        }
        return true;
    }


    /**
     *   Modifies an object's attributes, either adding, replacing or
     *   deleting the passed attributes.
     *
     *   @param Dn  distinguished name of object to modify
     *   @param modList a list of ModificationItems
     *   @return the success status of the operation
     */

    public boolean modifyAttributes(Name Dn, ModificationItem[] modList)
    {
        log.debug("modifying object " + Dn.toString() + " with list of mod items ");
        Name dn = preParse(Dn);

        if (ctx == null) return error("Null Directory Context\n  in BasicOps.modifyAttributes\n  (so can't do anything!)", null);

        try
        {
            ctx.modifyAttributes(dn, modList);
        }
        catch (NamingException e)
        {
            return error("Failed to modify entry " + dn + "\n  in BasicOps.modifyAttributes()", e);
        }
        return true;
    }

    /**
     *   Updates an object with a new set of attributes
     *
     *   @param Dn  distinguished name of object to update
     *   @param atts the new attributes to update the object with.
     *   @return the success status of the operation
     */

    public boolean updateObject (Name Dn, Attributes atts)
    {
        Name dn = preParse(Dn);

        if (ctx == null) return error("Null Directory Context\n  in BasicOps.updateObject()\n  (so can't do anything!)", null);

        return modifyAttributes(dn, DirContext.REPLACE_ATTRIBUTE, atts);
    }


    /**
     *   deletes an attribute from an object
     *
     *   @param Dn         distinguished name of object
     *   @param a       the attribute to delete
     *   @return           Whether the deletion was successful.
     */

    public boolean deleteAttribute(Name Dn, Attribute a)
    {
        Name dn = preParse(Dn);

        if (ctx == null) return error("Null Directory Context in BasicOps.deleteAttribute\n  (so can't do anything!)", null);

        BasicAttributes atts = new BasicAttributes();
        atts.put(a);
        return modifyAttributes(dn, DirContext.REMOVE_ATTRIBUTE, atts);
    }

    /**
     *   deletes a set of attribute-s from an object
     *
     *   @param Dn         distinguished name of object
     *   @param a          the Attributes object containing the
     *                     list of attribute-s to delete
     *   @return           Whether the deletion was successful.
     */


    public boolean deleteAttributes(Name Dn, Attributes a)
    {
        Name dn = preParse(Dn);

        if (ctx == null) return error("Null Directory Context\n  in BasicOps.deleteAttributes\n  (so can't do anything!)", null);

        return modifyAttributes(dn, DirContext.REMOVE_ATTRIBUTE, a);
    }

    /**
     *   updates an Attribute with a new value set
     *
     *   @param Dn         distinguished name of object
     *   @param a       the attribute to modify
     *   @return success of operation
     */

    public boolean updateAttribute(Name Dn, Attribute a)
    {
        Name dn = preParse(Dn);

        if (ctx == null) return error("Null Directory Context\n  in BasicOps.updateAttribute\n  (so can't do anything!)", null);

        BasicAttributes atts = new BasicAttributes();
        atts.put(a);
        return modifyAttributes(dn, DirContext.REPLACE_ATTRIBUTE, atts);
    }

    /**
     *   updates a set of Attribute-s.
     *
     *   @param Dn         distinguished name of object
     *   @param a          an Attributes object containing the attribute-s to modify
     *   @return success of operation
     */

    public boolean updateAttributes(Name Dn, Attributes a)
    {
        Name dn = preParse(Dn);

        if (ctx == null) return error("Null Directory Context\n  in BasicOps.updateAttributes\n  (so can't do anything!)", null);

        return modifyAttributes(dn, DirContext.REPLACE_ATTRIBUTE, a);
    }

    /**
     *   Adds a new attribute to a particular dn.
     *
     *   @param Dn         distinguished name of object
     *   @param a       the attribute to modify
     *   @return success status
     */

    public boolean addAttribute(Name Dn, Attribute a)
    {
        Name dn = preParse(Dn);

        if (ctx == null) return error("Null Directory Context\n  in BasicOps.addAttribute()\n  (so can't do anything!)", null);

        BasicAttributes atts = new BasicAttributes();
        atts.put(a);
        return modifyAttributes(dn, DirContext.ADD_ATTRIBUTE, atts);
    }

    /**
     *   Adds a set of attributes to a particular dn.
     *
     *   @param Dn         distinguished name of object
     *   @param a          the Attributes (set of attribute-s) to add
     *   @return success status
     */

    public boolean addAttributes(Name Dn, Attributes a)
    {
        Name dn = preParse(Dn);

        if (ctx == null) return error("Null Directory Context\n  in BasicOps.addAttribute()\n  (so can't do anything!)", null);

        return modifyAttributes(dn, DirContext.ADD_ATTRIBUTE, a);
    }



    /**
     *   returns the next level of a directory tree, returning
     *   a Enumeration of the results, *relative* to the SearchBase (i.e. not as
     *   absolute DNs).
     *
     *   @param Searchbase the node in the tree to expand
     *   @return list of results (NameClassPair); the next layer of the tree...
     */

    public NamingEnumeration list(Name Searchbase)
    {
        //    Attempt to read the names of the next level of subentries along with their object
        //    classes.  Failing that, try to just read their names.

        try
        {
            return rawOneLevelSearch(Searchbase, "(objectclass=*)", 0, 0, new String[] {"objectclass"} );
        }
        catch (NamingException e)  // can this be made more specific?
        {
            try
            {
                return rawOneLevelSearch(Searchbase, "(objectclass=*)", 0, 0, new String[] {"1.1"} );
            }
            catch (NamingException e2)
            {
                error("Unable to list sub entries!", e2);
                return null;
            }
        }
    }


    /**
     *   Performs a one-level directory search (i.e. a search of immediate children), without
     *   returning any attributes (e.g. just returns DNs).
     *
     *   @param Searchbase the domain name (relative to initial context in ldap) to seach from.
     *   @param filter the non-null filter to use for the search
     *   @param limit the maximum number of results to return
     *   @param timeout the maximum time to wait before abandoning the search
     *
     *   @return list of search results ('SearchResult's); entries matching the search filter.
     */

    public NamingEnumeration searchOneLevel(Name Searchbase, String filter, int limit, int timeout)
    {
        return searchOneLevel(Searchbase, filter, limit, timeout, new String[] {"objectClass"});
    }
    /**
     *   Performs a one-level directory search (i.e. a search of immediate children)
     *
     *   @param Searchbase the domain name (relative to initial context in ldap) to seach from.
     *   @param filter the non-null filter to use for the search
     *   @param limit the maximum number of results to return
     *   @param timeout the maximum time to wait before abandoning the search
     *   @param returnAttributes an array of strings containing the names of attributes to search. (null = all, empty array = none)
     *
     *   @return list of search results ('SearchResult's); entries matching the search filter.
     */


    public NamingEnumeration searchOneLevel(Name Searchbase, String filter, int limit,
                                                    int timeout, String[] returnAttributes)
    {
        Name searchbase = preParse(Searchbase);

        if (ctx == null) { error("Null Directory Context\n  in BasicOps.searchOneLevel()\n  (so can't do anything!)", null); return null; }
//XXX
        if (returnAttributes != null  &&  returnAttributes.length == 0)
            returnAttributes = new String[] {"objectClass"};

        try
        {
            return rawOneLevelSearch(searchbase, filter, limit, timeout, returnAttributes);
        }
        catch (NamingException e)
        {
            log.error("Search failed", e);
            return null;
        }

    }

    private NamingEnumeration rawOneLevelSearch(Name searchbase, String filter, int limit,
                int timeout, String[] returnAttributes ) throws NamingException
    {
        log.debug("searching next level: from " + searchbase.toString() + " with filter " + filter);
        /* specify search constraints to search one level */
        SearchControls constraints = new SearchControls();

        constraints.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        constraints.setCountLimit(limit);
        constraints.setTimeLimit(timeout);

        constraints.setReturningAttributes(returnAttributes);

        NamingEnumeration results = ctx.search(searchbase, filter, null);

        log.debug("finished next level search; results exist? " + results.hasMoreElements());
        results = postParseNameClassPairs(results, searchbase);
        return results;

    }

    /**
     *   Performs a directory sub tree search (i.e. of the next level and all subsequent levels below),
     *   returning no attributes (i.e. just DNs);
     *
     *   @param Searchbase the domain name (relative to initial context in ldap) to seach from.
     *   @param filter the non-null filter to use for the search
     *   @param limit the maximum number of results to return
     *   @param timeout the maximum time to wait before abandoning the search
     *   @return list of search results ('SearchResult's); entries matching the search filter.
     */
    public NamingEnumeration searchSubTree(Name Searchbase, String filter, int limit, int timeout)
    {
        return searchSubTree(Searchbase, filter,  limit, timeout, new String[] {"objectClass"});
    }
    /**
     *   Performs a directory sub tree search (i.e. of the next level and all subsequent levels below).
     *
     *   @param Searchbase the domain name (relative to initial context in ldap) to seach from.
     *   @param filter the non-null filter to use for the search
     *   @param limit the maximum number of results to return
     *   @param timeout the maximum time to wait before abandoning the search
     *   @param returnAttributes an array of strings containing the names of attributes to search. (null = all, empty array = none)
     *   @return list of search results ('SearchResult's); entries matching the search filter.
     */

    public NamingEnumeration searchSubTree(Name Searchbase, String filter, int limit,
                                    int timeout, String[] returnAttributes)
    {
        log.debug("searching subtree from " + Searchbase.toString() + " with filter " + filter);

        Name searchbase = preParse(Searchbase);

        if (ctx == null) {error("Null Directory Context\n  in BasicOps.searchSubTree()\n  (so can't do anything!)", null); return null; }

        NamingEnumeration result = null;

        if (returnAttributes != null  &&  returnAttributes.length == 0)
            returnAttributes = new String[] {"objectClass"};

        try
        {
            /* specify search constraints to search subtree */
            SearchControls constraints = new SearchControls();

            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            constraints.setCountLimit(limit);
            constraints.setTimeLimit(timeout);

            constraints.setReturningAttributes(returnAttributes);

            result = ctx.search(searchbase, filter, constraints);

            return postParseNameClassPairs(result, searchbase);

        }
        catch (NamingException e)
        {
            error("Search failed", e);
            return null;
        }
    }



   /**
	*   Performs a base object search (i.e. just a search of the current entry, nothing below it),
	*   returning no attributes (i.e. just DNs);
	*
	*   @param Searchbase the domain name (relative to initial context in ldap) to seach from.
	*   @param filter the non-null filter to use for the search
	*   @param limit the maximum number of results to return
	*   @param timeout the maximum time to wait before abandoning the search
	*   @return list of search results ('SearchResult's); entries matching the search filter.
	*/

    public NamingEnumeration searchBaseObject(Name Searchbase, String filter, int limit, int timeout)
    {
        return searchBaseObject(Searchbase, filter,  limit, timeout, new String[] {"objectClass"});
    }



   /**
	*   Performs a base object search (i.e. just a search of the current entry, nothing below it).
	*
	*   @param Searchbase the domain name (relative to initial context in ldap) to seach from.
	*   @param filter the non-null filter to use for the search
	*   @param limit the maximum number of results to return
	*   @param timeout the maximum time to wait before abandoning the search
	*   @param returnAttributes an array of strings containing the names of attributes to search. (null = all, empty array = none)
	*   @return list of search results ('SearchResult's); entries matching the search filter.
	*/

    public NamingEnumeration searchBaseObject(Name Searchbase, String filter, int limit,
                                    int timeout, String[] returnAttributes)
    {
        log.debug("searching object " + Searchbase.toString() + " with filter " + filter);

        Name searchbase = preParse(Searchbase);

        if (ctx == null) {error("Null Directory Context\n  in BasicOps.searchSubTree()\n  (so can't do anything!)", null); return null; }

        NamingEnumeration result = null;
//XXX
        if (returnAttributes != null  &&  returnAttributes.length == 0)
            returnAttributes = new String[] {"objectClass"};

        try
        {
            /* specify search constraints to search subtree */
            SearchControls constraints = new SearchControls();

            constraints.setSearchScope(SearchControls.OBJECT_SCOPE);
            constraints.setCountLimit(limit);
            constraints.setTimeLimit(timeout);

            constraints.setReturningAttributes(returnAttributes);

            result = ctx.search(searchbase, filter, constraints);

            return postParseNameClassPairs(result, searchbase);

        }
        catch (NamingException e)
        {
            error("Search failed", e);
            return null;
        }
    }



    /**
     *    Shuts down the current context.<p>
     *    nb. It is not an error to call this method multiple times.
     */

    public void close()
    {
        log.debug("closing context");

        if (ctx == null) return;  // it is not an error to multiply disconnect.

        setSchemaAttributes(null); // clear the schema

        try
        {
            ctx.close();
            ldapVersion = -1;
        }
        catch (NamingException e)
        {
            error("Error closing context in BasicOps.disconnect()", e);
        }
    }

    /**
     *    This picks up the name parser used at the root level... if
     *    the context only spans a single name space (i.e. for an ldap
     *    directory) this will be the same as the one used throughout.
     */

    public NameParser getBaseNameParser()
    {
        log.debug("getting base name parser");

        if (ctx == null) {error("Null Directory Context\n  in BasicOps.searchSubTree()\n  (so can't do anything!)", null); return null; }

        try
        {
            return ctx.getNameParser("");
        }
        catch (NamingException e)
        {
            error("Error getting Name Parser in BasicOps.getBaseNameParser()", e);
            return null;
        }
    }



    /**
     *  This function provides a common point for all error reporting.
     *  In order to customise it, simply over-ride this function in a
     *  class inheriting from BasicOps.
     *
     *  @param msg User friendly error message
     *  @param e The exception
     *  @return returns false (always) for easy chaining.
     */

    public boolean error(String msg, Exception e)
    {
        errorMsg = msg;         //TE: set the error msg.
        errorException = e;     //TE: set the exception.

        log.error("BasicOps error: " + msg + "\n  ", e);
        return false;
    }



   /**
    *   Returns the error message & exception as a string.
    *   NOTE: this method is used for JXweb - the only way to get error messages.
    *   @return errrorMsg+errorException (if not null - they are set in the 'error' method).
    */

    public String getError()
    {
        return errorException == null ? errorMsg : errorMsg + errorException;
    }

    /**
     *  This function provides a common point for all logging.
     *  In order to customise it, simply over-ride this function in a
     *  class inheriting from BasicOps.
     *
     *  @param msg log Message
     */

    public void log(String msg, int logLevel)
    {
        System.out.println("BasicOps Log: " + msg);
    }

    /**
     *    This preparses a name, preparitory to passing to the jndi operation.
     *    Usefull to over-ride if a Name needs to be escaped or re-formatted.
     *    @param name the pre jndi operation name.
     *    @return the version used by the operation.
     */

    protected Name preParse(Name name)
    {
        return name;
    }

    /**
     *    This postparses a name, after it has been returned from the jndi operation.
     *    Usefull to over-ride if the name needs to be unescaped or reformatted.
     *    @param name the post jndi operation name.
     *    @return the re-formatted version used by the application.
     */

    protected Name postParse(Name name)
    {
        return name;
    }

    /**
     *    This postparses a namingEnumeration of NameClassPairs, after it has been returned from the jndi operation.
     *    Usefull to over-ride if the names in the enumeration need to be unescaped or reformatted.
     *    @param e the post jndi operation namingEnumeration.
     *    @param  searchBase the 'base' dn from which the names in the enumeration (may) be relative.
     *            If the Names in
     *            the enumeration are suffixed by the searchBase, they are unaltered, otherwise the searchBase
     *            is added to the names to give the full DN in the namespace.
     *    @return the re-formatted version used by the application.
     */
    protected NamingEnumeration postParseNameClassPairs(NamingEnumeration e, Name searchBase)
        throws NamingException
    {
        if (log.isDebugEnabled())
        {

            log.debug("in post Parse Name Class Pairs.  Elements available = " + e.hasMoreElements());

            try
            {
                while (e.hasMoreElements())
                {
                    SearchResult bloop = (SearchResult)e.nextElement();
                    if (bloop == null)
                        log.debug("NULL RESULT");
                    else
                        log.debug("next result: " + bloop.getName());
                }
            }
            catch (Exception e2)
            {
                log.error("unexpected exception: ", e2);
            }
        }

        return e;
    }

    /**
     *    Returns the ldap version of the current connection
     */

    public int getLdapVersion()
    {
        return ldapVersion;
    }

    /**
     *    Get the raw context for occasions where direct
     *    jndi operations must be performed.
     */

    public DirContext getContext() { return ctx; }



    public boolean renameObject (Name OldDN, Name NewDN, boolean deleteOldRDN)
    {
        String value = (deleteOldRDN) ? "true" : "false" ;
        try
        {
            ctx.addToEnvironment("java.naming.ldap.deleteRDN", value);

            boolean retValue = renameObject(OldDN, NewDN);

            ctx.addToEnvironment("java.naming.ldap.deleteRDN", "false");  // reset to default of 'false' afterwards.

            return retValue;
        }
        catch (NamingException e)
        {
            log.error("Error changing context environment deleteRDN to " + value);
        }
        return false;
    }

}
