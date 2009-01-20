package com.idega.presentation.text;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.myfaces.shared_tomahawk.taglib.UIComponentELTagBase;

import com.idega.util.reflect.Property;

public class DownloadLinkTag extends UIComponentELTagBase {
	
	private String downloadWriter;
	private String id;
	private String styleClass;
	private String value;
	
	@Override
	public String getComponentType() {
		return DownloadLink.COMPONENT_TYPE;
	}

	@Override
	public String getRendererType() {
		return null;
	}

	@Override
	public void release() {
		super.release();
		
		this.downloadWriter = null;
		this.id = null;
		this.styleClass = null;
		this.value = null;
	}

	@Override
	protected void setProperties(UIComponent component) {
		if (component instanceof DownloadLink) {
			super.setProperties(component);
		
			DownloadLink link = (DownloadLink) component;
			try {
				link.setMediaWriterClass(Class.forName((String) Property.getValueFromExpression(downloadWriter, String.class)));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			link.setId(id);
			link.setStyleClass(styleClass);
			
			FacesContext fc = FacesContext.getCurrentInstance();
			link.setValueExpression("value", fc.getApplication().getExpressionFactory().createValueExpression(fc.getELContext(), value, String.class));
		}
	}

	public String getDownloadWriter() {
		return downloadWriter;
	}

	public void setDownloadWriter(String downloadWriter) {
		this.downloadWriter = downloadWriter;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
	
}
