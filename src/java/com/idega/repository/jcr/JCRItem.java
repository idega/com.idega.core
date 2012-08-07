package com.idega.repository.jcr;

import java.io.Serializable;
import java.util.logging.Level;

import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.lock.Lock;

import com.idega.repository.bean.RepositoryItem;
import com.idega.util.CoreConstants;

public abstract class JCRItem extends RepositoryItem {

	private static final long serialVersionUID = 7937464763729451494L;

	private String checkedOut;

	public abstract boolean isLocked();
	public abstract Lock lock(boolean isDeep, boolean isSessionScoped);
	public abstract void unlock();
	public abstract void unCheckOut();

	public abstract boolean setProperty(String property, Serializable value);

	public abstract Property getProperty(String property);

	public Property getProperty(String prefix, String name) {
		return getProperty(prefix.concat(CoreConstants.COLON).concat(name));
	}

	public <T extends Serializable> T getPropertyValue(String prefix, String property, int type) {
		Property prop = getProperty(prefix, property);
		return getPropertyValue(prop, type);
	}

	public <T extends Serializable> T getPropertyValue(String property, int type) {
		Property prop = getProperty(property);
		return getPropertyValue(prop, type);
	}

	public <T extends Serializable> T getPropertyValue(Property property, int type) {
		if (property == null)
			return null;

		try {
			Value value = property.getValue();
			if (value == null)
				return null;

			Object obj = null;
			switch (type) {
				case PropertyType.BINARY: {
					obj = value.getBinary();
					break;
				}
				case PropertyType.BOOLEAN: {
					obj = value.getBoolean();
					break;
				}
				case PropertyType.DATE: {
					obj = value.getDate();
					break;
				}
				case PropertyType.DECIMAL: {
					obj = value.getDecimal();
					break;
				}
				case PropertyType.DOUBLE: {
					obj = value.getDouble();
					break;
				}
				case PropertyType.LONG: {
					obj = value.getLong();
					break;
				}
				case PropertyType.STRING: {
					obj = value.getString();
					break;
				}
				default: {
					getLogger().warning("Do not know how to handle property type " + type + " for the property " + property);
					break;
				}
			}

			if (obj == null)
				return null;

			@SuppressWarnings("unchecked")
			T realValue = (T) obj;
			return realValue;
		} catch (RepositoryException e) {
			getLogger().log(Level.WARNING, "Error getting property value " + property + " by type " + type, e);
		}

		return null;
	}

	//	TODO
	public String getCheckedOut() {
		return checkedOut;
	}

	public void setCheckedOut(String checkedOut) {
		this.checkedOut = checkedOut;
	}

}