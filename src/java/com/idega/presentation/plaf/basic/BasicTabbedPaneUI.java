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
 * 
 * Title: IW
 * 
 * Copyright: Copyright (c) 2001
 * 
 * Company: idega.is
 * 
 * @author 
 * @version 1.0
 *  
 */

public class BasicTabbedPaneUI extends GenericTabbedPaneUI {

	public BasicTabbedPaneUI() {
		super();
	}

	public void initTab() {
		setTab(new BasicTabPresentation());
	}

	public void initTabPage() {
		setTabPage(new BasicTabPagePresentation(getMainColor()));
	}

	public void setMainColor(IWColor color) {
		super.setMainColor(color);
		getTabPresentation().setColor(color);
		getTabPagePresentation().setColor(color);
	}

	//inner class BasicTabPresentation starts
	public class BasicTabPresentation extends GenericTabPresentation {

		private Vector tabs;

		public BasicTabPresentation() {
			super();
			this.setCellpadding(0);
			this.setCellspacing(0);
		}

		public Link getTabLink(PresentationObject obj) {
			Link tempLink = new Link(obj.getName());
			if (getForm() != null) {
				tempLink.setToFormSubmit(getForm(), true);
			}
			return tempLink;
		}

		public PresentationObject getTab(int index, boolean selected) {
			Link tempObj = (Link) this.getAddedTabs().elementAt(index);
			tempObj.setStyleClass("styledLink");
			Tab tempTab = new Tab(this.getColor());
			tempTab.setSelected(selected);
			tempTab.addLink(tempObj);
			return tempTab;
		}

		public void setSelectedIndex(int index) {
			super.setSelectedIndex(index);
			lineUpTabs();
		}

		public void lineUpTabs() {
			this.empty();
			this.setBorder(0);
			setID("mamaseika");

			int tabSize = this.getAddedTabs().size();
			if (this.getSelectedIndex() == -1 && tabSize != 0) {
				this.setSelectedIndex(0);
			}

			int row = 1;
			int column = 1;

			for (int i = 0; i < tabSize; i++) {
				PresentationObject tempObj = this.getTab(i, (this.getSelectedIndex() == i));
				((Table) tempObj).setCellBorder(1, 1, 1, "#bbbbbb", "solid");
				if (column > 1) {
					((Table) tempObj).setLeftCellBorderWidth(1, 1, 0);
				}
				if (row > 1) {
					//((Table) tempObj).setCellpaddingTop(1, 1, 1);
					((Table) tempObj).setTopCellBorderWidth(1, 1, 0);
				}
				this.add(tempObj, column, row);
				this.setWidth(column, 120);
				column++;
				if (i == 3) {
					row++;
					column = 1;
				}
			}
		}

		/**
		 * unimplemented
		 */
		public void setWidth(String width) {
			super.setWidthStyle(width);
		}

		/**
		 * unimplemented
		 */
		public void SetHeight(String height) {
		}

		public void main(IWContext iwc) throws Exception {
			this.lineUpTabs();
		}

		//inner class BasicTabPresentation ends

		//inner class Tab starts
		private class Tab extends Table {

			private String Name;

			private boolean selected;

			private String styleSelectedBox = "selectedBox";

			private String styleBox = "box";

			private String styleName;

			public Tab(IWColor color) {
				super();
				selected = false;
				this.setCellpadding(1);
				this.setCellspacing(0);
				newStyleInitializeTab();
			}

			//a function to initialize the new style for tabbed
			// UserPropertyWindow
			public void newStyleInitializeTab() {
				styleName = isSelected() ? styleSelectedBox : styleBox;
				this.setStyleClass(styleName);
				setWidth(Table.HUNDRED_PERCENT);
				this.resize(1, 1);
				this.add(Text.emptyString(), 1, 1);
			}

			public void initilizeTab() {
				this.resize(1, 1);
			}

			public void addLink(PresentationObject link) {
				this.add(link, 1, 1); //changed from this.add(link,3,3);
			}

			public void setSelected(boolean select) {
				this.selected = select;
				newStyleInitializeTab();
			}

			public boolean isSelected() {
				return this.selected;
			}

			public void updateTab() {
				styleName = isSelected() ? styleSelectedBox : styleBox;
				setBorder(0);
				setCellpaddingLeft(1, 1, 3);
			}

			public void main(IWContext iwc) throws Exception {
				updateTab();
			}
		} // Inner(Inner)Class Tab END

	} // InnerClass BasicTabPresentation

	public class BasicTabPagePresentation extends GenericTabPagePresentation {

		public BasicTabPagePresentation() {
			super();
		}

		public BasicTabPagePresentation(IWColor color) {
			this();
			this.setColor(color);
			this.setCellpadding(0);
			this.setCellspacing(0);
			this.setWidth(Table.HUNDRED_PERCENT);
			setBorder(0);
			initilizePage();
		}

		public void initilizePage() {
			this.resize(1, 1);
			//   	this.add(Text.emptyString(),1,1);

			this.setWidth("100%");
			//	this.setHeight("100%");
			this.setAlignment(1, 1, "center"); //changed from
											   // ...ment(3,1,"center");
			this.setVerticalAlignment(1, 1, "top");

		}

		public void add(PresentationObject obj) {
			this.add(obj, 1, 1); //changed from 3,1
			this.setVerticalAlignment(1, 1, "top"); //changed from 3,1
		}

		//  public void empty(){}

		public void setWidth(String width) {
			super.setWidth(width);
		}

		public void setHeight(String height) {
			super.setHeight(height);
		}

		public void empty() {
			super.emptyCell(1, 1); //changed from 3,1
		}

		public void fireContentChange() {
		}
	} // InnerClass GenericTabPagePresentation

} // Class BasicTabbedPaneUI

