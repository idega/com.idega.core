package com.idega.core.category.business;


public class CategoryServiceHomeImpl extends com.idega.business.IBOHomeImpl implements CategoryServiceHome
{
 protected Class getBeanInterfaceClass(){
  return CategoryService.class;
 }


 public CategoryService create() throws javax.ejb.CreateException{
  return (CategoryService) super.createIBO();
 }



}