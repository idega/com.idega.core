package com.idega.presentation;

import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import com.idega.util.IWTimestamp;

/**
 * @author Laddi
 */
public class ImageDateRotater extends Block {

	SortedMap imageMap;
	IWTimestamp date;
	
	public ImageDateRotater() {
		date = new IWTimestamp();
		date.setAsDate();
	}
	
	/**
	 * @see com.idega.presentation.PresentationObject#main(IWContext)
	 */
	public void main(IWContext iwc) throws Exception {
		add(getDateImage());
	}
	
	private Image getDateImage() {
		Image image = new Image();
		if (imageMap != null) {
			Iterator iter = imageMap.keySet().iterator();
			while (iter.hasNext()) {
				IWTimestamp element = (IWTimestamp) iter.next();
				if (date.getMonth() >= element.getMonth()) {
					if (date.getDay() >= element.getDay())
						image = (Image) imageMap.get(element);
				}
			}	
		}
		return image;
	}
	
	public void setImage(int day, int month, Image image) {
		IWTimestamp stamp = new IWTimestamp(day, month, 0);
		stamp.setAsDate();
		
		if (imageMap == null)
			imageMap = new TreeMap();
		imageMap.put(stamp, image);
	}
	
	public void setDay(int day) {
		date.setDay(day);
	}
	
	public void setMonth(int month) {
		date.setMonth(month);
	}
}
