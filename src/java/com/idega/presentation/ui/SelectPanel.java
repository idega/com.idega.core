package com.idega.presentation.ui;

import com.idega.presentation.IWContext;

/**
 * @author laddi
 */
public class SelectPanel extends GenericSelect {

	/**
	 * The default size used for the <code>SelectPanel</code> if no size is given.
	 */
	protected static final int DEFAULT_SIZE = 3;
	
	/**
	 * The default constructor to construct a new <code>SelectPanel</code>.  Uses the name 'untitled' by default.
	 */
	public SelectPanel() {
		this("untitled");
	}

	/**
	 * Constructs a new <code>SelectPanel</code> with the name given.
	 */
	public SelectPanel(String name) {
		super(name);
		setMultiple(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.presentation.ui.GenericSelect#setMultiple(boolean)
	 */
	public void setMultiple(boolean multiple) {
		super.setMultiple(multiple);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.presentation.ui.GenericSelect#getMultiple()
	 */
	public boolean getMultiple() {
		return super.getMultiple();
	}

	/**
	 * Sets the number of rows to display for the <code>SelectPanel</code>
	 * 
	 * @param size
	 *          The number of rows to display.
	 */
	public void setSize(int size) {
		setMarkupAttribute("size", String.valueOf(size));
	}

	/**
	 * Returns the number of rows to display in the <code>SelectPanel</code>
	 * 
	 * @return The number of rows to display.
	 */
	public int getSize() {
		if (isMarkupAttributeSet("size")) {
			return Integer.parseInt(getMarkupAttribute("size"));
		}
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.presentation.PresentationObject#print(com.idega.presentation.IWContext)
	 */
	public void print(IWContext iwc) throws Exception {
		if (!getMultiple() && getSize() <= 1) {
			setSize(DEFAULT_SIZE);
		}
		super.print(iwc);
	}

	/**
	 * Sets the selected elements within the <code>SelectPanel</code>
	 * 
	 * @param values
	 *          The menu elements to set as selected.
	 */
	public void setSelectedElements(int[] values) {
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				int value = values[i];
				setSelectedOption(String.valueOf(value));
			}
		}
	}

	/**
	 * Sets the selected elements within the <code>SelectPanel</code>
	 * 
	 * @param values
	 *          The menu elements to set as selected.
	 */
	public void setSelectedElements(String[] values) {
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				String value = values[i];
				setSelectedOption(value);
			}
		}
	}
}