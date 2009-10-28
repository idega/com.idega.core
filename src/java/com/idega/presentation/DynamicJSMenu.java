package com.idega.presentation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author birna
 * 
 * Creates a dynamic JavaScript menu using external javaScript; coolmenus4.js
 * see <a
 * href="http://www.dhtmlcentral.com/projects/coolmenus/">http://www.dhtmlcentral.com/projects/coolmenus/</a>
 * 
 */
public class DynamicJSMenu extends PresentationObject {

	protected class LinkMenu {

		public List getTextList() {
			return this._textList;
		}

		public List getUrlList() {
			return this._urlList;
		}

		private List _textList = new ArrayList();
		private List _urlList = new ArrayList();
	}

	String _menuObjectName = "oCMenu";
	/** a list of LinkMenu */
	private List _menus = new ArrayList();
	private String backgroundColor = "";
	private String barBackgroundColor = "";
	private String barBorderClass = " ";
	private int barBorderX = 0;
	private int barBorderY = 0;
	private String barClass = " ";
	private String barHeight = "menu";
	private String barLayerBackgroundColor = "";
	private String barWidth = "menu";
	private String barX = "menu";
	private String barY = "menu";
	private String borderColor = "";
	private String borderLayerColor = "";
	private String coolMenuScript = "coolmenus4.js";
	private String coolMenuSrc;
	private String cursor = "";
	private String divMenuName = "div" + this._menuObjectName;
	private String fillImg = " ";
	private String fontOverStyle;
	private String fontStyle;
	private int fromLeft = 20;
	private int fromTop = 0;
	private String height = "";
	private String layerBackgroundColor = "";
	private String layerMenuName = "layer" + this._menuObjectName;
	private String left = "";
	private MenuLevelElement levelElement;
	private String[] levelProps = { "width", "height", "regClass", "overClass", "borderX", "borderY", "borderClass", "offsetX", "offsetY", "rows", "arrow", "arrowWidth", "arrowHeight", "align" };
	private String[] levelValues = { "110", "25", "\"clLevel0\"", "\"clLevel0over\"", "1", "1", "\"clLevel0border\"", "0", "0", "0", "0", "0", "0", "\"bottom\"" };
	private String menuPlacement = " ";
	private String[] menuProps = { "frames", "pxBetween", "rows", "menuPlacement", "offlineRoot", "onlineRoot", "resizeCheck", "wait", "zIndex", "useBar", "barWidth", "barHeight", "barClass", "barX", "barY", "barBorderX", "barBorderY", "barBorderClass" };
	private String menuStyleScript = "coolStyle.css";
	private String menuStyleSrc;
	private String[] menuValues = { "0", "30", "1", "\"center\"", "\"file:///idegaweb/daddara/\"", "", "1", "1000", "0", "1", "\"100%\"", "\"menu\"", "\"clBar\"", "0", "0", "0", "0", "\"\"" };
	private String offlineRoot = " ";
	private String onlineRoot = " ";
	private String overBackgroundColor = "";
	private String overLayerBackgroundColor = "";
	private String padding = "";
	private Page parentPage;
	private String position = "";
	private int pxBetween = 0;
	private int resizeCheck = 1;
	private int rows = 1;
	private List theMenuLevelElements;
	private String top = "";
	private int useBar = 1;
	private String visibility = "";
	private int wait = 1000;
	private String width = "";
	private int zIndex = 0;

	/**
	 * the default constructor
	 * 
	 */
	public DynamicJSMenu() {
		this("undefined");
	}

	/**
	 * constructor to creating a <code> DynamicJSMenu </code> object with a
	 * specific name.
	 * 
	 * @param name
	 */
	public DynamicJSMenu(String name) {
		setName(name);
		this.theMenuLevelElements = new ArrayList();
		initialMenuValues();
	}

	public String _getAttributeString() {
		return _getAttributeString(this.attributes);
	}

	/**
	 * 
	 * @param map
	 * @return a string of the attributes for the <code> DynamicJSMenu </code> on
	 *         the form menuName.attributeName=attributeValue
	 */
	public String _getAttributeString(Map map) {
		StringBuffer returnString = new StringBuffer();
		String Attribute = "";
		String attributeValue = "";
		Map.Entry mapEntry;

		if (map != null) {
			Iterator i = map.entrySet().iterator();
			while (i.hasNext()) {
				mapEntry = (Map.Entry) i.next();
				Attribute = (String) mapEntry.getKey();
				// returnString.append(" ");
				returnString.append(getName()); // added for javascript output
				returnString.append("."); // added for javascript output
				returnString.append(Attribute);
				attributeValue = (String) mapEntry.getValue();
				if (!attributeValue.equals(slash)) {
					returnString.append("="); // quotes removed, added in setAttribute()
					returnString.append(attributeValue);
					// returnString.append("\""); quotes are added in setAttribute()
					returnString.append("\n"); // added for readable output
				}
				returnString.append("");
			}
		}
		return returnString.toString();
	}

	/**
	 * adds a level to the <code> DynamicJSMenu </code>
	 * 
	 * @param level
	 */
	public void addLevel(String level) {
		this.levelElement = new MenuLevelElement(level);
		// lElement.setLevel(level);
		this.levelElement.setRegClass("clLevel" + level);
		this.levelElement.setOverClass("clLevel" + level + "over");
		this.levelElement.setBorderClass("clLevel" + level + "border");
		this.theMenuLevelElements.add(this.levelElement);
	}

	/**
	 * Adds a link to the i=th menu. Before a link is added to the i-th menu, make
	 * sure to add links to all preceeding menus, i.e. zeroth to (i-1)th.
	 * 
	 * @param i
	 *          the menu to add the link to, starting at zero.
	 * @param text
	 *          The text in the link
	 * @param url
	 *          The url for the link
	 */
	public void addLinkToMenu(int i, String text, String url) {
		LinkMenu menu = null;
		try {
			menu = (LinkMenu) this._menus.get(i);
		}
		catch (Exception e) {
			// first link being added to menu
		}
		if (menu == null) {
			menu = new LinkMenu();
			this._menus.add(menu);
		}

		System.out.println("Adding link \"" + text + "\" to menu #" + i);

		menu.getTextList().add(text);
		menu.getUrlList().add(url);
	}

	public void addStyles(Page page) {
		page.setStyleDefinition(".clCMAbs", commonStyleDefinitions() + defaultStyle());
		page.setStyleDefinition(".clBar", commonStyleDefinitions() + setBarStyle());
		page.setStyleDefinition(".clLevel1", commonLevelStyle() + setLevelStyle());
		page.setStyleDefinition(".clLevel1over", commonLevelStyle() + setLevelOverStyle());
		page.setStyleDefinition(".clLevel1border", setLevelBorderStyle());
	}

	public String commonLevelStyle() {
		StringBuffer attributeString = new StringBuffer();
		setStylePadding("2px");
		setStylePosition("absolute");
		attributeString.append("padding:" + this.padding + ";");
		attributeString.append("position:" + this.position + ";");
		return attributeString.toString();
	}

	public String commonStyleDefinitions() {
		StringBuffer attributeString = new StringBuffer();
		setStylePosition("absolute");
		setStyleVisibility("hidden");
		attributeString.append("position:" + this.position + ";");
		attributeString.append("visibility:" + this.visibility + ";");
		return attributeString.toString();
	}

	public String defaultStyle() {
		StringBuffer attributeString = new StringBuffer();
		setStyleLeft("0");
		setStyleTop("0");
		attributeString.append("left:" + this.left + ";");
		attributeString.append("top:" + this.top + ";");
		return attributeString.toString();
	}

	public String getBarStyleBackgroundColor() {
		this.barLayerBackgroundColor = this.barBackgroundColor;
		return this.barBackgroundColor;
	}

	public String getBarStyleLayerBackgroundColor() {
		return this.barLayerBackgroundColor;
	}

	public String getOverStyleBackgroundColor() {
		this.overLayerBackgroundColor = this.overBackgroundColor;
		return this.overBackgroundColor;
	}

	public String getOverStyleLayerBackgroundColor() {
		return this.overLayerBackgroundColor;
	}

	public String getStyleBackgroundColor() {
		return this.backgroundColor;
	}

	public String getStyleBorderColor() {
		return this.borderColor;
	}

	public String getStyleCursor() {
		return this.cursor;
	}

	public String getStyleFontOverStyle() {
		return this.fontOverStyle;
	}

	public String getStyleFontStyle() {
		return this.fontStyle;
	}

	public String getStyleHeight() {
		return this.height;
	}

	public String getStyleLayerBackgroundColor() {
		this.layerBackgroundColor = this.backgroundColor;
		return this.layerBackgroundColor;
	}

	public String getStyleLayerBorderColor() {
		this.borderLayerColor = this.borderColor;
		return this.borderLayerColor;
	}

	public String getStyleLeft() {
		return this.left;
	}

	public String getStylePadding() {
		return this.padding;
	}

	public String getStylePosition() {
		return this.position;
	}

	public String getStyleTop() {
		return this.top;
	}

	public String getStyleVisibility() {
		return this.visibility;
	}

	public String getStyleWidth() {
		return this.width;
	}

	/**
	 * sets the initial values for the <code> DynamicJSMenu </code>
	 * 
	 */
	public void initialMenuValues() {
		setBarBorderClass(this.barBorderClass);
		setBarBorderX(this.barBorderX);
		setBarBorderY(this.barBorderY);
		setBarClass(this.barClass);
		setBarHeight(this.barHeight);
		setBarWidth(this.barWidth);
		setBarX(this.barX);
		setBarY(this.barY);
		setFillImg(this.fillImg);
		setFromLeft(this.fromLeft);
		setFromTop(this.fromTop);
		setMenuPlacement(this.menuPlacement);
		setOfflineRoot(this.offlineRoot);
		setOnlineRoot(this.onlineRoot);
		setPxBetween(this.pxBetween);
		setResizeCheck(this.resizeCheck);
		setRows(this.rows);
		setUseBar(this.useBar);
		setZIndex(this.zIndex);
		setWait(this.wait);
	}

	@Override
	public void main(IWContext iwc) throws Exception {
		// get the current page to print the coolMenu4.js and the coolStyle.css src
		// to it
		this.parentPage = this.getParentPage();
		this.coolMenuSrc = scriptSource(this.coolMenuScript, iwc);
		this.menuStyleSrc = scriptSource(this.menuStyleScript, iwc);
		this.parentPage.addJavascriptURL(this.coolMenuSrc);
		this.parentPage.addStyleSheetURL(this.menuStyleSrc);
		addStyles(this.parentPage);

	}

	@Override
	public void print(IWContext main) throws Exception {
		if (getMarkupLanguage().equals("HTML")) {

			print(getTableWithLayer(main));

			print("<script type=\"text/javascript\">\n");
			print(getFindPositionJavaScript());
			print("pos = findPos() \n");

			print(this._menuObjectName + "=new makeCM(\"" + this._menuObjectName + "\")\n");

			print(this._menuObjectName + ".fromLeft=pos[0]\n");
			print(this._menuObjectName + ".fromTop=pos[1]\n");

			if (this.theMenuLevelElements != null && this.theMenuLevelElements.size() > 0) {
				print(_getAttributeString());
				Map levelMap = this.levelElement.attributes;
				Map.Entry mapEntry;

				Iterator iter = this.theMenuLevelElements.iterator();
				while (iter.hasNext()) {
					this.levelElement = (MenuLevelElement) iter.next();
					print(this._menuObjectName + ".level[" + this.levelElement.getLevel() + "]=new cm_makeLevel()\n");

					Iterator levelIter = levelMap.entrySet().iterator();
					while (levelIter.hasNext()) {
						mapEntry = (Map.Entry) levelIter.next();
						print(this._menuObjectName + ".level[" + this.levelElement.getLevel() + "]." + (String) mapEntry.getKey() + "=" + (String) mapEntry.getValue() + "\n");
					}
				}
			}
			else {
				printMenuProperties(this._menuObjectName);
				int count = this._menus.size();
				for (int i = 0; i < count; i++) {
					LinkMenu menu = (LinkMenu) this._menus.get(i);
					printMenu(menu, i);
				}
			}

			print(this._menuObjectName + ".construct()\n\n");
			print("</script>");
		}
		else if (getMarkupLanguage().equals("WML")) {
			println("");
		}
	}

	/**
	 * 
	 * @param fileName -
	 *          the file containing the javascript
	 * @param iwc -
	 *          the IWContext object
	 * @return String - the url of the javascript source code
	 */
	public String scriptSource(String fileName, IWContext iwc) {
		String url = iwc.getIWMainApplication().getCoreBundle().getResourcesURL();
		url = url + "/" + fileName;
		return url;
	}

	/**
	 * 
	 * @param barBordCl
	 */
	public void setBarBorderClass(String barBordCl) {
		setMarkupAttribute("barBorderClass", "\"" + barBordCl + "\"");
	}

	/**
	 * 
	 * @param barBordX
	 */
	public void setBarBorderX(int barBordX) {
		setMarkupAttribute("barBorderX", barBordX);
	}

	/**
	 * 
	 * @param barBordY
	 */
	public void setBarBorderY(int barBordY) {
		setMarkupAttribute("barBorderY", barBordY);
	}

	/**
	 * 
	 * @param barCl
	 */
	public void setBarClass(String barCl) {
		setMarkupAttribute("barClass", "\"" + barCl + "\"");
	}

	/**
	 * 
	 * @param barH
	 */
	public void setBarHeight(String barH) {
		setMarkupAttribute("barHeight", "\"" + barH + "\"");
	}

	public String setBarStyle() {
		StringBuffer attributeString = new StringBuffer();
		setStyleWidth("10");
		setStyleHeight("10");
		attributeString.append("width:" + this.width + ";");
		attributeString.append("height:" + this.height + ";");
		attributeString.append("background-color:" + getBarStyleBackgroundColor() + ";");
		attributeString.append("layer-background-color:" + getBarStyleLayerBackgroundColor() + ";");
		return attributeString.toString();
	}

	public void setBarStyleBackgroundColor(String barBCol) {
		this.barBackgroundColor = barBCol;
	}

	public void setBarStyleLayerBackgroundColor(String barLBCol) {
		this.barLayerBackgroundColor = barLBCol;
	}

	/**
	 * 
	 * @param barW
	 */
	public void setBarWidth(String barW) {
		setMarkupAttribute("barWidth", "\"" + barW + "\"");
	}

	/**
	 * 
	 * @param bX
	 */
	public void setBarX(String bX) {
		setMarkupAttribute("barX", "\"" + bX + "\"");
	}

	/**
	 * 
	 * @param bY
	 */
	public void setBarY(String bY) {
		setMarkupAttribute("barY", "\"" + bY + "\"");
	}

	/**
	 * 
	 * @param fImg
	 */
	public void setFillImg(String fImg) {
		setMarkupAttribute("fillImg", "\"" + fImg + "\"");
	}

	/**
	 * 
	 * @param f
	 */
	public void setFrames(int f) {
		setMarkupAttribute("frames", f);
	}

	/**
	 * sets the position the menu is located at from the left of the
	 * browserwindow, x-coordinate
	 * 
	 * @param fromL
	 */
	public void setFromLeft(int fromL) {
		setMarkupAttribute("fromLeft", fromL);
	}

	/**
	 * sets the position the menu is located at from the top of the browserwindow,
	 * y-coordinates
	 * 
	 * @param fromT
	 */
	public void setFromTop(int fromT) {
		setMarkupAttribute("fromTop", fromT);
	}

	public String setLevelBorderStyle() {
		StringBuffer attributeString = new StringBuffer();
		setStylePosition("absolute");
		setStyleVisibility("visible");
		attributeString.append("position:" + this.position + ";");
		attributeString.append("visibility:" + this.visibility + ";");
		attributeString.append("background-color:" + getStyleBorderColor() + ";");
		attributeString.append("layer-background-color:" + getStyleLayerBorderColor() + ";");
		return attributeString.toString();
	}

	public String setLevelOverStyle() {
		StringBuffer attributeString = new StringBuffer();
		setStyleCursor("pointer");
		attributeString.append("background-color:" + getOverStyleBackgroundColor() + ";");
		attributeString.append("layer-background-color:" + getOverStyleLayerBackgroundColor() + ";");
		// attributeString.append("color:" + getOverStyleColor() + ";");
		attributeString.append(getStyleFontOverStyle());
		attributeString.append("cursor:" + this.cursor + ";");
		return attributeString.toString();
	}

	public String setLevelStyle() {
		StringBuffer attributeString = new StringBuffer();
		attributeString.append("background-color:" + getStyleBackgroundColor() + ";");
		attributeString.append("layer-background-color:" + getStyleLayerBackgroundColor() + ";");
		attributeString.append(getStyleFontStyle());
		// attributeString.append("color:" + getStyleColor() + ";");
		return attributeString.toString();
	}

	/**
	 * 
	 * @param menuPlace
	 */
	public void setMenuPlacement(String menuPlace) {
		setMarkupAttribute("menuPlacement", "\"" + menuPlace + "\"");
	}

	/**
	 * 
	 * @param offlineR
	 */
	public void setOfflineRoot(String offlineR) {
		setMarkupAttribute("offlineRoot", "\"" + offlineR + "\"");
	}

	/**
	 * 
	 * @param onlineR
	 */
	public void setOnlineRoot(String onlineR) {
		setMarkupAttribute("onlineRoot", "\"" + onlineR + "\"");
	}

	public void setOverStyleBackgroundColor(String overBCol) {
		this.overBackgroundColor = overBCol;
	}

	public void setOverStyleLayerBackgroundColor(String overLBCol) {
		this.overLayerBackgroundColor = overLBCol;
	}

	/**
	 * sets the pixles between <code> MenuElements </code>
	 * 
	 * @param pxB
	 */
	public void setPxBetween(int pxB) {
		setMarkupAttribute("pxBetween", pxB);
	}

	/**
	 * 
	 * @param resizeCh
	 */
	public void setResizeCheck(int resizeCh) {
		setMarkupAttribute("resizeCheck", resizeCh);
	}

	/**
	 * sets if the menu should appear horizontal (r=0) or vertical (r=0)
	 * 
	 * @param r
	 */
	public void setRows(int r) {
		setMarkupAttribute("rows", r);
	}

	public void setStyleBackgroundColor(String backgrCol) {
		this.backgroundColor = backgrCol;
	}

	public void setStyleBorderColor(String bColor) {
		this.borderColor = bColor;
	}

	public void setStyleCursor(String curs) {
		this.cursor = curs;
	}

	public void setStyleFontOverStyle(String style) {
		this.fontOverStyle = style;
	}

	public void setStyleFontStyle(String style) {
		this.fontStyle = style;
	}

	public void setStyleHeight(String h) {
		this.height = h;
	}

	public void setStyleLayerBackgroundColor(String lBackgrCol) {
		this.layerBackgroundColor = lBackgrCol;
	}

	public void setStyleLayerBorderColor(String blColor) {
		this.borderLayerColor = blColor;
	}

	public void setStyleLeft(String l) {
		// styleObject.setAttribute("left", l);
		this.left = l;
	}

	public void setStylePadding(String pad) {
		this.padding = pad;
	}

	public void setStylePosition(String pos) {
		// styleObject.setAttribute("position", pos);
		this.position = pos;
	}

	public void setStyleTop(String t) {
		// setAttribute("top", t);
		this.top = t;
	}

	public void setStyleVisibility(String vis) {
		// styleObject.setAttribute("visibility", vis);
		this.visibility = vis;
	}

	public void setStyleWidth(String w) {
		this.width = w;
	}

	/**
	 * 
	 * @param useB
	 */
	public void setUseBar(int useB) {
		setMarkupAttribute("useBar", useB);
	}

	/**
	 * 
	 * @param w
	 */
	public void setWait(int w) {
		setMarkupAttribute("wait", w);
	}

	/**
	 * 
	 * @param zIn
	 */
	public void setZIndex(int zIn) {
		setMarkupAttribute("zIndex", zIn);
	}

	/**
	 * @return
	 */
	private String getFindPositionJavaScript() {
		StringBuffer script = new StringBuffer();

		script.append("//Extra code to find position:\n").append("function findPos(){ \n").append("if(bw.ns4){   //Netscape 4 \n").append("x = document.layers." + this.layerMenuName + ".pageX \n").append("y = document.layers." + this.layerMenuName + ".pageY \n").append("}else{ //other browsers \n").append("x=0; y=0; var el,temp \n").append("el = bw.ie4?document.all[\"" + this.divMenuName + "\"]:document.getElementById(\"" + this.divMenuName + "\"); \n").append("if(el.offsetParent){ \n").append("temp = el \n").append("while(temp.offsetParent){ //Looping parent elements to get the offset of them as well \n").append("temp=temp.offsetParent;  \n").append("x+=temp.offsetLeft \n").append("y+=temp.offsetTop; \n").append("} \n").append("} \n").append("x+=el.offsetLeft \n").append("y+=el.offsetTop \n").append("} \n").append("//Returning the x and y as an array \n").append("return [x,y] \n").append("} \n");

		return script.toString();
	}

	/**
	 * This table + layer + image hack is needed to position the layer within
	 * tables
	 * 
	 * @return
	 */
	private String getTableWithLayer(IWContext iwc) {
		StringBuffer buf = new StringBuffer();

		buf.append("<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\"><tr><td>\n").append("<!--This is a positioning hack for DynamicJSMenu-->\n").append("<ilayer id=\"" + this.layerMenuName + "\"><div id=\"" + this.divMenuName + "\">\n").append("<img src=\"").append(Table.getTransparentCell(iwc).getURL()).append("\" width=\"100%\" height=\"20\" alt=\"\" border=\"0\">\n").append("</div></ilayer>\n").append("<!-- END -->\n").append("</td></tr></table>");

		return buf.toString();

	}

	private void printMenu(LinkMenu menu, int id) {
		List texts = menu.getTextList();
		List urls = menu.getUrlList();
		int count = texts.size();
		String topId = "top" + id;
		String subIdPrefix = "sub" + id + "_";
		for (int i = 0; i < count; i++) {
			String menuId = i == 0 ? topId : (subIdPrefix + i);
			String parentMenuId = i == 0 ? "" : topId;
			System.out.println("Printing link \"" + texts.get(i) + "\" to menu \"" + topId + "\"");
			print(this._menuObjectName + ".makeMenu('" + menuId + "','" + parentMenuId + "','" + texts.get(i) + "','" + urls.get(i) + "'" + (i == 0 ? ", ''" : "") + ")\n");
		}
	}

	private void printMenuProperties(String menuName) {
		for (int i = 0; i < this.menuProps.length; i++) {
			print(menuName + "." + this.menuProps[i] + "=" + this.menuValues[i] + "\n");
		}

		// print(menuName + ".level[0]=new
		// cm_makeLevel(180,22,\"l1\",\"l1over\",0,1,\"clB\",0,\"right\",0,0,\"/images/arrow_closed.gif\",15,11)\n");
		print(menuName + ".level[0]=new cm_makeLevel()\n");
		for (int j = 0; j < this.levelProps.length; j++) {
			print(menuName + ".level[0]." + this.levelProps[j] + "=" + this.levelValues[j] + "\n");
		}
		print(menuName + ".level[1]=new cm_makeLevel()\n");
	}

}