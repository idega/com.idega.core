package com.idega.core.ldap.client.jndi;

/**
*	The ConnectionData inner class is used to pass
*  	connection data around.  Not all fields are
*  	guaranteed to be valid values.
*/

public class ConnectionData
{
   /**
	*	The base to start browsing from, e.g.'o=Democorp,c=au'.
	*   (This is often reset to what the directory says the base
	*   is in practice).
	*/

	public String baseDN = "";

   /**
	*	The LDAP Version (2 or 3) being used.
	*/

	public int version = 3;  // default to 3...

    /**
     *  Which protocol to use (currently "ldap", "dsml")
     */

    public static final String LDAP = "ldap";
    public static final String DSML = "dsml";
    public String protocol = LDAP;  // default is always to use LDAP

   /**
	*	A URL of the form ldap://hostname:portnumber.
	*/

	public String url;

   /**
	*	The Manager User's distinguished name (optionally null if not used).
	*/

	public String userDN;

   /**
	*	The Manager User's password - (is null if user is not manager).
	*/

	public char[] pwd;

   /**
	*	The jndi ldap referral type: [follow:ignore:throw] (may be null - defaults to 'follow').
	*/

	public String referralType = "follow";

   /**
	*	How aliases should be handled in searches ('always'|'never'|'find'|'search').
	*/

	public String aliasType = "searching";

   /**
	*	Whether to use SSL (either simple or client-authenticated).
	*/

	public boolean useSSL;

   /**
	*	The file containing the trusted server certificates (no keys).
	*
	*/

	// XXX we may want to expand this later to 'SSL type'
	public String cacerts;

   /**
	*	The file containing client certificates and private key(s).
	*/

	public String clientcerts;

   /**
	*	The password to the ca's keystore (may be null for non-client authenticated ssl).
	*/

	public char[] caKeystorePwd;

   /**
	*	The password to the client's keystore (may be null for non-client authenticated ssl).
	*/

	public char[] clientKeystorePwd;

   /**
	*	The type of ca keystore file; e.g. 'JKS', or 'PKCS12'.
	*/

	public String caKeystoreType;

   /**
	*	The type of client keystore file; e.g. 'JKS', or 'PKCS12'.
	*/

	public String clientKeystoreType;

   /**
	*	Whether to set BER tracing on or not.  (This is a very verbose
	*   dump of all the raw ldap data as it streams past).
	*/

	public boolean tracing;



   /**
	*	Empty constructor - data fields are intended
	*  	to be set directly.
	*/

	public ConnectionData() {};

    public void setProtocol(String newProtocol)
    {
        if (newProtocol.equalsIgnoreCase(LDAP))
            protocol = LDAP;
        else if (newProtocol.equalsIgnoreCase(DSML))
            protocol = DSML;
        else
            System.err.println("Unknown Protocol " + newProtocol);
    }

   /**
	*  	This should be used to clear all the passwords
	*  	saved in this data object when they have been
	*  	used and are no longer needed... make sure however
	*  	that no references to the passwords remain to be
	*  	used by other parts of the program first :-)!
	*/

	public void clearPasswords()
	{
		if (pwd!=null) for (int i=0; i<pwd.length; i++) pwd[i] = ' ';  //TE: null is incompatible.
		if (caKeystorePwd!=null) for (int i=0; i<caKeystorePwd.length; i++) caKeystorePwd[i] = ' ';
		if (clientKeystorePwd!=null) for (int i=0; i<clientKeystorePwd.length; i++) clientKeystorePwd[i] = ' ';
	}



   /**
    *	Sets the url from the host & port, e.g. "ldap://" + host + ":" + port".
    *   (NB: If the protocol is <i>NOT</i> LDAP, (e.g. DSML) this must be set first.
	*	@param host the host name to connect to, e.g. echidna or 168.10.5.122.
	*	@param port the host port to connect to, e.g. 19389.
	*/

    public void setURL(String host, int port)
    {
        if (protocol == LDAP)
            url = "ldap://" + host + ":" + port;
        else if (protocol == DSML)
            url = "http://" + host + ":" + port;

    }

    /**
     *	Sets the url from the host & port, e.g. "ldap://" + host + ":" + port".
     *   (NB: If the protocol is <i>NOT</i> LDAP, (e.g. DSML) this must be set first.
     *	@param URL The full URL to connect to
     */

    public void setURL(String URL)
    {
        if (protocol==LDAP)
        {
            if (URL.toLowerCase().startsWith("ldap://"))
                url = URL;
            else
                url = "ldap://" + URL;
        }
        else if (protocol == DSML)
        {
            if (URL.toLowerCase().startsWith("http://"))
                url = URL;
            else if (URL.toLowerCase().startsWith("dsml://"))
                url = "http://" + URL.substring(7);
            else
                url = "http://" + URL;
        }
        else    // not sure if this is necessary...
        {
            if (URL.toLowerCase().startsWith("ldap:"))
            {
                protocol = LDAP;
                url = URL;
            }
            else if (URL.toLowerCase().startsWith("http:"))
            {
                protocol = DSML;
                url = URL;
            }
            else if (URL.toLowerCase().startsWith("dsml:"))
            {
                protocol = DSML;
                url = "http:" + URL.substring(5);
            }
        }
    }

    public String getURL()
    {
        return url;
    }

   /**
    *	Gets the host name from the url string.
	*	@return the host name for example: DEMOCORP.
	*/

   // parse rules; the url is always of the form <protocol>://<hostname>:<port>[/server stuff (for dsml only)]

	public String getHost()
	{
		if(url==null)
			return null;

        int protocolSeparator = url.indexOf("://") + 3;
        int portSeparator = url.indexOf(":", protocolSeparator);
		return url.substring(protocolSeparator, portSeparator);
	}



   /**
    *	Gets the port number from the url string.
	*	@return the port number for example: 19389.
	*/

	public int getPort()
	{
		if(url==null)
			return -1;

        try
        {
            int protocolSeparator = url.indexOf("://") + 3;
            int portSeparator = url.indexOf(":", protocolSeparator)+1;
            int serverDetails = url.indexOf("/", portSeparator);

            String port = (serverDetails == -1)? url.substring(portSeparator):url.substring(portSeparator, serverDetails);
            int portNumber =  Integer.parseInt(port);
            if (portNumber > 65536 || portNumber <= 0)
                return -1;

            return portNumber;
        }
        catch (NumberFormatException nfe)
        {
            return -1;
        }
	}



   /**
    *	Returns this data object as a string (doesn't include passwords)..
	*	@return the data object as a string.
	*/

	public String toString()
	{
		return new String("baseDN: " 					+ baseDN +
							"\nversion: " 				+ Integer.toString(version) +
							"\nurl: " 					+ url +
							"\nuserDN: " 				+ userDN +
							"\nreferralType: " 			+ referralType +
							"\naliasType: " 			+ aliasType +
							"\nuseSSL: " 				+ String.valueOf(useSSL) +
							"\ncacerts: " 				+ cacerts +
							"\nclientcerts: " 			+ clientcerts +
							"\nclientKeystoreType: " 	+ clientKeystoreType +
							"\ncaKeystoreType: " 		+ caKeystoreType +
							"\ntracing: " 				+ String.valueOf(tracing) +
                            "\nprotocol: " 				+ protocol
							);
	}
}