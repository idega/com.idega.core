package com.idega.data.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

	private List<Column> columns;
	private List<LeftJoin> leftJoins;
	private List<Criteria> criteria;
	private List<Order> order;
	private List<Column> groupBy;
	private boolean _countQuery = false;
    private boolean _distinct =false;
    private boolean flag = false;


	public SelectQuery(Table baseTable) {
		this.baseTable = baseTable;
		this.columns = new ArrayList<Column>();
		this.leftJoins = new ArrayList<LeftJoin>();
		this.criteria = new ArrayList<Criteria>();
		this.order = new ArrayList<Order>();
		this.groupBy = new ArrayList<Column>();
	}

	public Table getBaseTable() {
		return this.baseTable;
	}

	public void addColumn(Column column) {
		this.columns.add(column);
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
		this.groupBy.add(column);
	}

	/**
	 * Syntax sugar for addColumn(Column).
	 */
	public void addGroupByColumn(Table table, String columname) {
		this.groupBy.add(table.getColumn(columname));
	}

	public void removeColumn(Column column) {
		this.columns.remove(column);
	}

	public void removeAllColumns() {
		this.columns.clear();
	}

	public void removeGroupByColumn(Column column) {
		this.groupBy.remove(column);
	}

	public void removeAllGroupByColumns() {
		this.groupBy.clear();
	}

	public List<Column> listColumns() {
		return Collections.unmodifiableList(this.columns);
	}

	public List<Column> listGroupByColumns() {
		return Collections.unmodifiableList(this.groupBy);
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

	public List<Criteria> listCriteria() {
		return Collections.unmodifiableList(this.criteria);
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

	public List<Order> listOrder() {
		return Collections.unmodifiableList(this.order);
	}

	@Override
	public String toString() {
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

	@Override
	public void write(Output out) {

		out.println("SELECT");

		 if (this._countQuery) {
        	out.indent();
        	out.println("COUNT(");
        }

        if(this._distinct){
        	out.indent();
        	out.print(" distinct ");
        }

		// Add columns to select
		out.indent();
		appendList(out, this.columns, ",");
		out.unindent();

		if (this._countQuery) {
        	out.println(")");
        	out.unindent();
        }

		// Add tables to select from
		out.println("FROM");

		// Determine all tables used in query
		out.indent();
		if(this.leftJoins.isEmpty()){
			appendList(out,findAllUsedTables(), ",");
		} else {
			List<Outputable> v = new ArrayList<Outputable>();
			v.addAll(findAllUsedTables());
			for (Iterator<LeftJoin> iter = this.leftJoins.iterator(); iter.hasNext();) {
				LeftJoin join = iter.next();
				v.removeAll(join.getTables());
				v.add(join);
			}
			appendList(out,v, ",");
		}
		out.unindent();

		// Add criteria
		if (this.criteria.size() > 0) {
			out.println("WHERE");
			out.indent();
			appendList(out, this.criteria, "AND");
			out.unindent();
		}

		// Add group by
		if (this.groupBy.size() > 0) {
			out.println("GROUP BY");
			out.indent();
			appendList(out, this.groupBy, ",");
			out.unindent();
		}

		// Add order
		if (this.order.size() > 0) {
			out.println("ORDER BY");
			out.indent();
			appendList(out, this.order, ",");
			out.unindent();
		}

	}

	/**
	 * Recurse through criterias and get the correct order of placement values
	 * @return
	 */
	@Override
	public <T> List<T> getValues(){
	    List<T> list = new ArrayList<T>();
	    for (Iterator<Criteria> iter = this.criteria.iterator(); iter.hasNext();) {
            Criteria crit = iter.next();
            if (crit instanceof PlaceHolder) {
            	List<T> critValues = ((PlaceHolder) crit).getValues();
				list.addAll(critValues);
            }
        }
	    return list;
	}


	/**
	 * Iterate through a Collection and append all entries (using .toString()) to
	 * a StringBuffer.
	 */
	private void appendList(Output out, Collection<? extends Outputable> collection, String seperator) {
		Iterator<? extends Outputable> i = collection.iterator();
		boolean hasNext = i.hasNext();

		while (hasNext) {
			Outputable curr = i.next();
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
	private Set<Table> findAllUsedTables() {
		Set<Table> allTables = new HashSet<Table>();
		allTables.add(this.baseTable);

		// Get all tables used by columns
		Iterator<Column> i = this.columns.iterator();
		while (i.hasNext()) {
			Table curr = i.next().getTable();
			if (curr != null && !curr.getName().equals("")) {
				allTables.add(curr);
			}
		}

		// Get all tables used by criteria
		Iterator<Criteria> critIter = this.criteria.iterator();
		while (critIter.hasNext()) {
			Criteria curr = critIter.next();
			if (curr.getTables() != null) {
				allTables.addAll(curr.getTables());
			}
		}

		// Get all tables used by columns
		Iterator<Order> orderIter = this.order.iterator();
		while (orderIter.hasNext()) {
			Order curr = orderIter.next();
			Table c = curr.getColumn().getTable();
			if (c != null) {
				allTables.add(c);
			}
		}

		return allTables;
	}

	/**
	 * @param countQuery The countQuery to set.
	 */
	public void setAsCountQuery(boolean countQuery) {
		this._countQuery = countQuery;
	}

	/**
	 *
	 * @param distinct The distinct to set
	 */
	public void setAsDistinct(boolean distinct){
		this._distinct = distinct;
	}

	@Override
	public Object clone(){
		SelectQuery obj = null;

		try {
			obj = (SelectQuery) super.clone();

			obj.baseTable = this.baseTable;

			obj.columns = new ArrayList<Column>(this.columns);
			obj.leftJoins = new ArrayList<LeftJoin>(this.leftJoins);
			obj.criteria = new ArrayList<Criteria>(this.criteria);
			obj.order = new ArrayList<Order>(this.order);
			obj.groupBy = new ArrayList<Column>(this.groupBy);

			obj._countQuery = this._countQuery;
			obj._distinct = this._distinct;

		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return obj;
	}

	public Collection<Criteria> getCriteria(){
		return this.criteria;
	}

	public Collection<Order> getOrder(){
		return this.order;
	}

    @Override
	public void flag(boolean flag) {
        this.flag = flag;
    }
    @Override
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
		this.leftJoins.add(join);
	}

	public void clearLeftJoins() {
	    this.leftJoins.clear();
	}
}