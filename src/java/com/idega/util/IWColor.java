package com.idega.util;



import java.awt.Color;
import java.awt.color.ColorSpace;



/**

 * Title:

 * Description:

 * Copyright:    Copyright (c) 2001

 * Company:      idega.is

 * @author gummi@idega.is

 * @version 1.0

 */



public class IWColor{



    protected Color awtColor;





    public IWColor(Color color){

      this.awtColor = color;

    }



    /**

     * Creates a color in the specified <code>ColorSpace</code>

     * with the color components specified in the <code>float</code>

     * array and the specified alpha.  The number of components is

     * determined by the type of the <code>ColorSpace</code>.  For

     * example, RGB requires 3 components, but CMYK requires 4

     * components.

     * @param cspace the <code>ColorSpace</code> to be used to

     *			interpret the components

     * @param components an arbitrary number of color components

     *                      that is compatible with the

     * @param alpha alpha value

     * @throws IllegalArgumentException if any of the values in the

     *         <code>components</code> array or <code>alpha</code> is

     *         outside of the range 0.0 to 1.0

     * @see #getComponents

     * @see #getColorComponents

     */

  public IWColor(ColorSpace cspace, float[] components, float alpha){

    this.awtColor = new Color(cspace, components, alpha);

  }



    /**

     * Creates an opaque sRGB color with the specified red, green, and blue

     * values in the range (0.0 - 1.0).  Alpha is defaulted to 1.0.  The

     * actual color used in rendering depends on finding the best

     * match given the color space available for a particular output

     * device.

     * @param r the red component

     * @param g the green component

     * @param b the blue component

     * @see #getRed

     * @see #getGreen

     * @see #getBlue

     * @see #getRGB

     */

  public IWColor(float r, float g, float b){

    this.awtColor = new Color(r, g, b);

  }



    /**

     * Creates an sRGB color with the specified red, green, blue, and

     * alpha values in the range (0.0 - 1.0).  The actual color

     * used in rendering depends on finding the best match given the

     * color space available for a particular output device.

     * @param r the red component

     * @param g the green component

     * @param b the blue component

     * @param a the alpha component

     * @see #getRed

     * @see #getGreen

     * @see #getBlue

     * @see #getAlpha

     * @see #getRGB

     */

  public IWColor(float r, float g, float b, float a){

    this.awtColor = new Color(r, g, b, a);

  }



    /**

     * Creates an opaque sRGB color with the specified combined RGB value

     * consisting of the red component in bits 16-23, the green component

     * in bits 8-15, and the blue component in bits 0-7.  The actual color

     * used in rendering depends on finding the best match given the

     * color space available for a particular output device.  Alpha is

     * defaulted to 255.

     * @param rgb the combined RGB components

     * @see java.awt.image.ColorModel#getRGBdefault

     * @see #getRed

     * @see #getGreen

     * @see #getBlue

     * @see #getRGB

     */

  public IWColor(int rgb){

    this.awtColor = new Color(rgb);

  }



    /**

     * Creates an sRGB color with the specified combined RGBA value consisting

     * of the alpha component in bits 24-31, the red component in bits 16-23,

     * the green component in bits 8-15, and the blue component in bits 0-7.

     * If the <code>hasalpha</code> argument is <code>false</code>, alpha

     * is defaulted to 255.

     * @param rgba the combined RGBA components

     * @param hasalpha <code>true</code> if the alpha bits are valid;

     * <code>false</code> otherwise

     * @see java.awt.image.ColorModel#getRGBdefault

     * @see #getRed

     * @see #getGreen

     * @see #getBlue

     * @see #getAlpha

     * @see #getRGB

     */

  public IWColor(int rgba, boolean hasalpha){

    this.awtColor = new Color( rgba, hasalpha);

  }



    /**

     * Creates an opaque sRGB color with the specified red, green,

     * and blue values in the range (0 - 255).

     * The actual color used in rendering depends

     * on finding the best match given the color space

     * available for a given output device.

     * Alpha is defaulted to 255.

     * @param r the red component

     * @param g the green component

     * @param b the blue component

     * @see #getRed

     * @see #getGreen

     * @see #getBlue

     * @see #getRGB

     */

  public IWColor(int r, int g, int b){

    this.awtColor = new Color( r, g, b);

  }



    /**

     * Creates an sRGB color with the specified red, green, blue, and alpha

     * values in the range (0 - 255).

     * @param r the red component

     * @param g the green component

     * @param b the blue component

     * @param a the alpha component

     * @see #getRed

     * @see #getGreen

     * @see #getBlue

     * @see #getAlpha

     * @see #getRGB

     */

  public IWColor(int r, int g, int b, int a){

    this.awtColor = new Color( r, g, b, a);

  }



  public int getRed(){

    return this.awtColor.getRed();

  }



  public int getGreen(){

    return this.awtColor.getGreen();

  }



  public int getBlue(){

    return this.awtColor.getBlue();

  }



  public IWColor darker(){

    return new IWColor(this.awtColor.darker());

  }



  public IWColor brighter(){

    return new IWColor(this.awtColor.brighter());

  }



  public String getHexColorString(){

    try {

      return getHexColorString(this.getRed(), this.getGreen(), this.getBlue());

    }

    catch (NumberFormatException ex) {

      ex.printStackTrace(System.err);

      return "#000000";

    }

  }



  public static String getHexColorString(int R, int G, int B) throws NumberFormatException {

    String colorString = "#";

    if ((R < 256 && R > -1) && (G < 256 && G > -1) && (B < 256 && B > -1)){

      if ( R < 16 ) {
		colorString += "0";
	}

      colorString += Integer.toHexString(R);



      if ( G < 16 ) {
		colorString += "0";
	}

      colorString += Integer.toHexString(G);



      if ( B < 16 ) {
		colorString += "0";
	}

      colorString += Integer.toHexString(B);

    }else{

      throw new NumberFormatException("Some int not in the range of 0 to 255");

    }

    return colorString;

  }



 public static IWColor getIWColorFromHex(String hex) {

    hex = com.idega.util.text.TextSoap.findAndCut(hex,"#");

    IWColor color = null;

    int red = getIntFromHex(hex.substring(0,2));

    int green = getIntFromHex(hex.substring(2,4));

    int blue = getIntFromHex(hex.substring(4,6));

    color = new IWColor(red,green,blue);

    return color;

  }



  public static Color getAWTColorFromHex(String hex) throws NumberFormatException {

    IWColor color = getIWColorFromHex(hex);

    return color.awtColor;

  }



  public static int getIntFromHex(String hex){

   return Integer.parseInt(hex,16);

  }







  public static String getHexColorString(Color color){

    return getHexColorString(color.getRed(),color.getGreen(),color.getBlue());

  }









}

