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
		date = new IWTimestamp();
		date.setAsDate();
	}
	
	public void main(IWContext iwc) throws Exception {
		String color = getDateColor();
		if (color != null)
			this.getParentPage().setBackgroundColor(color);
	}
	
	private String getDateColor() {
		if (colorMap != null) {
			IWTimestamp beforeStamp = null;
			IWTimestamp afterStamp = null;
			IWTimestamp firstStamp = null;
			IWTimestamp nextYear = null;
			
			Iterator iter = colorMap.keySet().iterator();
			while (iter.hasNext()) {
				afterStamp = (IWTimestamp) iter.next();
				if (beforeStamp != null) {
					if (date.isBetween(beforeStamp, afterStamp)) {
						int daysBetween = Math.abs(IWTimestamp.getDaysBetween(beforeStamp, afterStamp));
						int daysFrom = Math.abs(IWTimestamp.getDaysBetween(beforeStamp, date));
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
					if (date.isBetween(afterStamp, nextYear)) {
						int daysBetween = Math.abs(IWTimestamp.getDaysBetween(afterStamp, nextYear));
						int daysFrom = Math.abs(IWTimestamp.getDaysBetween(afterStamp, date));
						return getColor(afterStamp, firstStamp, daysBetween, daysFrom);	
					}
				}
			}	
		}
		return null;
	}
	
	private String getColor(IWTimestamp before, IWTimestamp after, int daysBetween, int daysFrom) {
		IWColor beforeColor = IWColor.getIWColorFromHex((String) colorMap.get(before));
		IWColor afterColor = IWColor.getIWColorFromHex((String) colorMap.get(after));
			
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
		
		if (colorMap == null)
			colorMap = new TreeMap();
		colorMap.put(stamp, color);
	}
	
	public void setDay(int day) {
		date.setDay(day);
	}
	
	public void setMonth(int month) {
		date.setMonth(month);
	}
}
