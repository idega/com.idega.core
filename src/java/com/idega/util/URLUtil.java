/*
 * Created on 15.2.2003
 */
package com.idega.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.core.builder.business.ICBuilderConstants;
import com.idega.core.builder.data.ICPage;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.util.datastructures.MultivaluedHashMap;
import com.idega.util.text.TextSoap;

/**
 * @author laddi
 */
public class URLUtil {

	private static final String IB_PAGE_PARAMETER = ICBuilderConstants.IB_PAGE_PARAMETER;

	private MultivaluedHashMap _parameters;
	private String _path;
	private String _page;
	private String _protocol;
	private String _host;
	private int _port = -1;
	
	boolean _convertSpecialCharacters = true;

	/**
	 * 
	 */
	public URLUtil(String URL) {
		parseURL(URL);
	}
	
	public URLUtil(IWApplicationContext iwac, ICPage page) {
		this(iwac, ((Integer)page.getPrimaryKey()).intValue());
	}
	
	public URLUtil(IWApplicationContext iwac, int pageID) {
		try {
			parseURL(BuilderServiceFactory.getBuilderService(iwac).getPageURI(pageID));
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public URLUtil(String URL, boolean convertSpecialCharacters) {
		this._convertSpecialCharacters = convertSpecialCharacters;
		parseURL(URL);
	}
	
	private void parseURL(String URL) {
		try {
			URL url = new URL(URL);
			setProtocol(url.getProtocol());
			setHost(url.getHost());
			setPort(url.getPort());
			setPath(url.getPath());
			parseQuery(url.getQuery());
		}
		catch (MalformedURLException mue) {
			//mue.printStackTrace();
			
			if (URL.indexOf("?") != -1) {
				setPath(URL.substring(0, URL.indexOf("?")));
				if (URL.length() > URL.indexOf("?") + 1) {
					parseQuery(URL.substring(URL.indexOf("?") + 1));
				}
			}
			else {
				setPath(URL);
			}
		}
	}
	
	private void parseQuery(String query) {
		if (query != null) {
			StringTokenizer tokens = new StringTokenizer(query, "&");
			while (tokens.hasMoreTokens()) {
				String parameter = tokens.nextToken();
				StringTokenizer token = new StringTokenizer(parameter, "=");
				while (token.hasMoreTokens()) {
					String name = token.nextToken();
					if (token.hasMoreTokens()) {
						String value = token.nextToken();
						addParameter(name, value);
					}
				}
			}
		}
	}
	
	public String toString() {
		StringBuffer returnString = new StringBuffer();
		if (getProtocol() != null) {
			returnString.append(getProtocol()).append("://");
		}
		if (getHost() != null) {
			returnString.append(getHost());
		}
		if (getPort() != 80 && getPort() != -1) {
			returnString.append(":").append(getPort());
		}
		if (getPath() != null) {
			returnString.append(getPath());
		}
		returnString.append(getParameters());
		
		return returnString.toString();
	}
	
	private String getParameters() {
		StringBuffer parameters = new StringBuffer();
		if (this._parameters != null) {
			parameters.append("?");
			int index = 0;
			Iterator iter = this._parameters.keySet().iterator();
			while (iter.hasNext()) {
				String name = (String) iter.next();
				Collection values = this._parameters.getCollection(name);
				if (name != null && values != null) {
					Iterator iterator = values.iterator();
					while (iterator.hasNext()) {
						String value = (String) iterator.next();
						if (index > 0) {
							parameters.append("&");
						}
						parameters.append(name);
						parameters.append("=");
						parameters.append(value);
						index++;
					}
				}
			}
		}
		
		if (this._convertSpecialCharacters) {
			return TextSoap.convertSpecialCharacters(parameters.toString());
		}
		else {
			return parameters.toString();
		}
	}

	/**
	 * @return String
	 */
	public String getPath() {
		return this._path;
	}

	/**
	 * Sets the path.
	 * @param path The path to set
	 */
	public void setPath(String path) {
		this._path = path;
	}

	public void addParameter(String name, String value) {
		if (this._parameters == null) {
			this._parameters = new MultivaluedHashMap();
		}
			
		if (name != null && !name.equalsIgnoreCase("null")) {
			this._parameters.put(name, value);
		}
	}
	
	public void addParameter(String name, int value) {
		addParameter(name, String.valueOf(value));
	}
	
	public void removeParameter(String name) {
		if (this._parameters != null) {
			this._parameters.remove(name);
		}
	}
	
	public void removeParameterValue(String name, String value) {
		if (this._parameters != null) {
			Collection values = this._parameters.getCollection(name);
			if (values != null) {
				values.remove(value);
			}
		}
	}
	
	public boolean containsParameter(String name) {
		if (this._parameters != null) {
			return this._parameters.containsKey(name);
		}
		return false;
	}
	
	public boolean containtsParameterValue(String name, String value) {
		if (this._parameters != null) {
			Collection values = this._parameters.getCollection(name);
			if (values != null) {
				return values.contains(value);
			}
		}
		return false;
	}
	
	public void setPage(ICPage page) {
		if ((page != null) && (page.getID() != -1)) {
			addParameter(IB_PAGE_PARAMETER, page.getPrimaryKey().toString());
		}
	}

	public void setPage(int pageID) {
		addParameter(IB_PAGE_PARAMETER, pageID);
	}

	/**
	 * @return String
	 */
	public String getHost() {
		return this._host;
	}

	/**
	 * @return int
	 */
	public int getPort() {
		return this._port;
	}

	/**
	 * @return String
	 */
	public String getProtocol() {
		return this._protocol;
	}

	/**
	 * Sets the host.
	 * @param host The host to set
	 */
	public void setHost(String host) {
		this._host = host;
	}

	/**
	 * Sets the port.
	 * @param port The port to set
	 */
	public void setPort(int port) {
		this._port = port;
	}

	/**
	 * Sets the protocol.
	 * @param protocol The protocol to set
	 */
	public void setProtocol(String protocol) {
		this._protocol = protocol;
	}
}
