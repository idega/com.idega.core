package com.idega.data.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	@Override
	public void write(Output out) {
		out.print("( ").print(this.left).print(' ').print(this.operator).print(' ').print(this.right).print(" )");
	}

	@Override
	public Set<Table> getTables() {
		Set<Table> s = new HashSet<Table>();
		s.addAll(this.left.getTables());
		s.addAll(this.right.getTables());
		return s;
	}

	@Override
	public List<Object> getValues() {
		List<Object> l = new ArrayList<Object>();
		if (this.left instanceof PlaceHolder) {
			l.addAll(((PlaceHolder) this.left).getValues());
		}
		if (this.right instanceof PlaceHolder) {
			l.addAll(((PlaceHolder) this.right).getValues());
		}
		return l;
	}

	@Override
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

	public Set<Criteria> getCriterias() {
		Set<Criteria> s = new HashSet<Criteria>();
		s.add(this.left);
		s.add(this.right);
		return s;
	}
}