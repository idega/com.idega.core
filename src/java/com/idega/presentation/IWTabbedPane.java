package com.idega.presentation;

import java.util.Vector;

import javax.swing.DefaultSingleSelectionModel;
import javax.swing.SingleSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.idega.event.IWLinkEvent;
import com.idega.event.IWLinkListener;
import com.idega.presentation.plaf.GenericTabbedPaneUI;
import com.idega.presentation.plaf.IWTabbedPaneUI;
import com.idega.presentation.plaf.basic.BasicTabbedPaneUI;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.Form;
import com.idega.util.Disposable;

/**
 * Title: idegaWeb project
 * Description:
 * Copyright: Copyright (c) 2001
 * Company: idega.is
 * 
 * @author 
 * @version 1.0
 */

public class IWTabbedPane extends Table implements SwingConstants, Disposable {

	private static final String uiClassID = "IWTabbedPaneUI";

	protected int tabPlacement = TOP;
	protected SingleSelectionModel model;
	protected ChangeListener changeListener = null;

	public Vector pages;
	private GenericTabbedPaneUI.GenericTabPagePresentation currentPage;
	protected ChangeEvent changeEvent = null;
	private static String TabbedPaneAttributeString = "-IWTabbedPane";
	private String attributeString;
	private boolean justConstructed;
	private IWTabbedPaneUI ui;

	public IWTabbedPane() {
		this(TOP);
	}

	public IWTabbedPane(int tabPlacement) {
		super(1, 3);
		this.setCellpadding(0);
		this.setCellspacing(0);
		setHeight(2, 5);
		setWidth(Table.HUNDRED_PERCENT);
		setBorder(0);

		setTabPlacement(tabPlacement);

		pages = new Vector(1);

		setModel(new DefaultSingleSelectionModel());
		getModel().addChangeListener(this.createChangeListener());
		setUI(new BasicTabbedPaneUI());
		this.currentPage = (GenericTabbedPaneUI.GenericTabPagePresentation) this.getUI().getTabPagePresentation();
		this.addTabePage(this.currentPage);
		justConstructed = true;
	}

	public static IWTabbedPane getInstance(String key, IWContext iwc, int tabPlacement) {
		Object obj = iwc.getSessionAttribute(key + TabbedPaneAttributeString);
		if (obj != null && obj instanceof IWTabbedPane) {
			IWTabbedPane TabbedPaneObj = (IWTabbedPane) obj;
			TabbedPaneObj.justConstructed(false);
			return TabbedPaneObj;
		}
		else {
			IWTabbedPane tempTab = new IWTabbedPane(tabPlacement);
			iwc.setSessionAttribute(key + TabbedPaneAttributeString, tempTab);
			tempTab.setAttributeString(key + TabbedPaneAttributeString);
			return tempTab;
		}
	}

	public boolean justConstructed() {
		return justConstructed;
	}

	public void justConstructed(boolean justConstructed) {
		this.justConstructed = justConstructed;
	}

	public String getAttributesString() {
		return attributeString;
	}

	public void setAttributeString(String attributeString) {
		this.attributeString = attributeString;
	}

	public void dispose(IWContext iwc) {
		iwc.getSession().removeAttribute(attributeString);
		PresentationObject[] obs = this.getAddedTabs();

		for (int i = 0; i < obs.length; i++) {
			if (obs[i] instanceof Disposable) {
				((Disposable) obs[i]).dispose(iwc);
			}
		}

		Vector evetLinks = this.getUI().getTabPresentation().getAddedTabs();
		if (evetLinks != null) {
			for (int i = 0; i < evetLinks.size(); i++) {
				((Link) evetLinks.get(i)).endEvent(iwc);
			}
		}
	}

	public static IWTabbedPane getInstance(String key, IWContext iwc) {
		return getInstance(key, iwc, TOP);
	}

	public IWTabbedPaneUI getUI() {
		return ui;
	}

	public void setUI(IWTabbedPaneUI ui) {
		this.ui = ui;
		updateUI();
	}

	public void updateUI() {
		this.currentPage = (GenericTabbedPaneUI.GenericTabPagePresentation) this.getUI().getTabPagePresentation();
	}

	public String getUIClassID() {
		return uiClassID;
	}

	protected class ModelListener implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			fireStateChanged();
		}

	}

	protected class LinkListener implements IWLinkListener {

		public void actionPerformed(IWLinkEvent e) {
			setSelectedIndex(getUI().getTabPresentation().getAddedTabs().indexOf(e.getSource()));
		}

	}

	protected IWLinkListener createLinkListener() {
		return new LinkListener();
	}

	protected ChangeListener createChangeListener() {
		return new ModelListener();
	}

	public void addChangeListener(ChangeListener l) {
		getEventListenerList().add(ChangeListener.class, l);
	}

	public void removeChangeListener(ChangeListener l) {
		getEventListenerList().remove(ChangeListener.class, l);
	}

	protected void fireStateChanged() {
		// Guaranteed to return a non-null array
		Object[] listeners = getEventListenerList().getListenerList();

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ChangeListener.class) {
				// Lazily create the event:
				if (changeEvent == null) {
					changeEvent = new ChangeEvent(this);
				}
				((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
			}
		}
	}

	public SingleSelectionModel getModel() {
		return model;
	}

	public void setModel(SingleSelectionModel model) {
		SingleSelectionModel oldModel = getModel();
		if (oldModel != null) {
			oldModel.removeChangeListener(changeListener);
			changeListener = null;
		}

		this.model = model;
		if (model != null) {
			changeListener = createChangeListener();
			model.addChangeListener(changeListener);
		}

		firePropertyChange("model", oldModel, model);
	}

	/**
	 * unimplemented
	 * @todo implement
	 */
	public void firePropertyChange(String key, SingleSelectionModel oldModel, SingleSelectionModel model) {
	}

	public int getTabPlacement() {
		return tabPlacement;
	}

	public void setTabPlacement(int tabPlacement) {
		if (tabPlacement != TOP && tabPlacement != LEFT && tabPlacement != BOTTOM && tabPlacement != RIGHT) {
			throw new IllegalArgumentException("illegal tab placement: must be TOP, BOTTOM, LEFT, or RIGHT");
		}

		if (this.tabPlacement != tabPlacement) {
			int oldValue = this.tabPlacement;
			this.tabPlacement = tabPlacement;
			firePropertyChange("tabPlacement", oldValue, tabPlacement);
		}
	}

	/**
	 * unimplemented
	 */
	public void firePropertyChange(String Key, int oldValue, int tabPlacement) {
		//TODO implement
	}

	public int getSelectedIndex() {
		return model.getSelectedIndex();
	}

	public void setSelectedIndex(int index) {
		model.setSelectedIndex(index);

		currentPage.empty();
		currentPage.add(this.getComponentAt(this.getSelectedIndex()));

		this.getUI().getTabPresentation().setSelectedIndex(this.getSelectedIndex());
	}

	public PresentationObject getSelectedComponent() {
		int index = getSelectedIndex();
		if (index == -1) {
			return null;
		}
		return getComponentAt(index);
	}

	public void setSelectedComponent(PresentationObject c) {
		int index = indexOfComponent(c);
		if (index != -1) {
			setSelectedIndex(index);
		}
		else {
			throw new IllegalArgumentException("moduleobject not found in tabbed pane");
		}
	}

	public void insertTab(String title, PresentationObject moduleobject, int index, IWContext iwc) {
		int i;
		if (moduleobject != null && (i = indexOfComponent(moduleobject)) != -1) {
			removeTabAt(i);
		}

		pages.insertElementAt(new Page(this, title != null ? title : " --- ", moduleobject, iwc), index);
		if (pages.size() == 1) {
			setSelectedIndex(0);
		}

		this.getUI().getTabPresentation().add(((Page) pages.elementAt(index)).getTabLink(), index);
	}

	public void removeTabAt(int index) {
		// If we are removing the currently selected tab AND
		// it happens to be the last tab in the bunch, then
		// select the previous tab
		int tabCount = getTabCount();

		int selected = getSelectedIndex();
		if (selected >= (tabCount - 1)) {
			setSelectedIndex(selected - 1);
		}

		pages.removeElementAt(index);
	}

	public boolean remove(PresentationObject moduleobject) {
		int index = indexOfComponent(moduleobject);
		if (index != -1) {
			removeTabAt(index);
		}

		return (true);
	}

	public void remove(int index) {
		removeTabAt(index);
	}

	public void removeAll() {
		setSelectedIndex(-1);

		int tabCount = getTabCount();
		for (int i = 0; i < tabCount; i++) {
			PresentationObject moduleobject = getComponentAt(i);
			// Reset visibility
			if (moduleobject != null) {
				//                moduleobject.setVisible(true);
			}
		}

		pages.removeAllElements();
	}

	public int getTabCount() {
		return pages.size();
	}

	public PresentationObject[] getAddedTabs() {
		PresentationObject obj[] = new PresentationObject[pages.size()];
		for (int i = 0; i < pages.size(); i++) {
			obj[i] = ((Page) pages.get(i)).content;
		}

		return obj;
	}

	public int getTabRunCount() {
		if (ui != null) {
			//            return ((IWTabbedPaneUI)ui).getTabRunCount(this);
		}

		return 0;
	}

	// Getters for the Pages
	public String getTitleAt(int index) {
		return ((Page) pages.elementAt(index)).title;
	}

	public PresentationObject getComponentAt(int index) {
		return ((Page) pages.elementAt(index)).content;
	}

	// Setters for the Pages
	public void setTitleAt(int index, String title) {
		String oldTitle = ((Page) pages.elementAt(index)).title;
		((Page) pages.elementAt(index)).title = title;
		if (title == null || oldTitle == null || !title.equals(oldTitle)) {

		}
	}

	public int indexOfTab(String title) {
		for (int i = 0; i < getTabCount(); i++) {
			if (getTitleAt(i).equals(title == null ? "" : title)) {
				return i;
			}
		}

		return -1;
	}

	public int indexOfComponent(PresentationObject moduleobject) {
		for (int i = 0; i < getTabCount(); i++) {
			PresentationObject c = getComponentAt(i);
			if ((c != null && c.equals(moduleobject)) || (c == null && c == moduleobject)) {
				return i;
			}
		}

		return -1;
	}

	private class Page {

		String title;
		IWTabbedPane parent;
		PresentationObject content;
		Link tabLink;
		boolean needsUIUpdate;

		public Page(IWTabbedPane parent, String title, PresentationObject content, IWContext iwc) {
			this.title = title;
			this.content = content;
			this.UpdateUI(getUI(), iwc);
		}

		public void UpdateUI(IWTabbedPaneUI ui, IWContext iwc) {
			tabLink = ui.getTabPresentation().getTabLink(this.content);
			tabLink.addIWLinkListener(createLinkListener(), iwc);
			this.needsUIUpdate(false);
		}

		public void needsUIUpdate(boolean update) {
			this.needsUIUpdate = update;
		}

		public Link getTabLink() {
			return tabLink;
		}

		public PresentationObject getContent() {
			return this.content;
		}
	} // InnerClass Page

	private void addTabs(PresentationObject obj) {
		this.add(obj, 1, 1);
	}

	private void addTabePage(PresentationObject obj) {
		this.add(obj, 1, 3);
	}

	public void setTabsToFormSubmit(Form form) {
		Vector tabs = getUI().getTabPresentation().getAddedTabs();
		if (tabs != null) {
			for (int i = 0; i < tabs.size(); i++) {
				((Link) tabs.get(i)).setToFormSubmit(form, true);
			}
		}

		getUI().getTabPresentation().setForm(form);
	}

	public void main(IWContext iwc) throws Exception {
		if (this.getUI().getTabPresentation() instanceof PresentationObject && this.justConstructed()) {
			addTabs((PresentationObject) this.getUI().getTabPresentation());
		}
	}

}

/**
 * @todo Kalla á TabPagePresentation hlut frá ui-inu og setja viðeigani síðu í
 *       hann.
 * 
 * @todo Selecta viðeigandi hlut og láta TabPresentatnion vita. Nota alltaf
 * sama TabPresentation hlutinn en kalla á fireStateChange() fallið.
 * adda í Tabpresentationhlutinn um leið og hlut er addað í TabbedPane. Veldur
 * vandræðum ef að skipta á um UI því þá þarf að adda öllu í TabPresentation aftur.
 * 
 * @todo stroka út setBorder í Table
 */