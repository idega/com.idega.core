package com.idega.core.accesscontrol.data;


public class LoginInfoHomeImpl extends com.idega.data.IDOFactory implements LoginInfoHome
{
 protected Class getEntityInterfaceClass(){
  return LoginInfo.class;
 }


 public LoginInfo create() throws javax.ejb.CreateException{
  return (LoginInfo) super.createIDO();
 }


 public LoginInfo findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (LoginInfo) super.findByPrimaryKeyIDO(pk);
 }



}