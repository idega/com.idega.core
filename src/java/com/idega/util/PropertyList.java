package com.idega.util;
import java.util.ArrayList;
/**
 * 
 * <p>Company: idegaweb </p>
 * @author aron
 * 
 *
 */
public class PropertyList extends ArrayList {
	/**
	 * @see java.util.List#get(int)
	 */
	public Object get(int arg0) {
		return null;
	}
	/**
	 * @see java.util.Collection#size()
	 */
	public int size() {
		return super.size();
	}
	
	public boolean add(Object object){
		if(!(object instanceof Property)) {
			throw new UnsupportedOperationException();
		}
		return super.add(object);
	}
	
	public void add(int index,Object object){
		if(!(object instanceof Property)) {
			throw new UnsupportedOperationException();
		}
		super.add(index,object);
	}
	
	public void add(Property property){
		super.add(property);
	}
	
	public void add(String key, String value){
		this.add(new Property(key,value));
	}
	
}
