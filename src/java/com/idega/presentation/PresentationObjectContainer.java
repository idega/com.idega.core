/*
 * $Id: PresentationObjectContainer.java,v 1.28 2004/05/24 20:22:14 gummi Exp $
 * 
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 *  
 */
package com.idega.presentation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.faces.component.UIComponent;

import com.idega.event.IWPresentationState;
import com.idega.idegaweb.IWLocation;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.text.Text;
/**
 * A base class for Containers of PresentationObjects
 * 
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.3
 */
public class PresentationObjectContainer extends PresentationObject
{
	private List children;
	protected List allObjects = null;
	protected boolean goneThroughMain = false;
	protected boolean _locked = true;
	protected String _label = null;
	public PresentationObjectContainer()
	{
	}
	public List getChildren(){
		if (this.children == null)
		{
			//this.children = new PresentationObjectList(this);
			this.children=new ArrayList();
		}
		return this.children;
	}
	/**
	 * Add an object inside this container
	 */
	protected void add(int index, PresentationObject modObject)
	{
		try
		{
			if (modObject != null)
			{
				modObject.setParentObject(this);
				//        modObject.setLocation(this.getLocation());
				getChildren().add(index, modObject);
			}
		}
		catch (Exception ex)
		{
			//ExceptionWrapper exep = new ExceptionWrapper(ex,this);
		}
	}
	/**
	 * Add an object inside this container
	 */
	public void add(PresentationObject modObject)
	{
		try
		{
			if (modObject != null)
			{
				modObject.setParentObject(this);
				//        modObject.setLocation(this.getLocation());
				getChildren().add(modObject);
			}
		}
		catch (Exception ex)
		{
			//ExceptionWrapper exep = new ExceptionWrapper(ex,this);
		}
	}
	public void add(UIComponent component)
	{
		try
		{
			getChildren().add(component);
		}
		catch (Exception ex)
		{
			//ExceptionWrapper exep = new ExceptionWrapper(ex,this);
		}
	}
	public void add(Object moduleObject)
	{
		if (moduleObject instanceof PresentationObject)
		{
			add((PresentationObject) moduleObject);
		}
		else
		{
			System.err.println(
				"Not instance of PresentationObject and therefore cannot be added to PresentationObjectContainer: " + moduleObject);
		}
	}
	public void addAtBeginning(PresentationObject modObject)
	{
		modObject.setParentObject(this);
		//    modObject.setLocation(this.getLocation());
		getChildren().add(0, modObject);
	}
	/**
	 * Add an object inside this container - same as the add() function
	 * 
	 * @deprecated replaced by the add function
	 */
	public void addObject(PresentationObject modObject)
	{
		add(modObject);
	}
	/**
	 * Adds an simple string (Creates a Text object around it)
	 */
	public void add(String theText)
	{
		add(new Text(theText));
	}
	/**
	 * Adds an array of strings and creates an end of line character after each
	 * element
	 */
	public void add(String[] theTextArray)
	{
		for (int i = 0; i < theTextArray.length; i++)
		{
			add(theTextArray[i]);
			addBreak();
		}
	}
	public void addBreak()
	{
		Text text = Text.getBreak();
		add(text);
	}
	public void addText(String theText)
	{
		add(new Text(theText));
	}
	public void addText(String theText, String format)
	{
		Text text = new Text();
		if (format != null)
		{
			if (format.equals("bold"))
			{
				text.setBold();
			}
			else if (format.equals("italic"))
			{
				text.setItalic();
			}
			else if (format.equals("underline"))
			{
				text.setUnderline();
			}
		}
		add(text);
	}
	public void addText(int integerToInsert)
	{
		addText(Integer.toString(integerToInsert));
	}
	public PresentationObject getContainedObject(Class objectClass) {
		List objects = getChildren();
		if (objects != null) {
			Iterator iter = objects.iterator();
			while (iter.hasNext()) {
				Object element = (Object) iter.next();
				if (element.getClass() == objectClass) {
					return (PresentationObject) element;
				}
			}
		}
		return null;
	}
	public List getChildrenRecursive()
	{
		if (allObjects == null)
		{
			List toReturn = null;
			List children = this.getChildren();
			if (children != null)
			{
				toReturn = new ArrayList();
				toReturn.containsAll(children);
				Iterator iter = children.iterator();
				while (iter.hasNext())
				{
					Object item = iter.next();
					if (item instanceof PresentationObjectContainer)
					{
						toReturn.add(item);
						//if(!toReturn.contains(item)){
						List tmp = ((PresentationObjectContainer) item).getChildrenRecursive();
						if (tmp != null)
						{
							toReturn.addAll(tmp);
						}
						//}
					}
					else
					{
						toReturn.add(item);
					}
				}
			}
			allObjects = toReturn;
		}
		return allObjects;
	}
	public void resetAllContainedObjectsRecursive()
	{
		allObjects = null;
	}
	public boolean isEmpty()
	{
		return getChildren().isEmpty();
	}
	public void _main(IWContext iwc) throws Exception
	{
		if (!initializedInMain)
		{
			this.initInMain(iwc);
		}
		if (!goneThroughMain)
		{
			initVariables(iwc);
			try
			{
				//super.main(iwc);
				main(iwc);
			}
			catch (Exception ex)
			{
				add(new ExceptionWrapper(ex, this));
			}
			if (!isEmpty())
			{
				for (int index = 0; index < numberOfObjects(); index++)
				{
					PresentationObject tempobj = objectAt(index);
					try
					{
						if (tempobj != null)
						{
							if (tempobj != this)
							{
								tempobj._main(iwc);
							}
						}
					}
					catch (Exception ex)
					{
						add(new ExceptionWrapper(ex, this));
					}
				}
			}
		}
		goneThroughMain = true;
	}
	/**
	 * Empties the container of all PresentationObjects stored inside
	 */
	public void empty()
	{
		getChildren().clear();
		//theObjects.removeAll(theObjects);
	}
	protected void setChildren(List newChildren)
	{
		this.children = newChildren;
	}
	/*
	 * protected void prepareClone(PresentationObject newObjToCreate){ int
	 * number = numberOfObjects(); for (int i = 0; i < number; i++) {
	 * PresentationObject tempObj = this.objectAt(i);
	 * ((PresentationObjectContainer)newObjToCreate).add((PresentationObject)tempObj.clone()); }
	 *  // if (this.theObjects!=null){
	 * //((PresentationObjectContainer)newObjToCreate).setObjects((Vector)this.theObjects.clone()); // }
	 */
	public void _print(IWContext iwc) throws Exception
	{
		goneThroughMain = false;
		super._print(iwc);
	}
	/**
	 * The default implementation for the print function for a container.
	 * 
	 * This function is invoked on each request by the user for each
	 * PresentationObject instance (after main(iwc)).
	 * 
	 * Override this function where it is needed to print out the specified
	 * content. This function should only be overrided in idegaWeb Elements.
	 */
	public void print(IWContext iwc) throws Exception
	{
		initVariables(iwc);
		//Workaround for JRun - JRun has hardcoded content type text/html in
		// JSP pages
		//if(this.doPrint(iwc)){
		if (iwc.getLanguage().equals("WML"))
		{
			iwc.setContentType("text/vnd.wap.wml");
		}
		if (!isEmpty())
		{
			int numberofObjects = numberOfObjects();
			for (int index = 0; index < numberofObjects; index++)
			{
				PresentationObject tempobj = objectAt(index);
				try
				{
					if (tempobj != null)
					{
						tempobj._print(iwc);
						flush();
					}
				}
				catch (Exception ex)
				{
					ExceptionWrapper exep = new ExceptionWrapper(ex, this);
					exep._print(iwc);
				}
			}
		}
	}
	/**
	 *  
	 */
	public PresentationObject getContainedObject(int objectInstanceID)
	{
		List list = this.getChildren();
		if (list != null)
		{
			Iterator iter = list.iterator();
			while (iter.hasNext())
			{
				PresentationObject item = (PresentationObject) iter.next();
				if (item.getICObjectInstanceID() == objectInstanceID)
				{
					return item;
				}
				else if (item instanceof PresentationObjectContainer)
				{
					PresentationObject theReturn = ((PresentationObjectContainer) item).getContainedObject(objectInstanceID);
					if (theReturn != null)
					{
						return theReturn;
					}
				}
			}
		}
		return null;
	}
	/**
	 *  
	 */
	public PresentationObject getContainedObject(String objectInstanceID)
	{
		try
		{
			try
			{
				return (getContainedObject(Integer.parseInt(objectInstanceID)));
			}
			catch (NumberFormatException e)
			{
				System.out.println("PresentationObjectContainer#getContainedObject("+objectInstanceID+") - NumberFormatException");
				int objectInstanceIDInt = Integer.parseInt(objectInstanceID.substring(0, objectInstanceID.indexOf(".")));
				String index = objectInstanceID.substring(objectInstanceID.indexOf(".") + 1, objectInstanceID.length());
				if (index.indexOf(".") == -1)
				{
					return (((PresentationObjectContainer) getContainedObject(objectInstanceIDInt)).objectAt(Integer.parseInt(index)));
				}
				else
				{
					int xindex = Integer.parseInt(index.substring(0, index.indexOf(".")));
					int yindex = Integer.parseInt(index.substring(index.indexOf(".") + 1, index.length()));
					return (((Table) getContainedObject(objectInstanceIDInt)).containerAt(xindex, yindex));
				}
			}
		}
		catch (NullPointerException ex)
		{
			return (null);
		}
	}
	/**
	 *  
	 */
	public PresentationObject getContainedLabeledObject(String label)
	{
		List list = getChildren();
		if (list != null)
		{
			Iterator iter = list.iterator();
			while (iter.hasNext())
			{
				PresentationObject item = (PresentationObject) iter.next();
				if (item instanceof PresentationObjectContainer)
				{
					String itemLabel = ((PresentationObjectContainer) item).getLabel();
					if (itemLabel != null)
						if (itemLabel.equals(label))
							return (item);
					PresentationObject theReturn = ((PresentationObjectContainer) item).getContainedLabeledObject(label);
					if (theReturn != null)
					{
						return (theReturn);
					}
				}
			}
		}
		return (null);
	}
	/*
	 * public PresentationObject getContainedObject(String objectTreeID) { if
	 * (objectTreeID.indexOf(".") == -1) { return
	 * objectAt(Integer.parseInt(objectTreeID)); } else { String newString =
	 * objectTreeID.substring(objectTreeID.indexOf(".") +
	 * 1,objectTreeID.length()); String index =
	 * objectTreeID.substring(0,objectTreeID.indexOf("."));
	 * 
	 * PresentationObject obj = objectAt(Integer.parseInt(index)); if (obj
	 * instanceof PresentationObjectContainer){ return
	 * ((PresentationObjectContainer)obj).getContainedObject(newString); } else {
	 * return obj; } }
	 */
	/*
	 * public void updateTreeIDs() { if (!isEmpty()) { String thisTreeID =
	 * this.getTreeID(); int numberOfObjects = numberOfObjects(); for(int index = 0;
	 * index < numberOfObjects; index++) { PresentationObject tempobj =
	 * objectAt(index); if (tempobj != null) { if (tempobj != this) { try { if
	 * (thisTreeID == null) { String treeID = Integer.toString(index);
	 * tempobj.setTreeID(treeID); } else { String treeID = thisTreeID + "." +
	 * index; tempobj.setTreeID(treeID); } } catch(Exception ex) {
	 * ExceptionWrapper exep = new ExceptionWrapper(ex,this); add(exep); } } } } }
	 */
	/*
	 * public void setTreeID(String ID) { super.setTreeID(ID); updateTreeIDs();
	 */
	public int numberOfObjects()
	{
		return getChildren().size();
	}
	public PresentationObject objectAt(int index)
	{
		return (PresentationObject)getChildren().get(index);
	}
	public int getIndex(PresentationObject ob)
	{
		return getChildren().indexOf(ob);
	}
	/**
	 * Insert element at specified index
	 */
	public void insertAt(PresentationObject modObject, int index)
	{
		try
		{
			if (modObject != null)
			{
				modObject.setParentObject(this);
				//        modObject.setLocation(this.getLocation());
				getChildren().add(index, modObject);
			}
		}
		catch (Exception ex)
		{
			//ExceptionWrapper exep = new ExceptionWrapper(ex,this);
		}
	}
	/**
	 * Replace element at specified index
	 */
	/*
	 * public void setAt(PresentationObject modObject, int index) { try { if
	 * (theObjects == null) { this.theObjects = new ArrayList(); } if
	 * (modObject != null) { <<<<<<< PresentationObjectContainer.java
	 * modObject.setParentObject(this); //
	 * modObject.setLocation(this.getLocation());
	 * theObjects.setElementAt(modObject,index); =======
	 * modObject.setParentObject(this);
	 * theObjects.setElementAt(modObject,index); >>>>>>> 1.13 } }
	 * catch(Exception ex) { ExceptionWrapper exep = new
	 * ExceptionWrapper(ex,this); }
	 */
	public void removeAll(java.util.Collection c)
	{
		getChildren().removeAll(c);
	}
	public void _setIWContext(IWContext iwc)
	{
		setIWContext(iwc);
		if (!isEmpty())
		{
			for (int index = 0; index < numberOfObjects(); index++)
			{
				PresentationObject tempobj = objectAt(index);
				if (tempobj != null)
				{
					if (tempobj != this)
					{
						tempobj._setIWContext(iwc);
					}
				}
			}
		}
	}
	public Object _clone(IWUserContext iwc, boolean askForPermission)
	{
		if (askForPermission || iwc != null)
		{
			if (iwc.hasViewPermission(this))
			{
				return this.clone(iwc, askForPermission);
			}
			else
			{
				return NULL_CLONE_OBJECT;
			}
		}
		else
		{
			return this.clone();
		}
	}
	public Object clone()
	{
		return this.clone(null, false);
	}
	public Object clone(IWUserContext iwc, boolean askForPermission)
	{
		PresentationObjectContainer obj = null;
		try
		{
			obj = (PresentationObjectContainer) super.clone();
			obj._locked = this._locked;
			//if(!(this instanceof Table)){
			//if (this.theObjects != null)
			//{
				//obj.setObjects((Vector)this.theObjects.clone());
				ArrayList alChildren = (ArrayList)this.getChildren();
				List clonedChildren = (List)alChildren.clone();
				obj.setChildren(clonedChildren);
				//obj.setChildren((List)((ArrayList) this.getChildren()).clone());
				ListIterator iter = obj.getChildren().listIterator();
				while (iter.hasNext())
				{
					int index = iter.nextIndex();
					Object item = iter.next();
					//Object item = obj.theObjects.elementAt(index);
					if (item instanceof PresentationObject)
					{
						PresentationObject newObject = (PresentationObject) ((PresentationObject) item)._clone(iwc, askForPermission);
						//newObject.setParentObject(obj);
						//newObject.setLocation(this.getLocation());
						obj.getChildren().set(index, newObject);
						newObject.setParent(obj);
					}
				}
				//}
			//}
		}
		catch (Exception ex)
		{
			//obj.theObjects = new ArrayList();
			ex.printStackTrace(System.err);
		}
		return obj;
	}
	public boolean remove(PresentationObject obj)
	{
		return getChildren().remove(obj);
	}
	/**
	 * index lies from 0,length-1
	 */
	public Object set(int index, PresentationObject o)
	{
		o.setParentObject(this);
		//    o.setLocation(this.getLocation());
		return this.getChildren().set(index, o);
	}
	/**
	 *  
	 */
	public void lock()
	{
		_locked = true;
	}
	/**
	 *  
	 */
	public void unlock()
	{
		_locked = false;
	}
	/**
	 *  
	 */
	public boolean isLocked()
	{
		return (_locked);
	}
	/**
	 *  
	 */
	public void setLabel(String label)
	{
		_label = label;
	}
	/**
	 *  
	 */
	public String getLabel()
	{
		return (_label);
	}
	public PresentationObject getContainedICObjectInstance(int id)
	{
		/*
		 * System.err.println("-------------------------------------");
		 * System.err.println("getContainedICObjectInstance("+id+")"); if(this
		 * instanceof Page){ System.err.println("ibpageid = "+
		 * ((Page)this).getPageID()); }else{
		 * System.err.println("this.instanceId =
		 * "+this.getICObjectInstanceID()); }
		 */
		List l = this.getChildrenRecursive();
		if (l != null)
		{
			Iterator iter = l.iterator();
			while (iter.hasNext())
			{
				Object item = iter.next();
				//        System.err.println("ObjectinstanceID = "
				// +((PresentationObject)item).getICObjectInstanceID());
				if (item instanceof PresentationObject && (((PresentationObject) item).getICObjectInstanceID() == id))
				{
					return ((PresentationObject) item);
				}
			}
		}
		return null;
	}
	public void setLocation(IWLocation location, IWUserContext iwuc)
	{
		super.setLocation(location, iwuc);
		List l = this.getChildren();
		if (l != null)
		{
			Iterator iter = l.iterator();
			while (iter.hasNext())
			{
				Object item = iter.next();
				if (item instanceof PresentationObject)
				{
					((PresentationObject) item).setLocation(location, iwuc);
				}
				if (item instanceof StatefullPresentation)
				{
					IWPresentationState state = ((StatefullPresentation) item).getPresentationState(iwuc);
					if (state != null)
					{
						state.setLocation(location);
					}
				}
			}
		}
	}
	
	/*
	 * Overrided methods from JSF's UIComponent:
	 */


	public void addChild(UIComponent child){
		this.add((PresentationObject)child);
	}

	public void addChild(int index,UIComponent child){
		this.add(index,(PresentationObject)child);
	}
	
	public void clearChildren(){
		this.empty();
	}
	
	public UIComponent getChild(int index){
		return (UIComponent)getChildren().get(index);
	}
	
	public int getChildrenCount(){
		return this.getChildren().size();
	}
	
	public void removeChild(int index){
		this.getChildren().remove(index);
	}

	public void remove(UIComponent child){
		this.remove((PresentationObject)child);
	}
	
	protected class PresentationObjectList extends ArrayList{
		private UIComponent pContainer;
		PresentationObjectList(UIComponent container){
			pContainer=container;
		}
		public boolean add(Object child){
			UIComponent comp = (UIComponent)child;
			comp.setParent(pContainer);
			return super.add(comp);
		}
		public void add(int index,Object child){
			UIComponent comp = (UIComponent)child;
			comp.setParent(pContainer);
			super.add(index,comp);
		}
		public Object set(int index,Object child){
			UIComponent comp = (UIComponent)child;
			comp.setParent(pContainer);
			return super.set(index,child);
		}
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	public boolean isContainer() {
		return true;
	}
}
