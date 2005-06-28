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
        out.print("( ")
            .print(left)
            .print(' ')
            .print(operator)
            .print(' ')
            .print(right)
            .print(" )");
    }
    
    public Set getTables(){
    		Set s = new HashSet();
    		s.addAll(left.getTables());
    		s.addAll(right.getTables());
    		return s; 
    }
    
    public List getValues(){
        	Vector l = new Vector();
        if(left instanceof PlaceHolder)
            l.addAll(((PlaceHolder) left).getValues());
        if(right instanceof PlaceHolder)
            l.addAll(((PlaceHolder)right).getValues());
        return l;
    }
    
    public Object clone(){
		BaseLogicGroup obj = (BaseLogicGroup)super.clone();
		if(left!=null){
			obj.left = (Criteria) this.left.clone();
		}
		
		if(right!=null){
			obj.right = (Criteria) this.right.clone();
		}
		return obj;
	}
    
    public Set getCriterias(){
    		Set s = new HashSet();
		s.add(left);
		s.add(right);
		return s; 
    }

}
