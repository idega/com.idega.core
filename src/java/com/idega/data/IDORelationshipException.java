package com.idega.data;

/**
 * Title:        idega Data Objects
 * Description:  Idega Data Objects is a Framework for Object/Relational mapping and seamless integration between datastores
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IDORelationshipException extends IDOException {

  public IDORelationshipException() {
    super();
  }


  public IDORelationshipException(String message) {
    super(message);
  }


  public IDORelationshipException(Exception exeption,IDOEntity thrower) {
    super(exeption,thrower);
  }
}