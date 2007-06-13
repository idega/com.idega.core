package com.idega.presentation;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import com.idega.core.file.data.ICFile;
public class GenericPlugin extends PresentationObject 
{
	private Map params;
	private String classId;
	private String codeBase;
	private String pluginspace;
	private ICFile file = null;
	public GenericPlugin()
	{
		super();
	}
	public GenericPlugin(String url)
	{
		this(url, "untitled");
	}
	public GenericPlugin(String url, String name)
	{
		this();
		setName(name);
		setURL(url);
	}
	public GenericPlugin(String url, int width, int height)
	{
		this();
		setURL(url);
		setWidth(width);
		setHeight(height);
	}
	public GenericPlugin(String url, String name, int width, int height)
	{
		this(url, width, height);
		setName(name);
	}
	public void setURL(String url)
	{
		setParamAndAttribute("src", url);
	}
	public void setSrc(String src)
	{
		setParamAndAttribute("src", src);
	}
	public void setParamAndAttribute(String name, String value)
	{
		setMarkupAttribute(name, value);
		setParam(name, value);
	}
	public void setParamAndAttribute(String name, boolean value)
	{
		setParamAndAttribute(name, String.valueOf(value));
	}
	public void setParam(String name, String value)
	{
		if (this.params == null) {
			this.params = new Hashtable();
		}
		this.params.put(name, value);
	}
	public void clearParams() {
		this.params.clear();
	}
	public void setParam(String name, boolean value)
	{
		setParam(name, String.valueOf(value));
	}
	public String getParams()
	{
		StringBuffer paramString = new StringBuffer();
		if (this.params != null)
		{
			Iterator iter = this.params.keySet().iterator();
			String key;
			while (iter.hasNext())
			{
				key = (String) iter.next();
				paramString.append("<param name=\"");
				paramString.append(key);
				paramString.append("\" value=\"");
				paramString.append(this.params.get(key));
				paramString.append("\" >\n");
			}
		}
		return paramString.toString();
	}
	public void setWidth(int width)
	{
		setWidth(Integer.toString(width));
	}
	public void setWidth(String width)
	{
		setParamAndAttribute("width", width);
	}
	public String getWidth()
	{
		return getMarkupAttribute("width");
	}
	public void setHeight(int height)
	{
		setHeight(Integer.toString(height));
	}
	public void setHeight(String height)
	{
		setParamAndAttribute("height", height);
	}
	public String getHeight()
	{
		return getMarkupAttribute("height");
	}
	public String getURL()
	{
		return this.getMarkupAttribute("src");
	}
	public String getHeightString()
	{
		return " height=\"" + getHeight() + "\" ";
	}
	public String getWidthString()
	{
		return " width=\"" + getWidth() + "\" ";
	}
	public void setBackgroundColor(String bgColor)
	{
		setParamAndAttribute("BGCOLOR", bgColor);
	}
	public void setClassId(String classId)
	{
		this.classId = classId;
	}
	public void setCodeBase(String codeBase)
	{
		this.codeBase = codeBase;
	}
	public void setPluginSpace(String pluginspace)
	{
		this.pluginspace = pluginspace;
	}
	public String getClassId(String classId)
	{
		return classId;
	}
	public String getCodeBase(String codeBase)
	{
		return codeBase;
	}
	public String getPluginSpace(String pluginspace)
	{
		return pluginspace;
	}
	public void print(IWContext iwc) throws Exception
	{
		if (doPrint(iwc))
		{
			if (this.file != null)
			{
				String url = getICFileSystem(iwc).getFileURI(this.file);
				setURL(url);
			}
			if (getMarkupLanguage().equals("HTML"))
			{
				StringBuffer buffer = new StringBuffer();
				buffer.append("<object ");
				if(this.classId != null && !this.classId.equals("")) {
					buffer.append("classid=\"clsid:");
					buffer.append(this.classId);
					buffer.append("\" ");
				}
				if(this.codeBase != null && !this.codeBase.equals("")) {
					buffer.append("codebase=\"");
					buffer.append(this.codeBase);
					buffer.append("\" ");
				}
				if (getHeight() != null) {
					buffer.append(getHeightString());
				}
				if (getWidth() != null) {
					buffer.append(getWidthString());
				}
				buffer.append(">\n");
				buffer.append(getParams());
				buffer.append("<embed ");
				if(this.pluginspace != null && !this.pluginspace.equals("")) {
					buffer.append("pluginspage=\"");
					buffer.append(this.pluginspace);
					buffer.append("\" ");
				}
				buffer.append(getMarkupAttributesString());
				buffer.append(">\n</embed>\n</object>");
				print(buffer.toString());
			}
		}
	}
	public void setFile(ICFile file)
	{
		this.file = file;
	}
	public synchronized Object clone()
	{
		GenericPlugin obj = null;
		try
		{
			obj = (GenericPlugin) super.clone();
			obj.params = this.params;
			obj.classId = this.classId;
			obj.codeBase = this.codeBase;
			obj.pluginspace = this.pluginspace;
			obj.file = this.file;
		}
		catch (Exception ex)
		{
			ex.printStackTrace(System.err);
		}
		return obj;
	}
}
