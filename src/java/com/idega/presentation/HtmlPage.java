/**
 * Copyright (C) 2004  idega Software
 * 
 */
package com.idega.presentation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWUserContext;
import com.idega.util.CoreConstants;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.idega.util.resources.ResourcesAdder;
import com.idega.util.text.AttributeParser;
import com.idega.util.xml.XmlUtil;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 * HTML based Page component.
 * This component can be used to template the look and feel
 * of your site in a simple manner. You supply an HTML template
 * directly.
 * Inside the Layout template tags are used to define "regions" where UIComponent
 * components can be added in and rendered dynamically.<br>
 * The regions are defined like this:
 * 
 * <code><pre>
 * <HTML>... <BODY>... <!-- TemplateBeginEditable name="MyUniqueRegionId1" -->MyUniqueRegionId1<!-- TemplateEndEditable --> ... <table><tr><td><!-- TemplateBeginEditable name="MyUniqueRegionId2" -->MyUniqueRegionId2<!-- TemplateEndEditable --</td></tr></table>
 * </pre></code>
 * 
 * This class parses the HTML and looks for the tag  <code><pre><!-- TemplateBeginEditable ... ></pre></code>
 * Where the first region found becomes the "default".
 * This class also parses the  <code><pre> <HEAD> </pre></code> attribute contents and includes the things normally found inside 
 * an idegaWeb Page.
 */
public class HtmlPage extends Page {

	private String html;
	private Map<String, Integer> regionMap;
	
	//This variable sets if regions are treated as facets if set to true. Otherwise they are treated as children
	private boolean regionAsFacet;

	public HtmlPage() {
		super();
	}
	
	/**
	 * @return
	 */
	public void setResource(InputStream htmlStream) {
		//String ret = "";
		
		//ret = getHtml();
		//if(ret == null && getResourceName() != null) {
			
			//InputStream htmlStream = this.getClass().getClassLoader().getResourceAsStream(getResourceName());
			if(htmlStream != null) {
				try {
					InputStreamReader htmlReader = new InputStreamReader(htmlStream);
					StringBuffer html = new StringBuffer();
					int nr = 0;
					char[] buffer = new char[1024];
					while(nr != -1) {
						nr = htmlReader.read(buffer);
						if(nr != -1) {
							html.append(buffer,0,nr);
						}
					}
					setHtml(html.toString());
					//ret = getHtml();
					htmlReader.close();
				} catch(IOException e) {
					throw new RuntimeException("Attribute <resourceName> for component <" + getId() + ">. Could not load the html from named resource <>");
				}
			}
		//}
		findOutRegions();
		//return ret;
	}

	/**
	 * Need to render my children myself. Typical for layout 
	 * management components.
	 * 
	 * @see javax.faces.component.UIComponent#getRendersChildren()
	 */
	@Override
	public boolean getRendersChildren() {
		return true;
	}
	
	/**
	 * Gets the default (first found) region.
	 * Returns null if none is found.
	 * @return
	 */
	public String getDefaultRegion(){
		for (Iterator<String> iter = getRegionIdsMap().keySet().iterator(); iter.hasNext();) {
			String key = iter.next();
			Integer value = getRegionIdsMap().get(key);
			if (value.intValue() == 0) {
				return key;
			}
		}
		return null;
	}
	
	/**
	 *
	 */
	@Override
	public List<UIComponent> getChildren() {
		return super.getChildren();
	}
	/**
	 *
	 */
	@Override
	protected void setChildren(List<UIComponent> newChildren) {
		super.setChildren(newChildren);
	}
	
	public UIComponent getRegion(String regionKey) {
		if (this.regionAsFacet) {
			return getFacets().get(regionKey);
		}
		else{
			Integer index = getRegionIdsMap().get(regionKey);
			if(index!=null){
				Object o = getChildren().get(index.intValue());
				UIComponent child = (UIComponent) o;
				return child;
			}
			else{
				return null;
			}
		}
	}
	
	public void setRegion(String regionKey, UIComponent region){
		if (this.regionAsFacet) {
			if (regionKey != null) {
				getFacets().put(regionKey,region);
			}
		}
		else {
			getChildren().add(region);
		}
	}
	
	public void add(UIComponent component, String regionId) {
		UIComponent region = getRegion(regionId);
		if (region != null) {
			region.getChildren().add(component);
		}
		else{
			getLogger().info("No Region found for regionId="+regionId);
		}
	}

	@Override
	public void add(UIComponent comp){
		add(comp, getDefaultRegion());
	}

	@Override
	public void add(PresentationObject po){
		add(po);
	}

	/**
	 * The Map over the regions.
	 * Has as a key the regionId and as the value the index of 
	 * the corresponding HtmlPageRegion object int the getChildren() List.
	 * @return
	 */
	private Map<String, Integer> getRegionIdsMap() {
		if (this.regionMap == null) {
			this.regionMap = new HashMap<String, Integer>();
		}
		return this.regionMap;
	}

	/**
	 * Returns all the regionIds as Strings
	 * @return
	 */
	public Set<String> getRegionIds(){
		return getRegionIdsMap().keySet();
	}

	private void findOutRegions(){
		String template = getHtml();
		if(template != null) {
			String[] parts = template.split("<!-- TemplateBeginEditable");
			//out.write(parts[0]);
			int regionIndex=0;
			for (int i = 1; i < parts.length; i++) {
				String part = parts[i];
				String[] t = part.split("TemplateEndEditable -->");
			
				String toParse = t[0];
				String[] a1=toParse.split("name=\"");
				String[] a2 = a1[1].split("\"");
				
				String regionId=a2[0];
				
				getRegionIdsMap().put(regionId,new Integer(regionIndex));
				//instanciate the region in the children list:
				HtmlPageRegion region = new HtmlPageRegion();
				region.setRegionId(regionId);
				setRegion(regionId,region);
				//getChildren().add(region);
				//getChildren().set(regionIndex,region);
				regionIndex++;
			}
		}
	}
	
	/**
	 * Overrided from Page
	 */
	@Override
	public void encodeBegin(FacesContext context)throws IOException{
		//Does nothing here
	}
	
	/**
	 * Overrided from Page
	 * @throws IOException
	 */
	@Override
	public void encodeChildren(FacesContext context) throws IOException{
		//Does just call the print(iwc) method below:
		callPrint(context);
	}
	
	/**
	 * Overrided from Page
	 */
	@Override
	public void encodeEnd(FacesContext context)throws IOException{
		//Does nothing here
		encodeRenderTime(context);
	}
	
	/**
	 * @see javax.faces.component.UIComponent#encodeChildren(javax.faces.context.FacesContext)
	 */
	//public void encodeChildren(FacesContext ctx) throws IOException {
	@Override
	public void print(IWContext ctx) throws IOException {
		addSessionPollingDWRFiles(ctx);
		Writer out;
		
		//ResponseWriter out = ctx.getResponseWriter();
		//RenderUtils.ensureAllTagsFinished();
		if(IWMainApplication.useJSF){
			out = ctx.getResponseWriter();
		}
		else{
			out = ctx.getWriter();
		}
		
		String template = getHtml();
		if(template != null) {
			//Process the HEAD first:
			Pattern headOpensPattern = Pattern.compile("<head>", Pattern.CASE_INSENSITIVE);
			String[] headOpensSplit = headOpensPattern.split(template);
			//String[] headOpensSplit = template.split("<head>");
			String preHead = headOpensSplit[0];
			String postHeadOpens = headOpensSplit[1];
			out.write(preHead);
			out.write("<head>");
			
			Pattern headClosesPattern = Pattern.compile("</head>", Pattern.CASE_INSENSITIVE);
			String[] headClosesSplit = headClosesPattern.split(postHeadOpens);
//			String[] headClosesSplit = postHeadOpens.split("</head>");
			String headContent = headClosesSplit[0];
			String body = headClosesSplit[1];
			
			//Get the contents from the superclass first
			out.write(getHeadContents(ctx));
			Script associatedScript = getAssociatedScript();
			renderChild(ctx,associatedScript);
			
			//then printout the head contents from the html page
			//find out where the title is in the head:
			String htmlTitle="";
			try{
				//Try to find where the TITLE tag is in the HEAD:
				Pattern titlePattern = Pattern.compile("<title>", Pattern.CASE_INSENSITIVE);
				String[] titleSplit = titlePattern.split(headContent);
				//String[] titleSplit = headContent.split("<title>");
				String preTitleHead= titleSplit[0];
				String postTitleOpens = titleSplit[1];
				
				Pattern postTitlePattern = Pattern.compile("</title>", Pattern.CASE_INSENSITIVE);
				String[] postTitleSplit = postTitlePattern.split(postTitleOpens);
				//String[] postTitleSplit = postTitleOpens.split("</title>");
				htmlTitle = postTitleSplit[0];
				String postTitleHead = postTitleSplit[1];
				
				//Print out all befor the TITLE tag in the HEAD
				out.write(preTitleHead);
				//Print out the title from the idegaWeb page
				String locTitle = this.getLocalizedTitle(ctx);
				out.write("<title>");
				if(locTitle!=null   && !locTitle.equals("")){
					out.write(locTitle);
				}
				else{
					out.write(htmlTitle);
				}
				out.write("</title>");
				//Print out all after the TITLE tag in the HEAD
				out.write(postTitleHead);
			}
			catch(ArrayIndexOutOfBoundsException ae){
				//If there is an error (title not found)
				//then just write out the whole head contents + idegaWeb Title
				out.write(headContent);
				String locTitle = this.getLocalizedTitle(ctx);
				out.write("<title>");
				if(locTitle!=null   && !locTitle.equals("")){
					out.write(locTitle);
				}
				else{
					out.write(htmlTitle);
				}
				out.write("</title>");
			}
			out.write("</head>");	
			
			String[] htmlBody = body.split("<body");
			int index = 0;
			if (htmlBody.length > 1) {
				out.write(htmlBody[index++]);
			}
			body = htmlBody[index];

			String attributes = body.substring(0, body.indexOf(">"));
			Map<String, String> attributeMap = AttributeParser.parse(attributes);
			for (Iterator<String> iter = attributeMap.keySet().iterator(); iter.hasNext();) {
				String attribute = iter.next();
				String value = attributeMap.get(attribute);
				if (attribute.equals("onload")) {
					this.setMarkupAttributeMultivalued("onload", value);
				}
				if (attribute.equals("onunload")) {
					this.setMarkupAttributeMultivalued("onunload", value);
				}
				else {
					if (!isMarkupAttributeSet(attribute)) {
						setMarkupAttribute(attribute, value);
					}
				}
			}
			out.write("<body " + getMarkupAttributesString() + " >\n");
			
			body = body.substring(body.indexOf(">") + 1);
			
			//Process the template regions:			
			String[] parts = body.split("<!-- TemplateBeginEditable");
			out.write(parts[0]);
			for (int i = 1; i < parts.length; i++) {
				String part = parts[i];
				String[] t = part.split("TemplateEndEditable -->");
			
				String toParse = t[0];
				String[] a1=toParse.split("name=\"");
				String[] a2 = a1[1].split("\"");
				
				String regionId=a2[0];
				
				
				//int childNumber = Integer.parseInt(t[0]) - 1;

				try{
					UIComponent region = getRegion(regionId);
					renderChild(ctx,region);
				}
				catch(ClassCastException cce){
					cce.printStackTrace();
				}
		
				out.write(t[1]);
			}
			
			for (UIComponent child: getChildren()) {
				if (!(child instanceof HtmlPageRegion)) {
					renderChild(ctx, child);
				}
			}
		} else {
			out.write("Template file could not be found.");
			
			List<UIComponent> children = getChildren();
			for (Iterator<UIComponent> iter = children.iterator(); iter.hasNext();) {
				renderChild(ctx, iter.next());
			}
		}
	}

	/**
	 * @see javax.faces.component.UIPanel#saveState(javax.faces.context.FacesContext)
	 */
	@Override
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[4];
		values[0] = super.saveState(ctx);
		values[1] = getHtml();
		values[2] = this.regionMap;
		values[3] = Boolean.valueOf(this.regionAsFacet);
		//values[2] = getResourceName();
		return values;
	}
	
	/**
	 * @see javax.faces.component.UIPanel#restoreState(javax.faces.context.FacesContext, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[])state;
		super.restoreState(ctx, values[0]);
		setHtml((String)values[1]);
		this.regionMap = (Map<String, Integer>) values[2];
		this.regionAsFacet = ((Boolean)values[3]).booleanValue();
	}

	/**
	 * @return
	 */
	public String getHtml() {
		return this.html;
	}

	/**
	 * @param string
	 */
	public void setHtml(String string) {
		this.html = string;
		findOutRegions();
		findOutResources();
	}
	
	private void findOutResources() {
		if (StringUtil.isEmpty(html)) {
			return;
		}
		
		if (!ResourcesAdder.isOptimizationTurnedOn(ResourcesAdder.OPTIMIZE_RESOURCES)) {
			return;
		}
		
		boolean optimizeJavaScript = ResourcesAdder.isOptimizationTurnedOn(ResourcesAdder.OPTIMIZE_JAVA_SCRIPT);
		boolean optimizeStyleSheet = ResourcesAdder.isOptimizationTurnedOn(ResourcesAdder.OPTIMIZE_STYLE_SHEET);
		if (!optimizeJavaScript && !optimizeStyleSheet) {
			return;
		}
		
		Document templateDoc = XmlUtil.getJDOMXMLDocument(html);
		if (templateDoc == null) {
			Logger.getLogger(getClass().getName()).warning("Unable to optimize resources! Template file can not be resolved!");
			return;
		}
		
		Namespace namespace = Namespace.getNamespace(CoreConstants.XHTML_NAMESPACE_ID, CoreConstants.XHTML_NAMESPACE);
		List<Element> uselessElements = new ArrayList<Element>();
		
		if (optimizeJavaScript) {
			List<Element> javaScripts = XmlUtil.getElementsByXPath(templateDoc.getRootElement(), namespace, "//" + CoreConstants.XHTML_NAMESPACE_ID + ":script");
			if (!ListUtil.isEmpty(javaScripts)) {
				for (Element script: javaScripts) {
					Attribute source = script.getAttribute("src");
					if (source == null || StringUtil.isEmpty(source.getValue())) {
						
					}
					else {
						String sourceUri = source.getValue();
						if (StringUtil.isEmpty(sourceUri)) {
							break;
						}
						
						addJavascriptURL(sourceUri);
					}
					
					uselessElements.add(script);
				}
			}
		}
		
		if (optimizeStyleSheet) {
			List<Element> styleSheets = XmlUtil.getElementsByXPath(templateDoc.getRootElement(), namespace, "//" + CoreConstants.XHTML_NAMESPACE_ID + ":link");
			if (!ListUtil.isEmpty(styleSheets)) {
				for (Element style: styleSheets) {
					Attribute source = style.getAttribute("href");
					if (source == null) {
						break;
					}
					String sourceUri = source.getValue();
					if (StringUtil.isEmpty(sourceUri)) {
						break;
					}
					
					Attribute media = style.getAttribute("media");
					if (media == null) {
						break;
					}
					
					addStyleSheetURL(sourceUri, media.getValue());
					uselessElements.add(style);
				}
			}
		}
		
		if (uselessElements.size() > 0) {
			for (Iterator<Element> uselessElementsIter = uselessElements.iterator(); uselessElementsIter.hasNext();) {
				uselessElementsIter.next().detach();
			}
			
			this.html = XmlUtil.getPrettyJDOMDocument(templateDoc);
		}
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#main(com.idega.presentation.IWContext)
	 */
	
	@SuppressWarnings("unchecked")
	@Override
	public Object clone(IWUserContext iwc, boolean askForPermission){
		HtmlPage newPage = (HtmlPage) super.clone(iwc,askForPermission);
		if (this.regionMap != null) {
			newPage.regionMap = (Map<String, Integer>) ((HashMap<String, Integer>) this.regionMap).clone();
		}
		return newPage;
	}
	
	@Override
	public void main(IWContext iwc) throws Exception {
		super.main(iwc);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#_main(com.idega.presentation.IWContext)
	 */
	@Override
	public void _main(IWContext iwc) throws Exception {
		super._main(iwc);
	}

	/**
	 * This variable gets if regions are treated as facets if set to true.
	 * Default is false.
	 * @return Returns the regionAsFacet.
	 */
	protected boolean isRegionAsFacet() {
		return this.regionAsFacet;
	}

	/**
	 * This sets if regions are treated as facets if set to true. Otherwise they are treated as children.
	 * Default value is set to false.
	 * @param regionAsFacet The regionAsFacet to set.
	 */
	protected void setRegionAsFacet(boolean regionAsFacet) {
		this.regionAsFacet = regionAsFacet;
	}
	
	@Override
	public Map<String, UIComponent> getFacets() {
		if (this.facetMap == null) {
			this.facetMap = new HtmlPageRegionFacetMap(this);
		}
		return this.facetMap;
	}
}
