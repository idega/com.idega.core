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
		setMenuName(this.menuName);
		setName(name); //name is used as an id for the MenuElement
		setParent(this.parent);
		setText(this.text);
		setLink(this.link);
		setTarget(this.target);
		setWidth(this.width);
		setHeight(this.height);
		setRegImg(this.regImg);
		setOverImg(this.overImg);
		setAlign(this.align);
		setRows(this.rows);
		setNoLink(this.noLink);
		setOnClick(this.onClick);
		setOnMouseOver(this.onMouseOver);
		setOnMouseOut(this.onMouseOut);
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
		this.children.add(child);
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
		this.children.add(child);
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
		this.menuName = mn;
	}
	/**
	 * @param n - sets the name of the MenuElement
	 */
	public void setName(String n){
		this.name = n;
	}
	/**
	 * 
	 * @param par - sets the parent of a MenuElement
	 */
	public void setParent(String par){
		this.parent = par;
	}
	/**
	 * 
	 * @param txt
	 */
	public void setText(String txt){
		this.text = txt;
	}
	/**
	 * 
	 * @param lnk
	 */
	public void setLink(String lnk){
		this.link = lnk;
	}
	/**
	 * 
	 * @param targ
	 */
	public void setTarget(String targ){
		this.target = targ;		
	}
	/**
	 * @param w
	 */
	public void setWidth(String w){
		this.width = w;
	}
	/**
	 * @param h
	 */
	public void setHeight(String h){
		this.height = h;
	}
	/**
	 * 
	 * @param rImg
	 */
	public void setRegImg(String rImg){
		this.regImg = rImg;
	}
	/**
	 * 
	 * @param oImg
	 */
	public void setOverImg(String oImg){
		this.overImg = oImg;
	}
	/**
	 * 
	 * @param rClass
	 */
	public void setRegClass (String rClass){
		this.regClass = rClass;
	}
	/**
	 * 
	 * @param oClass
	 */
	public void setOverClass(String oClass){
		this.overClass = oClass;
	}
	/**
	 * 
	 * @param aln
	 */
	public void setAlign(String aln){
		this.align = aln;
	}
	/**
	 * 
	 * @param r
	 */
	public void setRows(int r){
		this.rows = r;
	}
	/**
	 * 
	 * @param nLink
	 */
	public void setNoLink(String nLink){
		this.noLink = nLink;
	}
	/**
	 * 
	 * @param onCl
	 */
	public void setOnClick(String onCl){
		this.onClick = onCl;
	}
	/**
	 * 
	 * @param onMOver
	 */
	public void setOnMouseOver(String onMOver){
		this.onMouseOver = onMOver;
	}
	/**
	 * 
	 * @param onMOut
	 */
	public void setOnMouseOut(String onMOut){
		this.onMouseOut = onMOut;
	}
	/**
	 * 
	 * @return
	 */
	public String getMenuName(){
		return this.menuName;
	}
	/**
	 * 
	 */
	public String getName(){
		return this.name;
	}
	/**
	 * 
	 * @return
	 */
	public String getParentString(){
		return this.parent;
	}
	/**
	 * 
	 * @return
	 */
	public String getText(){
		return this.text;
	}
	/**
	 * 
	 * @return
	 */
	public String getLink(){
		return this.link;
	}
	/**
	 * 
	 * @return
	 */
	public String getTarget(){
		return this.target;
	}
	/**
	 * @return
	 */
	public String getWidth(){
		return this.width;
	}
	/**
	 * @return
	 */
	public String getHeight(){
		return this.height;
	}
	/**
	 * 
	 * @return
	 */
	public String getRegImg(){
		return this.regImg;
	}
	/**
	 * 
	 * @return
	 */
	public String getOverImg(){
		return this.overImg;
	}
	/**
	 * 
	 * @return
	 */
	public String getRegClass(){
		return this.regClass;
	}
	/**
	 * 
	 * @return
	 */
	public String getOverClass(){
		return this.overClass;
	}
	/**
	 * 
	 * @return
	 */
	public String getAlign(){
		return this.align;
	}
	/**
	 * 
	 * @return
	 */
	public int getRows(){
		return this.rows;
	}
	/**
	 * 
	 * @return
	 */
	public String getNoLink(){
		return this.noLink;
	}
	/**
	 * 
	 * @return
	 */
	public String getOnClick(){
		return this.onClick;
	}
	/**
	 * 
	 * @return
	 */
	public String getOnMouseOver(){
		return this.onMouseOver;
	}
	/**
	 * 
	 * @return
	 */
	public String getOnMouseOut(){
		return this.onMouseOut;
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
