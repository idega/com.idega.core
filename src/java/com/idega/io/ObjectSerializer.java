package com.idega.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author <br><a href="mailto:aron@idega.is">Aron Birkir</a><br>
 * @version 1.0
 */

public class ObjectSerializer {

  public ObjectSerializer() {
  }

  /**
  *  Serializes an object to a string
  */
  public static String serialize(Object object) throws Exception{
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream os = new ObjectOutputStream(bos);
    os.writeObject(object);
    os.flush();
    return bos.toString();
  }

  /**
  *  Desrializes an string to a object
  */
  public static Object deserialize(String serializedString)throws Exception{
    ByteArrayInputStream bis = new ByteArrayInputStream(serializedString.getBytes());
    ObjectInputStream is = new ObjectInputStream(bis);
    Object o = is.readObject();
    is.close();
    return o;
  }
}
