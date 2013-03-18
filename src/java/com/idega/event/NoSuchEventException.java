package com.idega.event;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class NoSuchEventException extends Exception {

  public NoSuchEventException() {
    super("No event of this type");
  }

  public NoSuchEventException(String message){
    super(message);
  }

}