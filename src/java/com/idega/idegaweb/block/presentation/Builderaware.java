package com.idega.idegaweb.block.presentation;

/**
 * An interface to declare certainn methods that are used by the Builder when manipulating Blocks.
 * This interface is optionally implemented by blocks.
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.1
 * @see com.idega.presentation.Block
 */

public interface Builderaware {

public final static String IW_CORE_BUNDLE_IDENTIFIER="com.idega.core";

  public boolean deleteBlock(int ICObjectInstanceId);
  public String getBundleIdentifier();
  public String getLocalizedNameKey();
  public String getLocalizedNameValue();

}
