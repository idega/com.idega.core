package com.idega.data.query;

import java.util.HashSet;
import java.util.Set;

import com.idega.data.query.output.Output;

/**
 * See OR and AND
 * 
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 */
public abstract class BaseLogicGroup extends Criteria {
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

}
