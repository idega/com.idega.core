
package com.idega.core.ldap.server.util;



public class Ldap
{

    private String server;
    private String port;
    private String baseDN;
    private String user;
    private String password;

    /**
     * Returns the baseDn.
     * @return String
     */
    public String getBaseDN() {
        return baseDN;
    }

    /**
     * Returns the password.
     * @return String
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the port.
     * @return String
     */
    public String getPort() {
        return port;
    }

    /**
     * Returns the server.
     * @return String
     */
    public String getServer() {
        return server;
    }

    /**
     * Returns the user.
     * @return String
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the baseDn.
     * @param baseDn The baseDn to set
     */
    public void setBaseDN(String baseDN) {
        this.baseDN = baseDN;
    }

    /**
     * Sets the password.
     * @param password The password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the port.
     * @param port The port to set
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * Sets the server.
     * @param server The server to set
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * Sets the user.
     * @param user The user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

   }
