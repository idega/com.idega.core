//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.idegaweb;


import com.idega.jmodule.object.ModuleInfo;
import com.idega.jmodule.object.Image;
import com.idega.core.ICComponent;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 0.5 - Under development
*UNIMPLEMENTED
*/
public interface IWComponent extends ICComponent{

    public IWMainApplication getIWMainApplication(ModuleInfo modinfo);

    public Image getImageIcon();

}
