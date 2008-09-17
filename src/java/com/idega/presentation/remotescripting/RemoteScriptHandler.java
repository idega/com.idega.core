package com.idega.presentation.remotescripting;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.idega.block.web2.business.Web2Business;
import com.idega.business.IBOLookup;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.Script;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.IFrame;
import com.idega.presentation.ui.InterfaceObject;
import com.idega.presentation.ui.TextInput;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.util.PresentationUtil;



/**
 * A class for handling remote scripting between two objects.
 * @author gimmi
 */
public class RemoteScriptHandler extends PresentationObjectContainer { //implements RemoteScriptable {
	
	private static final String PARAMETER_REMOTE_SCRIPT_HANDLING_CLASS = "prc";
	private static final String PARAMETER_SOURCE_NAME = "psn";
	public static final String PARAMETER_SOURCE_PARAMETER_NAME = "prmp";
	
	private InterfaceObject source;
	private PresentationObject target;
	private Map parameters = new HashMap();
	private Map toClear = new HashMap();
	private String iframeName;
	private RemoteScriptCollection remoteScriptCollection;
	
	private Web2Business web2Business;
	
	public Web2Business getWeb2Business(IWContext iwc) throws RemoteException {
		if(web2Business == null) {
			web2Business = (Web2Business) IBOLookup.getServiceInstance(iwc, Web2Business.class);
		}
		return web2Business;
	}
	private boolean sourceIsTrigger = true;
	/**
	 * Default construction should never be used unless
	 * class is receiving a remote call
	 */
	public RemoteScriptHandler() {
		// Should only be used for remote calls
	}
	/**
	 * @param source The source object, that triggers the event
	 * @param target The target object, the one affected by the event
	 */
	public RemoteScriptHandler(InterfaceObject source, PresentationObject target) {
		this.source = source;
		this.target = target;
		
		this.iframeName = source.getName()+"_"+target.getName();
	}
	
	public void main(IWContext iwc) throws Exception{
		if (isRemoteCall(iwc)) {
			
			handleRemoteCall(iwc);
			
		} else {

			// Adding object if they are not added already
			if (this.source.getParent() == null) {
				add(this.source);
			}
			
			if (this.target.getParent() == null) {
				add(this.target);
			}

			// source MUST BE added to something before these methods are called
			if (this.sourceIsTrigger) {
				if (this.source instanceof TextInput) {
					this.source.setOnKeyUp(getSubmitEvent(iwc));
				} else {
					this.source.setOnChange(getSubmitEvent(iwc));
				}
			}
			
			addRemoteScriptingScripts(iwc);
			
		}
		
	}
		
	private void addRemoteScriptingScripts(IWContext iwc) {
		if (this.target instanceof DropdownMenu) {
			addScriptForDropdown();
		} else if (this.target instanceof Layer) {
			addScriptForLayer();
		} else {
			throw new IllegalArgumentException("Unsupported target instance "+this.target.getClass().getName());
		}
		
		addCallToServer(iwc);
		
		addBuildQueryScript();
		
		addIFrame(iwc);
	}
	
	private void addCallToServer(IWContext iwc) {
		
		StringBuffer buff = new StringBuffer();
		buff.append("var IFrameObj; // our IFrame object").append("\n")
		.append("function callToServer_"+this.iframeName+"(theFormName) {").append("\n")
		.append("  if (!document.createElement) {return true};").append("\n")
		.append("  var IFrameDoc;").append("\n")
		.append("  if (!IFrameObj && document.createElement) {").append("\n")
		.append("    // create the IFrame and assign a reference to the").append("\n")
		.append("    // object to our global variable IFrameObj.").append("\n")
		.append("    // this will only happen the first time") .append("\n")
		.append("    // callToServer() is called").append("\n")
		.append("	   try {").append("\n")
		.append("      var tempIFrame=document.createElement('iframe');").append("\n")
		.append("      tempIFrame.setAttribute('id','"+this.iframeName+"');").append("\n")
		.append("      tempIFrame.style.border='0px';").append("\n")
		.append("      tempIFrame.style.width='0px';").append("\n")
		.append("      tempIFrame.style.height='0px';").append("\n")
		.append("      IFrameObj = document.body.appendChild(tempIFrame);").append("\n")
		      
		.append("      if (document.frames) {").append("\n")
		.append("        // this is for IE5 Mac, because it will only").append("\n")
		.append("        // allow access to the document object").append("\n")
		.append("        // of the IFrame if we access it through").append("\n")
		.append("        // the document.frames array").append("\n")
		.append("        IFrameObj = document.frames['"+this.iframeName+"'];").append("\n")
		.append("      }").append("\n")
		.append("    } catch(exception) {").append("\n")
		.append("      // This is for IE5 PC, which does not allow dynamic creation").append("\n")
		.append("      // and manipulation of an iframe object. Instead, we'll fake").append("\n")
		.append("      // it up by creating our own objects.").append("\n")
		.append("      iframeHTML='<iframe id=\""+this.iframeName+"\" style=\"';").append("\n")
		.append("      iframeHTML+='border:0px;';").append("\n")
		.append("      iframeHTML+='width:0px;';").append("\n")
		.append("      iframeHTML+='height:0px;';").append("\n")
		.append("      iframeHTML+='\"><\\/iframe>';").append("\n")
		.append("      document.body.innerHTML+=iframeHTML;").append("\n")
		.append("      IFrameObj = new Object();").append("\n")
		.append("      IFrameObj.document = new Object();").append("\n")
		.append("      IFrameObj.document.location = new Object();").append("\n")
		.append("      IFrameObj.document.location.iframe = document.getElementById('"+this.iframeName+"');").append("\n")
		.append("      IFrameObj.document.location.replace = function(location) {").append("\n")
		.append("	      this.iframe.src = location;").append("\n")
		.append(" 	   }").append("\n")
		.append(" 	 }").append("\n")
		.append("	}").append("\n")
		.append("	 if (navigator.userAgent.indexOf('Gecko') !=-1 && !IFrameObj.contentDocument) {").append("\n")
		.append("	   // we have to give NS6 a fraction of a second").append("\n")
		.append("	   // to recognize the new IFrame").append("\n")
		.append("	   setTimeout('callToServer_"+this.iframeName+"(\"'+theFormName+'\")',10);").append("\n")
		.append("	   return false;").append("\n")
		.append("	 }").append("\n")

		.append("  if (IFrameObj.contentDocument) {").append("\n")
		.append("    // For NS6").append("\n")
		.append("    IFrameDoc = IFrameObj.contentDocument;").append("\n") 
		.append("  } else if (IFrameObj.contentWindow) {").append("\n")
		.append("    // For IE5.5 and IE6").append("\n")
		.append("    IFrameDoc = IFrameObj.contentWindow.document;").append("\n")
		.append("  } else if (IFrameObj.document) {").append("\n")
		.append("    // For IE5").append("\n")
		.append("    IFrameDoc = IFrameObj.document;").append("\n")
		.append("  } else {").append("\n")
		.append("    return true;").append("\n")
		.append("  }").append("\n")

		.append("  IFrameDoc.location.replace('"+getRemoteUrl(iwc)+"' + buildQueryString_"+this.source.getID()+"(findObj('"+this.source.getForm().getID()+"')));").append("\n")
		.append("  return false;").append("\n")
		.append("}").append("\n");		
		if (getAssociatedScript() != null) {
			getAssociatedScript().addFunction("callToServer_"+this.iframeName, buff.toString());
		}
	}

	private void addIFrame(IWContext iwc) {
		IFrame iframe = new IFrame(this.iframeName);
		iframe.setID(this.iframeName);
		iframe.setHeight(0);
		iframe.setWidth(0);
		iframe.setBorder(0);
		iframe.setSrc(iwc.getIWMainApplication().getPublicObjectInstanciatorURI());
		add(iframe);
	}

	private void addBuildQueryScript() {
		StringBuffer params = new StringBuffer();
		params.append("&").append(PARAMETER_SOURCE_PARAMETER_NAME).append("=").append(this.source.getName());
		Set parNames = this.parameters.keySet();
		Iterator iter = parNames.iterator();
		while (iter.hasNext()) {
			String name = (String) iter.next();
			String value = (String) this.parameters.get(name);
			params.append("&").append(name).append("=").append(value);
		}
		
		if (getAssociatedScript() != null) {
			getAssociatedScript().addFunction("buildQueryString_"+this.source.getID()+"(theForm)", "function buildQueryString_"+this.source.getID()+"(theForm){ \n"
					+"  var qs = ''\n"
					+"  for (e=0;e<theForm.elements.length;e++) {\n"
					+"    if (theForm.elements[e].name != '') {\n"
					+"      qs+='&'\n"
					+"      qs+=theForm.elements[e].name+'='+theForm.elements[e].value\n"
//					+"      qs+=theForm.elements[e].name+'='+escape(theForm.elements[e].value)\n"
					+"    }\n"
					+"  } \n"
					+"  qs+='"+params.toString()+"';"
					+"  return qs\n"
					+"}\n");
		}
	}

	private void addScriptForDropdown() {
		StringBuffer buff = new StringBuffer();
		buff.append("function handleResponse_"+this.source.getID()+"(doc) {\n")
		.append("  var namesEl = document.getElementById('"+this.source.getID()+"');\n")
		.append("  var zipEl = document.getElementById('"+this.target.getID()+"');\n") 
		.append("  zipEl.options.length = 0; \n")
		.append("  var dataElID = doc.getElementById('"+RemoteScriptHandler.getLayerName(this.source.getName(), "id")+"');\n") 
		.append("  var dataElName = doc.getElementById('"+RemoteScriptHandler.getLayerName(this.source.getName(), "name")+"');\n") 
		.append("  namesColl = dataElName.childNodes; \n")
		.append("  idsColl = dataElID.childNodes; \n")
		.append("  var numNames = namesColl.length; \n")
		.append("  var str = '';\n")
		.append("  var ids = '';\n")
		.append("  for (var q=0; q<numNames; q++) {\n")
		.append("    if (namesColl[q].nodeType!=1) continue; // it's not an element node, let's skedaddle\n")
		.append("    str = namesColl[q].innerHTML;\n")
		.append("    ids = idsColl[q].innerHTML;\n")
		.append("    zipEl.options[zipEl.options.length] = new Option(str, ids);\n")
		.append("  }\n");
				
		buff = addClearMethods(buff);
				
		buff.append("}\n");
		getAssociatedScript().addFunction("handleResponse_"+this.source.getID(), buff.toString()); 
	}
	
	private void addScriptForLayer() {
		StringBuffer buff = new StringBuffer();
		buff.append("function handleResponse_"+this.source.getID()+"(doc) {\n")
		.append("  var dataEl = doc.getElementById('"+RemoteScriptHandler.getLayerName(this.source.getName())+"');\n") 
		.append("  var str = '';\n")
		.append("  if (dataEl != null) {\n")
		.append("    namesColl = dataEl.childNodes; \n")
		.append("    var numNames = namesColl.length; \n")
		.append("    for (var q=0; q<numNames; q++) {\n")
		.append("      if (namesColl[q].nodeType!=1) continue; // it's not an element node, let's skedaddle\n")
		.append("      str+= namesColl[q].innerHTML;\n")
		.append("    }\n")
		.append("  } else {\n")
		.append("    str = '';\n")
		.append("  }\n")
		.append("  var resultText = this.document.getElementById('"+this.target.getID()+"');\n")
		.append("  resultText.innerHTML = str;\n");
		
		buff = addClearMethods(buff);
		
		buff.append("}\n");	
		Script s = getAssociatedScript();
		if (s != null) {
			s.addFunction("handleResponse_"+this.source.getID(), buff.toString());
		}
	}
	
	private StringBuffer addClearMethods(StringBuffer script) {
		Set keySet = this.toClear.keySet();
		Iterator iter = keySet.iterator();
		PresentationObject po;
		String value;
		while (iter.hasNext()) {
			po = (InterfaceObject) iter.next();
			value = (String) this.toClear.get(po);
			if (po instanceof DropdownMenu) {
				script.append( 
				"  var zipEl = document.getElementById('"+po.getID()+"');\n"+ 
				"  zipEl.options.length = 0; \n" +
				"  zipEl.options[zipEl.options.length] = new Option('"+value+"', '-1');\n");
			} else if (po instanceof Layer) {
				if (value == null) {
					value = "";
				}
				script.append(
				"  var resultText = this.document.getElementById('"+po.getID()+"');\n"+
				"  resultText.innerHTML = '"+value+"';\n");
			} else {
				throw new IllegalArgumentException("Unsupported target instance "+this.target.getClass().getName());
			}
		}
		return script;
	}
	
	private void handleRemoteCall(IWContext iwc) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		String rscClassName = iwc.getParameter(PARAMETER_REMOTE_SCRIPT_HANDLING_CLASS);
		RemoteScriptCollection rsc = (RemoteScriptCollection) RefactorClassRegistry.forName(rscClassName).newInstance();
		
		try {
			PresentationUtil.addJavaScriptSourceLineToHeader(iwc, getWeb2Business(iwc).getBundleURIToMootoolsLib());
			PresentationUtil.addJavaScriptActionToBody(iwc, "window.addEvent('domready', function() {if (parent != self) parent.handleResponse_"+iwc.getParameter(PARAMETER_SOURCE_NAME)+"(document);});");
		} catch(RemoteException e) {
			e.printStackTrace();
		}
		
		add(rsc.getResults(iwc));
	}

	private String getRemoteUrl(IWContext iwc) {
//		iwc.getIWMainApplication().getw
		String url = iwc.getIWMainApplication().getPublicObjectInstanciatorURI(getClass());
		if (url.indexOf("?") < 0) {
			url += "?";
		} else {
			url += "&";
		}
		url += PARAMETER_REMOTE_SCRIPT_HANDLING_CLASS+"="+this.remoteScriptCollection.getClass().getName()+"&"+PARAMETER_SOURCE_NAME+"="+this.source.getID();
		return url;
	}

	private boolean isRemoteCall(IWContext iwc) {
		return iwc.isParameterSet(PARAMETER_REMOTE_SCRIPT_HANDLING_CLASS);
	}
	
	/**
	 * Method to get the name of a layer
	 * @param sourceName The name of the source object
	 * @return
	 */
	public static String getLayerName(String sourceName) {
		return sourceName+"_div";
	}

	/**
	 * Method to get the name of a layer
	 * @param sourceName The name of the source object
	 * @param addon A string to add to the name, e.g. <code>id</code> or <code>name</code>
	 * @return
	 */
	public static String getLayerName(String sourceName, String addon) {
		return sourceName+"_"+addon+"_div";
	}

	/**
	 * Method to get the event to trigger the remote script, can be used with onChange, onBlur, and so on.
	 * @param iwc IWContext
	 * @return
	 */
	public String getSubmitEvent(IWContext iwc) {
		return "return callToServer_"+this.iframeName+"(findObj('"+this.source.getForm().getID()+"').name)";
	}

	/**
	 * Set which class handles the remote procedure
	 * Class must implement RemoteScripCollection class
	 * @param remoteScriptCollectionClass
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public void setRemoteScriptCollectionClass(Class remoteScriptCollectionClass) throws InstantiationException, IllegalAccessException {
		this.remoteScriptCollection = (RemoteScriptCollection) remoteScriptCollectionClass.newInstance();
	}

	/**
	 * Set wether or not the source object triggers the event.
	 * Default value is <code>true</code>
	 * @param isSourceTrigger 
	 */
	public void setIsSourceTrigger(boolean isSourceTrigger) {
		this.sourceIsTrigger = isSourceTrigger;
	}

	/**
	 * Add a parameter that is submitted to the remote page
	 * @param name Name of the parameter
	 * @param value Value of the parameter
	 */
	public void addParameter(String name, String value) {
		this.parameters.put(name, value);
	}
	/**
	 * Set if the event is supposed to clear an object 
	 * @param po PresentationObject that is to be cleared
	 * @param emptyValue A value to use instead of nothing
	 */
	public void setToClear(PresentationObject po, String emptyValue) {
		this.toClear.put(po, emptyValue);
	}
	
}
