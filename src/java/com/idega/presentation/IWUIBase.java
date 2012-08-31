package com.idega.presentation;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.idega.core.idgenerator.business.UUIDGenerator;
import com.idega.util.CoreUtil;
import com.idega.util.PresentationUtil;
import com.idega.util.StringUtil;
/**
 * Some new things that I use most of the time.
 * @author alex
 *
 */
public abstract class IWUIBase extends IWBaseComponent{

	private StringBuilder scriptOnLoad = null;
	private IWContext iwc = null;
	private String tag = null;
	
	public IWUIBase(){
		super();
	}
	
	public IWUIBase(String tag){
		
	}
	
	
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	@Override
	public void encodeBegin(FacesContext context)throws IOException{
		
		if(!StringUtil.isEmpty(tag)){
			writeTag(context);
		}
		super.encodeBegin(context);
	}
	
	private void writeTag(FacesContext context) throws IOException{
		ResponseWriter responseWriter = context.getResponseWriter();
		responseWriter.startElement(tag, this);
		Map<String,Object> attributes = getAttributes();
		Set<String> names = getAttributes().keySet();
		for(String name : names){
			responseWriter.writeAttribute(name, String.valueOf(attributes.get(name)), null);
		}
		
	}
	
	@Override
	public void encodeEnd(FacesContext context)throws IOException{
		if(!StringUtil.isEmpty(tag)){
			ResponseWriter responseWriter = context.getResponseWriter();
			responseWriter.endElement(tag);
		}
	}
	
	protected void addScriptOnLoad(){
		Layer scriptLayer = new Layer();
		add(scriptLayer);
		scriptOnLoad.append("\n});");
		scriptLayer.add(PresentationUtil.getJavaScriptAction(scriptOnLoad.toString()));
	}
	
	protected StringBuilder getScriptOnLoad() {
		if(scriptOnLoad == null){
			scriptOnLoad = new StringBuilder("jQuery(document).ready(function(){");
		}
		return scriptOnLoad;
	}

	@Override
	protected void initializeComponent(FacesContext context) {
		super.initializeComponent(context);
		addScriptOnLoad();
		addFiles(getIwc(context));
	}

	protected void setScriptOnLoad(StringBuilder scriptOnLoad) {
		this.scriptOnLoad = scriptOnLoad;
	}
	
	
	protected IWContext getIwc(FacesContext context){
		if(iwc==null){
			iwc = IWContext.getIWContext(context);
		}
		return iwc;
	}
	protected IWContext getIwc() {
		if(iwc == null){
			iwc = CoreUtil.getIWContext();
		}
		return iwc;
	}

	protected void setIwc(IWContext iwc) {
		this.iwc = iwc;
	}


	private String generateID()
	{
		String UUID = UUIDGenerator.getInstance().generateId();
		return "iwid" + UUID.substring(UUID.lastIndexOf("-") + 1);
	}
	
	/**
	 * Sets the attribute with value attributeValue but, if there was previously a value 
	 * there old value will be kept and the new value be added with the semicolon separator ; 
	 * @param attributeName
	 * @param attributeValue
	 */
	public void setMarkupAttributeMultivalued(String attributeName, String attributeValue) {
		setMarkupAttributeMultivalued(attributeName, attributeValue, ";");
	}
	
	/**
	 * Sets the attribute with value attributeValue but, if there was previously a value 
	 * there old value will be kept and the new value be added with the supplied seperator 
	 * @param attributeName
	 * @param attributeValue
	 * @param seperator
	 */
	public void setMarkupAttributeMultivalued(String attributeName, String attributeValue, String seperator) {
		Object previousAttributeObject = getAttributes().get(attributeName);
		if (previousAttributeObject == null){
			getAttributes().put(attributeName, attributeValue);
			return;
		}
		String previousAttribute = String.valueOf(previousAttributeObject);
		if (previousAttribute.indexOf(attributeValue) == -1){
			String parameterValue = previousAttribute;
			if (previousAttribute.endsWith(seperator)) {
				parameterValue = parameterValue + attributeValue;
			}
			else {
				parameterValue = parameterValue + seperator + attributeValue;
			}
			getAttributes().put(attributeName, parameterValue);
		}
	}
	
	public void addStyleClass(String styleName) {
		setMarkupAttributeMultivalued("class", styleName, " ");
	}
	
	@Override
	public String getId() {
		String id = super.getId();
		if(StringUtil.isEmpty(id)){
			id = generateID();
			setId(id);
		}
		return id;
	}
	
	protected Logger getLogger(){
		return Logger.getLogger(getClass().getName());
	}
	protected void addFiles(IWContext iwc){
		PresentationUtil.addJavaScriptSourcesLinesToHeader(iwc, getScripts());
		PresentationUtil.addStyleSheetsToHeader(iwc, getStyleSheets());
	}
	public abstract List<String> getScripts();
	
	public abstract List<String> getStyleSheets();
}
