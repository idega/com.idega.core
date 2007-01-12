package com.idega.data.query;

import java.util.Set;

import com.idega.data.query.output.Output;
import com.idega.data.query.output.Outputable;
import com.idega.data.query.output.ToStringer;

/**
 * @author <a href="joe@truemesh.com">Joe Walnes</a>
 */
public abstract class Criteria implements Outputable, Cloneable{

    public abstract void write(Output out);

    public String toString() {
        return ToStringer.toString(this);
    }

    protected String quote(String s) {
        if (s == null) {
			return "null";
		}
        StringBuffer str = new StringBuffer();
        str.append('\'');
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\\'
                    || s.charAt(i) == '\"'
                    || s.charAt(i) == '\'') {
                str.append('\\');
            }
            str.append(s.charAt(i));
        }
        str.append('\'');
        return str.toString();
    }
    
    public abstract Set getTables();
    
    public Object clone(){
		Criteria obj = null;
		try {
			obj = (Criteria)super.clone();
		}
		catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return obj;
	}

}
