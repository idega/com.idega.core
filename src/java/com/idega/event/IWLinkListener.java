package com.idega.event;



import java.util.EventListener;



/**

 * Title:        IW Event

 * Description:

 * Copyright:    Copyright (c) 2001

 * Company:      idega.is

 * @author

 * @version 1.0

 */



public interface IWLinkListener extends EventListener{

  public void actionPerformed(IWLinkEvent e);

}
