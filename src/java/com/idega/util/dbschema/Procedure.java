package com.idega.util.dbschema;

import java.sql.ResultSet;

/**
 * 
 *  Last modified: $Date: 2004/11/01 10:05:31 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public interface Procedure {
    public String getName();
    public Class[] getParameterTypes();
    
    public Object processResultSet(ResultSet rs);
    public boolean isAvailable();

}
