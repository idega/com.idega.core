package com.idega.presentation.awt;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;


/**
 * Title: SingleLineItem
 * Description: SingleLineItem an awt component used in implementations of lists/menus/standalone/buttons
 * i.e. As a Label with an image and textlabel or as an image button. It exepts mouse events
 * it can be fed a Dialog
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class SingleLineItem extends Panel {
  private int height = 16;
  private int width = 100;
  private int nextXpos = 0;
  private int nextYpos = 0;
  private int componentOffset = 5;
  private ActionListener actionListener = null;
  private boolean isSelected = false;
  private Window window;
  private GridBagConstraints gbc = null;
  private Color bgColor = Color.white;
  private Container parentContainer;
  private String ID;


  public SingleLineItem(Container parent) {
    addMouseListener(new ClickAdapter());
    //setSize(getPreferredSize());
    GridBagLayout grid = new GridBagLayout();
    setLayout(grid);
    setBackground(bgColor);
    setForeground(Color.black);
    parentContainer = parent;
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

  public Dimension getMinimumSize() {
         return getPreferredSize();

    //return new Dimension(0, 0);
  }

  public Dimension getMaximumSize() {
      return getPreferredSize();
  }

  /*public void paint(Graphics g) {
        super.paint(g);

        if( isSelected() ){
          g.setColor(Color.blue);
          g.fillRect(0,0,width,height);
          g.setColor(Color.red);
          g.drawString("ON",10,10);
        }
        else{
          g.setColor(Color.white);
          g.fillRect(0,0,width,height);
          g.setColor(Color.black);
          g.drawString("OFF",10,10);
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
  //}


  public void isSelected(boolean isSelected){
   this.isSelected = isSelected;
  }

  public boolean isSelected(){
   return this.isSelected;
  }

  public void setWindowToOpen(Window window){
    this.window = window;
  }

  private void openWindow(){
    if(window!=null) window.setVisible(true);
  }


  public void setId(String ID){
   this.ID = ID;
  }

  public String getId(){
   return ID;
  }

  public Component add(Component component){
    if(  gbc == null ){
      gbc = new GridBagConstraints();
      gbc.gridx = nextXpos;
      gbc.gridy = nextYpos; //Set at position 0,0
      gbc.weightx = gbc.weighty = 0; //No weight so component wont resize
      //gbc.anchor = gbc.SOUTHWEST;
      gbc.fill = GridBagConstraints.NONE;

      /*
      gbc.fill = GridBagConstraints.NONE;
      gbc.weightx = gbc.weighty = 1.0; //we want component to get all extra space
      gbc.fill = GridBagConstraints.BOTH; //Expand the component in both directions

      gbc.gridx = 1;
      gbc.gridy = 2; // set at position 1, 2
      gbc.gridheight = 1;
      gbc.gridwidth = 2; //make component take up 2 cells horizontally
      gbc.weighty = 0; //keep the current weightx, and set weighty back to 0
      gbc.fill = GridBagConstraints.HORIZONTAL; //take up extra space horizontally*/

    }
      gbc.gridx = nextXpos;
      nextXpos++;

      component.addMouseListener(new ClickAdapter());
      super.add(component,gbc);
      doLayout();
      repaint();
      return component;
  }

  /*public Image getGrayImage() {
    return(grayImage);
  }
*/

  private final class ClickAdapter extends MouseAdapter {
    public void mousePressed(MouseEvent e) {
      if( e.getClickCount() > 1){
        isSelected = true;

        setBackground(Color.blue);
        setForeground(Color.white);

        openWindow();
      }
      else{
        isSelected = !isSelected;
        if( isSelected ) {
          setBackground(Color.blue);
          setForeground(Color.white);
        }
        else{
          setBackground(bgColor);
          setForeground(Color.black);
        }
      }

      Component[] comps = SingleLineItem.this.getComponents();
      for (int i = 0; i < comps.length; i++) {
        comps[i].repaint();
      }


//      layout();
      SingleLineItem.this.doLayout();
      SingleLineItem.this.repaint();
//      parentContainer.layout();
      SingleLineItem.this.parentContainer.doLayout();
      SingleLineItem.this.parentContainer.repaint();

            SingleLineItem.this.doLayout();
      SingleLineItem.this.repaint();

      actionListener.actionPerformed(new ActionEvent(SingleLineItem.this,ActionEvent.ACTION_PERFORMED, "iw-selected"));
    }
/*
    public void mouseReleased(MouseEvent e) {
       // isSelected = !isSelected;
        repaint();

        if (actionListener != null) {
            actionListener.actionPerformed(new ActionEvent(SingleLineItem.this,ActionEvent.ACTION_PERFORMED, ""));
        }

    }*/

  }

}