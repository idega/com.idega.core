package com.idega.presentation.awt;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;


/**
 * Title: SingleLineItem
 * Description: SingleLineItem an awt component used in implementations of lists/menus/standalone/buttons
 * i.e. As a Label with an image and textlabel or as an image button. It can except a number of events
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class SingleLineItem extends Component {
  private int height = 16;
  private int width = 100;
  private int componentOffset = 5;
  private ActionListener actionListener = null;
  private boolean isSelected = false;


  public SingleLineItem() {
    addMouseListener(new ClickAdapter());
    setSize(getPreferredSize());
    Panel panel = new Panel();
  }

  public void setComponentOffset(int componentOffset){
    this.componentOffset = componentOffset;
  }



  public void addActionListener(ActionListener l) {
      actionListener = AWTEventMulticaster.add(actionListener, l);
  }

  public void removeActionListener(ActionListener l) {
      actionListener = AWTEventMulticaster.remove(actionListener, l);
  }



  public Dimension getPreferredSize() {
    return new Dimension(width, height);
  }
/*
  public Dimension getMinimumSize() {
    return new Dimension(0, 0);
  }

  public Dimension getMaximumSize() {
      return getPreferredSize();
  }*/

  public void paint(Graphics g) {
        super.paint(g);
        if( isSelected() ){
          g.setColor(Color.blue);
          g.fillRect(0,0,width,height);
        }
       /* if (isEnabled()) {
            if (pressed) {

            }
            else {
                g.setColor(Color.black);
                g.fillOval(0, 0, radius*2, radius*2);
            }
        }
        else {
            g.setColor(Color.gray);
            g.fillOval(0, 0, radius*2, radius*2);
        }

    make gray images
    if (grayImage == null) createGrayImage(g);
      */
  }


  public void isSelected(boolean isSelected){
   this.isSelected = isSelected;
  }

  public boolean isSelected(){
   return this.isSelected;
  }


  /*public Image getGrayImage() {
    return(grayImage);
  }
*/

  private final class ClickAdapter extends MouseAdapter {
    public void mousePressed(MouseEvent e) {
     // if( contains(e.getX(),e.getY()) ){
        isSelected = !isSelected;
        repaint();
     // }
    }

    public void mouseReleased(MouseEvent e) {
    // if( contains(e.getX(),e.getY()) ){
        isSelected = !isSelected;
        repaint();

        if (actionListener != null) {
            actionListener.actionPerformed(new ActionEvent(SingleLineItem.this,ActionEvent.ACTION_PERFORMED, ""));
        }
    //  }

    }

  }

}