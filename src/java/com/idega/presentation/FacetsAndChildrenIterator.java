/*
 * $Id: FacetsAndChildrenIterator.java,v 1.1 2004/11/14 23:22:44 tryggvil Exp $
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
 * Last modified: $Date: 2004/11/14 23:22:44 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.1 $
 */
class FacetsAndChildrenIterator
        implements Iterator
{
    private Iterator _facetsIterator;
    private Iterator _childrenIterator;

    FacetsAndChildrenIterator(Map facetMap, List childrenList)
    {
        _facetsIterator   = facetMap != null ? facetMap.values().iterator() : null;
        _childrenIterator = childrenList != null ? childrenList.iterator() : null;
    }

    public boolean hasNext()
    {
        return (_facetsIterator != null && _facetsIterator.hasNext()) ||
               (_childrenIterator != null && _childrenIterator.hasNext());
    }

    public Object next()
    {
        if (_facetsIterator != null && _facetsIterator.hasNext())
        {
            return _facetsIterator.next();
        }
        else if (_childrenIterator != null && _childrenIterator.hasNext())
        {
            return _childrenIterator.next();
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
