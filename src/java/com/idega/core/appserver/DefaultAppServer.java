/*
 * Created on 1.7.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.idega.core.appserver;

/**
 * @author tryggvil
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public abstract class DefaultAppServer implements AppServer{

    private String version;
    /* (non-Javadoc)
     * @see com.idega.core.appserver.AppServer#getSupportsSettingCharactersetInRequest()
     */
    public boolean getSupportsSettingCharactersetInRequest() {
        return true;
    }
    /* (non-Javadoc)
     * @see com.idega.core.appserver.AppServer#getVersion()
     */
    public String getVersion() {
        return version;
    }
    public boolean isOfficiallySupported(){
        return true;
    }
    /**
     * @param version The version to set.
     */
    public void setVersion(String version) {
        this.version = version;
    }
}
