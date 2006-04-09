package com.idega.presentation;

import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import com.idega.util.IWColor;
import com.idega.util.IWTimestamp;

/**
 * @author Laddi
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class DateBackground extends Block {

	SortedMap colorMap;
	IWTimestamp date;
	
	public DateBackground() {
		this.date = new IWTimestamp();
		this.date.setAsDate();
	}
	
	public void main(IWContext iwc) throws Exception {
		String color = getDateColor();
		if (color != null) {
			this.getParentPage().setBackgroundColor(color);
		}
	}
	
	private String getDateColor() {
		if (this.colorMap != null) {
			IWTimestamp beforeStamp = null;
			IWTimestamp afterStamp = null;
			IWTimestamp firstStamp = null;
			IWTimestamp nextYear = null;
			
			Iterator iter = this.colorMap.keySet().iterator();
			while (iter.hasNext()) {
				afterStamp = (IWTimestamp) iter.next();
				if (beforeStamp != null) {
					if (this.date.isBetween(beforeStamp, afterStamp)) {
						int daysBetween = Math.abs(IWTimestamp.getDaysBetween(beforeStamp, afterStamp));
						int daysFrom = Math.abs(IWTimestamp.getDaysBetween(beforeStamp, this.date));
						return getColor(beforeStamp, afterStamp, daysBetween, daysFrom);	
					}
					else {
						beforeStamp = afterStamp;
					}
				}
				else {
					beforeStamp = new IWTimestamp(afterStamp);
					firstStamp = new IWTimestamp(afterStamp);
					nextYear = new IWTimestamp(afterStamp);
					nextYear.setYear(nextYear.getYear()+1);	
				}
				
				if (!iter.hasNext()) {
					if (this.date.isBetween(afterStamp, nextYear)) {
						int daysBetween = Math.abs(IWTimestamp.getDaysBetween(afterStamp, nextYear));
						int daysFrom = Math.abs(IWTimestamp.getDaysBetween(afterStamp, this.date));
						return getColor(afterStamp, firstStamp, daysBetween, daysFrom);	
					}
				}
			}	
		}
		return null;
	}
	
	private String getColor(IWTimestamp before, IWTimestamp after, int daysBetween, int daysFrom) {
		IWColor beforeColor = IWColor.getIWColorFromHex((String) this.colorMap.get(before));
		IWColor afterColor = IWColor.getIWColorFromHex((String) this.colorMap.get(after));
			
		int redDifference = beforeColor.getRed() - afterColor.getRed();
		int greenDifference = beforeColor.getGreen() - afterColor.getGreen();
		int blueDifference = beforeColor.getBlue() - afterColor.getBlue();
		
		int red = beforeColor.getRed() - ((int) ((float) redDifference / (float) daysBetween * daysFrom));
		int green = beforeColor.getGreen() - ((int) ((float) greenDifference / (float) daysBetween * daysFrom));
		int blue = beforeColor.getBlue() - ((int) ((float) blueDifference / (float) daysBetween * daysFrom));
		
		IWColor newColor = new IWColor(red, green, blue);
		return newColor.getHexColorString();
	}
	
	public void setBackgroundColor(int day, int month, String color) {
		IWTimestamp stamp = new IWTimestamp();
		stamp.setDay(day);
		stamp.setMonth(month);
		stamp.setAsDate();
		
		if (this.colorMap == null) {
			this.colorMap = new TreeMap();
		}
		this.colorMap.put(stamp, color);
	}
	
	public void setDay(int day) {
		this.date.setDay(day);
	}
	
	public void setMonth(int month) {
		this.date.setMonth(month);
	}
}
