/*
 * $Id: YearPeriod.java,v 1.1 2004/10/20 11:41:59 aron Exp $
 * Created on 20.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.util;

/**
 *  Presents a period of years, defined with the start year and ending year.
 *  The start of the period is the 01.01.firstYear and end is 31.12.secondYear
 *  Last modified: $Date: 2004/10/20 11:41:59 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public class YearPeriod extends TimePeriod {
    
    public YearPeriod(int firstYear, int secondYear){
        super(new IWTimestamp(1,1,firstYear),new IWTimestamp(31,12,secondYear));
    }
}
