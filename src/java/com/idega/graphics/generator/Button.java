package com.idega.graphics.generator;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import com.idega.graphics.encoder.gif.Gif89Encoder;
import com.idega.util.FileUtil;
import com.idega.util.IWColor;
import com.idega.util.text.TextSoap;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class Button {

  private static Color defaultBorderColor = new Color(85,87,92);
  private static int defaultBorderSize = 1;
  private static Color defaultUnderColor = new Color(148,151,156);
  private static Color defaultOverColor = Color.white;
  private static Color defaultFillColor = new Color(201,203,206);
  private static Color defaultFontColor = Color.black;
  private static Color defaultHightlightColor = new Color(221,223,226);
  public static String BUTTON_UP = "_BUTTON_UP";
  public static String BUTTON_OVER = "_BUTTON_OVER";
  public static String BUTTON_DOWN = "_BUTTON_DOWN";

  protected Color underColor = defaultUnderColor;
  protected Color fillColor = defaultFillColor;
  protected Color overColor = defaultOverColor;
  protected Color borderColor = defaultBorderColor;
  protected Color fontColor = defaultFontColor;
  protected Color highlightColor = defaultHightlightColor;

  //Used for the transparancy
  protected Color backgroundColor=Color.cyan;

  protected int borderSize = defaultBorderSize;

  protected boolean drawBorder = true;
  protected int width = 54;
  protected int height = 15;
  int tWidth = 0;
  int tHeight = 0;

  protected int doubleBorder = (2*borderSize);
  protected int textXPos = 5;
  protected int textYPos = 10;
  protected int verticalPadding = 5;

  public String buttonUpName;
  public String buttonDownName;
  public String buttonOverName;

  private String text;
  private String name = null;
  private Font font;
  private BufferedImage image;
  private Graphics2D g;
  private boolean resize = false;
  private boolean onlyCreateUpState = false;

  public Button() {
    this("");
  }

  public Button(String text) {
    this.text = text;
  }

  public Button(String text, Font font) {
    this(text);
    this.font = font;
  }

  public Button(String text, int width, int height) {
    this(text);
    this.width = width;
    this.height = height;
  }

  public Button(String text, int width, int height, Color fillColor) {
    this(text,width,height);
    this.fillColor = fillColor;
    highlightColor = fillColor.brighter();
  }

  public Button(String text, Color fillColor) {
    this(text);
    this.fillColor = fillColor;
    highlightColor = fillColor.brighter();
  }


  /**
   * This method allows direct generating of buttons through a command line or cgi script<br/>
   * the allowed parameters in correct order are<br/>
   * 1:inputfile 2:fillcolor 3:highlightcolor 4:width 5:height 6:pathtofontfile 7:fontsize
   */
  public static void main(String[] args) {
    try{
      Vector tokenizers = FileUtil.getCommaSeperatedTokensFromLinesFromFile(args[0],"=");
      Button button = new Button();
      button.fillColor = IWColor.getAWTColorFromHex(args[1]);
      button.highlightColor = IWColor.getAWTColorFromHex(args[2]);
      File file = new File(args[5]);
      FileInputStream fis = new FileInputStream(file);
      Font font = Font.createFont(Font.TRUETYPE_FONT, fis);
      button.setFont(font.deriveFont(Float.parseFloat(args[6])));
      //button.setFont(font.deriveFont(10f));
      button.onlyCreateUpState(true);

      Iterator iter = tokenizers.iterator();
      while (iter.hasNext()) {
        StringTokenizer tokens = (StringTokenizer)iter.next();

        while(tokens.hasMoreTokens()){
          button.name = tokens.nextToken();
          button.text = tokens.nextToken().toUpperCase();
        }

        button.width = Integer.parseInt(args[3]);
        button.height = Integer.parseInt(args[4]);
        button.resize = false;
        button.generate();
      }


    }
    catch(Exception e){
      e.printStackTrace(System.err);
    }
    System.exit(0);
  }

	/**
	 * 
	 * @uml.property name="fontColor"
	 */
	public void setFontColor(Color color) {
		fontColor = color;
	}

	/**
	 * 
	 * @uml.property name="font"
	 */
	public void setFont(Font theFont) {
		font = theFont;
	}

	/**
	 * 
	 * @uml.property name="fillColor"
	 */
	public void setFillColor(Color color) {
		fillColor = color;
	}

	/**
	 * 
	 * @uml.property name="highlightColor"
	 */
	public void setHighlightColor(Color color) {
		highlightColor = color;
	}

	/**
	 * 
	 * @uml.property name="borderColor"
	 */
	public void setBorderColor(Color color) {
		borderColor = color;
	}

	/**
	 * 
	 * @uml.property name="overColor"
	 */
	public void setOverColor(Color color) {
		overColor = color;
	}

	/**
	 * 
	 * @uml.property name="underColor"
	 */
	public void setUnderColor(Color color) {
		underColor = color;
	}


  public void onlyCreateUpState(boolean onlyCreateUpState){
    this.onlyCreateUpState = onlyCreateUpState;
  }

  public String getUpName(){
    return buttonUpName;
  }

  public String getOverName(){
    return buttonOverName;
  }

  public String getDownName(){
    return buttonDownName;
  }

  public String getStaticButtonDownString(){
    return BUTTON_DOWN;
  }

  public String getStaticButtonUpString(){
    return BUTTON_UP;
  }

  public String getStaticButtonOverString(){
    return BUTTON_OVER;
  }

	/**
	 * 
	 * @uml.property name="width"
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * 
	 * @uml.property name="height"
	 */
	public int getHeight() {
		return height;
	}


/*
  public Color getHighlightColor(){
        highlightColor = color;
    new Color(fillColor.getRed()+20,fillColor.getGreen()+20,fillColor.getBlue()+20);

  }*/

  public void generate() {
    generate("");
  }

  public void generate(String folderPath) {
	
    image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

    g = image.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    if( font!= null ){
      g.setFont(font);
    }
    else System.out.println("FONT IS NULL");

    fitText(g);

    if( resize ){
      image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
      g = image.createGraphics();
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      if( font!= null ) g.setFont(font);
    }


    makeButton(g,text,image,folderPath,getStaticButtonUpString());

    if( !onlyCreateUpState ){
      makeButton(g,text,image,folderPath,getStaticButtonOverString());
      makeButton(g,text,image,folderPath,getStaticButtonDownString());
    }

  }

  public void fitText(Graphics2D g){

    FontMetrics fm = g.getFontMetrics(g.getFont());
    //System.out.println("Leading : "+fm.getLeading());
    /*System.out.println("string width : "+fm.stringWidth(text));
    System.out.println("string height : "+fm.getHeight());
    System.out.println("string ascend : "+fm.getAscent());*/


    tWidth = fm.stringWidth(text);
    tHeight = fm.getAscent();

    if( tWidth >= width ){
      width = tWidth+(2*verticalPadding);
      resize = true;
    }

    textXPos = (width-tWidth)/2;
    textYPos = ((height+tHeight)/2)-1;
  }

  public void makeButton(Graphics2D g, String text, Image image, String filename, String effect){
   // g.setStroke(new BasicStroke(0.5f));

   if( drawBorder){
    g.setColor(borderColor);
    g.fillRect(0,0,width,height);
   }

    if(effect==BUTTON_DOWN) g.setColor(underColor);
    else g.setColor(overColor);

    g.fillRect(borderSize,borderSize,width-borderSize-2,height-doubleBorder-1);

    if(effect==BUTTON_OVER) g.setColor(highlightColor);
    else g.setColor(fillColor);

    g.fillRect(doubleBorder,doubleBorder,width-doubleBorder-2,height-doubleBorder-2);

    if(effect==BUTTON_DOWN) g.setColor(overColor);
    else g.setColor(underColor);

    //g.setStroke(new BasicStroke(1f));
    g.drawLine(borderSize,height-doubleBorder,width-borderSize-1,height-doubleBorder);
    g.drawLine(width-borderSize-1,height-doubleBorder,width-borderSize-1,doubleBorder-1);

    //g.setStroke(new BasicStroke(2f));
    //g.drawLine(0,height,width,0);

    if( effect==BUTTON_DOWN ) textYPos++;
    g.setColor(fontColor);
    g.drawString(text,textXPos,textYPos);

    encode(image,filename,effect);

  }

  public void encode(Image image, String path, String effect){
   try {
      //GIFEncoder encode = new GIFEncoder(image);
      //net.jmge.gif.Gif89Encoder gif89aEncoder = new net.jmge.gif.Gif89Encoder(image);
      Gif89Encoder gif89aEncoder = new Gif89Encoder(image);
      //gif89aEncoder.setTransparentIndex(0);
      gif89aEncoder.setTransparentColor(this.backgroundColor);
      String sName = null;

      if( name==null ){
        StringBuffer name = new StringBuffer();
        name.append(TextSoap.findAndReplace(text," ",""));
        name.append(width);
        name.append("x");
        name.append(height);
        name.append(effect);
        name.append(".gif");

        sName = name.toString();
      }
      else {//only used for multiple button generation from localized.strings file.
        sName = name;
        if( effect != getStaticButtonUpString() ){
          sName+=effect;
        }
        sName+=".gif";
      }

      sName= sName.toLowerCase();
      path+=sName;

     // System.out.print("BUTTON NAME : "+sName);

      if( effect == getStaticButtonUpString() ){
        buttonUpName = URLEncoder.encode(sName);
      }
      else if( effect == getStaticButtonDownString() ){
        buttonDownName = URLEncoder.encode(sName);
      }
      else{
        buttonOverName = URLEncoder.encode(sName);
      }

      //FileUtil.delete(path); why???
      OutputStream output = new BufferedOutputStream(new FileOutputStream(path));

      //encode.Write(output);
      gif89aEncoder.encode(output);
      output.close();
    }
    catch (Exception e) {
      e.printStackTrace(System.out);
    }

  }

/**
 * @return
 * 
 * @uml.property name="font"
 */
public Font getFont() {
	return font;
}

}