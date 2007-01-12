/*
 * Created on 16.12.2003 by  tryggvil in project com.project
 */
package com.idega.util;

import java.sql.Date;

/**
 * TimePeriod A class do describe a period with a start and end in time
 * Copyright (C) idega software 2003
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class TimePeriod {

	private IWTimestamp firstTimestamp;
	private IWTimestamp lastTimestamp;
	
	/**
	 * Constructs a TimePeriod instance with the given beginning and the last timestamp of th period.
	 */
	public TimePeriod(IWTimestamp firstTimestamp,IWTimestamp lastTimestamp){
		setFirstTimestamp(firstTimestamp);
		setLastTimestamp(lastTimestamp);
	}
	/**
	 * Constructs a TimePeriod instance with the given beginning and the last timestamp of th period.
	 */
	public TimePeriod(Date firstDate,Date lastDate){
		this(new IWTimestamp(firstDate),new IWTimestamp(lastDate));
	}
	
	public IWTimestamp getFirstTimestamp(){
		return this.firstTimestamp;
	}
	
	public IWTimestamp getLastTimestamp(){
		return this.lastTimestamp;
	}

	public void setFirstTimestamp(IWTimestamp stamp){
		this.firstTimestamp=stamp;
	}
	
	public void setLastTimestamp(IWTimestamp stamp){
		this.lastTimestamp=stamp;
	}
	
	public boolean beginsBefore(IWTimestamp otherTimestamp){
		return this.getFirstTimestamp().isEarlierThan(otherTimestamp);
	}
	
	public boolean beginsBeforeBeginningOf(TimePeriod otherPeriod){
		return beginsBefore(otherPeriod.getFirstTimestamp());
	}

	public boolean beginsLaterThan(IWTimestamp otherTimestamp){
		return this.getFirstTimestamp().isLaterThan(otherTimestamp);
	}	
	
	public boolean beginsLaterThanBeginningOf(TimePeriod otherPeriod){
		return beginsLaterThan(otherPeriod.getFirstTimestamp());
	}
	
	public boolean endsBefore(IWTimestamp otherTimestamp){
		return this.getLastTimestamp().isEarlierThan(otherTimestamp);
	}
	
	public boolean endsBeforeEndOf(TimePeriod otherPeriod){
		return beginsBefore(otherPeriod.getLastTimestamp());
	}

	public boolean endsLaterThan(IWTimestamp otherTimestamp){
		return this.getLastTimestamp().isLaterThan(otherTimestamp);
	}	
	
	public boolean endsLaterThanEndOf(TimePeriod otherPeriod){
		return beginsLaterThan(otherPeriod.getLastTimestamp());
	}
	

}
