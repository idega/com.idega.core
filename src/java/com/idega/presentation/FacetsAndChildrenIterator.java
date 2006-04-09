/*
 * $Id: FacetsAndChildrenIterator.java,v 1.4 2006/04/09 12:13:13 laddi Exp $
 * Created in 2004 by Tryggvi Larusson
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Class to override the standard iterator for the method getFacetsAndChildren() in UIComponent
 * 
 * Last modified: $Date: 2006/04/09 12:13:13 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.4 $
 */
class FacetsAndChildrenIterator
        implements Iterator
{
    private Iterator _facetsIterator;
    private Iterator _childrenIterator;
    
    FacetsAndChildrenIterator(Map facetMap, List childrenList)
    {
        this._facetsIterator   = facetMap != null ? facetMap.values().iterator() : null;
        this._childrenIterator = childrenList != null ? childrenList.iterator() : null;
    }

    public boolean hasNext()
    {
    		boolean facetsHasNext = false;
    		boolean childrenHasNext=false;
    		
    		facetsHasNext = (this._facetsIterator != null && this._facetsIterator.hasNext());
    		childrenHasNext = (this._childrenIterator != null && this._childrenIterator.hasNext());
    		
    		return ( facetsHasNext || childrenHasNext );
    }

    public Object next()
    {
		boolean facetsHasNext = false;
		boolean childrenHasNext=false;
		
		facetsHasNext = (this._facetsIterator != null && this._facetsIterator.hasNext());
		childrenHasNext = (this._childrenIterator != null && this._childrenIterator.hasNext());
    	
        if (facetsHasNext)
        {
            return this._facetsIterator.next();
        }
        else if (childrenHasNext)
        {
        		try{
        			return this._childrenIterator.next();
        
        		}
        		catch(NoSuchElementException nse){
        			nse.printStackTrace();
        			return null;
        		}
        		catch(ConcurrentModificationException cme){
        			cme.printStackTrace();
        			return null;
        		}
        }
        else
        {
            throw new NoSuchElementException();
        }
    }

    public void remove()
    {
        throw new UnsupportedOperationException(this.getClass().getName() + " UnsupportedOperationException");
    }

}
