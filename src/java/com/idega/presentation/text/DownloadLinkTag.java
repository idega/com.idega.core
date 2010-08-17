package com.idega.presentation.text;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.idega.io.MediaWritable;
import com.idega.presentation.ComponentTag;
import com.idega.util.reflect.Property;

public class DownloadLinkTag extends ComponentTag {

	private String id;
	private String styleClass;

	private Object downloadWriter;
	private Object value;

	@Override
	public String getComponentType() {
		return DownloadLink.COMPONENT_TYPE;
	}

	@Override
	public String getRendererType() {
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void release() {
		super.release();

		this.downloadWriter = null;
		this.id = null;
		this.styleClass = null;
		this.value = null;
	}

	@SuppressWarnings("unchecked")
	private Class<? extends MediaWritable> getClass(String className) {
		try {
			return (Class<? extends MediaWritable>) Class.forName(className);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	protected void setProperties(UIComponent component) {
		if (component instanceof DownloadLink) {
			super.setProperties(component);

			DownloadLink link = (DownloadLink) component;
			Class<? extends MediaWritable> mediaWriter = null;
			if (downloadWriter instanceof ValueExpression) {
				try {
					Object writer = ((ValueExpression) downloadWriter).getValue(getELContext());
					if (writer instanceof Class) {
						mediaWriter = (Class<? extends MediaWritable>) writer;
					} else if (writer instanceof String) {
						mediaWriter = getClass((String) writer);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (downloadWriter instanceof String) {
				try {
					String writer = Property.getValueFromExpression((String) downloadWriter, String.class);
					mediaWriter = getClass(writer);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (downloadWriter instanceof Class) {
				try {
					mediaWriter = (Class<? extends MediaWritable>) downloadWriter;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (mediaWriter != null) {
				link.setMediaWriterClass(mediaWriter);
			}


			link.setId(id);
			link.setStyleClass(styleClass);

			ValueExpression valueExpression = null;
			if (value instanceof String) {
				try {
					valueExpression = FacesContext.getCurrentInstance().getApplication().getExpressionFactory()
						.createValueExpression(getELContext(), (String) value, String.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (value instanceof ValueExpression) {
				valueExpression = (ValueExpression) value;
			}
			if (valueExpression != null) {
				link.setValueExpression("value", valueExpression);
			}
		}
	}

	public String getDownloadWriter() {
		return getValue(downloadWriter);
	}

	public void setDownloadWriter(String downloadWriter) {
		this.downloadWriter = downloadWriter;
	}
	public void setDownloadWriter(Object downloadWriter) {
		this.downloadWriter = downloadWriter;
	}
	public void setDownloadWriter(ValueExpression downloadWriter) {
		this.downloadWriter = downloadWriter;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getValue() {
		return getValue(value);
	}

	public void setValue(String value) {
		this.value = value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public void setValue(ValueExpression value) {
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