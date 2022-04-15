package com.idega.presentation.ui;

import java.util.List;
import java.util.Vector;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Script;
import com.idega.util.text.TextSoap;

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

	private String content;
	private String name;
	private String extraForTotal = "";
	private String extraForEach = "";
	private String formatFunction;

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

	private void addCalculationScript() {
		if (this.moduleObjects.size() < 0) {
			return;
		}
		Script script = getParentPage().getAssociatedScript();
		StringBuffer theScript = new StringBuffer();
		theScript.append("\n function " + this.functionName + "(myForm) {");
		theScript.append("\n\t var value = (");
		for (int i = 0; i < this.moduleObjects.size(); i++) {
			if (i != 0) {
				theScript.append((String) this.operatorVector.get(i));
			}
			PresentationObject moduleObject = (PresentationObject) this.moduleObjects.get(i);;
			String extraTxt = (String) this.extraTextVector.get(i);
			theScript.append("(1*myForm." + moduleObject.getName()
					+ ".value");
			theScript.append(")");
			theScript.append(this.extraForEach);
			theScript.append(extraTxt);
		}
		theScript.append(")");
		theScript.append(this.extraForTotal);
		theScript.append(";");
		if(this.formatFunction != null) {
			theScript.append(
					"\n\t value = "
							+ formatFunction
							+ "(value);"
			);
		}
		theScript.append(
				"\n\t  myForm."
						+ this.getName()
						+ ".value=value;"
		);
		theScript.append("\n }");
		script.addFunction(this.functionName, theScript.toString());
	}

	@Override
	public void main(IWContext iwc) throws Exception {
		//super._main(iwc);
		Script script = getParentPage().getAssociatedScript();

		this.setName(this.name);
		this.setValue(this.content);
		PresentationObject moduleObject = null;
		String extraTxt = "";

		if (this.moduleObjects.size() > 0) {
			StringBuffer theScript = new StringBuffer();
			theScript.append("function " + this.functionName + "(myForm) {");
			theScript.append("\n  myForm." + this.getName()
					+ ".value=(");
			for (int i = 0; i < this.moduleObjects.size(); i++) {
				if (i != 0) {
					theScript.append((String) this.operatorVector.get(i));
				}
				if (this.moduleObjects.get(i) instanceof TextInput) {
					moduleObject = (TextInput) this.moduleObjects.get(i);
				} else if (this.moduleObjects.get(i) instanceof IntegerInput) {
					moduleObject = (IntegerInput) this.moduleObjects.get(i);
				} else if (this.moduleObjects.get(i) instanceof DoubleInput) {
					moduleObject = (DoubleInput) this.moduleObjects.get(i);
				} else if (this.moduleObjects.get(i) instanceof FloatInput) {
					moduleObject = (FloatInput) this.moduleObjects.get(i);
				} else if (this.moduleObjects.get(i) instanceof ResultOutput) {
					moduleObject = (ResultOutput) this.moduleObjects.get(i);
				}
				extraTxt = (String) this.extraTextVector.get(i);
				theScript.append("(1*myForm." + moduleObject.getName()
						+ ".value");
				theScript.append(")");
				theScript.append(this.extraForEach);
				theScript.append(extraTxt);
			}
			theScript.append(")");
			theScript.append(this.extraForTotal);
			theScript.append(";");

			theScript.append("\n}");

			script.addFunction(this.functionName, theScript.toString());
		}
		addCalculationScript();

		this.setDisabled(true);
		for (int i = 0; i < this.onChangeVector.size(); i++) {
			this.setOnKeyUp((String) this.onChangeVector.get(i));
		}

	}

	public void setSize(int size) {
		setMarkupAttribute("size", Integer.toString(size));
	}

	@Override
	public void setOnChange(String s) {
		this.onChangeVector.add(s);
	}

	public List getAddedObjects() {
		return this.moduleObjects;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public void setExtraForEach(String s) {
		this.extraForEach = s;
	}

	public void setExtraForTotal(String s) {
		this.extraForTotal = s;
	}

	@Override
	public void add(PresentationObject mo) {
		add(mo, EMPTY_STRING);
	}

	public void add(PresentationObject mo, String extraText) {
		add(mo, OPERATOR_PLUS, extraText);
	}

	public void add(PresentationObject mo, String operatori, String extraText) {
		if (mo instanceof TextInput) {
			TextInput temp = (TextInput) mo;
			temp.setOnKeyUp(this.functionName + "(this.form)");
			this.moduleObjects.add(temp);
			this.operatorVector.add(operatori);
			if (extraText == null) {
				extraText = "";
			}
			this.extraTextVector.add(extraText);
		} else if (mo instanceof IntegerInput) {
			IntegerInput temp = (IntegerInput) mo;
			temp.setOnKeyUp(this.functionName + "(this.form)");
			this.moduleObjects.add(temp);
			this.operatorVector.add(operatori);
			if (extraText == null) {
				extraText = "";
			}
			this.extraTextVector.add(extraText);
		} else if (mo instanceof DoubleInput) {
			DoubleInput temp = (DoubleInput) mo;
			temp.setOnChange(this.functionName + "(this.form)");
			this.moduleObjects.add(temp);
			this.operatorVector.add(operatori);
			if (extraText == null) {
				extraText = "";
			}
			this.extraTextVector.add(extraText);
		} else if (mo instanceof FloatInput) {
			FloatInput temp = (FloatInput) mo;
			temp.setOnChange(this.functionName + "(this.form)");
			this.moduleObjects.add(temp);
			this.operatorVector.add(operatori);
		} else if (mo instanceof DropdownMenu) {
			DropdownMenu temp = (DropdownMenu) mo;
			temp.setOnChange(this.functionName + "(this.form)");
			this.moduleObjects.add(temp);
			this.operatorVector.add(operatori);
			if (extraText == null) {
				extraText = "";
			}
			this.extraTextVector.add(extraText);
		} else if (mo instanceof ResultOutput) {
			handleAddResultOutput((ResultOutput) mo, operatori);
			this.extraTextVector.add(extraText);
		}
	}

	/**
	 * If this object as other js registered  to the same action, this action will happen last
	 * @param obj
	 */
	public void addTrigger(InterfaceObject obj) {
		if (obj instanceof TextInput) {
			TextInput temp = (TextInput) obj;
			String sName = this.functionName + "(this.form)";
			String tmp = temp.getMarkupAttribute(InterfaceObject.ACTION_ON_KEY_UP);
			tmp = TextSoap.findAndCut(tmp, sName+";");
			temp.setMarkupAttribute(InterfaceObject.ACTION_ON_KEY_UP, tmp);
			temp.setOnKeyUp(sName);
		} else if (obj instanceof DropdownMenu) {
			DropdownMenu temp = (DropdownMenu) obj;
			temp.setOnChange(this.functionName + "(this.form)");
		} else if (obj instanceof CheckBox) {
			((CheckBox) obj).setOnClick(this.functionName + "(this.form)");
		} else {
			obj.setOnChange(this.functionName + "(this.form)");
		}
	}

	private void handleAddResultOutput(ResultOutput resOut, String operatori) {
		List list = resOut.getAddedObjects();
		for (int a = 0; a < list.size(); a++) {
			if (list.get(a) instanceof TextInput) {
				TextInput text = (TextInput) list.get(a);
				text.setOnKeyUp(this.functionName + "(this.form)");
			} else if (list.get(a) instanceof IntegerInput) {
				IntegerInput i = (IntegerInput) list.get(a);
				i.setOnKeyUp(this.functionName + "(this.form)");
			} else if (list.get(a) instanceof DoubleInput) {
				DoubleInput f = (DoubleInput) list.get(a);
				f.setOnChange(this.functionName + "(this.form)");
			} else if (list.get(a) instanceof FloatInput) {
				FloatInput f = (FloatInput) list.get(a);
				f.setOnChange(this.functionName + "(this.form)");
			} else if (list.get(a) instanceof ResultOutput) {
				handleAddResultOutput((ResultOutput) list.get(a), operatori);
			}
		}
		this.moduleObjects.add(resOut);
		this.operatorVector.add(operatori);
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public void handleKeepStatus(IWContext iwc) {
		try {
			super.handleKeepStatus(iwc);
		} catch (AssertionError e) {
			return;
		}

		String pname = iwc.getParameter(this.name);
		if (pname != null) {
			this.content = pname;
		}
	}

	public String getFormatFunction() {
		return formatFunction;
	}

	public void setFormatFunction(String formatFunction) {
		this.formatFunction = formatFunction;
	}
}