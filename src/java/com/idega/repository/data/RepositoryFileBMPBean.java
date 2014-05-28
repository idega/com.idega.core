package com.idega.repository.data;

import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileBMPBean;

public class RepositoryFileBMPBean extends ICFileBMPBean implements ICFile, RepositoryFile {
    
	private static final long serialVersionUID = -4731503952797946597L;

	private static final String EXTERNALURL = "EXT_URL" ;

    /* (non-Javadoc)
     * @see com.idega.data.GenericEntity#initializeAttributes()
     */
    public void initializeAttributes() {
        // TODO Auto-generated method stub
        super.initializeAttributes();
        addAttribute(EXTERNALURL,"",true,true,String.class,1000);
    }
    
    public String getExternalURL(){
	    return getStringColumnValue(EXTERNALURL);
	}
	
	public void setExternalURL(String url){
	    setColumn(EXTERNALURL,url);
	}

}