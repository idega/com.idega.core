
package com.idega.idegaweb.template;

import com.idega.jmodule.object.*;
import com.idega.jmodule.login.business.AccessControl;

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
  public void add1(ModuleObject obj){}

  /**
   * <H2>Unimplemented</H2>
   */
  public void add2(ModuleObject obj){}
  /**
   * <H2>Unimplemented</H2>
   */
  public void add3(ModuleObject obj){}
  /**
   * <H2>Unimplemented</H2>
   */
  public void add4(ModuleObject obj){}
  /**
   * <H2>Unimplemented</H2>
   */
  public void add5(ModuleObject obj){}
  /**
   * <H2>Unimplemented</H2>
   */
  public void add6(ModuleObject obj){}
  /**
   * <H2>Unimplemented</H2>
   */
  public void add7(ModuleObject obj){}
  /**
   * <H2>Unimplemented</H2>
   */
  public void add8(ModuleObject obj){}
  /**
   * <H2>Unimplemented</H2>
   */
  public void add9(ModuleObject obj){}
  /**
   * <H2>Unimplemented</H2>
   */
  public boolean isAdministrator(ModuleInfo modinfo)throws Exception{
    return AccessControl.isAdmin(modinfo);
  }
  /**
   * <H2>Unimplemented</H2>
   */
  public boolean isDeveloper(ModuleInfo modinfo)throws Exception{
    return false;
  }
  /**
   * <H2>Unimplemented</H2>
   */
  public boolean isUser(ModuleInfo modinfo)throws Exception{
    return false;
  }
  /**
   * <H2>Unimplemented</H2>
   */
  public boolean isMemberOf(ModuleInfo modinfo,String groupName)throws Exception{
    return false;
  }
  /**
   * <H2>Unimplemented</H2>
   */
  public boolean hasPermission(String permissionType,ModuleInfo modinfo,ModuleObject obj)throws Exception{
  //  return AccessControl.hasPermission(permissionType,modinfo,obj);
    return false;
  }






} //  Class TemplateClass