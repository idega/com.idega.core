//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.presentation.ui;

import java.io.*;
import java.util.*;
import com.idega.presentation.*;
import com.idega.idegaweb.*;
import com.idega.idegaweb.IWURL;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class Form extends InterfaceObjectContainer{

private Window window;
private Vector maintainedParameters;
private boolean maintainAllParameters;
private PresentationObject submitToObject;
private Parameter controlParameter;
private Class windowClass;

private static String FORM_EVENT_PARAMETER="idega_special_form_event";


/**
*Defaults to send to the page itself and the POST method
**/
public Form(){
	//super();
	setName(getID());
	setMethod("post");
	maintainAllParameters=false;
	initialize();
}

/**
*Defaults to the POST method
**/
public Form(String action){
	this(action,"post");
}

public Form(Class classToInstanciateAndSubmitTo){
	this(IWMainApplication.getObjectInstanciatorURL(classToInstanciateAndSubmitTo));
}


public Form(String actionURL,String method){
	//super();
	setName(getID());
	setMethod(method);
	setAction(actionURL);
	maintainAllParameters=false;
	initialize();
}

public Form(Class classToInstanciateAndSubmitTo,String method){
  this(IWMainApplication.getObjectInstanciatorURL(classToInstanciateAndSubmitTo),method);
}



/**
*Use this constructor to submit this form to a "pop-up" window
**/
public Form(Window myWindow){
	setName(getID());
	setWindow(myWindow);
	maintainAllParameters=false;
	initialize();
}

public void initialize(){
   add(new Parameter(FORM_EVENT_PARAMETER,this.getID()));
   this.add(new HiddenInput(IWMainApplication.IWEventSessionAddressParameter,""));
}


private void setOnAction(String actionType,String action){
  String attributeName = actionType;
  String previousAttribute = getAttribute(attributeName);
  if(previousAttribute==null){
    setAttribute(attributeName,action);

  }
  else{
    setAttribute(attributeName,previousAttribute+";"+action);
  }
}

public void setAction(String actionURL){
	setAttribute("action",actionURL);
}

protected String getAction(){
	return getAttribute("action");
}

public void setMethod(String method){
	setAttribute("method",method);
}

public String getMethod(){
	return getAttribute("method");
}

public void setTarget(String target){
	setAttribute("target",target);
}

protected void setWindow(Window window){
  this.window = window;
}

public void setMultiPart(){
	setAttribute("ENCTYPE","multipart/form-data");
}

public void setOnReset(String script){
	setOnAction("onReset",script);
}

public void setOnSubmit(String script){
	setOnAction("onSubmit",script);
}



private List findAllInputNamesHelper(List vector,PresentationObjectContainer cont){
	List objects = cont.getAllContainingObjects();
	if (objects != null){
		//for (Enumeration enum = objects.elements();enum.hasMoreElements();){
                for (Iterator iter = objects.iterator();iter.hasNext();){
			PresentationObject mo = (PresentationObject)iter.next();
			if (mo instanceof PresentationObjectContainer){
				vector = findAllInputNamesHelper(vector,(PresentationObjectContainer) mo);
			}
			else{
				if (mo instanceof InterfaceObject){
					vector.add(mo.getName());
				}
			}

		}
	}
	return vector;
}


public String[] findAllInputNames(){

	List vector = new Vector();

	vector = findAllInputNamesHelper(vector,this);

	return (String[]) vector.toArray(new String[1]);

}

private String getIdegaSpecialRequestURI(IWContext iwc){
	if (iwc.getParameter("idegaspecialrequesttype") == null){
		return iwc.getRequestURI();
	}
	else{

		//return encodeSpecialRequestString(iwc.getRequest().getParameter("idegaspecialrequesttype"),iwc.getRequest().getParameter("idegaspecialrequestname"),iwc);
		add(new Parameter("idegaspecialrequesttype",iwc.getParameter("idegaspecialrequesttype")));
		add(new Parameter("idegaspecialrequestname",iwc.getParameter("idegaspecialrequestname")));
		return iwc.getRequestURI();
	}
}


public void main(IWContext iwc){
  if(window!=null){
   //iwc.setSessionAttribute(IdegaWebHandler.windowOpenerParameter,window);
    com.idega.servlet.WindowOpener.storeWindow(iwc,window);
  }
}

public void addParameter(String parameterName,String parameterValue){
  add(new Parameter(parameterName,parameterValue));
}

/**
 *
 */
public void addParameter(String parameterName, int parameterValue) {
  addParameter(parameterName,Integer.toString(parameterValue));
}


public void maintainAllParameters(){
	maintainAllParameters=true;
}



/**
 * Creates a hidden field if there is an action on the form again
 */
public void maintainParameter(String parameterName){
	if (maintainedParameters == null){
		maintainedParameters = new Vector();
	}
	maintainedParameters.addElement(parameterName);
}



private void addGloballyMaintainedParameters(IWContext iwc){
  List list = com.idega.idegaweb.IWURL.getGloballyMaintainedParameters(iwc);
  if(list!=null){
    Iterator iter = list.iterator();
    while (iter.hasNext()) {
      String parameterName = (String)iter.next();
      String parameterValue = iwc.getParameter(parameterName);
      if(parameterValue!=null){
        addParameter(parameterName,parameterValue);
      }
    }
  }
}


/**
 * For printing out the maintained hidden parameters
 *
 * Currently not implemented well enough, parameters should be dynamically added
 */
private void addTheMaintainedParameters(IWContext iwc){


        /**
         * Should be probably deprecated if the "submitTo()" function is deprecated
         */
        /*if(submitToObject!= null){
          String treeID=submitToObject.getTreeID();
          if(treeID!=null){
            this.add(new Parameter("idega_special_tree_node",treeID));
          }
        }*/

        this.addGloballyMaintainedParameters(iwc);

	if (maintainAllParameters){
		if (iwc.getParameter("idega_special_form_parameter") != null){
			PresentationObjectContainer cont = (PresentationObjectContainer) iwc.getSessionAttribute("idega_special_form_parameters");
			if (cont != null){
				this.add(cont);
			}
		}
		else{

			PresentationObjectContainer cont = new PresentationObjectContainer();
			for (Enumeration enum = iwc.getParameterNames();enum.hasMoreElements();){
				String tempString = (String)enum.nextElement();
				cont.add(new Parameter(tempString,iwc.getParameter(tempString)));
			}
			cont.add(new Parameter("idega_special_form_parameter",""));
			this.add(cont);
			iwc.setSessionAttribute("idega_special_form_parameters",cont);
		}
	}
	else if (maintainedParameters != null){
		for (Enumeration e = maintainedParameters.elements(); e.hasMoreElements(); ){

			String tempParameter = (String)  e.nextElement();

			if (iwc.getParameter(tempParameter) != null){
                                                                            String[] strings = iwc.getParameterValues(tempParameter);
                                                                            for (int i = 0; i < strings.length; i++) {
                                                                                add(new Parameter(tempParameter,strings[i]));
                                                                            }

			          //this.add(new Parameter(tempParameter,iwc.getParameter(tempParameter)));
			}

		}
	}

        /*Map globalMaintainedParameters = iwc.getTheMaintainedParameters();
        if (globalMaintainedParameters!=null){
            for (Enumeration enum = globalMaintainedParameters.getE.getParameterNames();enum.hasMoreElements();){
                    String tempString = (String)enum.nextElement();
                    add();
            }
        }*/
}

public void print(IWContext iwc)throws Exception{
	initVariables(iwc);
	//if ( doPrint(iwc) ){

		addTheMaintainedParameters(iwc);
		if (window != null){
			//setAction(window.getURL(iwc)+"?idega_session_id="+iwc.getSession().getId());
			setAction(window.getURL(iwc));
			setTarget(window.getTarget());
			//setTarget("#");
			setOnSubmit(window.getCallingScriptStringForForm(iwc));
		}

		if(getAction() == null){
			//setAction(getIdegaSpecialRequestURI(iwc)+"?idega_session_id="+iwc.getSession().getId());
			setAction(getIdegaSpecialRequestURI(iwc));
		}
		if (getLanguage().equals("HTML")){
			//String Action = getAction();
			//if (Action.indexOf("idega_session_id") == -1){
				//setAction(Action+"?idega_session_id="+iwc.getSession().getId());
			//}


			if (getInterfaceStyle().equals("default")){
				getPrintWriter().println("<form name=\""+getName()+"\" "+getAttributeString()+" >");
				super.print(iwc);
				getPrintWriter().print("</form>");
			}
		}
		else if (getLanguage().equals("WML")){
			//setAction(getIdegaSpecialRequestURI(iwc)+"?idega_session_id="+iwc.getSession().getId());
			setAction(getIdegaSpecialRequestURI(iwc));
			println("<onevent type=\"onenterforward\" >");
			println("<refresh>");
			for (int j = 0; j < findAllInputNames().length; j++){
				println("<setvar name=\""+findAllInputNames()[j]+"\" value=\"\" />");
			}
			println("</refresh>");
			println("</onevent>");
			println("<do type=\"accept\">");
			println("<go href=\""+getAction()+"\" method=\""+getMethod()+"\" >");
			for (int i = 0; i < findAllInputNames().length; i++){
				println("<postfield name=\""+findAllInputNames()[i]+"\" value=\"$"+findAllInputNames()[i]+"\" />");
			}
			println("</go>");
			println("</do>");
			super.print(iwc);
		}
	//}
	//else{
	//	super.print(iwc);
	//}
}


  public synchronized Object clone() {
    Form obj = null;
    try {
      obj = (Form)super.clone();

      if(this.submitToObject != null){
        obj.submitToObject = (PresentationObject)this.submitToObject.clone();
      }
      if(window != null){
        obj.window = (Window)this.window.clone();
      }

      if (maintainedParameters != null){
        obj.maintainedParameters = (Vector)this.maintainedParameters.clone();
      }

      if(controlParameter != null){
        obj.controlParameter = (Parameter)this.controlParameter.clone();
      }

      if(windowClass != null){
        obj.windowClass = this.windowClass;
      }

      obj.maintainAllParameters = this.maintainAllParameters;

    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }

    return obj;
  }

protected Parameter getControlParameter(){
  return controlParameter;
}

protected void setControlParameter(String parameterName,String parameterValue){
  if (controlParameter==null){
    setControlParameter(new Parameter(parameterName,parameterValue));
  }
}

protected void setControlParameter(Parameter parameter){
  if (controlParameter==null){
    controlParameter=parameter;
    add(controlParameter);
  }
}

public void setEventListener(Class eventListenerClass){
   setEventListener(eventListenerClass.getName());
}

public void setEventListener(String eventListenerClassName){
    add(new Parameter(IWMainApplication.IdegaEventListenerClassParameter,IWMainApplication.getEncryptedClassName(eventListenerClassName)));
}


  public void sendToControllerFrame(){
    this.setTarget(IWConstants.IW_CONTROLLER_FRAME_NAME);
  }

  public void setWindowToOpen(Class windowClass){
    this.windowClass=windowClass;
    setAction(IWMainApplication.windowOpenerURL);
    addParameter(Page.IW_FRAME_CLASS_PARAMETER,windowClass.getName());
    setWindow(Window.getStaticInstance(windowClass));
  }

  public void setWindowToOpen(Class windowClass, int instanceId){
    this.windowClass=windowClass;
    setAction(IWMainApplication.windowOpenerURL);
    addParameter(Page.IW_FRAME_CLASS_PARAMETER,windowClass.getName());
    this.addParameter(IWMainApplication._PARAMETER_IC_OBJECT_INSTANCE_ID,instanceId);
    setWindow(Window.getStaticInstance(windowClass));
  }

} // Class ends

