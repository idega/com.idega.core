/**
 * Copyright (C) 2004  idega Software
 * 
 */
package com.idega.presentation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWUserContext;
import com.idega.util.text.AttributeParser;

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
	//private String family = RenderUtils.SMILE_FAMILY;
	private Map regionMap;
	//This variable sets if regions are treated as facets if set to true. Otherwise they are treated as children
	private boolean regionAsFacet=false;

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
	public boolean getRendersChildren() {
		return true;
	}
	
	/**
	 * Gets the default (first found) region.
	 * Returns null if none is found.
	 * @return
	 */
	public String getDefaultRegion(){
		for (Iterator iter = getRegionIdsMap().keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			Integer value = (Integer) getRegionIdsMap().get(key);
			if(value.intValue()==0){
				return key;
			}
		}
		return null;
	}
	
	
	/**
	 *
	 */
	public List getChildren(){
		//return getChildren(getDefaultRegion());
		return super.getChildren();
	}
	/**
	 *
	 */
	protected void setChildren(List newChildren)
	{
		//setChildren(getDefaultRegion(),newChildren);
		super.setChildren(newChildren);
	}
	
	
	public UIComponent getRegion(String regionKey){
		if(regionAsFacet){
			Object o = getFacets().get(regionKey);
			UIComponent region = (UIComponent) o;
			return region;
		}
		else{
			Integer index = (Integer) getRegionIdsMap().get(regionKey);
			Object o = getChildren().get(index.intValue());
			HtmlPageRegion region = (HtmlPageRegion) o;
			return region;
		}
	}
	
	public void setRegion(String regionKey,UIComponent region){
		if(regionAsFacet){
			Object previous = getFacets().get(regionKey);
			if(regionKey!=null){
				getFacets().put(regionKey,region);
			}
		}
		else{
			getChildren().add(region);
		}
	}
	
	
	public void add(UIComponent component,String regionId){
		UIComponent region = getRegion(regionId);
		region.getChildren().add(component);
	}

	
	public void add(UIComponent comp){
		add(comp,getDefaultRegion());
	}

	public void add(PresentationObject po){
		add((UIComponent)po);
	}

	/**
	 * The Map over the regions.
	 * Has as a key the regionId and as the value the index of 
	 * the corresponding HtmlPageRegion object int the getChildren() List.
	 * @return
	 */
	private Map getRegionIdsMap() {
		if (regionMap==null){
			regionMap=new HashMap();
		}
		return regionMap;
	}

	/**
	 * Returns all the regionIds as Strings
	 * @return
	 */
	public Set getRegionIds(){
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
	public void encodeBegin(FacesContext context)throws IOException{
		//Does nothing here
	}
	
	/**
	 * Overrided from Page
	 * @throws IOException
	 */
	public void encodeChildren(FacesContext context) throws IOException{
		//Does just call the print(iwc) method below:
		callPrint(context);
	}
	
	/**
	 * Overrided from Page
	 */
	public void encodeEnd(FacesContext context)throws IOException{
		//Does nothing here
	}
	
	/**
	 * @see javax.faces.component.UIComponent#encodeChildren(javax.faces.context.FacesContext)
	 */
	//public void encodeChildren(FacesContext ctx) throws IOException {
	public void print(IWContext ctx) throws IOException {
		
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
			Map attributeMap = AttributeParser.parse(attributes);
			Iterator iter = attributeMap.keySet().iterator();
			while (iter.hasNext()) {
				String attribute = (String) iter.next();
				String value = (String) attributeMap.get(attribute);
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
		} else {
			//out.startElement("p",null);
			out.write("Template file could not be found.");
			//out.endElement("p");
			List children = getChildren();
			for (Iterator iter = children.iterator(); iter.hasNext();) {
				UIComponent element = (UIComponent) iter.next();
				renderChild(ctx,element);
			}
		}
	}

	/**
	 * @see javax.faces.component.UIPanel#saveState(javax.faces.context.FacesContext)
	 */
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
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[])state;
		super.restoreState(ctx, values[0]);
		setHtml((String)values[1]);
		regionMap = (Map)values[2];
		regionAsFacet = ((Boolean)values[3]).booleanValue();
		//setResourceName((String)values[2]);
	}

	/**
	 * @return
	 */
	public String getHtml() {
		return html;
	}

	/**
	 * @param string
	 */
	public void setHtml(String string) {
		html = string;
		findOutRegions();
	}


//	/**
//	 * @see javax.faces.component.UIComponent#decode(javax.faces.context.FacesContext)
//	 */
//	public void decode(FacesContext ctx) {
//		List children = getChildren();
//		for (Iterator iter = children.iterator(); iter.hasNext();) {
//			UIComponent comp = (UIComponent) iter.next();
//			comp.decode(ctx);
//		}
//	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#main(com.idega.presentation.IWContext)
	 */
	
	public Object clone(IWUserContext iwc, boolean askForPermission){
		HtmlPage newPage = (HtmlPage) super.clone(iwc,askForPermission);
		if(regionMap!=null){
			newPage.regionMap=(Map)((HashMap) regionMap).clone();
		}
		return newPage;
	}
	
	public void main(IWContext iwc) throws Exception {
		// TODO Auto-generated method stub
		super.main(iwc);
	}
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#_main(com.idega.presentation.IWContext)
	 */
	public void _main(IWContext iwc) throws Exception {
		// TODO Auto-generated method stub
		super._main(iwc);
	}


	
	/**
	 * This variable gets if regions are treated as facets if set to true.
	 * Default is false.
	 * @return Returns the regionAsFacet.
	 */
	protected boolean isRegionAsFacet() {
		return regionAsFacet;
	}

	/**
	 * This sets if regions are treated as facets if set to true. Otherwise they are treated as children.
	 * Default value is set to false.
	 * @param regionAsFacet The regionAsFacet to set.
	 */
	protected void setRegionAsFacet(boolean regionAsFacet) {
		this.regionAsFacet = regionAsFacet;
	}
	
	public Map getFacets(){
		if(this.facetMap==null){
			facetMap = new HtmlPageRegionFacetMap(this);
		}
		return facetMap;
	}
}
