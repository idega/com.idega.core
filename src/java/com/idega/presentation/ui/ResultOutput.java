package com.idega.presentation.ui;

import java.util.List;
import java.util.Vector;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Script;

/**
 * Title: ResultOutput Description: Copyright: Copyright (c) 2001 - 2004
 * Company: idega
 * 
 * @author <a href="mailto:gimmi@idega.is">Grimur Jonsson </a>
 * @version 1.3
 */

public class ResultOutput extends GenericInput {
	
	public static final String OPERATOR_PLUS = "+";
	public static final String OPERATOR_SUBTRACT = "-";
	public static final String OPERATOR_MULTIPLY = "*";
	public static final String OPERATOR_DIVIDE = "/";

	protected String functionName = "resultOutputFunction";
	
	protected List moduleObjects = new Vector();
	protected List onChangeVector = new Vector();
	protected List extraTextVector = new Vector();
	protected List operatorVector = new Vector();
	
	private int size = -1;
	private String content;
	private String name;
	private String extraForTotal = "";
	private String extraForEach = "";
	
	private static final String EMPTY_STRING = "";
	private static final String UNSPECIFIC = "unspecific";
	private static final String INPUT_APPEND = "_RO_input";
	
	public ResultOutput() {
		this(UNSPECIFIC, EMPTY_STRING);
	}
	
	public ResultOutput(String name) {
		this(name, EMPTY_STRING);
	}
	
	public ResultOutput(String name, String content) {
		this.functionName = name;
		StringBuffer nameBuffer = new StringBuffer(name);
		nameBuffer.append(INPUT_APPEND);
		this.name = nameBuffer.toString();
		this.content = content;
	}
	
	public void main(IWContext iwc) throws Exception {
		//super._main(iwc);
		Script script = getParentPage().getAssociatedScript();
		
		this.setName(name);
		this.setValue(content);
		PresentationObject moduleObject = null;
		String extraTxt = "";
		
		if (moduleObjects.size() > 0) {
			StringBuffer theScript = new StringBuffer();
			theScript.append("function " + functionName + "(myForm) {");
			theScript.append("\n  myForm." + this.getName()
					+ ".value=(");
			for (int i = 0; i < moduleObjects.size(); i++) {
				if (i != 0) {
					theScript.append((String) operatorVector.get(i));
				}
				if (moduleObjects.get(i) instanceof TextInput) {
					moduleObject = (TextInput) moduleObjects.get(i);
				} else if (moduleObjects.get(i) instanceof IntegerInput) {
					moduleObject = (IntegerInput) moduleObjects.get(i);
				} else if (moduleObjects.get(i) instanceof DoubleInput) {
					moduleObject = (DoubleInput) moduleObjects.get(i);
				} else if (moduleObjects.get(i) instanceof FloatInput) {
					moduleObject = (FloatInput) moduleObjects.get(i);
				} else if (moduleObjects.get(i) instanceof ResultOutput) {
					moduleObject = (ResultOutput) moduleObjects.get(i);
				}
				extraTxt = (String) extraTextVector.get(i);
				theScript.append("(1*myForm." + moduleObject.getName()
						+ ".value");
				theScript.append(")");
				theScript.append(extraForEach);
				theScript.append(extraTxt);
			}
			theScript.append(")");
			theScript.append(extraForTotal);
			theScript.append(";");
			
			theScript.append("\n}");
			
			script.addFunction(functionName, theScript.toString());
		}
		
		this.setDisabled(true);
		for (int i = 0; i < onChangeVector.size(); i++) {
			this.setOnChange((String) onChangeVector.get(i));
		}
		
	}
	
	public void setSize(int size) {
		setMarkupAttribute("size", Integer.toString(size));
	}
	
	public void setOnChange(String s) {
		onChangeVector.add(s);
	}
	
	public List getAddedObjects() {
		return moduleObjects;
	}
	
	public String getName() {
		return name;
	}
	
	public void setExtraForEach(String s) {
		extraForEach = s;
	}
	
	public void setExtraForTotal(String s) {
		extraForTotal = s;
	}
	
	public void add(PresentationObject mo) {
		add(mo, EMPTY_STRING);
	}
	
	public void add(PresentationObject mo, String extraText) {
		add(mo, OPERATOR_PLUS, extraText);
	}
	
	public void add(PresentationObject mo, String operatori, String extraText) {
		if (mo instanceof TextInput) {
			TextInput temp = (TextInput) mo;
			temp.setOnChange(functionName + "(this.form)");
			moduleObjects.add(temp);
			operatorVector.add(operatori);
			if (extraText == null) { 
				extraText = "";
			}
			extraTextVector.add(extraText);
		} else if (mo instanceof IntegerInput) {
			IntegerInput temp = (IntegerInput) mo;
			temp.setOnChange(functionName + "(this.form)");
			moduleObjects.add(temp);
			operatorVector.add(operatori);
			if (extraText == null) {
				extraText = "";
			}
			extraTextVector.add(extraText);
		} else if (mo instanceof DoubleInput) {
			DoubleInput temp = (DoubleInput) mo;
			temp.setOnChange(functionName + "(this.form)");
			moduleObjects.add(temp);
			operatorVector.add(operatori);
			if (extraText == null) {
				extraText = "";
			}
			extraTextVector.add(extraText);
		} else if (mo instanceof FloatInput) {
			FloatInput temp = (FloatInput) mo;
			temp.setOnChange(functionName + "(this.form)");
			moduleObjects.add(temp);
			operatorVector.add(operatori);
			if (extraText == null) {
				extraText = "";
			}
			extraTextVector.add(extraText);
		} else if (mo instanceof ResultOutput) {
			handleAddResultOutput((ResultOutput) mo, operatori);
		}
	}
	
	private void handleAddResultOutput(ResultOutput resOut, String operatori) {
		List list = resOut.getAddedObjects();
		for (int a = 0; a < list.size(); a++) {
			if (list.get(a) instanceof TextInput) {
				TextInput text = (TextInput) list.get(a);
				text.setOnChange(functionName + "(this.form)");
			} else if (list.get(a) instanceof IntegerInput) {
				IntegerInput i = (IntegerInput) list.get(a);
				i.setOnChange(functionName + "(this.form)");
			} else if (list.get(a) instanceof DoubleInput) {
				DoubleInput f = (DoubleInput) list.get(a);
				f.setOnChange(functionName + "(this.form)");
			} else if (list.get(a) instanceof FloatInput) {
				FloatInput f = (FloatInput) list.get(a);
				f.setOnChange(functionName + "(this.form)");
			} else if (list.get(a) instanceof ResultOutput) {
				handleAddResultOutput((ResultOutput) list.get(a), operatori);
			}
		}
		moduleObjects.add(resOut);
		operatorVector.add(operatori);
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public void handleKeepStatus(IWContext iwc) {
		String pname = iwc.getParameter(name);
		if (pname != null) {
			this.content = pname;
		}
	}
}