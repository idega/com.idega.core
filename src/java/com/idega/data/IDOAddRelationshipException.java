package com.idega.data;

/**
 * Title:        idega Data Objects
 * Description:  Idega Data Objects is a Framework for Object/Relational mapping and seamless integration between datastores
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IDOAddRelationshipException extends IDORelationshipException {

  public IDOAddRelationshipException() {
    super();
  }


  public IDOAddRelationshipException(String message) {
    super(message);
  }


  public IDOAddRelationshipException(Exception exeption,IDOEntity thrower) {
    super(exeption,thrower);
  }
}