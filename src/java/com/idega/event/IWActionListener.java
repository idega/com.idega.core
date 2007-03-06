package com.idega.event;
import java.util.EventListener;

import com.idega.idegaweb.IWException;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public interface IWActionListener extends EventListener {

  public void actionPerformed(IWPresentationEvent e) throws IWException;



}