package com.idega.util.text;

import com.idega.idegaweb.IWApplicationContext;

/**
 *  Title: Description: Copyright: Copyright (c) 2001 Company:
 *
 * @author     <br>
 *      <a href="mailto:aron@idega.is">Aron Birkir</a> <br>
 *
 * @created    9. mars 2002
 * @version    1.0
 */

public interface ContentParsable {

    /**
     *  Returns all available tags from this parser
     *
     * @return    The parse tags value
     */
    String[] getParseTags();


    /**
     *  Returns a parsed tags value
     *
     * @param  tag  Description of the Parameter
     * @return      The parsed string value
     */
    String getParsedString(IWApplicationContext iwac,String tag);


    /**
     *  Returns the object to be parsed
     *
     * @return    The parse object value
     */
    Object getParseObject();


    /**
     *  Returns available types
     *
     * @return    The parse types value
     */
    String[] getParseTypes();

    /**
     *  Returns the tag delimiters
     */
    String getDelimiters();

    String formatTag(String tag);
}
