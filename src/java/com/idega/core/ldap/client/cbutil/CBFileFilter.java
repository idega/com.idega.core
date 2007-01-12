package com.idega.core.ldap.client.cbutil;

import java.io.File;

/**
 *    Quick Hack to cover up yet another
 *    Swing inadequacy.
 */

public class CBFileFilter extends javax.swing.filechooser.FileFilter
    implements java.io.FileFilter
{
    protected String[] extensions; // make it possible to extend this class
    String   description;

    public CBFileFilter(String[] exts)
    {
        this(exts, "no description given");
    }

    public CBFileFilter(String[] exts, String desc)
    {
        this.extensions = new String[exts.length];
        for (int i=0; i<exts.length; i++)
        {
            this.extensions[i] = exts[i].toLowerCase();
        }

        this.description = desc;
    }

    public boolean accept(File f)
    {
        if (f.isDirectory()) {
			return true;
		}

        for (int i=0; i<this.extensions.length; i++) {
			if (f.getName().toLowerCase().endsWith(this.extensions[i])) {
				return true;
			}
		}

        return false;
    }

    public String getDescription() { return this.description; }

}