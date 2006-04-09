package com.idega.presentation;

import java.util.ArrayList;
import java.util.Iterator;
/**
 * 
 * @author birna
 * consrtucts a Level for the <code>DynamicJSMenu</code> 
 */
public class MenuLevelElement extends PresentationObject {
	private int width = 110;
	private int height = 25;
	private String regClass = "";
	private String overClass = "";
	private int borderX = 1;
	private int borderY = 1;
	private String borderClass = "";
	private int offsetY = 0;
	private int offsetX = 0;
	private int rows = 0;
	private int arrow = 0;
	private int arrowWidth = 0;
	private int arrowHeight = 0;
	private String align = "bottom";
	private String menuLevel; // = "0";
	
	protected ArrayList theLevels = new ArrayList();
	/**
	 * The default constructor
	 *
	 */
	public MenuLevelElement(){
		this("undefined");
	}
	/**
	 * 
	 * @param level
	 */
	public MenuLevelElement(String level){
		setLevel(level);
		initialLevelValues();		
	}
//	public MenuLevelElement(String name, int level){
//		this(name, String.valueOf(level));
//	}
//	public MenuLevelElement(String name, String level){
//		setLevel(level);
//		initialLevelValues();
//	}
	/**
	 * sets the initial values for the <code>MenuLevelElement</code>
	 */
	public void initialLevelValues(){
		setWidth(this.width);
		setHeight(this.height);
		setRegClass(this.regClass);
		setOverClass(this.overClass);
		setBorderX(this.borderX);
		setBorderY(this.borderY);
		setBorderClass(this.borderClass);
		setOffsetX(this.offsetX);
		setOffsetY(this.offsetY);
		setRows(this.rows);
		setArrow(this.arrow);
		setArrowHeight(this.arrowHeight);
		setArrowWidth(this.arrowWidth);
		setAlign(this.align);
	}
//	public void addLevel(String l){
//		MenuLevelElement levelElement = new MenuLevelElement(l);
//		menuLevel = l;
//		levelElement.setLevel(l);
//		levelElement.setRegClass("clLevel" + l);
//		levelElement.setOverClass("clLevel" + l + "over");
//		levelElement.setBorderClass("clLevel" + l + "border");
//		theLevels.add(levelElement);
//	}
	/**
	 * sets the level
	 * @param l
	 */
	public void setLevel(String l){
		this.menuLevel = l;
//		setAttribute("level", l);
	}
	/**
	 * 
	 * @param l
	 */
	public void setLevel(int l){
		setLevel(Integer.toString(l));
	}
	/**
	 * 
	 * @return the level of the <code> MenuLevelElement </code>
	 */
	public String getLevel(){
		return this.menuLevel;
//		return getAttribute("level");
	}
	/**
	 * 
	 * @return an iterator containing the levels of the <code>MenuLevelElement </code>
	 */
	public Iterator getLevels(){
		return this.theLevels.iterator();
	}
	/**
	 * sets the width of the level
	 * @param w
	 */
	public void setWidth(int w){
		setMarkupAttribute("width", w);
	}
	/**
	 * sets the height of the level
	 * @param h
	 */
	public void setHeight(int h){
		setMarkupAttribute("height", h);
	}
	/**
	 * sets the regular style class for the level
	 * @param regCl
	 */
	public void setRegClass(String regCl){
//		regCl = "clLevel" + menuLevel;
		setMarkupAttribute("regClass", "\"" + regCl + "\"");
	}
	/**
	 * sets the mouseOver style class for the level
	 * @param overCl
	 */
	public void setOverClass(String overCl){
//		overCl = "clLevel" + menuLevel + "over";
		setMarkupAttribute("overClass", "\"" + overCl + "\"");
	}
	/**
	 * 
	 * @param bX
	 */
	public void setBorderX(int bX){
		setMarkupAttribute("borderX", bX);
	}
	/**
	 * 
	 * @param bY
	 */
	public void setBorderY(int bY){
		setMarkupAttribute("borderY", bY);
	}
	/**
	 * sets the border class for the level
	 * @param borderCl
	 */
	public void setBorderClass(String borderCl){
//		borderCl = "clLevel" + menuLevel + "border";
		setMarkupAttribute("borderClass", "\"" + borderCl + "\"");
	}
	/**
	 * sets the x-offset of the level - if the level should overlap the previous level
	 * @param offX
	 */
	public void setOffsetX(int offX){
		setMarkupAttribute("offsetX", offX);
	}
	/**
	 * sets the y-offset of the level - if the level should overlap the previous level
	 * @param offY
	 */
	public void setOffsetY(int offY){
		setMarkupAttribute("offsetY", offY);
	}
	/**
	 * sets if the level should be horizontal (r=0) or vertical (r=1)
	 * @param r
	 */
	public void setRows(int r){
		setMarkupAttribute("rows", r);
	}
	/**
	 * 
	 * @param a
	 */
	public void setArrow(int a){
		setMarkupAttribute("arrow", a);
	}
	/**
	 * 
	 * @param aWidth
	 */
	public void setArrowWidth(int aWidth){
		setMarkupAttribute("arrowWidth", aWidth);
	}
	/**
	 * 
	 * @param aHeight
	 */
	public void setArrowHeight(int aHeight){
		setMarkupAttribute("arrowHeight", aHeight);
	}
	/**
	 * 
	 * @param ali
	 */
	public void setAlign(String ali){
		setMarkupAttribute("align",  "\"" + ali + "\"");
	}
//	public String output(){
//		StringBuffer buffer = new StringBuffer();
//		
////		MenuLevelElement levelElement = new MenuLevelElement();
//		
//		Map levelMap = this.attributes;
//		Map.Entry mapEntry;
//
////		Iterator iter = theLevels.iterator();
////		while (iter.hasNext()) {
////			levelElement = (MenuLevelElement) iter.next();
//			buffer.append(menu.getName() + ".level[" + this.getLevel() + "]=new cm_makeLevel()\n");
//			buffer.append("\n now in output!!!");
////			Iterator levelIter = levelMap.entrySet().iterator();
////			while (levelIter.hasNext()) {
////				mapEntry = (Map.Entry) levelIter.next();
////				buffer.append(menu.getName() + ".level[" + this.getLevel() + "]." + (String) mapEntry.getKey() + "=" + (String) mapEntry.getValue() + "\n");
////			}
//		return buffer.toString();
////		}
//
//	}
}
