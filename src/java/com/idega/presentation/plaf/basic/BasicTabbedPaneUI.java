package com.idega.presentation.plaf.basic;



import java.util.Vector;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.plaf.GenericTabbedPaneUI;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.util.IWColor;





/**

 * Title:        IW

 * Copyright:    Copyright (c) 2001

 * Company:      idega.is

 * @author

 * @version 1.0

 */



public class BasicTabbedPaneUI extends GenericTabbedPaneUI{
	
  public BasicTabbedPaneUI(){
    super();
  }

  public void initTab(){
    setTab(new BasicTabPresentation(getMainColor()));
  }

  public void initTabPage(){
    setTabPage(new BasicTabPagePresentation(getMainColor()));
  }

  public void setMainColor(IWColor color){
    super.setMainColor(color);
    getTabPresentation().setColor(color);
    getTabPagePresentation().setColor(color);
  }
  
//inner class BasicTabPresentation starts
  public class BasicTabPresentation extends GenericTabPresentation {
    private Vector tabs;
    
    public BasicTabPresentation(){
      super();
//      this.setBorder(1);
    }

    public BasicTabPresentation( IWColor color ){
      this();
      this.setCellpadding(0);
      this.setCellspacing(0);
      this.setColor(color);
    }

    public Link getTabLink(PresentationObject obj){
//      if(obj instanceof Link)
      Link tempLink = new Link(obj.getName());
      if(getForm() != null){
        tempLink.setToFormSubmit(getForm(),true);
      }
      return tempLink;
    }

    public PresentationObject getTab(int index,boolean selected){
      Link tempObj = (Link)this.getAddedTabs().elementAt(index);
      tempObj.setStyleClass("styledLink"); //added - birna
      Tab tempTab = new Tab(this.getColor());
      tempTab.setSelected(selected);
      tempTab.addLink(tempObj); //added - birna
      //commented out - birna
//      if(selected){
//        tempTab.addLink(tempObj.getObject());
//      } else {
//        tempTab.addLink(tempObj);
//      }
      return tempTab;
    }

    public void setSelectedIndex(int index){
      super.setSelectedIndex(index);
      lineUpTabs();
    }

    public void lineUpTabs(){
      //this.resize(this.getAddedTabs().size()+3, 2);
      this.empty();
      IWColor color = getColor();
      IWColor dark = color.darker();
      IWColor darker = dark.darker();
      IWColor darkest = darker.darker();
      IWColor bright = color.brighter();
      
      int tabSize = this.getAddedTabs().size();
			if(this.getSelectedIndex() == -1 && tabSize != 0){
        this.setSelectedIndex(0);
      }
      
      int row = 2;
      int column = 2;
      if ( tabSize > 5 )//changed from 5
      	row = 4;

      for (int i = 0; i < tabSize; i++) {
        PresentationObject tempObj = this.getTab(i,(this.getSelectedIndex()==i));
        this.add(tempObj,column,row-1);
        this.add(Text.emptyString(),column,row);
//        this.setWidth(i+2,tempObj.getWidth());
        this.setWidth(column,"100%");
//        this.setColor(column++, row, (this.getSelectedIndex()==i) ? color : bright);        
        if ( i == 4 ) {
        	row = 2;
        	column = 2;
        }
      }

			int size = getAddedTabs().size();
			if ( size > 5 ) {
				size = 5;
				row = 4;	
			}
      this.add(Text.emptyString(), 1, row-1);
      this.add(Text.emptyString(), 1, row);
      this.add(Text.emptyString(), size+2, row);
      this.add(Text.emptyString(), size+3, row-1);
      this.add(Text.emptyString(), size+3, row);
      
      this.setWidth(size+3,"1");
      this.setWidth(size+2,"100%");
      this.setHeight(2,"1");
      this.setWidth("100%");
      this.setWidth(1,"3");

//      this.setColor(1, row, bright);
//      this.setColor(size+2, row, bright);
//      this.setColor(size+3, row, darkest);
    }



//  public void empty(PresentationObject obj){}



    /**

     * unimplemented

     */

    public void setWidth(String width){
    }

    /**

     * unimplemented

     */

    public void SetHeight(String height){
    }

    public void main(IWContext iwc) throws Exception {
      this.lineUpTabs();
    }
//inner class BasicTabPresentation ends

//inner class Tab starts
    private class Tab extends Table{
        private String Name;
        private boolean selected;
        private IWColor color;
        private IWColor dark;
        private IWColor tabColor;
        private String styleSelectedBox = "selectedBox";
        private String styleBox = "box";
        private String styleName;

        public Tab(IWColor color){
          super();
//          this.color = color;
          selected = false;
          this.setCellpadding(0);
          this.setCellspacing(0);
//          this.setWidth(TabName.length()*10);
//          this.setWidth(75);
          newStyleInitializeTab();
          //initilizeTab();
        }
        //a function to initialize the new style for tabbed UserPropertyWindow 
        public void newStyleInitializeTab() {
        	styleName = isSelected() ? styleSelectedBox : styleBox;
					this.setStyleClass(styleName); 
					this.resize(1,1);
					this.add(Text.emptyString(),1,1);
					this.setAlignment("left");
        }
        
        public void initilizeTab(){
          this.resize(5,3);
          for (int i=1;i <= 5 ; i++) {
            for (int j=1;j <= 3; j++) {
              this.add(Text.emptyString(),i,j);
            }
          }

          dark = color.darker();
          IWColor darker = dark.darker();
          IWColor darkest = darker.darker();
          IWColor bright = color.brighter();

//          tabColor = isSelected() ? color : dark;
          this.setWidth(1, "1");
          this.setWidth(2, "1");
          this.setWidth(4, "1");
          this.setWidth(5, "1");
          this.setHeight(1, "1");
          this.setHeight(2, "1");

//          this.setColor(1,3,bright.getHexColorString());
//          
//          this.setColor(2,2,bright.getHexColorString());
//          this.setColor(2,3,tabColor.getHexColorString());
//
//          this.setColor(3,1,bright.getHexColorString());
//          this.setColor(3,2,tabColor.getHexColorString());
//          this.setColor(3,3,tabColor.getHexColorString());
//
//          this.setColor(4,2,darkest.getHexColorString());
//          this.setColor(4,3,darker.getHexColorString());
//
//          this.setColor(5,3,darkest.getHexColorString());

//temp

          super.setHeight(3,3, Integer.toString(23));

          this.setAlignment(3,3,"center");

        }

        public void addLink(PresentationObject link){
          
          this.add(link,2,2); //changed from this.add(link,3,3);
        }

        public void setSelected(boolean select){
          this.selected = select;
          newStyleInitializeTab();
         // initilizeTab();
        }
        
        public boolean isSelected(){
          return this.selected;
        }

        public void updateTab(){
        	styleName = isSelected() ? styleSelectedBox : styleBox;
        	
//        	IWColor bright = color.brighter();
//          tabColor = isSelected() ? dark : bright; //changed from color:dark;
//changed for the new userPropertyWindow         
//          this.setColor(2,2,tabColor.getHexColorString());
  
//          this.setColor(2,3,tabColor.getHexColorString());
//          this.setColor(3,2,tabColor.getHexColorString());
//          this.setColor(3,3,tabColor.getHexColorString());
        }
        public void main(IWContext iwc) throws Exception {
          updateTab();
        }
    } // Inner(Inner)Class Tab END











  } // InnerClass BasicTabPresentation
  
  public class BasicTabPagePresentation extends GenericTabPagePresentation {

    public BasicTabPagePresentation(){
      super();
    }

    public BasicTabPagePresentation( IWColor color ){
      this();
      this.setColor(color);
      this.setCellpadding(0);
      this.setCellspacing(10); //changed from (0) - birna
      this.setWidth("100%");
      initilizePage();
    }

    public void initilizePage(){
    	this.resize(1,1);
    	this.add(Text.emptyString(),1,1);
    	
    	this.setWidth("100%");
    	this.setHeight("100%");
    	this.setStyleClass("back");
			this.setAlignment(1,1,"center"); //changed from ...ment(3,1,"center");
			this.setVerticalAlignment(1,1,"middle");


//      this.resize(5,3);
//      for (int i=1;i <= 5 ; i++) {
//        for (int j=1;j <= 3; j++) {
//          this.add(Text.emptyString(),i,j);
//        }
//      }

//    IWColor color = getColor();
//    IWColor dark = color.darker();
//    IWColor darker = dark.darker();
//    IWColor darkest = darker.darker();
//    IWColor bright = color.brighter();
//
//    this.setWidth(1, "1");
//    this.setWidth(2, "1");
//    this.setWidth(4, "1");
//    this.setWidth(5, "1");
//    this.setHeight(2, "1");
//    this.setHeight(3, "1");
//    
//    this.setBorder("1");
//
//    this.setColor(color.getHexColorString());
//    
//    this.setColor(1,1,darkest.getHexColorString());
//    this.setColor(1,2,bright.getHexColorString());
//    this.setColor(1,3,darkest.getHexColorString());
//
//    this.setColor(2,2,darker.getHexColorString());
//    this.setColor(2,3,darkest.getHexColorString());
//
//    this.setColor(3,2,darker.getHexColorString());
//    this.setColor(3,3,darkest.getHexColorString());
//
//    this.setColor(4,1,darker.getHexColorString());
//    this.setColor(4,2,darker.getHexColorString());
//    this.setColor(4,3,darkest.getHexColorString());
//
//    this.setColor(5,1,darkest.getHexColorString());
//    this.setColor(5,2,darkest.getHexColorString());
//    this.setColor(5,3,darkest.getHexColorString());

//    super.setHeight(3,1, Integer.toString(paneModel.getTabPageHeight()-2));
//    super.setWidth(3,1,Integer.toString(paneModel.getTabPageWidth()-4));

//    super.setHeight(3,1, Integer.toString(410));
//    super.setWidth(3,1,Integer.toString(390));

 
}

  public void add(PresentationObject obj){
    this.add(Text.getBreak(),1,1); //changed from 3,1
    this.add(obj,1,1); //changed from 3,1
    this.setVerticalAlignment(1,1,"top"); //changed from 3,1
  }

//  public void empty(){}

    public void setWidth(String width){
      super.setWidth(width);
    }

    public void setHeight(String height){
      super.setHeight(height);
    }

    public void empty(){
      super.emptyCell(1,1); //changed from 3,1
    }

    public void fireContentChange(){}
  } // InnerClass GenericTabPagePresentation

} // Class BasicTabbedPaneUI



