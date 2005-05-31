package com.idega.data.query;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.idega.data.IDOCompositePrimaryKeyException;
import com.idega.data.IDOEntityDefinition;
import com.idega.data.IDOEntityField;
import com.idega.data.IDORelationshipException;
import com.idega.data.query.output.Output;
import com.idega.data.query.output.Outputable;

/**
 * @author <a href="joe@truemesh.com">Joe Walnes </a>
 */
public class SelectQuery implements Outputable,PlaceHolder,Cloneable,Flag {

	public static final int indentSize = 4;

	private Table baseTable;

	private Vector columns;
	private Vector leftJoins;
	private Vector criteria;
	private Vector order;
	private Vector groupBy;
	private boolean _countQuery = false;
    private boolean _distinct =false;
    private boolean flag = false;
    

	public SelectQuery(Table baseTable) {
		this.baseTable = baseTable;
		columns = new Vector();
		leftJoins = new Vector();
		criteria = new Vector();
		order = new Vector();
		groupBy = new Vector();
	}

	public Table getBaseTable() {
		return baseTable;
	}

	public void addColumn(Column column) {
		columns.add(column);
	}

	/**
	 * Syntax sugar for addColumn(Column).
	 */
	public void addColumn(Table table, String columname) {
		addColumn(table.getColumn(columname));
	}

	/**
	 * Syntax sugar for addColumn(Column).
	 */
	public void addColumn(Table table, String columname, boolean distinct) {
		Column column = table.getColumn(columname);
		if (distinct) {
			column.setAsDistinct();
		}
		addColumn(column);
	}

	public void addGroupByColumn(Column column) {
		groupBy.add(column);
	}

	/**
	 * Syntax sugar for addColumn(Column).
	 */
	public void addGroupByColumn(Table table, String columname) {
		groupBy.add(table.getColumn(columname));
	}

	public void removeColumn(Column column) {
		columns.remove(column);
	}
	
	public void removeAllColumns() {
		columns.clear();
	}

	public void removeGroupByColumn(Column column) {
		groupBy.remove(column);
	}
	
	public void removeAllGroupByColumns() {
		groupBy.clear();
	}

	public List listColumns() {
		return Collections.unmodifiableList(columns);
	}

	public List listGroupByColumns() {
		return Collections.unmodifiableList(groupBy);
	}

	public void addCriteria(Criteria criteria) {
		this.criteria.add(criteria);
	}

	public void removeCriteria(Criteria criteria) {
		this.criteria.remove(criteria);
	}
	
	public void removeAllCriteria(){
		this.criteria.clear();
	}

	public List listCriteria() {
		return Collections.unmodifiableList(criteria);
	}

	/**
	 * Syntax sugar for addCriteria(JoinCriteria)
	 */
	public void addJoin(Table srcTable, String srcColumnname, Table destTable, String destColumnname) {
		addCriteria(new JoinCriteria(srcTable.getColumn(srcColumnname), destTable.getColumn(destColumnname)));
	}

	public void addJoin(Table srcTable, Table destTable) throws IDORelationshipException {
		if (srcTable.hasEntityDefinition() && destTable.hasEntityDefinition()) {
			IDOEntityDefinition source = srcTable.getEntityDefinition();
			IDOEntityDefinition destination = destTable.getEntityDefinition();

			IDOEntityField[] fields = source.getFields();
			for (int i = 0; i < fields.length; i++) {
				IDOEntityField field = fields[i];
				if (field.isPartOfManyToOneRelationship()) {
					if (field.getManyToOneRelated().equals(destination)) {
						try {
							addCriteria(new JoinCriteria(srcTable.getColumn(field.getSQLFieldName().toLowerCase()), destTable.getColumn(destination.getPrimaryKeyDefinition().getField().getSQLFieldName().toLowerCase())));
						}
						catch (IDOCompositePrimaryKeyException e) {
							throw new IDORelationshipException(e.getMessage());
						}
						return;
					}
				}
			}

			addManyToManyJoin(srcTable, destTable);
			return;
		}
		throw new IDORelationshipException("No relation found between tables " + srcTable.getName().toUpperCase() + " and " + destTable.getName().toUpperCase());
	}

	public void addJoin(Table srcTable, Table destTable, String columnName) throws IDORelationshipException {
		if (srcTable.hasEntityDefinition() && destTable.hasEntityDefinition()) {
			IDOEntityDefinition source = srcTable.getEntityDefinition();
			IDOEntityDefinition destination = destTable.getEntityDefinition();

			IDOEntityField field = source.findFieldByUniqueName(columnName);
			if (field.isPartOfManyToOneRelationship()) {
				if (field.getManyToOneRelated().equals(destination)) {
					try {
						addCriteria(new JoinCriteria(srcTable.getColumn(field.getSQLFieldName().toLowerCase()), destTable.getColumn(destination.getPrimaryKeyDefinition().getField().getSQLFieldName().toLowerCase())));
					}
					catch (IDOCompositePrimaryKeyException e) {
						throw new IDORelationshipException(e.getMessage());
					}
					return;
				}
			}

			addManyToManyJoin(srcTable, destTable);
		}
		throw new IDORelationshipException("No relation found between tables " + srcTable.getName().toUpperCase() + " and " + destTable.getName().toUpperCase() + " for column = " + columnName.toUpperCase());
	}

	public void addManyToManyJoin(Table srcTable, Table destTable) throws IDORelationshipException {
		addManyToManyJoin(srcTable, destTable, null);
	}
	
	public void addManyToManyJoin(Table srcTable, Table destTable, String alias) throws IDORelationshipException {
		if (srcTable.hasEntityDefinition() && destTable.hasEntityDefinition()) {
			IDOEntityDefinition source = srcTable.getEntityDefinition();
			IDOEntityDefinition destination = destTable.getEntityDefinition();

			IDOEntityDefinition[] definitions = source.getManyToManyRelatedEntities();
			if (definitions != null && definitions.length > 0) {
				for (int i = 0; i < definitions.length; i++) {
					IDOEntityDefinition definition = definitions[i];
					if (destination.equals(definition)) {
						try {
							String middleTableName = source.getMiddleTableNameForRelation(destination.getSQLTableName());
							if (middleTableName == null) { throw new IDORelationshipException("Middle table not found for tables " + srcTable.getName().toUpperCase() + " and " + destTable.getName().toUpperCase()); }
	
							Table middleTable = new Table(middleTableName, alias);
	
							addCriteria(new JoinCriteria(srcTable.getColumn(source.getPrimaryKeyDefinition().getField().getSQLFieldName().toLowerCase()), middleTable.getColumn(source.getPrimaryKeyDefinition().getField().getSQLFieldName().toLowerCase())));
							addCriteria(new JoinCriteria(middleTable.getColumn(destination.getPrimaryKeyDefinition().getField().getSQLFieldName().toLowerCase()), destTable.getColumn(destination.getPrimaryKeyDefinition().getField().getSQLFieldName().toLowerCase())));
						}
						catch (IDOCompositePrimaryKeyException e) {
							throw new IDORelationshipException(e.getMessage());
						}
						return;
					}
				}
			}
		}
		throw new IDORelationshipException("No relation found between tables " + srcTable.getName().toUpperCase() + " and " + destTable.getName().toUpperCase());
	}

	public void addOrder(Order order) {
		this.order.add(order);
	}

	/**
	 * Syntax sugar for addOrder(Order).
	 */
	public void addOrder(Table table, String columnname, boolean ascending) {
		addOrder(new Order(table.getColumn(columnname), ascending));
	}

	public void removeOrder(Order order) {
		this.order.remove(order);
	}

	public void removeAllOrder() {
		this.order.clear();
	}

	public List listOrder() {
		return Collections.unmodifiableList(order);
	}

	public String toString() {
		//return ToStringer.toString(this);
	    Output out = new Output("    ");
	    out.flag(isFlagged());
        this.write(out);
        return out.toString();
	}
	
	public String toString(boolean flag){
	    Output out =new Output("");
	    out.flag(flag);
	    this.write(out);
	    return out.toString();
	}

	public void write(Output out) {

		out.println("SELECT");
		
		 if (_countQuery) {
        	out.indent();
        	out.println("COUNT(");
        }
        
        if(_distinct){
        	out.indent();
        	out.print(" distinct ");
        }

		// Add columns to select
		out.indent();
		appendList(out, columns, ",");
		out.unindent();
		
		if (_countQuery) {
        	out.println(")");
        	out.unindent();
        }

		// Add tables to select from
		out.println("FROM");

		// Determine all tables used in query
		out.indent();
		if(leftJoins.isEmpty()){
			appendList(out,findAllUsedTables(), ",");
		} else {
			Vector v = new Vector();
			v.addAll( findAllUsedTables());
			for (Iterator iter = leftJoins.iterator(); iter.hasNext();) {
				LeftJoin join = (LeftJoin) iter.next();
				v.removeAll(join.getTables());
				v.add(join);
			}
			appendList(out,v, ",");
		}
		out.unindent();

		// Add criteria
		if (criteria.size() > 0) {
			out.println("WHERE");
			out.indent();
			appendList(out, criteria, "AND");
			out.unindent();
		}
		
		// Add group by
		if (groupBy.size() > 0) {
			out.println("GROUP BY");
			out.indent();
			appendList(out, groupBy, ",");
			out.unindent();
		}
		
		// Add order
		if (order.size() > 0) {
			out.println("ORDER BY");
			out.indent();
			appendList(out, order, ",");
			out.unindent();
		}

	}
	
	/**
	 * Recurse through criterias and get the correct order of placement values
	 * @return
	 */
	public List getValues(){
	    Vector list = new Vector();
	    for (Iterator iter = criteria.iterator(); iter.hasNext();) {
            Criteria crit = (Criteria) iter.next();
            if(crit instanceof PlaceHolder)
                list.addAll(((PlaceHolder)crit).getValues());
            
        }
	    return list;
	}


	/**
	 * Iterate through a Collection and append all entries (using .toString()) to
	 * a StringBuffer.
	 */
	private void appendList(Output out, Collection collection, String seperator) {
		Iterator i = collection.iterator();
		boolean hasNext = i.hasNext();

		while (hasNext) {
			Outputable curr = (Outputable) i.next();
			hasNext = i.hasNext();
			if (curr != null) {
				curr.write(out);
				out.print(' ');
				if (hasNext) {
					out.print(seperator);
				}
				out.println();
			}
		}
	}

	/**
	 * Find all the tables used in the query (from columns, criteria and order).
	 * 
	 * @return List of {@link com.truemesh.squiggle.Table}s
	 */
	private Set findAllUsedTables() {

		Set allTables = new HashSet();
		allTables.add(baseTable);

		{ // Get all tables used by columns
			Iterator i = columns.iterator();
			while (i.hasNext()) {
				Table curr = ((Column) i.next()).getTable();
				if (curr != null && !curr.getName().equals("")) {
					allTables.add(curr);
				}
			}
		}

		{ // Get all tables used by criteria
			Iterator i = criteria.iterator();
			while (i.hasNext()) {
				Criteria curr = (Criteria) i.next();
				if (curr.getTables() != null) {
					allTables.addAll(curr.getTables());
				}
			}
		}

		{ // Get all tables used by columns
			Iterator i = order.iterator();
			while (i.hasNext()) {
				Order curr = (Order) i.next();
				Table c = curr.getColumn().getTable();
				if (c != null) {
					allTables.add(c);
				}
			}
		}

		return allTables;
	}
	
	/**
	 * @param countQuery The countQuery to set.
	 */
	public void setAsCountQuery(boolean countQuery) {
		_countQuery = countQuery;
	}
	
	/**
	 * 
	 * @param distinct The distinct to set
	 */
	public void setAsDistinct(boolean distinct){
		_distinct = distinct;
	}
	
	public Object clone(){
		SelectQuery obj = null;
		
		try {
			obj = (SelectQuery) super.clone();		
			
			obj.baseTable = baseTable;
			
			//obj.columns = (Vector)columns.clone();
			//obj.criteria = (Vector)criteria.clone();
			//obj.order = (Vector)order.clone();
			//obj.groupBy = (Vector)groupBy.clone();
			
			obj.columns = new Vector();
			if(this.columns.size()>0){
				obj.columns.setSize(this.columns.size());
				Collections.copy(obj.columns,this.columns);
			}
			obj.leftJoins = new Vector();
			if(this.leftJoins.size()>0){
				obj.leftJoins.setSize(this.leftJoins.size());
				Collections.copy(obj.leftJoins,this.leftJoins);
			}
			obj.criteria = new Vector();
			if(this.criteria.size()>0){
				obj.criteria.setSize(this.criteria.size());
				Collections.copy(obj.criteria,this.criteria);
			}
			obj.order = new Vector();
			if(this.order.size()>0){
				obj.order.setSize(this.order.size());
				Collections.copy(obj.order,this.order);
			}
			obj.groupBy = new Vector();
			if(this.groupBy.size()>0){
				obj.groupBy.setSize(this.groupBy.size());
				Collections.copy(obj.groupBy,this.groupBy);
			}
			
			obj._countQuery = _countQuery;
			obj._distinct = _distinct;

		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return obj;
	}
	
	public Collection getCriteria(){
		return criteria;
	}
	
	public Collection getOrder(){
		return order;
	}
	
    public void flag(boolean flag) {
        this.flag = flag;
    }
    public boolean isFlagged() {
        return this.flag;
    }

	/**
	 * @param resultTable
	 * @param columnName
	 * @param table
	 * @param columnName2
	 */
	public void addLeftJoin(Table table1, String columnName1, Table table2, String columnName2) {
		addLeftJoin(new LeftJoin(table1.getColumn(columnName1), table2.getColumn(columnName2)));
	}


	public void addLeftJoin(LeftJoin join) {
		leftJoins.add(join);
	}

	public void clearLeftJoins() {
	    leftJoins.clear();
	}
}