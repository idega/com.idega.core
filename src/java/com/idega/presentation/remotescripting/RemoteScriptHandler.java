package com.idega.presentation.remotescripting;

import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.IFrame;
import com.idega.presentation.ui.InterfaceObject;
import com.idega.presentation.ui.TextInput;


/**
 * @author gimmi
 */
public class RemoteScriptHandler extends PresentationObjectContainer { //implements RemoteScriptable {
	
	private static final String PARAMETER_REMOTE_SCRIPT_HANDLING_CLASS = "prc";
	private static final String PARAMETER_SOURCE_NAME = "psn";
	public static final String PARAMETER_SOURCE_PARAMETER_NAME = "prmp";
	
	private InterfaceObject source;
	private PresentationObject target;
	
	private String iframeName;
	private RemoteScriptCollection remoteScriptCollection;
	
	private boolean sourceIsTrigger = true;
	/**
	 * Default construction should never be used unless
	 * class is receiving a remote call
	 */
	public RemoteScriptHandler() {
		// Should only be used for remote calls
	}
	
	public RemoteScriptHandler(InterfaceObject source, PresentationObject target) {
		this.source = source;
		this.target = target;
		
		iframeName = source.getName()+"_"+target.getName();
	}
	
	public void main(IWContext iwc) throws Exception{
		
		if (isRemoteCall(iwc)) {
			
			handleRemoteCall(iwc);
			
		} else {

			// Adding object if they are not added already
			if (source.getParent() == null) {
				add(source);
			}
			
			if (target.getParent() == null) {
				add(target);
			}

			// source MUST BE added to something before these methods are called
			if (sourceIsTrigger) {
				if (source instanceof TextInput) {
					source.setOnKeyUp(getSubmitEvent(iwc));
				} else {
					source.setOnChange(getSubmitEvent(iwc));
				}
			}
			
			addRemoteScriptingScripts();
			
		}
		
	}
	
	public String getSubmitEvent(IWContext iwc) {
		return "return callToServer('"+getRemoteUrl(iwc)+"' + buildQueryString_"+source.getID()+"(document."+source.getForm().getID()+".name), '"+iframeName+"', document."+source.getForm().getID()+".name)";
	}
	
	private void addRemoteScriptingScripts() {
		if (target instanceof DropdownMenu) {
			addScriptForDropdown();
		} else if (target instanceof Layer) {
			addScriptForLayer();
		} else {
			throw new IllegalArgumentException("Unsupported target instance "+target.getClass().getName());
		}
		
		addBuildQueryScript();
		
		addIFrame();
	}

	private void addIFrame() {
		IFrame iframe = new IFrame(iframeName);
		iframe.setID(iframeName);
		iframe.setHeight(0);
		iframe.setWidth(0);
		iframe.setBorder(0);
		iframe.setSrc("blank.html");
		add(iframe);
	}

	private void addBuildQueryScript() {
		getAssociatedScript().addFunction("buildQueryString_"+source.getID()+"(theFormName)", "function buildQueryString_"+source.getID()+"(theFormName){ \n"
				+"  theForm = document.forms[theFormName];\n"
				+"  var qs = ''\n"
				+"  for (e=0;e<theForm.elements.length;e++) {\n"
				+"    if (theForm.elements[e].name != '') {\n"
				+"      qs+='&'\n"
				+"      qs+=theForm.elements[e].name+'='+escape(theForm.elements[e].value)\n"
				+"    }\n"
				+"  } \n"
				+"  qs+='&"+PARAMETER_SOURCE_PARAMETER_NAME+"="+source.getName()+"';"
				+"  return qs\n"
				+"}\n");
	}

	private void addScriptForDropdown() {
		getAssociatedScript().addFunction("handleResponse_"+source.getID(), "function handleResponse_"+source.getID()+"(doc) {\n" +
				"  var namesEl = document.getElementById('"+source.getID()+"');\n"+
				"  var zipEl = document.getElementById('"+target.getID()+"');\n"+ 
				"  zipEl.options.length = 0; \n"+
				"  var dataElID = doc.getElementById('"+RemoteScriptHandler.getLayerName(source.getName(), "id")+"');\n" + 
				"  var dataElName = doc.getElementById('"+RemoteScriptHandler.getLayerName(source.getName(), "name")+"');\n" + 
				"  namesColl = dataElName.childNodes; \n"+
				"  idsColl = dataElID.childNodes; \n" +
				"  var numNames = namesColl.length; \n"+
				"  var str = '';\n"+
				"  var ids = '';\n"+
				"  for (var q=0; q<numNames; q++) {\n"+
				"    if (namesColl[q].nodeType!=1) continue; // it's not an element node, let's skedaddle\n"+
				"    str = namesColl[q].id;\n"+
				"    ids = idsColl[q].id;\n"+
				"    zipEl.options[zipEl.options.length] = new Option(str, ids);\n" +
				"  }\n" +
		"}\n");
	}
	
	private void addScriptForLayer() {
		getAssociatedScript().addFunction("handleResponse_"+source.getID(), "function handleResponse_"+source.getID()+"(doc) {\n" +
				"  var dataEl = doc.getElementById('"+RemoteScriptHandler.getLayerName(source.getName())+"');\n" + 
				"  var str = '';\n" +
				"  if (dataEl != null) {\n" +
				"    namesColl = dataEl.childNodes; \n"+
				"    var numNames = namesColl.length; \n"+
				"    for (var q=0; q<numNames; q++) {\n"+
				"      if (namesColl[q].nodeType!=1) continue; // it's not an element node, let's skedaddle\n"+
				"      str+= namesColl[q].id;\n"+
				"    }\n"+
				"  } else {\n" +
				"    str = '';\n" +
				"  }\n"+
				"  var resultText = this.document.getElementById('"+target.getID()+"');\n"+
				"  resultText.innerHTML = str;\n" +
		"}\n");	}

	public static String getLayerName(String sourceName) {
		return sourceName+"_div";
	}

	public static String getLayerName(String sourceName, String addon) {
		return sourceName+"_"+addon+"_div";
	}
	/**
	 * Class must implement RemoteScripCollection class
	 * @param remoteScriptCollectionClass
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public void setRemoteScriptCollectionClass(Class remoteScriptCollectionClass) throws InstantiationException, IllegalAccessException {
		this.remoteScriptCollection = (RemoteScriptCollection) remoteScriptCollectionClass.newInstance();
	}
	
	private void handleRemoteCall(IWContext iwc) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String rscClassName = iwc.getParameter(PARAMETER_REMOTE_SCRIPT_HANDLING_CLASS);
		RemoteScriptCollection rsc = (RemoteScriptCollection) Class.forName(rscClassName).newInstance();
		this.getParentPage().setOnLoad("if (parent != self) parent.handleResponse_"+iwc.getParameter(PARAMETER_SOURCE_NAME)+"(document)");
		add(rsc.getResults(iwc));
	}

	private String getRemoteUrl(IWContext iwc) {
		String url = iwc.getIWMainApplication().getObjectInstanciatorURI(getClass().getName());
		url += "&"+PARAMETER_REMOTE_SCRIPT_HANDLING_CLASS+"="+remoteScriptCollection.getClass().getName()+"&"+PARAMETER_SOURCE_NAME+"="+source.getID();
		return url;
	}

	private boolean isRemoteCall(IWContext iwc) {
		return iwc.isParameterSet(PARAMETER_REMOTE_SCRIPT_HANDLING_CLASS);
	}
	
	public void setIsSourceTrigger(boolean isSourceTrigger) {
		this.sourceIsTrigger = isSourceTrigger;
	}

}
