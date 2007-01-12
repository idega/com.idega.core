/*
 * $Id: FacetsAndChildrenIterator.java,v 1.1.2.1 2007/01/12 19:31:32 idegaweb Exp $
 * Created in 2004 by Tryggvi Larusson
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Class to override the standard iterator for the method getFacetsAndChildren() in UIComponent
 * 
 * Last modified: $Date: 2007/01/12 19:31:32 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.1.2.1 $
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
        return (this._facetsIterator != null && this._facetsIterator.hasNext()) ||
               (this._childrenIterator != null && this._childrenIterator.hasNext());
    }

    public Object next()
    {
        if (this._facetsIterator != null && this._facetsIterator.hasNext())
        {
            return this._facetsIterator.next();
        }
        else if (this._childrenIterator != null && this._childrenIterator.hasNext())
        {
            return this._childrenIterator.next();
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
