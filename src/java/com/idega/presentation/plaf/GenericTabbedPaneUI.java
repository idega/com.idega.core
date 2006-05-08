package com.idega.presentation.plaf;

import java.util.EventListener;
import java.util.Vector;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.Form;
import com.idega.util.IWColor;

/**
 * Title: idegaWeb project Description: Copyright: Copyright (c) 2001 Company:
 * idega.is
 * 
 * @author @version 1.0
 */

public abstract class GenericTabbedPaneUI implements IWTabbedPaneUI {

	private TabPresentation tab;
	private TabPagePresentation tabpage;
	private IWColor MainColor;

	public GenericTabbedPaneUI() {
		setMainColor(new IWColor(207, 208, 210));
		initTab();
		initTabPage();
	}

	public abstract void initTab();

	public abstract void initTabPage();

	public void setTab(TabPresentation tab) {
		this.tab = tab;
	}

	public void setTabPage(TabPagePresentation page) {
		this.tabpage = page;
	}

	public TabPresentation getTabPresentation() {
		if (this.tab == null) {
			initTab();
		}
		return this.tab;
	}

	public TabPagePresentation getTabPagePresentation() {
		if (this.tabpage == null) {
			initTabPage();
		}
		return this.tabpage;
	}

	public void setMainColor(IWColor color) {
		this.MainColor = color;
	}

	public IWColor getMainColor() {
		return this.MainColor;
	}

	public abstract class GenericTabPresentation extends Table implements TabPresentation {

		private IWColor tabPageColor;
		protected Vector tabs;
		protected ChangeEvent changeEvent = null;
		protected EventListenerList iListenerList = new EventListenerList();
		protected Form linkForm = null;

		private int index = -1;

		public GenericTabPresentation() {
			super();
			setCellpadding(0);
			setCellspacing(0);
			setBorder(1);
			//setWidth(Table.HUNDRED_PERCENT);
			this.tabs = new Vector();
		}

		public void setForm(Form form) {
			this.linkForm = form;
		}

		public Form getForm() {
			return this.linkForm;
		}

		public void add(PresentationObject obj, int index) {
			this.tabs.insertElementAt(obj, index);
		}

		public void empty(int index) {
			this.tabs.remove(index);
		}

		public abstract Link getTabLink(PresentationObject obj);

		public abstract void setWidth(String width);

		public abstract void SetHeight(String height);

		public Vector getAddedTabs() {
			return this.tabs;
		}

		public void setAddedTabs(Vector tabs) {
			this.tabs = tabs;
		}

		public IWColor getColor() {
			return this.tabPageColor;
		}

		public void setColor(IWColor color) {
			this.tabPageColor = color;
		}

		// SingleSelectionModel methods
		public int getSelectedIndex() {
			return this.index;
		}

		public void setSelectedIndex(int index) {
			if (this.index != index) {
				this.index = index;
				fireStateChanged();
			}
		}

		public void clearSelection() {
			setSelectedIndex(-1);
		}

		public boolean isSelected() {
			boolean ret = false;
			if (getSelectedIndex() != -1) {
				ret = true;
			}

			return ret;
		}

		public void addChangeListener(ChangeListener l) {
			this.iListenerList.add(ChangeListener.class, l);
		}

		public void removeChangeListener(ChangeListener l) {
			this.iListenerList.remove(ChangeListener.class, l);
		}

		public void fireStateChanged() {
			// Guaranteed to return a non-null array
			Object[] listeners = this.iListenerList.getListenerList();

			// Process the listeners last to first, notifying
			// those that are interested in this event
			for (int i = listeners.length - 2; i >= 0; i -= 2) {
				if (listeners[i] == ChangeListener.class) {
					// Lazily create the event:
					if (this.changeEvent == null) {
						this.changeEvent = new ChangeEvent(this);
					}
					((ChangeListener) listeners[i + 1]).stateChanged(this.changeEvent);
				}
			}
		}

		public EventListener[] getListeners(Class listenerType) {
			return this.iListenerList.getListeners(listenerType);
		}
	} // InnerClass GenericTabPresentation

	public abstract class GenericTabPagePresentation extends Table implements TabPagePresentation {

		private IWColor tabPageColor;

		public GenericTabPagePresentation() {
			super();
			setCellpadding(0);
			setCellspacing(0);
			setBorder(1);
			setWidth(Table.HUNDRED_PERCENT);
		}

		public void setColor(IWColor color) {
			this.tabPageColor = color;
		}

		public IWColor getColor() {
			return this.tabPageColor;
		}

		public void setWidth(String width) {
			super.setWidth(width);
		}

		public void setHeight(String height) {
			super.setHeight(height);
		}

		public void fireContentChange() {
		}
	} // InnerClass GenericTabPagePresentation
} // Class GenericTabbedPaneUI