package com.idega.presentation.awt;

import java.awt.*;
import java.net.*;
import java.awt.image.*;     // For ImageFilter stuff

// This appears in Core Web Programming from
// Prentice Hall Publishers, and may be freely used
// or adapted. 1997 Marty Hall, hall@apl.jhu.edu.

//======================================================
/**
 * A button class that uses an image instead of a
 * textual label. Clicking and releasing the mouse over
 * the button triggers an ACTION_EVENT, so you can add
 * behavior in the same two ways as you with a normal
 * Button (in Java 1.0):
 * <OL>
 *  <LI>Make an ImageButton subclass and put the
 *      behavior in the action method of that subclass.
 *  <LI>Use the main ImageButton class but then catch
 *      the events in the action method of the Container.
 * </OL>
 * <P>
 * Normally, the ImageButton's preferredSize (used,
 * for instance, by FlowLayout) is just big enough
 * to hold the image. However, if you give an explicit
 * resize or reshape call <B>before</B> adding the
 * ImageButton to the Container, this size will
 * override the defaults.
 * <P>
 * @author Marty Hall (hall@apl.jhu.edu)
 * @see Icon
 * @see GrayFilter
 * @version 1.0 (1997)
 */

public class ImageButton extends ImageLabel {
  //----------------------------------------------------

  /** Default width of 3D border around image.
   *  Currently 4.
   * @see ImageLabel#setBorder
   * @see ImageLabel#getBorder
   */
  protected static final int defaultBorderWidth = 4;

  /** Default color of 3D border around image.
   *  Currently a gray with R/G/B of 160/160/160.
   *  Light grays look best.
   * @see ImageLabel#setBorderColor
   * @see ImageLabel#getBorderColor
   */
  protected static final Color defaultBorderColor =
    new Color(160, 160, 160);

  private boolean mouseIsDown = false;

  //----------------------------------------------------
  // Constructors

  /** Create an ImageButton with the default image.
   * @see ImageLabel#getDefaultImageString
   */
  public ImageButton() {
    super();
    setBorders();
  }

  /** Create an ImageButton using the image at URL
   *  specified by the string.
   * @param imageURLString A String specifying the URL
   *        of the image.
   */
  public ImageButton(String imageURLString) {
    super(imageURLString);
    setBorders();
  }

  /** Create an ImageButton using the image at URL
   *  specified.
   * @param imageURL The URL of the image.
   */
  public ImageButton(URL imageURL) {
    super(imageURL);
    setBorders();
  }

  /** Creates an ImageButton using the file in
   *  the directory specified.
   * @param imageDirectory The URL of a directory
   * @param imageFile File in the above directory
   */
  public ImageButton(URL imageDirectory,
                     String imageFile) {
    super(imageDirectory, imageFile);
    setBorders();
  }

  /** Create an ImageButton using the image specified.
   *  You would only want to use this if you already
   *  have an image (e.g. created via createImage).
   * @param image The image.
   */
  public ImageButton(Image image) {
    super(image);
    setBorders();
  }

  //----------------------------------------------------
  /** Draws the image with the border around it. If you
   *  override this in a subclass, call super.paint().
   */
  public void paint(Graphics g) {
    super.paint(g);
    if (this.grayImage == null) {
			createGrayImage(g);
		}
    drawBorder(true);
  }

  //----------------------------------------------------
  // You only want mouseExit to repaint when mouse
  // is down, so you have to set that flag here.

  /** When the mouse is clicked, reverse the 3D border
   *  and draw a dark-gray version of the image.
   *  The action is not triggered until mouseUp.
   */
  public boolean mouseDown(Event event, int x, int y) {
    this.mouseIsDown = true;
    Graphics g = getGraphics();
    int border = getTheBorder();
    if (hasExplicitSize()) {
			g.drawImage(this.grayImage, border, border,
                  getWidth()-2*border,
                  getHeight()-2*border,
                  this);
		}
		else {
			g.drawImage(this.grayImage, border, border, this);
		}
    drawBorder(false);
    return(true);
  }

  //----------------------------------------------------
  /** If cursor is still inside, trigger the action
   *  event and redraw the image (non-gray, button
   *  "out"). Otherwise ignore this.
   */
  public boolean mouseUp(Event event, int x, int y) {
    this.mouseIsDown = false;
    if (inside(x,y)) {
      paint(getGraphics());
      event.id = Event.ACTION_EVENT;
      event.arg = getImage();
      return(action(event, event.arg));
    }
		else {
			return(false);
		}
  }

  //----------------------------------------------------
  /** Generated when the button is clicked and released.
   *  Override this in subclasses to give behavior to
   *  the button. Alternatively, since the default
   *  behavior is to pass the ACTION_EVENT along to the
   *  Container, you can catch events for a bunch of
   *  buttons there.
   * @see Component#action
   */

  public boolean action(Event event, Object arg) {
    debug("Clicked on button for " +
          getImageString() + ".");
    return(false);
  }

  //----------------------------------------------------
  /** If you move the mouse off the button while the
   *  mouse is down, abort and do <B>not</B> trigger
   *  the action. Ignore this if button was not
   *  already down.
   */
  public boolean mouseExit(Event event, int x, int y) {
    if (this.mouseIsDown) {
			paint(getGraphics());
		}
    return(true);
  }

  //----------------------------------------------------

  /** The darkness value to use for grayed images.
   * @see #setDarkness
   */
  public int getDarkness() {
    return(this.darkness);
  }

  /** An int whose bits are combined via "and" ("&")
   *  with the alpha, red, green, and blue bits of the
   *  pixels of the image to produce the grayed-out
   *  image to use when button is depressed.
   *  Default is 0xffafafaf: af combines with r/g/b
   *  to darken image.
   */

  public void setDarkness(int darkness) {
    this.darkness = darkness;
  }

  // Changing darker is consistent with regular buttons

  private int darkness = 0xffafafaf;

  //----------------------------------------------------
  /** The gray image used when button is down.
   * @see #setGrayImage
   */

  public Image getGrayImage() {
    return(this.grayImage);
  }

  /** Sets gray image created automatically from regular
   *  image via an image filter to use when button is
   *  depressed. You won't normally use this directly.
   */
  public void setGrayImage(Image grayImage) {
    this.grayImage = grayImage;
  }

  private Image grayImage = null;

  //----------------------------------------------------

  private void drawBorder(boolean isUp) {
    Graphics g = getGraphics();
    g.setColor(getBorderColor());
    int left = 0;
    int top = 0;
    int width = getWidth();
    int height = getHeight();
    int border = getTheBorder();
    for(int i=0; i<border; i++) {
      g.draw3DRect(left, top, width, height, isUp);
      left++;
      top++;
      width = width - 2;
      height = height - 2;
    }
  }

  //----------------------------------------------------

  private void setBorders() {
    setBorder(defaultBorderWidth);
    setBorderColor(defaultBorderColor);
  }

  //----------------------------------------------------
  // The first time the image is drawn, update() is
  // called, and the result does not come out correctly.
  // So this forces a brief draw on loadup, replaced
  // by real, non-gray image.

  private void createGrayImage(Graphics g) {
    ImageFilter filter = new GrayFilter(this.darkness);
    ImageProducer producer =
      new FilteredImageSource(getImage().getSource(),
                              filter);
    this.grayImage = createImage(producer);
    int border = getTheBorder();
    if (hasExplicitSize()) {
			prepareImage(this.grayImage, getWidth()-2*border,
                   getHeight()-2*border, this);
		}
		else {
			prepareImage(this.grayImage, this);
		}
    super.paint(g);
  }

  //----------------------------------------------------
}
