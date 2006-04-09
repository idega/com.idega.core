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
    setBackground(this.bgColor);
    setForeground(Color.black);
    this.parentContainer = parent;
  }

  public void setComponentOffset(int componentOffset){
  }

  public void setComponentOffset(int width, int height){
    this.iWidth = width;
    this.iHeight = height;
  }


  public void addActionListener(ActionListener l) {
      this.actionListener = AWTEventMulticaster.add(this.actionListener, l);
  }

  public void removeActionListener(ActionListener l) {
      this.actionListener = AWTEventMulticaster.remove(this.actionListener, l);
  }

  public Dimension getPreferredSize() {
    return new Dimension(this.iWidth,this.iHeight);
  }

  public Dimension getMinimumSize() {
    return new Dimension(this.iWidth,this.iHeight);
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
    if(this.window!=null) {
			this.window.setVisible(true);
		}
  }


  public void setId(String ID){
   this.ID = ID;
  }

  public String getId(){
   return this.ID;
  }

  public Component add(Component component){
    if(  this.gbc == null ){
      this.gbc = new GridBagConstraints();
      this.gbc.gridx = this.nextXpos;
      this.gbc.gridy = this.nextYpos; //Set at position 0,0
      //gbc.weightx = gbc.weighty = 0; //No weight so component wont resize
      //gbc.anchor = gbc.SOUTHWEST;
      this.gbc.fill = GridBagConstraints.NONE;

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
      if( this.fillRight ){
        this.gbc.fill = GridBagConstraints.BOTH;
        this.gbc.weightx = this.gbc.weighty = 1;
        System.out.println("FILL");
      }


      this.gbc.gridx = this.nextXpos;
      this.nextXpos++;

      component.addMouseListener(new ClickAdapter());
      super.add(component,this.gbc);
      return component;
  }

  public void refresh() {
   doLayout();
   Component[] comps = SingleLineItem.this.getComponents();
   for (int i = 0; i < comps.length; i++) {
    comps[i].repaint();
   }
   this.parentContainer.doLayout();
   this.parentContainer.repaint();
   repaint();

  }

  public void setNextToFillRight(boolean fillRight){
   this.fillRight = fillRight;
  }

  protected final class ClickAdapter extends MouseAdapter {
    public void mousePressed(MouseEvent e) {
      if( e.getClickCount() > 1){
        SingleLineItem.this.isSelected = true;

        setBackground(Color.blue);
        setForeground(Color.white);

        openWindow();
      }
      else{
        SingleLineItem.this.isSelected = !SingleLineItem.this.isSelected;
        if( SingleLineItem.this.isSelected ) {
          setBackground(Color.blue);
          setForeground(Color.white);
        }
        else{
          setBackground(SingleLineItem.this.bgColor);
          setForeground(Color.black);
        }
      }

      refresh();

      SingleLineItem.this.actionListener.actionPerformed(new ActionEvent(SingleLineItem.this,ActionEvent.ACTION_PERFORMED, "iw-selected"));
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
