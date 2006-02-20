package com.idega.presentation.awt;

import java.awt.AWTEventMulticaster;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


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
  private int iHeight = 16;
  private int iWidth = 120;
  private int nextXpos = 0;
  private int nextYpos = 0;
  private int componentOffset = 5;
  ActionListener actionListener = null;
  boolean isSelected = false;
  private Window window;
  private GridBagConstraints gbc = null;
  Color bgColor = Color.white;
  private Container parentContainer;
  private String ID;
  private boolean fillRight = false;


  public SingleLineItem(Container parent) {
    addMouseListener(new ClickAdapter());
    GridBagLayout grid = new GridBagLayout();
    setLayout(grid);
    setBackground(bgColor);
    setForeground(Color.black);
    parentContainer = parent;
  }

  public void setComponentOffset(int componentOffset){
    this.componentOffset = componentOffset;
  }

  public void setComponentOffset(int width, int height){
    this.iWidth = width;
    this.iHeight = height;
  }


  public void addActionListener(ActionListener l) {
      actionListener = AWTEventMulticaster.add(actionListener, l);
  }

  public void removeActionListener(ActionListener l) {
      actionListener = AWTEventMulticaster.remove(actionListener, l);
  }

  public Dimension getPreferredSize() {
    return new Dimension(iWidth,iHeight);
  }

  public Dimension getMinimumSize() {
    return new Dimension(iWidth,iHeight);
  }

  public Dimension minimumSize() {
    return getMinimumSize();
  }

  public Dimension preferredSize() {
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

  void openWindow(){
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
      //gbc.weightx = gbc.weighty = 0; //No weight so component wont resize
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
      if( fillRight ){
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = gbc.weighty = 1;
        System.out.println("FILL");
      }


      gbc.gridx = nextXpos;
      nextXpos++;

      component.addMouseListener(new ClickAdapter());
      super.add(component,gbc);
      return component;
  }

  public void refresh() {
   doLayout();
   Component[] comps = SingleLineItem.this.getComponents();
   for (int i = 0; i < comps.length; i++) {
    comps[i].repaint();
   }
   parentContainer.doLayout();
   parentContainer.repaint();
   repaint();

  }

  public void setNextToFillRight(boolean fillRight){
   this.fillRight = fillRight;
  }

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

      refresh();

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
