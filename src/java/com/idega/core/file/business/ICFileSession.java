/*
 * $Id: ICFileSession.java,v 1.1 2004/11/29 16:02:35 aron Exp $
 * Created on 29.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.file.business;

import com.idega.business.IBOSession;
import com.idega.core.data.ICTreeNode;
import com.idega.core.file.data.ICFile;

/**
 *  ICFileSession handles the necessary file actions on the file repository installed.
 *  The session bean keeps track of the current directory the user has changed to.
 *  Methods which take no parameters will act upon the current directory. 
 * 
 *  Last modified: $Date: 2004/11/29 16:02:35 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public interface ICFileSession extends IBOSession {
    
    /**
     * Gets current user's home folder
     * @return
     */
    public ICFile getHome();
    
    /**
     * Gets current user's document folder
     * @return
     */
    public ICFile getDocumentsHome();
    
    /**
     * Gets current user's libaray folder, used for settings and other configs
     * @return
     */
    public ICFile getLibraryHome();
    
    /**
     * Lists filenames under current directory
     * @return
     */
    public String[] list();
    
    /**
     * Change file timestamps or create file if not exists
     * under the current directory
     * @param name
     */
    public void touch(String name);
    
    /**
     * Creates new directory a file with specified name in current directory
     * @param name
     */
    public void mkdir(String name);
    
    /**
     * Creates a new directory structure according to a tree structure 
     * presented by the given tree node.
     * @param tree
     */
    public void mktree(ICTreeNode tree);
    
    /**
     * Change directory to the directory specified under current directory
     * @param file
     */
    public void cd(String name);
    
    /**
     * Removes a file 
     * @param file
     */
    public void rm(ICFile file);
    
    /**
     * Checks in a newer version of a given file
     *
     */
    public void checkin(ICFile file);
    
    /**
     * Checks out the newest version of a file
     *
     */
    public void checkout(ICFile file);
    
    /**
     * Locks a file
     * @param file
     */
    public void lock(ICFile file);
    
    /**
     * Unlocks a file
     * @param file
     */
    public void unlock(ICFile file);

    
    /**
     * Gets an array of versions for a file
     * @param file
     * @return
     */
    public ICFileVersion[] getVersionHistory(ICFile file);
    
}
