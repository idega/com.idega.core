package com.idega.util.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2007/09/27 16:23:30 $ by $Author: civilis $
 */
public class NamespaceContextImpl implements NamespaceContext {

	private HashMap<String, String> prefixMap;

	public NamespaceContextImpl() {
		prefixMap = new HashMap<String, String>();
	}

	@Override
	public String getNamespaceURI(String prefix) {

		return prefixMap.containsKey(prefix) ? prefixMap.get(prefix) : XMLConstants.DEFAULT_NS_PREFIX;
	}

	@Override
	public String getPrefix(String uri) {

		if(prefixMap.containsValue(uri)) {

			Set<Entry<String, String>> entries = prefixMap.entrySet();

			for (Entry<String, String> entry : entries) {

				if(entry.getValue().equals(uri))
					return entry.getKey();
			}
		}

		return null;
	}

	@Override
	public Iterator<String> getPrefixes(String uri) {

		if(!prefixMap.containsValue(uri))
			return null;

		Set<Entry<String, String>> entries = prefixMap.entrySet();
		List<String> prefixes = new ArrayList<String>();

		for (Entry<String, String> entry : entries)

			if(entry.getValue().equals(uri))
				prefixes.add(entry.getKey());

		return prefixes.iterator();
	}

	public void addPrefix(String pref, String uri) {

		prefixMap.put(pref, uri);
	}

	public void removePrefix(String pref) {

		if(prefixMap.containsKey(pref))
			prefixMap.remove(pref);
	}
}