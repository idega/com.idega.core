package com.idega.presentation;
import com.idega.idegaweb.IWUserContext;
import com.idega.event.IWPresentationState;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public interface StatefullPresentation {
//  public void setPresentationState(IWPresentationState state);
  public IWPresentationState getPresentationState(IWUserContext iwuc);
  public Class getPresentationStateClass();
}