package com.idega.event;

import java.util.*;
import com.idega.presentation.IWContext;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public abstract class IWPresentationEvent extends EventObject {

  private static final String PARAM_DEFAULT_SOURCE = "iw_ev_src";

  private Map valueMap;


  public IWPresentationEvent(){
    super(null);
  }

  public IWPresentationEvent(IWContext iwc){
    super(null);
    initialize(iwc);
  }

  protected abstract List getParameterList();

  public void initialize(IWContext iwc){
    valueMap=new HashMap();
    List params = this.getParameterList();
    Iterator iter = params.iterator();
    while (iter.hasNext()){
      String param = (String)iter.next();
      String[] values = iwc.getParameterValues(param);
      if(values!=null){
        if(values.length==1){
          valueMap.put(param,values[0]);
        }
        else{
          valueMap.put(param,values);
        }
      }
    }
  }

  protected Map getValueMap(){
    return valueMap;
  }

  protected String getStringValue(String paramName){
    return (String)getValueMap().get(paramName);
  }

  public String getPrmSource(){
    return PARAM_DEFAULT_SOURCE;
  }

  public Object getSource(){
    return getStringValue(getPrmSource());
  }

}