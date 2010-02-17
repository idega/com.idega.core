package com.idega.data.query;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.idega.data.query.output.Output;

/**
 * See OR and AND
 * 
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 */
public abstract class BaseLogicGroup extends Criteria implements PlaceHolder {

	private String operator;
	private Criteria left;
	private Criteria right;

	public BaseLogicGroup(String operator, Criteria left, Criteria right) {
		this.left = left;
		this.right = right;
		this.operator = operator;
	}

	public void write(Output out) {
		out.print("( ").print(this.left).print(' ').print(this.operator).print(' ').print(this.right).print(" )");
	}

	public Set getTables() {
		Set s = new HashSet();
		s.addAll(this.left.getTables());
		s.addAll(this.right.getTables());
		return s;
	}

	public List getValues() {
		Vector l = new Vector();
		if (this.left instanceof PlaceHolder) {
			l.addAll(((PlaceHolder) this.left).getValues());
		}
		if (this.right instanceof PlaceHolder) {
			l.addAll(((PlaceHolder) this.right).getValues());
		}
		return l;
	}

	public Object clone() {
		BaseLogicGroup obj = (BaseLogicGroup) super.clone();
		if (this.left != null) {
			obj.left = (Criteria) this.left.clone();
		}

		if (this.right != null) {
			obj.right = (Criteria) this.right.clone();
		}
		return obj;
	}

	public Set getCriterias() {
		Set s = new HashSet();
		s.add(this.left);
		s.add(this.right);
		return s;
	}
}