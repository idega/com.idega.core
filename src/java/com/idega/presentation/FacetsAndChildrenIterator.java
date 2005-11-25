/*
 * $Id: FacetsAndChildrenIterator.java,v 1.2 2005/11/25 15:24:28 tryggvil Exp $
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
 * Last modified: $Date: 2005/11/25 15:24:28 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.2 $
 */
class FacetsAndChildrenIterator
        implements Iterator
{
    private Iterator _facetsIterator;
    private Iterator _childrenIterator;
    
    private boolean iteratorFacetsHasNext;
    private boolean iteratorChildrenHasNext;

    FacetsAndChildrenIterator(Map facetMap, List childrenList)
    {
        _facetsIterator   = facetMap != null ? facetMap.values().iterator() : null;
        _childrenIterator = childrenList != null ? childrenList.iterator() : null;
    }

    public boolean hasNext()
    {
    		boolean facetsHasNext = false;
    		boolean childrenHasNext=false;
    		
    		facetsHasNext = (_facetsIterator != null && _facetsIterator.hasNext());
    		childrenHasNext = (_childrenIterator != null && _childrenIterator.hasNext());
    		
    		this.iteratorFacetsHasNext=facetsHasNext;
    		this.iteratorChildrenHasNext=childrenHasNext;
    		
        return ( facetsHasNext || childrenHasNext );
    }

    public Object next()
    {
		boolean facetsHasNext = false;
		boolean childrenHasNext=false;
		
		facetsHasNext = (_facetsIterator != null && _facetsIterator.hasNext());
		childrenHasNext = (_childrenIterator != null && _childrenIterator.hasNext());
    	
        if (facetsHasNext)
        {
            return _facetsIterator.next();
        }
        else if (childrenHasNext)
        {
        		try{
        			return _childrenIterator.next();
        
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
