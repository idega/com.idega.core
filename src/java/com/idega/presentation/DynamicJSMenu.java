package com.idega.presentation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.idega.presentation.text.Text;
/**
 * 
 * @author birna
 *
 * Creates a dynamic JavaScript menu using external
 * javaScript; coolmenus4.js 
 * 
 */
public class DynamicJSMenu extends PresentationObject {
	
	private PresentationObject _obj;
	
	IWContext iwc = new IWContext();
	
	private List theMenuElements;
	private List theMenuLevelElements;
	
	private MenuLevelElement levelElement;
	private MenuElement menuElement;
	
	private Text text;
	
	private Page parentPage;
	private String coolMenuScript = "coolmenus4.js";
	private String menuStyleScript = "coolStyle.css";
	private String coolMenuSrc;
	private String menuStyleSrc;
	
	private int frames = 0;
	private int pxBetween = 0;
	private int fromLeft = 20;
	private int fromTop = 0;
	private int rows = 1;
	private String menuPlacement = " ";
	private String offlineRoot = " ";
	private String onlineRoot = " ";
	private int resizeCheck = 1;
	private int wait = 1000;
	private String fillImg = " ";
	private int zIndex = 0;
	
	private String barBorderClass = " ";
	private int barBorderX = 0;
	private int barBorderY = 0;
	private String barClass  = " ";
	private String barHeight = "menu";
	private String barWidth = "menu";
	private String barX = "menu";
	private String barY = "menu";
	private int useBar = 1;
	
	private String position = ""; 
	private String visibility = ""; 
	private String padding = ""; 
	private String top = ""; 
	private String left = "";
	private String width = "";
	private String height = "";  
	private String fontStyle; 
	private String fontOverStyle;
	private String barLayerBackgroundColor = "";
	private String barBackgroundColor = "";
	private String layerBackgroundColor = ""; 
	private String backgroundColor = "";
	private String overLayerBackgroundColor = "";
	private String overBackgroundColor = "";
	private String cursor = ""; 
	private String borderColor = ""; 
	private String borderLayerColor = "";
	/**
	 * the default constructor
	 *
	 */
	public DynamicJSMenu (){
		this("undefined");
	}
	/**
	 * constructor to creating a <code> DynamicJSMenu </code> object whith
	 * a specific name.
	 * @param name
	 */
	public DynamicJSMenu(String name){
		setName(name);
		theMenuLevelElements = new ArrayList();
		theMenuElements = new ArrayList();
		initialMenuValues();
	}
	/**
	 * sets the initial values for the <code> DynamicJSMenu </code>
	 *
	 */
	public void initialMenuValues(){
		setBarBorderClass(barBorderClass);
		setBarBorderX(barBorderX);
		setBarBorderY(barBorderY);
		setBarClass(barClass);
		setBarHeight(barHeight);
		setBarWidth(barWidth);
		setBarX(barX);
		setBarY(barY);
		setFillImg(fillImg);
		setFromLeft(fromLeft);
		setFromTop(fromTop);
		setMenuPlacement(menuPlacement);
		setOfflineRoot(offlineRoot);
		setOnlineRoot(onlineRoot);
		setPxBetween(pxBetween);
		setResizeCheck(resizeCheck);
		setRows(rows);
		setUseBar(useBar);
		setZIndex(zIndex);
		setWait(wait);
	}
	/**
	 * adds a level to the <code> DynamicJSMenu </code>
	 * @param level
	 */
	public void addLevel(String level){
		levelElement = new MenuLevelElement(level);
//		lElement.setLevel(level);
		levelElement.setRegClass("clLevel" + level);
		levelElement.setOverClass("clLevel" + level + "over");
		levelElement.setBorderClass("clLevel" + level + "border");
		theMenuLevelElements.add(levelElement);
	}
	/**
	 * 
	 * @param f
	 */
	public void setFrames(int f){
		setMarkupAttribute("frames", f);
	}
	/**
	 * sets the pixles between <code> MenuElements </code>
	 * @param pxB
	 */
	public void setPxBetween(int pxB){
		setMarkupAttribute("pxBetween", pxB);
	}
	/**
	 * sets the position the menu is located at from the left of the browserwindow, x-coordinate
	 * @param fromL
	 */
	public void setFromLeft(int fromL){
		setMarkupAttribute("fromLeft", fromL);
	}
	/**
	 * sets the position the menu is located at from the top of the browserwindow, y-coordinates
	 * @param fromT
	 */
	public void setFromTop(int fromT){
		setMarkupAttribute("fromTop", fromT);
	}	
	/**
	 * sets if the menu should appear horizontal (r=0) or vertical (r=0)
	 * @param r
	 */
	public void setRows(int r){
		setMarkupAttribute("rows", r);
	}
	/**
	 * 
	 * @param menuPlace
	 */
	public void setMenuPlacement(String menuPlace){
		setMarkupAttribute("menuPlacement", "\"" + menuPlace + "\"");
	}
	/**
	 * 
	 * @param offlineR
	 */
	public void setOfflineRoot(String offlineR){
		setMarkupAttribute("offlineRoot", "\"" + offlineR + "\"");
	}
	/**
	 * 
	 * @param onlineR
	 */
	public void setOnlineRoot(String onlineR){
		setMarkupAttribute("onlineRoot", "\"" + onlineR + "\"");
	}
	/**
	 * 
	 * @param resizeCh
	 */
	public void setResizeCheck(int resizeCh){
		setMarkupAttribute("resizeCheck", resizeCh);
	}
	/**
	 * 
	 * @param w
	 */
	public void setWait(int w){
		setMarkupAttribute("wait", w);
	}
	/**
	 * 
	 * @param fImg
	 */
	public void setFillImg(String fImg){
		setMarkupAttribute("fillImg", "\"" + fImg + "\"");
	}
	/**
	 * 
	 * @param zIn
	 */
	public void setZIndex (int zIn){
		setMarkupAttribute("zIndex", zIn);
	}
	/**
	 * 
	 * @param useB
	 */
	public void setUseBar(int useB){
		setMarkupAttribute("useBar", useB);
	}
	/**
	 * 
	 * @param barW
	 */
	public void setBarWidth(String barW){
		setMarkupAttribute("barWidth", "\"" + barW + "\"");
	}
	/**
	 * 
	 * @param barH
	 */
	public void setBarHeight(String barH){
		setMarkupAttribute("barHeight", "\"" + barH + "\"");
	}
	/**
	 * 
	 * @param barCl
	 */
	public void setBarClass(String barCl){
		setMarkupAttribute("barClass", "\"" + barCl + "\"");
	}
	/**
	 * 
	 * @param bX
	 */
	public void setBarX(String bX){
		setMarkupAttribute("barX", "\"" + bX + "\"");
	}
	/**
	 * 
	 * @param bY
	 */
	public void setBarY(String bY){
		setMarkupAttribute("barY", "\"" + bY + "\"");
	}
	/**
	 * 
	 * @param barBordX
	 */
	public void setBarBorderX(int barBordX){
		setMarkupAttribute("barBorderX", barBordX);
	}
	/**
	 * 
	 * @param barBordY
	 */
	public void setBarBorderY(int barBordY){
		setMarkupAttribute("barBorderY", barBordY);
	}
	/**
	 * 
	 * @param barBordCl
	 */
	public void setBarBorderClass(String barBordCl){
		setMarkupAttribute("barBorderClass", "\"" + barBordCl + "\"");
	}
	public void setStylePosition(String pos){
//		styleObject.setAttribute("position", pos);
		position = pos;
	}
	public void setStyleVisibility(String vis){
//		styleObject.setAttribute("visibility", vis);
		visibility = vis;
	}
	public void setStyleLeft(String l){
//		styleObject.setAttribute("left", l);
		left = l;
	}
	public void setStyleTop(String t){
//		setAttribute("top", t);
		top = t;
	}
	public void setStyleWidth(String w){
		width = w;
	}
	public void setStyleHeight(String h){
		height = h;
	}
	public void setStylePadding(String pad){
		padding = pad;
	}
	public void setStyleFontStyle(String style){
		fontStyle = style;
	}
	public void setStyleFontOverStyle(String style){
		fontOverStyle = style;
	}
	public void setBarStyleLayerBackgroundColor(String barLBCol){
		barLayerBackgroundColor = barLBCol;
	}
	public void setBarStyleBackgroundColor(String barBCol){
		barBackgroundColor = barBCol;
	}
	public void setStyleLayerBackgroundColor(String lBackgrCol){
		layerBackgroundColor = lBackgrCol;
	}
	public void setStyleBackgroundColor(String backgrCol){
		backgroundColor = backgrCol;
	}
	public void setOverStyleLayerBackgroundColor(String overLBCol){
		overLayerBackgroundColor = overLBCol;
	}
	public void setOverStyleBackgroundColor(String overBCol){
		overBackgroundColor = overBCol;
	}
	public void setStyleCursor(String curs){
		cursor = curs;
	}
	public void setStyleBorderColor(String bColor){
		borderColor = bColor;
	}
	public void setStyleLayerBorderColor(String blColor){
		borderLayerColor = blColor;
	}
	public String getStylePosition(){
		return position;
	}
	public String getStyleVisibility(){
		return visibility;
	}
	public String getStyleLeft(){
		return left;
	}
	public String getStyleTop(){
		return top;
	}
	public String getStyleWidth(){
		return width; 
	}
	public String getStyleHeight(){
		return height;
	}
	public String getStylePadding(){
		return padding; 
	}
	public String getBarStyleBackgroundColor(){
		barLayerBackgroundColor = barBackgroundColor;
		return barBackgroundColor; 
	}
	public String getBarStyleLayerBackgroundColor(){
		return barLayerBackgroundColor;
	}
	public String getStyleBackgroundColor(){
		return backgroundColor;
	}
	public String getStyleLayerBackgroundColor(){
		layerBackgroundColor = backgroundColor;
		return layerBackgroundColor;
	}
	public String getOverStyleBackgroundColor(){
		overLayerBackgroundColor = overBackgroundColor;
		return overBackgroundColor;
	}
	public String getOverStyleLayerBackgroundColor(){
		return overLayerBackgroundColor;
	}
	public String getStyleCursor(){
		return cursor;
	}
	public String getStyleFontStyle(){
		return fontStyle;
	}
	public String getStyleFontOverStyle(){
		return fontOverStyle;
	}
	public String getStyleBorderColor(){
		return borderColor;
	}
	public String getStyleLayerBorderColor(){
		borderLayerColor = borderColor;
		return borderLayerColor;
	}
	
	public String commonStyleDefinitions(){
		StringBuffer attributeString = new StringBuffer();
		setStylePosition("absolute");
		setStyleVisibility("hidden");
		attributeString.append("position:" + position + ";");
		attributeString.append("visibility:" + visibility + ";");
		return attributeString.toString();
	}
	public String defaultStyle(){
		StringBuffer attributeString = new StringBuffer();
		setStyleLeft("0");
		setStyleTop("0");
		attributeString.append("left:" + left + ";");
		attributeString.append("top:" + top + ";");
		return attributeString.toString();
	}
	public String setBarStyle(){
		StringBuffer attributeString = new StringBuffer();
		setStyleWidth("10");
		setStyleHeight("10");
		attributeString.append("width:" + width + ";");
		attributeString.append("height:" + height + ";");
		attributeString.append("background-color:" + getBarStyleBackgroundColor() + ";");
		attributeString.append("layer-background-color:" + getBarStyleLayerBackgroundColor() + ";");
		return attributeString.toString();
	}
	public String commonLevelStyle(){
		StringBuffer attributeString = new StringBuffer();
		setStylePadding("2px");
		setStylePosition("absolute");
		attributeString.append("padding:" + padding + ";");
		attributeString.append("position:" + position + ";");
		return attributeString.toString();
	}
	public String setLevelStyle(){
		StringBuffer attributeString = new StringBuffer();
		attributeString.append("background-color:" + getStyleBackgroundColor() + ";");
		attributeString.append("layer-background-color:" + getStyleLayerBackgroundColor() + ";");
		attributeString.append(getStyleFontStyle());
//		attributeString.append("color:" + getStyleColor() + ";");
		return attributeString.toString();
	}
	public String setLevelOverStyle(){
		StringBuffer attributeString = new StringBuffer();
		setStyleCursor("pointer");
		attributeString.append("background-color:" + getOverStyleBackgroundColor() + ";");
		attributeString.append("layer-background-color:" + getOverStyleLayerBackgroundColor() + ";");
//		attributeString.append("color:" + getOverStyleColor() + ";");
		attributeString.append(getStyleFontOverStyle());
		attributeString.append("cursor:" + cursor + ";");
		return attributeString.toString();
	}
	public String setLevelBorderStyle(){
		StringBuffer attributeString = new StringBuffer();
		setStylePosition("absolute");
		setStyleVisibility("visible");
		attributeString.append("position:" + position + ";");
		attributeString.append("visibility:" + visibility + ";");
		attributeString.append("background-color:" + getStyleBorderColor() + ";");
		attributeString.append("layer-background-color:" + getStyleLayerBorderColor() + ";");
		return attributeString.toString();
	}
	public void addStyles(Page page){
		page.setStyleDefinition(".clCMAbs", commonStyleDefinitions() + defaultStyle());
		page.setStyleDefinition(".clBar", commonStyleDefinitions() + setBarStyle());
		page.setStyleDefinition(".clLevel1", commonLevelStyle() + setLevelStyle());
		page.setStyleDefinition(".clLevel1over", commonLevelStyle() + setLevelOverStyle());
		page.setStyleDefinition(".clLevel1border", setLevelBorderStyle());
	}
	/**
	 * 
	 * @param map
	 * @return a string of the attributes for the <code> DynamicJSMenu </code>
	 * on the form menuName.attributeName=attributeValue
	 */
	public String _getAttributeString(Map map){
		StringBuffer returnString = new StringBuffer();
		String Attribute ="";
		String attributeValue = "";
		Map.Entry mapEntry;

		if (map != null) {
		  Iterator i = map.entrySet().iterator();
		  while (i.hasNext()) {
		mapEntry = (Map.Entry) i.next();
		Attribute = (String) mapEntry.getKey();
//		returnString.append(" ");
		returnString.append(getName()); //added for javascript output
		returnString.append("."); //added for javascript output
		returnString.append(Attribute);
		attributeValue = (String) mapEntry.getValue();
		if(!attributeValue.equals(slash)){
		  returnString.append("=");  //quotes removed, added in setAttribute()
		  returnString.append(attributeValue);
//		  returnString.append("\""); quotes are added in setAttribute()
		  returnString.append("\n"); //added for readable output
		}
		returnString.append("");
		  }
		}
		return returnString.toString();
	  }
	  public String _getAttributeString() {
		return _getAttributeString(this.attributes);
	  }
	/**
	 * 
	 * @param fileName - the file containing the javascript
	 * @param iwc - the IWContext object
	 * @return String - the url of the javascript source code
	 */
	public String scriptSource(String fileName, IWContext iwc){
		String url = iwc.getIWMainApplication().getCoreBundle().getResourcesURL();
		url = url + "/" + fileName;
		return url;
	}	
	public void main(IWContext iwc) throws Exception{
		// get the current page to print the coolMenu4.js and the coolStyle.css src to it
		parentPage = this.getParentPage();
		coolMenuSrc = scriptSource(coolMenuScript, iwc);
//		menuStyleSrc = iwc.getApplication().getTranslatedURIWithContext("/idegaweb/style/" + menuStyleScript);
		parentPage.addJavascriptURL(coolMenuSrc);
//		parentPage.addStyleSheetURL(menuStyleSrc);
		addStyles(parentPage);	
		this.addLevel("0");
		this.addLevel("1");		

	}
	public void print(IWContext main) throws Exception {
		if (getLanguage().equals("HTML")){
			String menuName = "oCMenu";
			
			print("<script>");
			print(menuName + "=new makeCM(\"" + menuName + "\")\n" );
			print(_getAttributeString());
								
			Map levelMap = levelElement.attributes;
			Map.Entry mapEntry;
				
            Iterator iter = theMenuLevelElements.iterator();
			while (iter.hasNext()) {
				levelElement = (MenuLevelElement) iter.next();
				print(menuName + ".level[" + levelElement.getLevel() + "]=new cm_makeLevel()\n");

				Iterator levelIter = levelMap.entrySet().iterator();
				while (levelIter.hasNext()) {
					mapEntry = (Map.Entry) levelIter.next();
					print(menuName + ".level[" + levelElement.getLevel() + "]." + (String) mapEntry.getKey() + "=" + (String) mapEntry.getValue() + "\n");
				}
			}
			MenuElement root1 = new MenuElement();
			root1.setMenuName("oCMenu");
			root1.setName("root1");
			root1.setText("this is the root1");
			root1.setLink("http://bla.bla");
			root1.addChild("child1", "root1", "this is child1", "http://bla.bla");
			root1.addChild("child2", "root1", "this is child2", "http://bla.bla");
			print(root1.output() + "\n");
			
			for(Iterator childIter = root1.getChildren().iterator(); childIter.hasNext();){
				MenuElement child = (MenuElement) childIter.next();
				child.setMenuName("oCMenu");
				print(child.output() + "\n");
			}
			print(menuName + ".construct()\n\n");
			print("</script>");			
		}
		else if (getLanguage().equals("WML")){
			println("");
		}
	}
}