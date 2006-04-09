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
		this.date = new IWTimestamp();
		this.date.setAsDate();
	}
	
	/**
	 * @see com.idega.presentation.PresentationObject#main(IWContext)
	 */
	public void main(IWContext iwc) throws Exception {
		add(getDateImage());
	}
	
	private Image getDateImage() {
		Image image = new Image();
		if (this.imageMap != null) {
			Iterator iter = this.imageMap.keySet().iterator();
			while (iter.hasNext()) {
				IWTimestamp element = (IWTimestamp) iter.next();
				if (this.date.getMonth() >= element.getMonth()) {
					if (this.date.getDay() >= element.getDay()) {
						image = (Image) this.imageMap.get(element);
					}
				}
			}	
		}
		return image;
	}
	
	public void setImage(int day, int month, Image image) {
		IWTimestamp stamp = new IWTimestamp(day, month, 0);
		stamp.setAsDate();
		
		if (this.imageMap == null) {
			this.imageMap = new TreeMap();
		}
		this.imageMap.put(stamp, image);
	}
	
	public void setDay(int day) {
		this.date.setDay(day);
	}
	
	public void setMonth(int month) {
		this.date.setMonth(month);
	}
}
