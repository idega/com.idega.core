package com.idega.presentation;

import java.util.Collection;
import com.idega.idegaweb.block.presentation.Builderaware;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author <br><a href="mailto:aron@idega.is">Aron Birkir</a><br>
 * @version 1.0
 */

public interface MenuBlock {


  public void addBlockObject(Builderaware obj);

  public void addBlockObjectAll(Collection blockCollection);

  public void addStandardObjects();

  public Class getDefaultBlockClass();

  public PresentationObject getLinks();

}
