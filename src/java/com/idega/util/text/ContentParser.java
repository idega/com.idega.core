package com.idega.util.text;
import com.idega.idegaweb.IWApplicationContext;
import java.util.*;

/**
 *  Title: Description: Copyright: Copyright (c) 2001 Company:
 *
 * @author     <br>
 *      <a href="mailto:aron@idega.is">Aron Birkir</a> <br>
 *
 * @created    9. mars 2002
 * @version    1.0
 */

public class ContentParser {

    /**
     *  Static method to parse given text with a parsable object
     *
     * @param  parsableObject  Description of the Parameter
     * @param  text            Description of the Parameter
     * @return                 Description of the Return Value
     */
    public String parse(IWApplicationContext iwac,ContentParsable parsableObject, String text) {
        StringBuffer finalText = new StringBuffer();
        StringTokenizer st = new StringTokenizer(text, "[]");
        Map M = getMappedParseStrings(iwac,parsableObject);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (M.containsKey(token)) {
                finalText.append(M.get(token));
            }
						else {
							finalText.append(token);
						}
        }
        return finalText.toString();
    }


    /**
     *  Gets the mappedParseStrings of the ContentParser object
     *
     * @param  parsableObject  Description of the Parameter
     * @return                 The mapped parse strings value
     */
    private Map getMappedParseStrings(IWApplicationContext iwac,ContentParsable parsableObject) {
        Hashtable H = new Hashtable();
        String[] tags = parsableObject.getParseTags();
        String value;
        for (int i = 0; i < tags.length; i++) {
            value = parsableObject.getParsedString(iwac,tags[i]);
            if (value != null) {
              //System.err.println("parsing "+tags[i]+" value: "+value);
                H.put(tags[i], value);
            }
            else{
              //System.err.println("parsing "+tags[i]+" no value ");
            }
        }
        return H;
    }

}