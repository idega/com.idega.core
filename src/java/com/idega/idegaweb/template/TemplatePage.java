
package com.idega.idegaweb.template;

import com.idega.presentation.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega multimedia - iceland
 * @author        <a href="tryggvi@idega.is">Tryggvi Larusson</a>, <a href="gummi@idega.is">Gudmundur Saemundsson</a>
 * @version 1.0
 */

public class TemplatePage extends Page {

  public TemplatePage() {
  }

 /**
   * <H2>Unimplemented</H2>
   */
  public void add1(PresentationObject obj){}

  /**
   * <H2>Unimplemented</H2>
   */
  public void add2(PresentationObject obj){}
  /**
   * <H2>Unimplemented</H2>
   */
  public void add3(PresentationObject obj){}
  /**
   * <H2>Unimplemented</H2>
   */
  public void add4(PresentationObject obj){}
  /**
   * <H2>Unimplemented</H2>
   */
  public void add5(PresentationObject obj){}
  /**
   * <H2>Unimplemented</H2>
   */
  public void add6(PresentationObject obj){}
  /**
   * <H2>Unimplemented</H2>
   */
  public void add7(PresentationObject obj){}
  /**
   * <H2>Unimplemented</H2>
   */
  public void add8(PresentationObject obj){}
  /**
   * <H2>Unimplemented</H2>
   */
  public void add9(PresentationObject obj){}
  /**
   * <H2>Unimplemented</H2>
   */
  public boolean isAdministrator(IWContext iwc)throws Exception{
    return iwc.hasEditPermission(this);
  }
  /**
   * <H2>Unimplemented</H2>
   */
  public boolean isDeveloper(IWContext iwc)throws Exception{
    return false;
  }
  /**
   * <H2>Unimplemented</H2>
   */
  public boolean isUser(IWContext iwc)throws Exception{
    return false;
  }
  /**
   * <H2>Unimplemented</H2>
   */
  public boolean isMemberOf(IWContext iwc,String groupName)throws Exception{
    return false;
  }
  /**
   * <H2>Unimplemented</H2>
   */
  public boolean hasPermission(String permissionType,IWContext iwc,PresentationObject obj)throws Exception{
  //  return AccessControl.hasPermission(permissionType,iwc,obj);
    return false;
  }






} //  Class TemplateClass