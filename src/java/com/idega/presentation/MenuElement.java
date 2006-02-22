package com.idega.presentation;

import java.util.ArrayList;
import java.util.List;
/**
 * A class that constructs a <code>MenuElement</code> of the <code>DynamicJSMenu</code>
 * @author birna
 * 
 */
public class MenuElement extends PresentationObject {
	private String menuName = "";
	private String name = "";
	private String parent = "";
	private String text = "";
	private String link = "";
	private String target = "";
	private String width = "";
	private String height = "";
	private String regImg = "";
	private String overImg = "";
	private String regClass = "";
	private String overClass = "";
	private String align = "";
	private int rows = 0;
	private String noLink = "";
	private String onClick = "";
	private String onMouseOver = "";
	private String onMouseOut = "";
	private ArrayList children = new ArrayList();
	/**
	 * The default constructor
	 *
	 */
	public MenuElement() {
		this("untitled");
	}
	/**
	 * 
	 * @param name
	 */
	public MenuElement(String name){
		setMenuName(menuName);
		setName(name); //name is used as an id for the MenuElement
		setParent(parent);
		setText(text);
		setLink(link);
		setTarget(target);
		setWidth(width);
		setHeight(height);
		setRegImg(regImg);
		setOverImg(overImg);
		setAlign(align);
		setRows(rows);
		setNoLink(noLink);
		setOnClick(onClick);
		setOnMouseOver(onMouseOver);
		setOnMouseOut(onMouseOut);
	}
	/**
	 * can be used to create a rootNode
	 * the rootNode's name is set and a boolean variable <code>isRoot</code> is set to true
	 * @param rootNode
	 * @param rootName
	 */
	public void createRootNode(MenuElement rootNode, String rootName){
		rootNode.setName(rootName);
	}
	/**
	 * adds a child to the ArrayList containing the children of the 
	 * specific root node
	 * @param childNode
	 * @param childName
	 */
	public void addChild(String childName, String parentName){
		MenuElement child = new MenuElement();
		child.setParent(parentName);
		child.setName(childName);
		children.add(child);
	}
	/**
	 * adds a child to the ArrayList containing the children of the
	 * specific root node
	 * @param childName
	 * @param parentName
	 * @param tx
	 * @param url
	 */
	public void addChild(String childName, String parentName, String tx, String url){
		MenuElement child = new MenuElement();
		child.setName(childName);
		child.setParent(parentName);
		child.setText(tx);
		child.setLink(url);
		children.add(child);
	}
	/**
	 * 
	 * @return Arraylist which contains the children of a node
	 */
	public List getChildren(){
		return this.children;
	}
	/**
	 * 
	 * @param mn
	 */
	public void setMenuName(String mn){
		menuName = mn;
	}
	/**
	 * @param n - sets the name of the MenuElement
	 */
	public void setName(String n){
		name = n;
	}
	/**
	 * 
	 * @param par - sets the parent of a MenuElement
	 */
	public void setParent(String par){
		parent = par;
	}
	/**
	 * 
	 * @param txt
	 */
	public void setText(String txt){
		text = txt;
	}
	/**
	 * 
	 * @param lnk
	 */
	public void setLink(String lnk){
		link = lnk;
	}
	/**
	 * 
	 * @param targ
	 */
	public void setTarget(String targ){
		target = targ;		
	}
	/**
	 * @param w
	 */
	public void setWidth(String w){
		width = w;
	}
	/**
	 * @param h
	 */
	public void setHeight(String h){
		height = h;
	}
	/**
	 * 
	 * @param rImg
	 */
	public void setRegImg(String rImg){
		regImg = rImg;
	}
	/**
	 * 
	 * @param oImg
	 */
	public void setOverImg(String oImg){
		overImg = oImg;
	}
	/**
	 * 
	 * @param rClass
	 */
	public void setRegClass (String rClass){
		regClass = rClass;
	}
	/**
	 * 
	 * @param oClass
	 */
	public void setOverClass(String oClass){
		overClass = oClass;
	}
	/**
	 * 
	 * @param aln
	 */
	public void setAlign(String aln){
		align = aln;
	}
	/**
	 * 
	 * @param r
	 */
	public void setRows(int r){
		rows = r;
	}
	/**
	 * 
	 * @param nLink
	 */
	public void setNoLink(String nLink){
		noLink = nLink;
	}
	/**
	 * 
	 * @param onCl
	 */
	public void setOnClick(String onCl){
		onClick = onCl;
	}
	/**
	 * 
	 * @param onMOver
	 */
	public void setOnMouseOver(String onMOver){
		onMouseOver = onMOver;
	}
	/**
	 * 
	 * @param onMOut
	 */
	public void setOnMouseOut(String onMOut){
		onMouseOut = onMOut;
	}
	/**
	 * 
	 * @return
	 */
	public String getMenuName(){
		return menuName;
	}
	/**
	 * 
	 */
	public String getName(){
		return name;
	}
	/**
	 * 
	 * @return
	 */
	public String getParentString(){
		return parent;
	}
	/**
	 * 
	 * @return
	 */
	public String getText(){
		return text;
	}
	/**
	 * 
	 * @return
	 */
	public String getLink(){
		return link;
	}
	/**
	 * 
	 * @return
	 */
	public String getTarget(){
		return target;
	}
	/**
	 * @return
	 */
	public String getWidth(){
		return width;
	}
	/**
	 * @return
	 */
	public String getHeight(){
		return height;
	}
	/**
	 * 
	 * @return
	 */
	public String getRegImg(){
		return regImg;
	}
	/**
	 * 
	 * @return
	 */
	public String getOverImg(){
		return overImg;
	}
	/**
	 * 
	 * @return
	 */
	public String getRegClass(){
		return regClass;
	}
	/**
	 * 
	 * @return
	 */
	public String getOverClass(){
		return overClass;
	}
	/**
	 * 
	 * @return
	 */
	public String getAlign(){
		return align;
	}
	/**
	 * 
	 * @return
	 */
	public int getRows(){
		return rows;
	}
	/**
	 * 
	 * @return
	 */
	public String getNoLink(){
		return noLink;
	}
	/**
	 * 
	 * @return
	 */
	public String getOnClick(){
		return onClick;
	}
	/**
	 * 
	 * @return
	 */
	public String getOnMouseOver(){
		return onMouseOver;
	}
	/**
	 * 
	 * @return
	 */
	public String getOnMouseOut(){
		return onMouseOut;
	}
	/**
	 * 
	 * @return - a string of the form:
	 * menuName.makeMenu('name', 'parent_name', 'text', 'link', 'target', 'width', 'height', 'regImage', 'overImage', 'regClass', 'overClass' , 'align', 'rows', 'nolink', 'onclick', 'onmouseover', 'onmouseout')
	 *  
	 */
	public String output(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.getMenuName());
		buffer.append(".makeMenu(");
		buffer.append("'" + this.getName() + "',");
		buffer.append("'" + this.getParentString() + "',");
		buffer.append("'" + this.getText() + "',");
		buffer.append("'" + this.getLink() + "',");
		buffer.append("'" + this.getTarget() + "',");
		buffer.append("'" + this.getWidth() + "',");
		buffer.append("'" + this.getHeight() + "',");
		buffer.append("'" + this.getRegImg() + "',");
		buffer.append("'" + this.getOverImg() + "',");
		buffer.append("'" + this.getRegClass() + "',");
		buffer.append("'" + this.getOverClass() + "',");
		buffer.append("'" + this.getAlign() + "',");
		buffer.append("'" + this.getRows() + "',");
		buffer.append("'" + this.getNoLink() + "',");
		buffer.append("'" + this.getOnClick() + "',");
		buffer.append("'" + this.getOnMouseOver() + "',");
		buffer.append("'" + this.getOnMouseOut() + "'");
		buffer.append(")");
		
		return buffer.toString();
	}	
}
