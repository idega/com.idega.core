package com.idega.presentation.ui;

import java.util.Collection;
import com.idega.core.location.data.Country;
import com.idega.core.location.data.CountryHome;
import com.idega.core.location.data.PostalCode;
import com.idega.core.location.data.PostalCodeHome;
import com.idega.data.IDOLookup;
import com.idega.presentation.IWContext;
import com.idega.presentation.remotescripting.RemoteScriptHandler;
import com.idega.presentation.ui.util.SelectorUtility;



/**
 * @author gimmi
 */
public class LocationInput extends InterfaceObject {

	private static final String ID_LAYER_ID = "loc_res_id";
	private static final String NAME_LAYER_ID = "loc_res_name";

	public static final String PARAMETER_NAME_COUNTRIES = "par_con";
	public static final String PARAMETER_NAME_ZIPS = "par_zip";
	
	private static final String PARAMETER_REMOTE_CALL = "par_rmc";
	
	private String selectedCountryID = null;
	private String selectedZipID = null;
	
	private String parCountryID = null;
	private String parZipID = null;
	
	private DropdownMenu countryDrop = null;
	private DropdownMenu zipDrop = null;
	
	private String iframeName = "tmpFrame";
	private String separator = " ";
	
	public LocationInput() {
		this("lipc", "lipz");
	}
	
	public LocationInput(String countryParameterName, String postalCodeParameterName) {
		parCountryID = countryParameterName;
		parZipID = postalCodeParameterName;
		iframeName = parCountryID + "_" + zipDrop;
		
		countryDrop = new DropdownMenu(parCountryID);
		zipDrop = new DropdownMenu(parZipID);
	}
	
	public Object clone() {
		LocationInput inp = (LocationInput) super.clone();
		if (countryDrop != null) {
			inp.countryDrop = (DropdownMenu) countryDrop.clone();
		}
		if (zipDrop != null) {
			inp.zipDrop = (DropdownMenu) zipDrop.clone();
		}
		
		return inp;
	}
	
	public DropdownMenu getCountryDropdown() {
		return countryDrop;
	}
	
	public DropdownMenu getPostalCodeDropdown() {
		return zipDrop;
	}
	
	public void main(IWContext iwc) throws Exception {

		getForm().addParameter(PARAMETER_NAME_COUNTRIES, parCountryID);
		getForm().addParameter(PARAMETER_NAME_ZIPS, parZipID);
		
		selectedCountryID = iwc.getParameter(parCountryID);
		selectedZipID = iwc.getParameter(parZipID);

		CountryHome countryHome = (CountryHome) IDOLookup.getHome(Country.class);
		Collection countries = countryHome.findAll();
		Collection postalCodes = null;

		SelectorUtility su = new SelectorUtility();
		
		countryDrop.addMenuElements(countries);
		if (selectedCountryID != null) {
			PostalCodeHome pcHome = (PostalCodeHome) IDOLookup.getHome(PostalCode.class);
			postalCodes = pcHome.findAllByCountryIdOrderedByPostalCode(Integer.parseInt(selectedCountryID));
			countryDrop.setSelectedElement(selectedCountryID);
		}
		
		
		zipDrop = (DropdownMenu) su.getSelectorFromIDOEntities(zipDrop, postalCodes, "getPostalAddress");
		if (postalCodes == null) {
			zipDrop.addFirstOption(new SelectOption("Select a country", "-1"));
		}
		if (selectedZipID != null) {
			zipDrop.setSelectedElement(selectedZipID);
		}

		RemoteScriptHandler rsh = new RemoteScriptHandler(countryDrop, zipDrop);
		rsh.setRemoteScriptCollectionClass(LocationInputCollectionHandler.class);
		add(rsh);
			
			
//			IFrame iFrame = addRemoteScriptingScripts(getForm(), zipDrop, url);
//			add(iFrame);
//
//	    boolean addCountryDrop = countryDrop.getParent()==null;
//	    boolean addZipDrop = zipDrop.getParent()==null;
//	    
//	    if (addCountryDrop) {
//	    	add(countryDrop);
//	    }
//	    
//	    if (addCountryDrop && addZipDrop) {
//	    	add(separator);
//	    }
//	    
//	    if (addZipDrop) {
//	    	add(zipDrop);
//	    }
	}
//	
//	public void handleKeepStatus(IWContext iwc) {
//  }
//
//	private IFrame addRemoteScriptingScripts(Form form, DropdownMenu res, String remotePageURL) {
//		getAssociatedScript().addFunction("handleResponse", "function handleResponse(doc) {\n" +
//				"  var namesEl = document.getElementById('"+countryDrop.getID()+"');\n"+   // Hard coded ...
//				"  var zipEl = document.getElementById('"+zipDrop.getID()+"');\n"+ 
//				"  zipEl.options.length = 0; \n"+
//				"  var dataElID = doc.getElementById('"+ID_LAYER_ID+"');\n" + 
//				"  var dataElName = doc.getElementById('"+NAME_LAYER_ID+"');\n" + 
//				"  namesColl = dataElName.childNodes; \n"+
//				"  idsColl = dataElID.childNodes; \n" +
//				"  var numNames = namesColl.length; \n"+
//				"  var str = '';\n"+
//				"  var ids = '';\n"+
//				"  for (var q=0; q<numNames; q++) {\n"+
//				"    if (namesColl[q].nodeType!=1) continue; // it's not an element node, let's skedaddle\n"+
//				"    str = namesColl[q].id;\n"+
//				"    ids = idsColl[q].id;\n"+
//				"    zipEl.options[zipEl.options.length] = new Option(str, ids);\n" +
//				"  }\n" +
//		"}\n");
//		
//		getAssociatedScript().addFunction("buildQueryString(theFormName)", "function buildQueryString(theFormName){ \n"
//				+"  theForm = document.forms[theFormName];\n"
//				+"  var qs = ''\n"
//				+"  for (e=0;e<theForm.elements.length;e++) {\n"
//				+"    if (theForm.elements[e].name!='') {\n"
//				+"      qs+='&'\n"
//				+"      qs+=theForm.elements[e].name+'='+escape(theForm.elements[e].value)\n"
//				+"      }\n"
//				+"    }\n"
//				+"  return qs\n}\n");
//		
//		
//		IFrame iframe = new IFrame(iframeName);
//		iframe.setID(iframeName);
//		iframe.setHeight(0);
//		iframe.setWidth(0);
//		iframe.setBorder(0);
//		iframe.setSrc("blank.html");
//		return iframe;
//	}
//
//	
//	public void handleRemoteCall(IWContext iwc) {
//		String countryParName = iwc.getParameter(LocationInput.PARAMETER_NAME_COUNTRIES);
//		String countryID = iwc.getParameter(countryParName);
//	
//	  this.getParentPage().setOnLoad("if (parent != self) parent.handleResponse(document)");
//	
//	
//	  if (countryID != null) {
//	    
//			try {
//				PostalCodeHome pcHome = (PostalCodeHome) IDOLookup.getHome(PostalCode.class);
//				Collection countryZips = pcHome.findAllByCountryIdOrderedByPostalCode(Integer.parseInt(countryID));
//	
//		    Vector ids = new Vector();
//		    Vector names = new Vector();
//		    
//		    Iterator itZips = countryZips.iterator();
//		    while (itZips.hasNext()) {
//		    	PostalCode p = (PostalCode) itZips.next();
//		    	ids.add(p.getPrimaryKey().toString());
//		    	names.add(p.getPostalAddress());
//		    }
//		    
//		    if (countryZips.isEmpty()) {
//		    	ids.add("-1");
//		    	names.add("Unavailable");
//		    }
//		    
//		    
//		    RemoteScriptingResults rsr = new RemoteScriptingResults(ID_LAYER_ID, ids);
//		    rsr.addLayer(NAME_LAYER_ID, names);
//	
//		    add(rsr);
//			}
//			catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
// 
//	}
//	
//
//	public String getRemoteUrl(IWContext iwc) {
//		String url = iwc.getIWMainApplication().getObjectInstanciatorURI(getClass().getName());
//		url += "&"+PARAMETER_REMOTE_CALL+"=true";
//		return url;
//	}
//
//	public boolean isRemoteCall(IWContext iwc) {
//		return iwc.isParameterSet(PARAMETER_REMOTE_CALL);
//	}
	public boolean isContainer() {
		return false;
	}	
	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public void handleKeepStatus(IWContext iwc) {
		// Done in main
	}
	
}
