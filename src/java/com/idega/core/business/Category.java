package com.idega.core.business;

/**
 *  Title: Description: Copyright: Copyright (c) 2001 Company:
 *
 * @author     <br>
 *      <a href="mailto:aron@idega.is">Aron Birkir</a> <br>
 *
 * @created    14. mars 2002
 * @version    1.0
 */

public interface Category {

  /**
   *  Gets the iD of the Category object
   *
   * @return    The ID value
   */
  public int getID();


  /**
   *  Gets the name of the Category object
   *
   * @return    The name value
   */
  public String getName();


  /**
   *  Gets the description of the Category object
   *
   * @return    The description value
   */
  public String getDescription();


  /**
   *  Gets the type of the Category object
   *
   * @return    The type value
   */
  public String getType();
}
