package com.idega.presentation.ui;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;

/**
 * @author Joakim
 *
 */
public class InputContainer extends InterfaceObject {
	
	private PresentationObject inputObj;
	private Text text;
	private Table table;
	private int cellWidth=-1;
	
	public InputContainer(){
	}
	
	public InputContainer(String text,PresentationObject inputObject){
		setText(text);
		setInputObject(inputObject);
	}
	public InputContainer(Text text,PresentationObject inputObject){
		setText(text);
		setInputObject(inputObject);
	}
	/**
	 * @param obj
	 */
	private void setInputObject(PresentationObject inputObject) {
		inputObj=inputObject;
	}
	/**
	 * @param text
	 */
	private void setText(String theText) {
		if(this.text==null){
			setText(new Text(theText));
		}
		else{
			text.setText(theText);
		}
	}
	/**
	 * 
	 */
	public void _main(IWContext iwc) throws Exception{
		initTable();
		super._main(iwc);
		table._main(iwc);
		//this.main(iwc);
	}
	
	/**
	 * 
	 */
	private void initTable() {
		setTable(new Table(2,1));
	}

	public void main(IWContext iwc){
		
		//add(t);
		
		if(cellWidth!=-1){
			getTable().setWidth(1,cellWidth);
			getTable().setWidth(2,cellWidth);
		}
		getTable().add(text,1,1);
		getTable().add(inputObj,2,1);
	}
	
	
	/**
	 * 
	 */
	private Table getTable() {
		return table;
	}

	/**
	 * @param t
	 */
	private void setTable(Table t) {
		this.table=t;
	}

	/**
	 * @param i
	 */
	public void setCellWidth(int i) {
		cellWidth = i;
	}

	/**
	 * @param text
	 */
	public void setText(Text text) {
		this.text = text;
	}
	
	public Object clone() {
		InputContainer obj = null;
		obj = (InputContainer) super.clone();
		if(this.text!=null){
			obj.text=(Text)this.text.clone();
		}
		if(this.inputObj!=null){
			obj.inputObj=(PresentationObject)this.inputObj.clone();
		}
		if(this.table!=null){
			obj.table=(Table)this.table.clone();
		}
		return obj;
	}


	public void _print(IWContext iwc) throws Exception{
		this.table._print(iwc);
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(com.idega.presentation.IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
		if(inputObj!=null){
			if(inputObj instanceof InterfaceObject){
				((InterfaceObject)inputObj).handleKeepStatus(iwc);
			}
			
		}
		
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	public boolean isContainer() {
		return false;
	}
}