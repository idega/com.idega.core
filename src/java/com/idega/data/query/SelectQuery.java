package com.idega.data.query;

import com.idega.data.IDOCompositePrimaryKeyException;
import com.idega.data.IDOEntityDefinition;
import com.idega.data.IDOEntityField;
import com.idega.data.IDORelationshipException;
import com.idega.data.query.output.Output;
import com.idega.data.query.output.Outputable;
import com.idega.data.query.output.ToStringer;

import java.util.*;

/**
 * @author <a href="joe@truemesh.com">Joe Walnes </a>
 */
public class SelectQuery implements Outputable {

	public static final int indentSize = 4;

	private Table baseTable;

	private List columns;

	private List criteria;

	private List order;

	private boolean _countQuery = false;

	public SelectQuery(Table baseTable) {
		this.baseTable = baseTable;
		columns = new ArrayList();
		criteria = new ArrayList();
		order = new ArrayList();
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

	public void removeColumn(Column column) {
		columns.remove(column);
	}

	public List listColumns() {
		return Collections.unmodifiableList(columns);
	}

	public void addCriteria(Criteria criteria) {
		this.criteria.add(criteria);
	}

	public void removeCriteria(Criteria criteria) {
		this.criteria.remove(criteria);
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
		}
		throw new IDORelationshipException("No relation found between tables!");
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
		throw new IDORelationshipException("No relation found between tables!");
	}
	
	public void addManyToManyJoin(Table srcTable, Table destTable) throws IDORelationshipException {
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
							if (middleTableName == null) { throw new IDORelationshipException("Middle table not found for tables."); }
	
							Table middleTable = new Table(middleTableName);
	
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

	public List listOrder() {
		return Collections.unmodifiableList(order);
	}

	public String toString() {
		return ToStringer.toString(this);
	}

	public void write(Output out) {

		out.println("SELECT");

		if (_countQuery) {
			out.indent();
			out.println("COUNT(");
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
		appendList(out, findAllUsedTables(), ",");
		out.unindent();

		// Add criteria
		if (criteria.size() > 0) {
			out.println("WHERE");
			out.indent();
			appendList(out, criteria, "AND");
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
	 * Iterate through a Collection and append all entries (using .toString()) to
	 * a StringBuffer.
	 */
	private void appendList(Output out, Collection collection, String seperator) {
		Iterator i = collection.iterator();
		boolean hasNext = i.hasNext();

		while (hasNext) {
			Outputable curr = (Outputable) i.next();
			if (curr != null) {
				hasNext = i.hasNext();
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
	private List findAllUsedTables() {

		List allTables = new ArrayList();
		allTables.add(baseTable);

		{ // Get all tables used by columns
			Iterator i = columns.iterator();
			while (i.hasNext()) {
				Table curr = ((Column) i.next()).getTable();
				if (!allTables.contains(curr)) {
					allTables.add(curr);
				}
			}
		}

		{ // Get all tables used by criteria
			Iterator i = criteria.iterator();
			while (i.hasNext()) {
				try {
					JoinCriteria curr = (JoinCriteria) i.next();
					if (!allTables.contains(curr.getSource().getTable())) {
						allTables.add(curr.getSource().getTable());
					}
					if (!allTables.contains(curr.getDest().getTable())) {
						allTables.add(curr.getDest().getTable());
					}
				}
				catch (ClassCastException e) {
				} // not a JoinCriteria
			}
		}

		{ // Get all tables used by columns
			Iterator i = order.iterator();
			while (i.hasNext()) {
				Order curr = (Order) i.next();
				Table c = curr.getColumn().getTable();
				if (!allTables.contains(c)) {
					allTables.add(c);
				}
			}
		}

		return allTables;
	}

	/**
	 * @param countQuery
	 *          The countQuery to set.
	 */
	public void setAsCountQuery(boolean countQuery) {
		_countQuery = countQuery;
	}
}