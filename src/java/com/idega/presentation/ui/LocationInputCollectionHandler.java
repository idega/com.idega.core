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
		String action = iwc.getParameter(LocationInput.PARAMETER_ACTION);

		String sourceID = iwc.getParameter(sourceName);

		if (LocationInput.ACTION_UPDATE_CITIES.equals(action)) {
		  return handleCityUpdate(sourceName, sourceID);
		} else if (LocationInput.ACTION_UPDATE_POSTAL_CODE.equals(action)) {
		  return handlePostalCodeUpdate(iwc, sourceName, sourceID);
		}
		return null;
	}
	
	private RemoteScriptingResults handleCityUpdate(String sourceName, String countryID) {
		if (countryID != null) {
		    
				try {
					PostalCodeHome pcHome = (PostalCodeHome) IDOLookup.getHome(PostalCode.class);
					Collection countryZips = pcHome.getUniquePostalCodeNamesByCountryIdOrderedByPostalCodeName(Integer.parseInt(countryID));
		
			    Vector ids = new Vector();
			    Vector names = new Vector();
			    
			    Iterator itZips = countryZips.iterator();
			    if (itZips.hasNext()) {
			    	ids.add("-1");
			    	names.add("Select");
			    }
			    while (itZips.hasNext()) {
			    	String s = (String) itZips.next();
			    	ids.add(s);
			    	names.add(s);
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
	
	private RemoteScriptingResults handlePostalCodeUpdate(IWContext iwc, String sourceName, String cityName) {
		if (cityName != null) {

				try {
					String parCountryID = iwc.getParameter(LocationInput.PARAMETER_COUNTRY_ID);
					String countryID = iwc.getParameter(parCountryID);
					PostalCodeHome pcHome = (PostalCodeHome) IDOLookup.getHome(PostalCode.class);
					Collection countryZips = pcHome.findByNameAndCountry(cityName, new Integer(countryID));
		
			    Vector ids = new Vector();
			    Vector names = new Vector();
			    
			    Iterator itZips = countryZips.iterator();
			    if (itZips.hasNext()) {
			    	ids.add("-1");
			    	names.add("Select");
			    }
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
