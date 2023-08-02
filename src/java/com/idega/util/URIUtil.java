package com.idega.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision$
 *
 * Last modified: $Date$ by $Author$
 */
public class URIUtil {

	private String uri;
	private Map<String, String> parameters;

	public URIUtil() {
		this(CoreConstants.EMPTY);
	}

	public URIUtil(String uriStr) {
		setUri(uriStr);
	}

	public void setUri(String uri) {
		parameters = null;
		this.uri = uri;
	}

	public Map<String, String> getParameters() {

		if(parameters == null)
			parameters = new HashMap<>();

		if(uri != null) {

			try {
				String query = new URI(uri).getQuery();

				if(StringUtil.isEmpty(query))
					return parameters;

				StringTokenizer st = new StringTokenizer(query, CoreConstants.AMP, false);

				while (st.hasMoreTokens()) {

					String current = st.nextToken();

					if(current.contains(CoreConstants.EQ)) {

						int eqOcc = current.indexOf(CoreConstants.EQ);
						parameters.put(URLDecoder.decode(current.substring(0, eqOcc), CoreConstants.ENCODING_UTF8), URLDecoder.decode(current.substring(eqOcc+1), CoreConstants.ENCODING_UTF8));
					}
				}

			} catch (Exception e) {
				parameters = null;
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage());
			}
		} else {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "No uri while resolving uri parameters");
		}

		return parameters;
	}

	public void setParameter(String key, String value) {
		if (StringUtil.isEmpty(key) || value == null) {
			return;
		}

		if(parameters == null)
			parameters = getParameters();

		String notEncodedKey = key;
		String notEncodedValue = value;

		try {
			key = URLEncoder.encode(key, CoreConstants.ENCODING_UTF8);
			value = URLEncoder.encode(value, CoreConstants.ENCODING_UTF8);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		String query = null;

		if (uri.contains(CoreConstants.HASH) && uri.contains(CoreConstants.QMARK)) {
			query = uri.substring(uri.indexOf(CoreConstants.QMARK) + 1);
		} else {
			try {
				query = new URI(uri).getRawQuery();

			} catch (URISyntaxException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
				return;
			}
		}

		if(query == null)
			query = CoreConstants.EMPTY;

		String nonQuery = uri.substring(0, uri.lastIndexOf(query));

		if(parameters.containsKey(notEncodedKey)) {

			StringTokenizer st = new StringTokenizer(query, CoreConstants.AMP, false);
			StringBuilder newquery = new StringBuilder(CoreConstants.EMPTY);

			while (st.hasMoreTokens()) {
				String current = st.nextToken();

				if(!current.startsWith(key)) {

					newquery.append(current);

					if(st.hasMoreTokens())
						newquery.append(CoreConstants.AMP);
				}

			}

			query = newquery.toString();
		}

		uri =
		new StringBuilder(nonQuery)
		.append(nonQuery.endsWith(CoreConstants.QMARK) ? CoreConstants.EMPTY : CoreConstants.QMARK)
		.append(query)
		.append(CoreConstants.EMPTY.equals(query) ? CoreConstants.EMPTY : CoreConstants.AMP)
		.append(key)
		.append(CoreConstants.EQ)
		.append(value)
		.toString();

		parameters.put(notEncodedKey, notEncodedValue);
	}

	public String getUri() {

		return uri;
	}
}