//idega 2001 - Tryggvi Larusson

/*

*Copyright 2001 idega.is All Rights Reserved.

*/



package com.idega.idegaweb;





import com.idega.presentation.IWContext;

import com.idega.presentation.Image;

import com.idega.core.ICComponent;



/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 0.5 - Under development

*UNIMPLEMENTED

*/

public interface IWComponent extends ICComponent{



    public IWMainApplication getIWMainApplication(IWContext iwc);



    public Image getImageIcon();



}

