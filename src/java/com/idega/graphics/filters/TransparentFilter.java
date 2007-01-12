package com.idega.graphics.filters;

import java.awt.image.*;
import java.awt.Color;

/** Image filter which maker the colour given as the argument to the
 * contstuctor transparent
 * Written wil a bit of help form Java in a Nutshell.
 @author Simon Homan
 */

public class TransparentFilter extends RGBImageFilter{

   int rgb;

   /** Create a filter that will make the parameter transparent
    @param rgb the coulour in 6 digit hex
    */
   public TransparentFilter(int rgb){
      super();
      this.canFilterIndexColorModel = true;   // Can filter the colour map
	                                 // irrespective pf pixel locations
      this.rgb = rgb;
   }

   /** Create a filter that will make the parameter transparent
    @param color the coulour as a java.awt.Color
    */
   public TransparentFilter(Color color){
      // we only want the rgb bits
      this(color.getRGB() & 0xffffff);
   }

   /** sets the alpah value to 0 on the coulout we want to be transparent as
    * specified in the constructor
    */
   public int filterRGB(int x, int y, int rgb){
      if( (rgb & this.rgb) ==  this.rgb){
	 return 0x00000000 | (rgb & this.rgb);
      }
      else{
	 return rgb;
      }
   }
}



