package com.idega.presentation.ui;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import com.idega.core.location.data.PostalCode;
import com.idega.core.location.data.PostalCodeHome;
import com.idega.data.IDOLookup;
import com.idega.presentation.IWContext;
import com.idega.presentation.remotescripting.RemoteScriptCollection;
import com.idega.presentation.remotescripting.RemoteScriptHandler;
import com.idega.presentation.remotescripting.RemoteScriptingResults;


/**
 * @author gimmi
 */
public class LocationInputCollectionHandler implements RemoteScriptCollection {

	public RemoteScriptingResults getResults(IWContext iwc) {
		String sourceName = iwc.getParameter(RemoteScriptHandler.PARAMETER_SOURCE_PARAMETER_NAME);
		String countryID = iwc.getParameter(sourceName);
	
//	  this.getParentPage().setOnLoad("if (parent != self) parent.handleResponse(document)");
	
	
	  if (countryID != null) {
	    
			try {
				PostalCodeHome pcHome = (PostalCodeHome) IDOLookup.getHome(PostalCode.class);
				Collection countryZips = pcHome.findAllByCountryIdOrderedByPostalCode(Integer.parseInt(countryID));
	
		    Vector ids = new Vector();
		    Vector names = new Vector();
		    
		    Iterator itZips = countryZips.iterator();
		    while (itZips.hasNext()) {
		    	PostalCode p = (PostalCode) itZips.next();
		    	ids.add(p.getPrimaryKey().toString());
		    	names.add(p.getPostalAddress());
		    }
		    
		    if (countryZips.isEmpty()) {
		    	ids.add("-1");
		    	names.add("Unavailable");
		    }
		    
		    
		    RemoteScriptingResults rsr = new RemoteScriptingResults(RemoteScriptHandler.getLayerName(sourceName, "id"), ids);
		    rsr.addLayer(RemoteScriptHandler.getLayerName(sourceName, "name"), names);
	
		    return rsr;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	  
	  return null;
	}
}
