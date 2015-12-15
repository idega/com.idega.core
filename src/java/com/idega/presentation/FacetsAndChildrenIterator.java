/*
 * $Id: FacetsAndChildrenIterator.java,v 1.5 2007/12/28 13:23:05 valdas Exp $
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

import javax.faces.component.UIComponent;

/**
 * Class to override the standard iterator for the method getFacetsAndChildren() in UIComponent
 *
 * Last modified: $Date: 2007/12/28 13:23:05 $ by $Author: valdas $
 *
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.5 $
 */
class FacetsAndChildrenIterator implements Iterator<UIComponent>
{
    private Iterator<UIComponent> _facetsIterator;
    private Iterator<UIComponent> _childrenIterator;

    FacetsAndChildrenIterator(Map<String, UIComponent> facetMap, List<UIComponent> childrenList)
    {
        this._facetsIterator   = facetMap != null ? facetMap.values().iterator() : null;
        this._childrenIterator = childrenList != null ? childrenList.iterator() : null;
    }

    @Override
	public boolean hasNext()
    {
    		boolean facetsHasNext = false;
    		boolean childrenHasNext=false;

    		facetsHasNext = (this._facetsIterator != null && this._facetsIterator.hasNext());
    		childrenHasNext = (this._childrenIterator != null && this._childrenIterator.hasNext());

    		return ( facetsHasNext || childrenHasNext );
    }

    @Override
	public UIComponent next()
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
        			return this._childrenIterator.hasNext() ? this._childrenIterator.next() : null;

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

    @Override
	public void remove()
    {
        throw new UnsupportedOperationException(this.getClass().getName() + " UnsupportedOperationException");
    }

}
