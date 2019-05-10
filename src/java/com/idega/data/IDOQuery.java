package com.idega.data;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.idega.data.query.SelectQuery;
import com.idega.util.ArrayUtil;
import com.idega.util.CoreConstants;
import com.idega.util.DBUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.idega.util.datastructures.map.MapUtil;
import com.idega.util.dbschema.SQLSchemaAdapter;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public class IDOQuery implements Cloneable {

	StringBuffer _buffer;

	private static final String SELECT_ALL_FROM = "SELECT * FROM ";
	private static final String SELECT_COUNT_FROM = "SELECT COUNT(*) FROM ";
	private static final String SELECT_COUNT = "SELECT COUNT(*) ";
	private static final String SELECT = "SELECT ";
	private static final String FROM = " FROM ";
	private static final String SUM = "SUM";
	private static final String COUNT = "COUNT";
	private static final String STAR = " * ";
	private static final String DISTINCT = " DISTINCT ";
	private static final String ORDER_BY = " ORDER BY ";
	private static final String GROUP_BY = " GROUP BY ";
	private static final String HAVING = " HAVING ";
	private static final String WHERE = " WHERE ";
	private static final String LIKE = " LIKE ";
	private static final String NOT_LIKE = " NOT LIKE ";
	private static final String EQUAL_SIGN = "=";
	private static final String NOT_EQUAL_SIGN = "!=";
	private static final String WHITE_SPACE = " ";
	private static final String QUOTATION_MARK = "'";
	private static final String DOUBLE_QUOTATION_MARK = "\"";

	private static final String LESS_THAN_SIGN = "<";
	private static final String GREATER_THAN_SIGN = ">";
	private static final String LESS_THAN_OR_EQUAL_SIGN = "<=";
	private static final String GREATER_THAN_OR_EQUAL_SIGN = ">=";
	//parenthesis
	private static final String PARENTHESIS_LEFT = "(";
	private static final String PARENTHESIS_RIGHT = ")";
	private static final String DELETE = "DELETE ";
	private static final String UPDATE = "UPDATE ";
	private static final String SET = "SET ";
	private static final String IN = " IN ";
	private static final String NOT_IN = " NOT IN ";
	private static final String COMMA = ",";
	private static final String AND = " AND ";
	private static final String OR = " OR ";
	private static final String IS_NULL = " IS NULL ";
	private static final String IS_NOT_NULL = " IS NOT NULL ";
	private static final String DESCENDING = " DESC ";
	private static final String TRUE = CoreConstants.Y;
	private static final String FALSE = CoreConstants.N;
	private static final String QUESTIONMARK = "?";

	public static final String JOIN = "JOIN ";
	public static final String ON = "ON ";
	public static final String AS = " ";
	public static final String UNION = "UNION";
	public static final String ENTITY_TO_SELECT = "selected_entity";
	public static final String MIDDLE_ENTITY = "middle_entity_";
	public static final String RELATED_ENTITY = "related_entity_";
	public static final String RELATED_VIEW = "related_view_";
	public boolean useDefaultAlias = false;


	private DatastoreInterface dataStore = null;
	private List<Object> objectValues = new ArrayList<Object>();

	private IDOEntity entityToSelect = null;

	private int joinNumber = 0;

	private Integer limit;

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	protected IDOEntity getEntityToSelect() {
		if (this.entityToSelect == null) {
			Logger.getLogger(IDOQuery.class.getName()).warning(
					"Use IDOQuery.appendSelect...(IDOEntity entity, ...) to use this!");
		}

		return entityToSelect;
	}

	protected void setEntityToSelect(IDOEntity entityToSelect) {
		this.entityToSelect = entityToSelect;
	}

	private String primaryKeyColumnName = null;

	protected String getPrimaryKeyColumnName() {
		if (StringUtil.isEmpty(this.primaryKeyColumnName)) {
			this.primaryKeyColumnName = getColumnNameForPrimaryKey(
					getEntityToSelect()
					);
		}

		return this.primaryKeyColumnName;
	}

	private String primaryKeyColumnNameForSelectedEntity = null;

	protected String getPrimaryKeyColumnNameForSelectedEntity() {
		if (StringUtil.isEmpty(this.primaryKeyColumnNameForSelectedEntity)) {
			this.primaryKeyColumnNameForSelectedEntity = new StringBuilder(ENTITY_TO_SELECT)
					.append(CoreConstants.DOT)
					.append(getPrimaryKeyColumnName())
					.append(CoreConstants.SPACE)
					.toString();
		}

		return this.primaryKeyColumnNameForSelectedEntity;
	}

	/**
	 *
	 * @param entity to get column name for, not <code>null</code>;
	 * @return primary key column name of {@link IDOEntity} or <code>null</code>
	 * if composite primary key or failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	protected String getColumnNameForPrimaryKey(IDOEntity entity) {
		if (entity == null) {
			return null;
		}

		IDOEntityDefinition entityDefinition = entity.getEntityDefinition();
		if (entityDefinition == null) {
			return null;
		}

		IDOPrimaryKeyDefinition primaryKeyDefinition = entityDefinition.getPrimaryKeyDefinition();
		if (primaryKeyDefinition == null) {
			return null;
		}

		IDOEntityField field = null;
		try {
			field = primaryKeyDefinition.getField();
		} catch (IDOCompositePrimaryKeyException e) {
			java.util.logging.Logger.getLogger(getClass().getName())
					.log(Level.WARNING, "Composite keys not supported yet...");
		}

		if (field == null) {
			return null;
		}

		return field.getSQLFieldName();
	}

	/**
	 *
	 * <p>Constructs JOIN ON... part for related EJB entities</p>
	 * @param entities to search by, should be only one type, not <code>null</code>;
	 * @return query for filtering required entity by these given entities;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	protected IDOQuery appendJoinOnEntity(Collection<IDOEntity> entities) {
		if (ListUtil.isEmpty(entities) || getEntityToSelect() == null) {
			return this;
		}

		/* Related table info */
		IDOEntity relatedEntity = entities.iterator().next();
		if (relatedEntity == null) {
			return this;
		}

		String relatedTableName = relatedEntity.getEntityDefinition().getSQLTableName();
		String relatedTablePrimaryKeySqlName = getColumnNameForPrimaryKey(relatedEntity);
		String relatedName = RELATED_ENTITY + joinNumber;
		String relatedTableKeyColumn = new StringBuilder(RELATED_ENTITY)
				.append(joinNumber)
				.append(CoreConstants.DOT)
				.append(relatedTablePrimaryKeySqlName)
				.append(CoreConstants.SPACE)
				.toString();

		/*
		 * This for marking if correct relation given
		 */
		boolean relationFound = Boolean.FALSE;

		/*
		 * Case of many to many relation
		 */
		EntityRelationship relation = EntityControl.getManyToManyRelationShip(
				relatedEntity, getEntityToSelect());
		if (relation != null && !relationFound) {
			relationFound = Boolean.TRUE;

			/* Middle table info */
			String middleName = MIDDLE_ENTITY + joinNumber;
			String middleTableName = relation.getTableName() + CoreConstants.SPACE;
			String middleTableCurrentKeyColumn = new StringBuilder(MIDDLE_ENTITY)
					.append(joinNumber)
					.append(CoreConstants.DOT)
					.append(getPrimaryKeyColumnName())
					.toString();
			String middleTableRelatedKeyColumn = new StringBuilder(MIDDLE_ENTITY)
					.append(joinNumber)
					.append(CoreConstants.DOT)
					.append(relatedTablePrimaryKeySqlName)
					.toString();

			/* Joining middle table */
			append(JOIN).append(middleTableName);
			append(AS).append(middleName).append(CoreConstants.SPACE);
			append(ON).appendEquals(
					getPrimaryKeyColumnNameForSelectedEntity(),
					middleTableCurrentKeyColumn);
			append(CoreConstants.SPACE);

			/* Joining related entity */
			append(JOIN).append(relatedTableName);
			append(AS).append(relatedName).append(CoreConstants.SPACE);
			append(ON).appendEquals(relatedTableKeyColumn, middleTableRelatedKeyColumn);
			append(CoreConstants.SPACE);
		}

		/*
		 * In case of many to one relation or one to one
		 */
		EntityAttribute manyToOneRelation = EntityControl.getNToOneRelation(getEntityToSelect(), relatedEntity);
		if (manyToOneRelation != null && !relationFound) {
			relationFound = Boolean.TRUE;

			String currentTableForeignKeyColumn = new StringBuilder(ENTITY_TO_SELECT)
					.append(CoreConstants.DOT)
					.append(manyToOneRelation.getColumnName())
					.append(CoreConstants.SPACE)
					.toString();

			/* Joining related entity */
			append(JOIN).append(relatedTableName);
			append(AS).append(relatedName).append(CoreConstants.SPACE);
			append(ON).appendEquals(currentTableForeignKeyColumn, relatedTableKeyColumn);
			append(CoreConstants.SPACE);
		}

		/*
		 * In case of one to many or one to one relation
		 */
		EntityAttribute oneToManyRelation = EntityControl.getOneToNRelation(
				getEntityToSelect(), relatedEntity);
		if (oneToManyRelation != null && !relationFound) {
			relationFound = Boolean.TRUE;

			String foreignKeyColumn = new StringBuilder(RELATED_ENTITY)
					.append(joinNumber)
					.append(CoreConstants.DOT)
					.append(oneToManyRelation.getColumnName())
					.append(CoreConstants.SPACE)
					.toString();

			/* Joining related entity */
			append(JOIN).append(relatedTableName);
			append(AS).append(relatedName).append(CoreConstants.SPACE);
			append(ON).appendEquals(
					getPrimaryKeyColumnNameForSelectedEntity(),
					foreignKeyColumn);
			append(CoreConstants.SPACE);
		}

		/*
		 * Checking if entities are in given collection
		 */
		if (relationFound) {
			appendAnd();
			append(relatedTableKeyColumn);
			appendInCollectionWithSingleQuotes(entities);
			joinNumber++;
		}

		return this;
	}

	/**
	 *
	 * <p>Constructs JOIN ON... part for related EJB entities</p>
	 * @param sortedEntities is {@link IDOEntity}s sorted by
	 * their table names, not <code>null</code>;
	 * @return query for filtering required entity by these given entities;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	protected IDOQuery appendJoinOn(Map<String, Collection<IDOEntity>> sortedEntities) {
		if (MapUtil.isEmpty(sortedEntities)) {
			return this;
		}

		Set<String> types = sortedEntities.keySet();
		if (types.size() > 1) {
			String relatedView = new StringBuilder(RELATED_VIEW)
					.append(joinNumber).toString();
			String viewKeyColumn = new StringBuilder(relatedView)
					.append(CoreConstants.DOT)
					.append(getPrimaryKeyColumnName())
					.append(CoreConstants.SPACE)
					.toString();

			append(JOIN).append(CoreConstants.BRACKET_LEFT);
			Iterator<String> typeIterator = types.iterator();
			while (typeIterator.hasNext()) {
				append(CoreConstants.BRACKET_LEFT);

				try {
					appendSelectIDColumnFrom(getEntityToSelect());
					appendJoinOnEntity(sortedEntities.get(typeIterator.next()));
				} catch (IDOCompositePrimaryKeyException e) {
					Logger.getLogger(getClass().getName()).log(Level.WARNING,
							"No solution for composite keys: ", e);
				}

				append(CoreConstants.BRACKET_RIGHT);
				if (typeIterator.hasNext()) {
					append(CoreConstants.SPACE);
					append(UNION);
					append(CoreConstants.SPACE);
				}
			}

			append(CoreConstants.BRACKET_RIGHT);
			append(CoreConstants.SPACE);
			append(relatedView);
			append(CoreConstants.SPACE);
			append(ON).appendEquals(
					viewKeyColumn,
					getPrimaryKeyColumnNameForSelectedEntity());
		} else {
			appendJoinOnEntity(sortedEntities.get(types.iterator().next()));
		}

		return this;
	}

	/**
	 *
	 * <p>Constructs JOIN ON... part for related EJB entities</p>
	 * @param entities to search by, not <code>null</code>;
	 * @return query for filtering required entity by these given entities;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public IDOQuery appendJoinOn(Collection<? extends IDOEntity> entities) {
		if (!ListUtil.isEmpty(entities)) {
			/*
			 * Sorting by type if some sub-types are passed
			 */
			Map<String, Collection<IDOEntity>> sortedEntities = new TreeMap<String, Collection<IDOEntity>>();
			for (IDOEntity entity : entities) {
				String tableName = entity.getEntityDefinition().getSQLTableName();
				Collection<IDOEntity> entityList = sortedEntities.get(tableName);
				if (ListUtil.isEmpty(entityList)) {
					entityList = new ArrayList<IDOEntity>();
				}

				entityList.add(entity);
				sortedEntities.put(tableName, entityList);
			}

			return appendJoinOn(sortedEntities);
		}

		return this;
	}

	public static IDOQuery getStaticInstance(boolean useDefaultAlias) {
		IDOQuery query = new IDOQuery(useDefaultAlias);
		return query;
	}

	public static IDOQuery getStaticInstance() {
		return getStaticInstance(false);
	}

	/**
	 * @see com.idega.data.GenericEntity.idoQuery()
	 */
	protected IDOQuery() {
		this(false);
	}

	protected IDOQuery(boolean useDefaultAlias) {
		this.useDefaultAlias = useDefaultAlias;
		this._buffer = new StringBuffer();
	}

	protected IDOQuery(int length) {
		this(length, false);
	}

	protected IDOQuery(int length, boolean useDefaultAlias) {
		this.useDefaultAlias = useDefaultAlias;
		this._buffer = new StringBuffer(length);
	}

	protected IDOQuery(String str) {
		this(str, false);
	}

	protected IDOQuery(String str, boolean useDefaultAlias) {
		this.useDefaultAlias = useDefaultAlias;
		this._buffer = new StringBuffer(str);
	}

	/**
	 * Appends an SQL counterpart for a Boolean value (declared by Boolean.class in the BMPBean).
	 */
	public IDOQuery append(boolean b) {
		if (b) {
			return this.appendWithinSingleQuotes(TRUE);
		}
		else {
			return this.appendWithinSingleQuotes(FALSE);
		}
	}
	/**
	 * @see java.lang.StringBuffer#append(char)
	 */
	public IDOQuery append(char c) {
		this._buffer.append(c);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#append(char[],int,int)
	 */
	public IDOQuery append(char[] str, int offset, int len) {
		this._buffer.append(str, offset, len);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#append(char[])
	 */
	public IDOQuery append(char[] str) {
		this._buffer.append(str);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#append(java.lang.String)
	 */
	public IDOQuery append(double d) {
		this._buffer.append(d);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#append(float)
	 */
	public IDOQuery append(float f) {
		this._buffer.append(f);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#append(int)
	 */
	public IDOQuery append(int i) {
		this._buffer.append(i);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#append(long)
	 */
	public IDOQuery append(long l) {
		this._buffer.append(l);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#append(java.lang.Object)
	 */
	public IDOQuery append(Object obj) {
		this._buffer.append(obj);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#append(java.lang.String)
	 */
	public IDOQuery append(String str) {
		this._buffer.append(str);
		return this;
	}

	/**
	 * Appends quoted string like 'anObject'
	 * @param string
	 * @return
	 */
	public IDOQuery appendQuoted(Object anObject) {
		this._buffer.append(QUOTATION_MARK);
		this._buffer.append(anObject);
		this._buffer.append(QUOTATION_MARK);
		return this;
	}

	public IDOQuery append(IDOEntityField field){
		return this.append(field.getSQLFieldName());
	}


	public IDOQuery append(Date date) {
		//IWTimestamp stamp = new IWTimestamp(date);
		//this.appendWithinSingleQuotes(stamp.toSQLString());
		this.append(getDatastore().format(date));
		return this;
	}

	public IDOQuery append(Timestamp timestamp) {
		//IWTimestamp stamp = new IWTimestamp(timestamp);
		//this.appendWithinSingleQuotes(stamp.toSQLString());
		this.append(getDatastore().format(timestamp));
		return this;
	}

	public IDOQuery append(IWTimestamp timestamp) {
		return this.append(timestamp.getTimestamp());
	}

	public IDOQuery append(IDOEntity entity) {
		if (entity == null) {
			Logger.getLogger(IDOQuery.class.getName()).warning("Provided IDOEntity is null!");
			return this;
		}

		Object pk = entity.getPrimaryKey();
		if (pk instanceof Integer) {
			return this.append(pk);
		}
		else {
			return this.appendWithinSingleQuotes(pk);
		}
	}

	/**
	 * @see java.lang.StringBuffer#capacity()
	 */
	public int capacity() {
		return this._buffer.capacity();
	}
	/**
	 * @see java.lang.StringBuffer#charAt(int)
	 */
	public char charAt(int index) {
		return this._buffer.charAt(index);
	}
	/**
	 * @see java.lang.StringBuffer#delete(int,int){
	 */
	public IDOQuery delete(int start, int end) {
		this._buffer.delete(start, end);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#deleteCharAt(int)
	 */
	public IDOQuery deleteCharAt(int index) {
		this._buffer.deleteCharAt(index);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#ensureCapacity(int)
	 */
	public void ensureCapacity(int minimumCapacity) {
		this._buffer.ensureCapacity(minimumCapacity);
	}

	@Override
	public boolean equals(Object obj) {
		return this._buffer.equals(obj);
	}
	/**
	 * @see java.lang.StringBuffer#getChars(int,int,char[],int)
	 */
	public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
		this._buffer.getChars(srcBegin, srcEnd, dst, dstBegin);
	}

	@Override
	public int hashCode() {
		return this._buffer.hashCode();
	}

	/**
	 * @see java.lang.StringBuffer#insert(int,boolean)
	 */
	public IDOQuery insert(int offset, boolean b) {
		this._buffer.insert(offset, b);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#insert(int,char)
	 */
	public IDOQuery insert(int offset, char c) {
		this._buffer.insert(offset, c);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#insert(int,char[],int,int)
	 */
	public IDOQuery insert(int index, char[] str, int offset, int len) {
		this._buffer.insert(index, str, offset, len);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#insert(int,char[])
	 */
	public IDOQuery insert(int offset, char[] str) {
		this._buffer.insert(offset, str);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#insert(int,java.lang.String)
	 */
	public IDOQuery insert(int offset, double d) {
		this._buffer.insert(offset, d);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#insert(int,float)
	 */
	public IDOQuery insert(int offset, float f) {
		this._buffer.insert(offset, f);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#insert(int,int)
	 */
	public IDOQuery insert(int offset, int i) {
		this._buffer.insert(offset, i);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#insert(int,long)
	 */
	public IDOQuery insert(int offset, long l) {
		this._buffer.insert(offset, l);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#insert(int,java.lang.Object)
	 */
	public IDOQuery insert(int offset, Object obj) {
		this._buffer.insert(offset, obj);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#insert(int,java.lang.String)
	 */
	public IDOQuery insert(int offset, String str) {
		this._buffer.insert(offset, str);
		return this;
	}

	/**
	 * @see java.lang.StringBuffer#replace(int,int,String)
	 */
	public IDOQuery replace(int start, int end, String str) {
		this._buffer.replace(start, end, str);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#reverse()
	 */
	public IDOQuery reverse() {
		this._buffer.reverse();
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#setCharAt(int,char)
	 */
	public void setCharAt(int index, char ch) {
		this._buffer.setCharAt(index, ch);
	}
	/**
	 * @see java.lang.StringBuffer#setLength(int)
	 */
	public void setLength(int newLength) {
		this._buffer.setLength(newLength);
	}
	/**
	 * @see java.lang.StringBuffer#substring(int,int)
	 */
	public String substring(int start, int end) {
		return this._buffer.substring(start, end);
	}
	/**
	 * @see java.lang.StringBuffer#substring(int)
	 */
	public String substring(int start) {
		return this._buffer.substring(start);
	}
	/**
	 * @see java.lang.StringBuffer#toString()
	 */
	@Override
	public String toString() {
		String sql = this._buffer.toString();

		if (limit != null) {
			String dataStoreType = DBUtil.getDatastoreType();
			if (!StringUtil.isEmpty(dataStoreType) && SQLSchemaAdapter.DBTYPE_MYSQL.equals(dataStoreType)) {
				sql += " LIMIT " + limit;
			}
		}

		return sql;
	}

	public IDOQuery appendLeftParenthesis() {
		return this.append(PARENTHESIS_LEFT);
	}

	public IDOQuery appendRightParenthesis() {
		return this.append(PARENTHESIS_RIGHT);
	}

	public IDOQuery appendCommaDelimited(String[] str) {
		for (int i = 0; i < str.length; i++) {
			if (i != 0) {
				this.append(COMMA);
			}
			this.append(str[i]);
		}
		return this;
	}

	public IDOQuery appendCommaDelimited(Collection<?> collection) {
		for (Iterator<?> iter = collection.iterator(); iter.hasNext();) {
			Object item = iter.next();
			if (item instanceof IDOEntity) {
				this.append(((IDOEntity)item).getPrimaryKey());
			} else {
				this.append(item);
			}

			if (iter.hasNext()) {
				this.append(COMMA);
			}
		}
		return this;
	}

	public IDOQuery appendCommaDelimitedWithinSingleQuotes(String[] str) {
		for (int i = 0; i < str.length; i++) {
			if (i != 0) {
				this.append(COMMA);
			}
			this.appendWithinSingleQuotes(str[i]);
		}
		return this;
	}

	public IDOQuery appendCommaDelimitedWithinSingleQuotes(Collection<?> collection) {
		boolean first = true;
		for (Iterator<?> iter = collection.iterator(); iter.hasNext();) {
			Object item = iter.next();
			if (!first) {
				this.append(COMMA);
			}

			if (item instanceof IDOEntity) {
				this.appendWithinSingleQuotes(((IDOEntity)item).getPrimaryKey());
			} else {
				this.appendWithinSingleQuotes(item);
			}

			first = false;
		}
		return this;
	}
	public IDOQuery appendCommaDelimitedWithinDoubleQuotes(String[] str) {
		for (int i = 0; i < str.length; i++) {
			if (i != 0) {
				this.append(COMMA);
			}
			this.appendWithinDoubleQuotes(str[i]);
		}
		return this;
	}

	public IDOQuery appendCommaDelimitedWithinDoubleQuotes(Collection<?> collection) {
		boolean first = true;
		for (Iterator<?> iter = collection.iterator(); iter.hasNext();) {
			Object item = iter.next();
			if (!first) {
				this.append(COMMA);
			}

			if (item instanceof IDOEntity) {
				this.appendWithinDoubleQuotes(((IDOEntity)item).getPrimaryKey());
			} else {
				this.appendWithinSingleQuotes(item);
			}

			first = false;
		}
		return this;
	}

	public IDOQuery appendWithinDoubleQuotes(String str) {
		this.appendDoubleQuote();
		this.append(str);
		this.appendDoubleQuote();
		return this;
	}

	public IDOQuery appendWithinSingleQuotes(String str) {
		this.appendSingleQuote();
		this.append(str);
		this.appendSingleQuote();
		return this;
	}

	public IDOQuery appendWithinDoubleQuotes(Object obj) {
		this.appendDoubleQuote();
		this.append(obj);
		this.appendDoubleQuote();
		return this;
	}

	public IDOQuery appendWithinSingleQuotes(Object obj) {
		this.appendSingleQuote();
		this.append(obj);
		this.appendSingleQuote();
		return this;
	}

	public IDOQuery appendWithinParentheses(String str) {
		this.append(PARENTHESIS_LEFT);
		this.append(str);
		this.append(PARENTHESIS_RIGHT);
		return this;
	}
	public IDOQuery appendWithinParentheses(IDOQuery query) {
		return appendWithinParentheses((Object)query);
	}
	public IDOQuery appendWithinParentheses(Object obj) {
		this.append(PARENTHESIS_LEFT);
		this.append(obj);
		this.append(PARENTHESIS_RIGHT);
		return this;
	}

	public IDOQuery appendSelectAllFrom() {
		return this.append(SELECT_ALL_FROM);
	}
	public IDOQuery appendSelectAllFrom(IDOEntity entity) {
		setEntityToSelect(entity);
		//return this.appendSelectAllFrom(((IDOLegacyEntity)entity).getTableName());
		appendSelectAllFrom(entity.getEntityDefinition().getSQLTableName());
		if (useDefaultAlias) {
			append(AS).append(ENTITY_TO_SELECT).append(CoreConstants.SPACE);
		}
		return this;
	}

	/**
	 *
	 * @param entity to select from, not <code>null</code>;
	 * @return SELECT selected_entity.* FROM given entity;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public IDOQuery appendSelectDistinctFrom(IDOEntity entity) {
		return appendSelectDistinctFrom(entity, null);
	}

	/**
	 *
	 * <p>Selects SQL DISTINCT entity or given column name.</p>
	 * @param entity to select from, not <code>null</code>;
	 * @param columnName to select from entity, skipped if <code>null</code>;
	 * @return query appended with SELECT DISTINCT selected_entity.columnName
	 * or unmodified query on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public IDOQuery appendSelectDistinctFrom(IDOEntity entity, String columnName) {
		if (entity != null) {
			setEntityToSelect(entity);
			appendSelect();
			appendDistinct();

			if (useDefaultAlias) {
				append(ENTITY_TO_SELECT).append(CoreConstants.DOT);
			}

			if (!StringUtil.isEmpty(columnName)) {
				append(columnName);
			} else {
				append(CoreConstants.STAR);
			}

			append(CoreConstants.SPACE);
			appendFrom();
			append(entity.getEntityDefinition().getSQLTableName());
			if (useDefaultAlias) {
				append(AS).append(ENTITY_TO_SELECT).append(CoreConstants.SPACE);
			}
		}

		return this;
	}

	public IDOQuery appendSelectAllFrom(String entityName) {
		this.append(SELECT_ALL_FROM);
		this.append(entityName);
		return this;
	}

	public IDOQuery appendSelectIDColumnFrom(IDOEntity entity) throws IDOCompositePrimaryKeyException {
		setEntityToSelect(entity);
		appendSelect();
		append(entity.getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName());
		appendFrom();
		append(entity.getEntityDefinition().getSQLTableName());
		if (useDefaultAlias) {
			append(AS).append(ENTITY_TO_SELECT).append(CoreConstants.SPACE);
		}

		return this;
	}

	public IDOQuery appendSelectCountFrom() {
		return this.append(SELECT_COUNT_FROM);
	}

	public IDOQuery appendSelectCount() {
		return this.append(SELECT_COUNT);
	}

	public IDOQuery appendSelectCountFrom(IDOEntity entity) {
		setEntityToSelect(entity);
		appendSelectCountFrom(entity.getEntityDefinition().getSQLTableName());
		if (useDefaultAlias) {
			append(AS).append(ENTITY_TO_SELECT).append(CoreConstants.SPACE);
		}
		return this;
	}
	public IDOQuery appendSelectCountFrom(String entityName) {
		this.append(SELECT_COUNT_FROM);
		this.append(entityName);
		return this;
	}

	public IDOQuery appendSelectCountIDFrom(String entityName, String idColumn) {
	    return appendSelectCountIDFrom(entityName, idColumn, null);
	}

	public IDOQuery appendSelectCountIDFrom(String entityName, String idColumn, String tableAlias) {
	    return appendSelectCountIDFrom(entityName, idColumn, tableAlias, false);
	}

	public IDOQuery appendSelectCountIDFrom(String entityName, String idColumn, boolean distinct) {
	    return appendSelectCountIDFrom(entityName, idColumn, null, distinct);
	}

	public IDOQuery appendSelectCountIDFrom(String entityName, String idColumn, String tableAlias, boolean distinct) {
		this.appendSelect().appendCount(idColumn, tableAlias, distinct).appendFrom().append(entityName);
		if (tableAlias != null) {
		    this.append(" ").append(tableAlias);
		}
		return this;
	}

	public IDOQuery appendSelect() {
		return this.append(SELECT);
	}

	public IDOQuery appendSum(String columnName) {
		return this.append(SUM).appendLeftParenthesis().append(columnName).appendRightParenthesis();
	}

	public IDOQuery appendSelectSumFrom(String columnName, String entityName) {
		return this.appendSelect().appendSum(columnName).appendFrom().append(entityName);
	}

	public IDOQuery appendSelectSumFrom(String columnName, IDOEntity entity) {
		return this.appendSelectSumFrom(columnName, entity.getEntityDefinition().getSQLTableName());
	}

	/**
	 * Create Query like "UPDATE tableName SET "
	 * @param entity
	 * @return
	 */
	public IDOQuery appendUpdateSet(IDOEntity entity) {
		String tableName = entity.getEntityDefinition().getSQLTableName();
		return append(UPDATE).append(tableName).append(WHITE_SPACE).append(SET);
	}

	public IDOQuery appendFrom() {
			return this.append(FROM);
	}

	public IDOQuery appendFrom(String tableName) {
		return this.append(FROM).append(" ").append(tableName);
}

	public IDOQuery appendFrom(String[] tableNames, String[] prmNames) {
		if(tableNames != null){
			this.append(FROM);
			for (int i = 0; i < tableNames.length; i++) {
				if(i>0){
					this.append(", ");
				}
				this.append(tableNames[i]);
				this.append(" ");
				this.append(prmNames[i]);
			}
		} else {
			this.append(FROM);
		}
		return this;
	}

	public IDOQuery appendWhiteSpace() {
		return this.append(WHITE_SPACE);
	}

	public IDOQuery appendDelete() {
		return this.append(DELETE);
	}

	public IDOQuery appendStar() {
		return this.append(STAR);
	}

	public IDOQuery appendDistinct() {
		return this.append(DISTINCT);
	}

	public IDOQuery appendOrderBy() {
		return this.append(ORDER_BY);
	}

	public IDOQuery appendDescending() {
		return this.append(DESCENDING);
	}

	public IDOQuery appendOrderBy(String columnName) {
		this.append(ORDER_BY);
		this.append(columnName);
		return this;
	}

	public IDOQuery appendGroupBy(String columnName) {
		this.append(GROUP_BY);
		this.append(columnName);
		return this;
	}

	public IDOQuery appendHaving(){
		this.append(HAVING);
		return this;
	}

	public IDOQuery appendCount(String columnName){
	    return appendCount(columnName, null);
	}

	public IDOQuery appendCount(String columnName, String tableAlias){
	    return appendCount(columnName, tableAlias, false);
	}

	public IDOQuery appendCount(String columnName, boolean distinct){
	    return appendCount(columnName, null, distinct);
	}

	public IDOQuery appendCount(String columnName, String tableAlias, boolean distinct){
		this.append(COUNT);
		this.appendLeftParenthesis();
		if (distinct) {
		    this.append(DISTINCT);
		}
		if (tableAlias != null) {
		    this.append(tableAlias).append(".");
		}
		this.append(columnName);
		this.appendRightParenthesis();
		return this;
	}

	public IDOQuery appendOrderBy(String[] columnNames) {
		this.append(ORDER_BY);
		this.append(IDOUtil.getInstance().convertArrayToCommaseparatedString(columnNames));
		return this;
	}

	public IDOQuery appendOrderByDescending(String columnName) {
		this.appendOrderBy(columnName);
		this.appendDescending();
		return this;
	}

	/**
	 *
	 * @param columnNames to ORDER BY, not <code>null</code>;
	 * @return query appended with ORDER BY column1 DESC, column2 DESC,..
	 * or same query on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public IDOQuery appendOrderByDescending(String[] columnNames) {
		if (!ArrayUtil.isEmpty(columnNames)) {
			appendOrderBy();
			for (int i = 0; i < columnNames.length; i++) {
				append(columnNames[i]);
				appendDescending();
				if (i + 1 < columnNames.length) {
					append(CoreConstants.COMMA);
					append(CoreConstants.SPACE);
				}
			}
		}

		return this;
	}

	public IDOQuery appendWhere() {
		return this.append(WHERE);
	}

	public IDOQuery appendWhere(String str) {
		this.append(WHERE);
		this.append(str);
		return this;
	}

	/**
	 * Appends a where (where columnName=columnValue) without quotemarks
	 * @param columnName the name of the field
	 * @param columnValue the value
	 * @return IDOQuery this Object
	 */
	public IDOQuery appendWhereEquals(String columnName, String columnValue) {
		appendWhere(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}

	public IDOQuery appendWhereEquals(String columnName, boolean columnValue) {
		appendWhere(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}

	/**
	 * Appends a where (where columnName=columnValue) with single quotemarks
	 * @param columnName the name of the field
	 * @param columnValue the value
	 * @return IDOQuery this Object
	 */
	public IDOQuery appendWhereEqualsWithSingleQuotes(String columnName, String columnValue) {
		appendWhere(columnName);
		this.appendEqualSign();
		this.appendWithinSingleQuotes(columnValue);
		return this;
	}

	/**
	 * Appends a where (where columnName=columnValue) without quotemarks
	 * @param columnName the name of the field
	 * @param columnValue the value
	 * @return IDOQuery this Object
	 */
	public IDOQuery appendWhereEquals(String columnName, Object columnValue) {
		appendWhere(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}

	/**
	 * Appends a where (where columnName=columnValue) without quotemarks
	 * @param columnName the name of the field
	 * @param columnValue the value
	 * @return IDOQuery this Object
	 */
	public IDOQuery appendWhereEquals(String columnName, IDOEntity entity) {
		appendWhere(columnName);
		this.appendEqualSign();
		this.append(entity);
		return this;
	}

	/**
	 * Appends a where (where columnName=columnValue) without quotemarks
	 * @param columnName the name of the field
	 * @param columnValue the value
	 * @return IDOQuery this Object
	 */
	public IDOQuery appendWhereEquals(String columnName, int columnValue) {
		return appendWhereEquals(columnName, Integer.toString(columnValue));
	}


	/**
	 * Appends a where (where columnName=columnValue) without quotemarks
	 * @param columnName the name of the field
	 * @param columnValue the value
	 * @return IDOQuery this Object
	 */
	public IDOQuery appendWhereEquals(String columnName, Integer columnValue) {
		return appendWhereEquals(columnName, columnValue.toString());
	}

	public IDOQuery appendWhereEqualsQuoted(String columnName, String columnValue) {
		appendWhere(columnName);
		this.appendEqualSign();
		this.appendWithinSingleQuotes(columnValue);
		return this;
	}

	public IDOQuery appendWhereEquals(String columnName, Date date) {
		appendWhere(columnName);
		this.appendEqualSign();
		this.append(date);
		return this;
	}

	/**
	 *
	 * <p>TODO</p>
	 * @param columnName
	 * @param columnValue
	 * @return
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public IDOQuery appendNotEquals(String columnName, String columnValue) {
		this.append(WHITE_SPACE);
		this.append(columnName);
		this.appendNOTEqual();
		this.append(columnValue);
		return this;
	}

	public IDOQuery appendEqualsQuoted(String columnName, String columnValue) {
		this.append(WHITE_SPACE);
		this.append(columnName);
		this.appendEqualSign();
		this.appendWithinSingleQuotes(columnValue);
		return this;
	}

	public IDOQuery appendEquals(String columnName, String columnValue) {
		this.append(WHITE_SPACE);
		this.append(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}

	public IDOQuery appendEquals(String columnName, boolean columnValue) {
		this.append(WHITE_SPACE);
		this.append(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}

	public IDOQuery appendEquals(String columnName, int columnValue) {
		this.append(WHITE_SPACE);
		this.append(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}

	public IDOQuery appendEquals(String columnName, IDOEntity entity) {
		this.append(WHITE_SPACE);
		this.append(columnName);
		this.appendEqualSign();
		this.append(entity);
		return this;
	}

	public IDOQuery appendAndEqualsQuoted(String columnName, String columnValue) {
		appendAnd();
		append(columnName);
		this.appendEqualSign();
		this.appendWithinSingleQuotes(columnValue);
		return this;
	}

	public IDOQuery appendAndEquals(String columnName, Date date) {
		appendAnd();
		append(columnName);
		this.appendEqualSign();
		this.append(date);
		return this;
	}

	public IDOQuery appendAndEquals(String columnName, int columnValue) {
		appendAnd();
		append(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}

	public IDOQuery appendAndEquals(String columnName, Integer columnValue) {
		appendAndEquals(columnName,columnValue.intValue());
		return this;
	}

	public IDOQuery appendAndEquals(String columnName, String columnValue) {
		appendAnd();
		append(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}

	public IDOQuery appendAndEquals(String columnName, boolean columnValue) {
		appendAnd();
		append(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}

	public IDOQuery appendAndEqualsTrue(String columnName) {
		appendAnd();
		append(columnName);
		this.appendEqualSign();
		this.append(true);
		return this;
	}

	/** Handles all values different from true ('Y') as false.
	 */

	public IDOQuery appendAndNotEqualsTrue(String columnName) {
		appendAnd();
		append(columnName);
		this.appendNOTEqual();
		this.append(true);
		return this;
	}

	public IDOQuery appendAndEquals(String columnName, Object columnValue) {
		appendAnd();
		append(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}

	public IDOQuery appendAndEquals(String columnName, IDOEntity entity) {
		appendAnd();
		append(columnName);
		this.appendEqualSign();
		this.append(entity);
		return this;
	}

	public IDOQuery appendOrEqualsQuoted(String columnName, String columnValue) {
		appendOr();
		append(columnName);
		this.appendEqualSign();
		this.appendWithinSingleQuotes(columnValue);
		return this;
	}

	public IDOQuery appendOrEquals(String columnName, Date date) {
		appendOr();
		append(columnName);
		this.appendEqualSign();
		this.append(date);
		return this;
	}

	public IDOQuery appendOrEquals(String columnName, int columnValue) {
		appendOr();
		append(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}

	public IDOQuery appendOrEquals(String columnName, boolean columnValue) {
		appendOr();
		append(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}

	public IDOQuery appendOrEquals(String columnName, String columnValue) {
		appendOr();
		append(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}

	public IDOQuery appendOrEquals(String columnName, Object columnValue) {
		appendOr();
		append(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}

	public IDOQuery appendOrEquals(String columnName, IDOEntity entity) {
		appendOr();
		append(columnName);
		this.appendEqualSign();
		this.append(entity);
		return this;
	}

	public IDOQuery appendIn() {
		return this.append(IN);
	}
	public IDOQuery appendIn(String str) {
		this.append(IN);
		this.appendWithinParentheses(str);
		return this;
	}
	public IDOQuery appendIn(IDOQuery query) {
		this.append(IN);
		this.appendWithinParentheses(query);
		return this;
	}
	public IDOQuery appendIn(SelectQuery query) {
		this.append(IN);
		this.appendWithinParentheses(query.toString());
		return this;
	}

	public IDOQuery appendNotIn() {
		return this.append(NOT_IN);
	}
	/**
	 * Appends a not in clause within parantheses.
	 * @param str the String to be inside the not in clause.
	 * @return IDOQuery Returns this
	 */
	public IDOQuery appendNotIn(String str) {
		this.append(NOT_IN);
		this.appendWithinParentheses(str);
		return this;
	}
	public IDOQuery appendNotIn(IDOQuery query) {
		this.append(NOT_IN);
		this.appendWithinParentheses(query);
		return this;
	}

	public IDOQuery appendLike() {
		return this.append(LIKE);
	}

	public IDOQuery appendEqualSign() {
		return this.append(EQUAL_SIGN);
	}

	public IDOQuery appendLessThanSign() {
		return this.append(LESS_THAN_SIGN);
	}

	public IDOQuery appendGreaterThanSign() {
		return this.append(GREATER_THAN_SIGN);
	}

	public IDOQuery appendGreaterThanOrEqualsSign() {
		this.appendGreaterThanSign();
		return this.appendEqualSign();
	}

	public IDOQuery appendLessThanOrEqualsSign() {
		this.appendLessThanSign();
		return this.appendEqualSign();
	}

	public IDOQuery appendNOTEqual() {
		return this.append(NOT_EQUAL_SIGN);
	}


	public IDOQuery appendNOTLike() {
		return this.append(NOT_LIKE);
	}

	public IDOQuery appendSingleQuote() {
		return this.append(QUOTATION_MARK);
	}

	public IDOQuery appendDoubleQuote() {
		return this.append(DOUBLE_QUOTATION_MARK);
	}

	public IDOQuery appendAnd() {
		return this.append(AND);
	}

	public IDOQuery appendOr() {
		return this.append(OR);
	}

	public IDOQuery appendInArray(String[] array) {
		return this.appendIn().appendWithinParentheses(IDOUtil.getInstance().convertArrayToCommaseparatedString(array));
	}

	public IDOQuery appendInArrayWithSingleQuotes(String[] array) {
		return this.appendIn().appendWithinParentheses(IDOUtil.getInstance().convertArrayToCommaseparatedString(array, true));
	}

	public IDOQuery appendInCollection(Collection<?> coll) {
		return this.appendIn().append(PARENTHESIS_LEFT).appendCommaDelimited(coll).append(PARENTHESIS_RIGHT);
		//return this.appendIn().appendWithinParentheses(IDOUtil.getInstance().convertListToCommaseparatedString(coll));
	}

	public IDOQuery appendNotInCollection(Collection<?> coll) {
		return this.appendNotIn().append(PARENTHESIS_LEFT).appendCommaDelimited(coll).append(PARENTHESIS_RIGHT);
	}

	/**
	 *
	 * @param coll to append, not <code>null</code>;
	 * @return appended {@link IDOQuery} or same {@link IDOQuery} on failure;
	 */
	public IDOQuery appendInCollectionWithSingleQuotes(Collection<? extends IDOEntity> coll) {
		return appendIn().appendWithinParentheses(
				IDOUtil.getInstance().convertListToCommaseparatedString(coll, true)
				);
	}

	public IDOQuery appendNotInCollectionWithSingleQuotes(Collection<? extends IDOEntity> coll) {
		return this.appendNotIn().appendWithinParentheses(IDOUtil.getInstance().convertListToCommaseparatedString(coll,true));
	}

	public IDOQuery appendInForStringCollectionWithSingleQuotes(Collection<String> coll) {
		return this.appendIn().appendWithinParentheses(IDOUtil.getInstance().convertCollectionOfStringsToCommaseparatedString(coll));
	}

	public IDOQuery appendInForIntegerCollectionWithSingleQuotes(Collection<Integer> coll) {
		return this.appendIn().appendWithinParentheses(IDOUtil.getInstance().convertCollectionOfIntegersToCommaseparatedString(coll));
	}

	public IDOQuery appendNotInForStringCollectionWithSingleQuotes(Collection<String> coll) {
		return this.appendNotIn().appendWithinParentheses(IDOUtil.getInstance().convertCollectionOfStringsToCommaseparatedString(coll));
	}

	public IDOQuery appendNotInForIntegerCollectionWithSingleQuotes(Collection<Integer> coll) {
		return this.appendNotIn().appendWithinParentheses(IDOUtil.getInstance().convertCollectionOfIntegersToCommaseparatedString(coll));
	}


	public IDOQuery appendNotInArray(String[] array) {
		return this.appendNotIn().appendWithinParentheses(IDOUtil.getInstance().convertArrayToCommaseparatedString(array));
	}

	public IDOQuery appendNotInArrayWithSingleQuotes(String[] array) {
		return this.appendNotIn().appendWithinParentheses(IDOUtil.getInstance().convertArrayToCommaseparatedString(array, true));
	}

	public IDOQuery appendWhereIsNull(String columnName) {
		appendWhere(columnName);
		this.append(IS_NULL);
		return this;
	}

	public IDOQuery appendAndIsNull(String columnName) {
		this.appendAnd();
		this.append(columnName);
		this.append(IS_NULL);
		return this;
	}

	public IDOQuery appendOrIsNull(String columnName) {
		this.appendOr();
		this.append(columnName);
		this.append(IS_NULL);
		return this;
	}

	public IDOQuery appendIsNull() {
		this.append(IS_NULL);
		return this;
	}

	public IDOQuery appendAndIsNotNull(String columnName) {
		this.appendAnd();
		this.append(columnName);
		this.append(IS_NOT_NULL);
		return this;
	}

	public IDOQuery appendOrIsNotNull(String columnName) {
		this.appendOr();
		this.append(columnName);
		this.append(IS_NOT_NULL);
		return this;
	}

	public IDOQuery appendIsNotNull() {
		this.append(IS_NOT_NULL);
		return this;
	}

	/**
	 *
	 * @return query appended with SQL JOIN;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public IDOQuery appendJoin() {
		return append(JOIN);
	}

	/**
	 *
	 * @param selectedEntityColumn is column name of {@link IDOEntity}
	 * to filter by, not <code>null</code>;
	 * @param joinedEntityColumn is column name of {@link IDOEntity}
	 * to be matched by, not <code>null</code>;
	 * @return query appended with SQL ON selectedEntityColumn = joinedEntityColumn
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public IDOQuery appendOnEquals(String selectedEntityColumn, String joinedEntityColumn) {
		return append(ON).appendEquals(selectedEntityColumn, joinedEntityColumn);
	}

	/**
	 *
	 * @param selectedEntityColumn is column name of {@link IDOEntity}
	 * to filter by, not <code>null</code>;
	 * @param joinedEntityColumn is column name of {@link IDOEntity}
	 * to be matched by, not <code>null</code>;
	 * @return query appended with SQL ON selectedEntityColumn != joinedEntityColumn
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public IDOQuery appendOnNotEquals(String selectedEntityColumn, String joinedEntityColumn) {
		return append(ON).appendNotEquals(selectedEntityColumn, joinedEntityColumn);
	}

	/**
	 * Appends a condition where date column specified is between the provided dates
	 * exluding the provided dates (see appendWithinDates for included dates)
	 * @param dateColumnName
	 * @param fromDate
	 * @param toDate
	 * @return the query itself
	 */
	public IDOQuery appendBetweenDates(String dateColumnName,Date fromDate,Date toDate){
		this.append(dateColumnName).appendGreaterThanSign().append(fromDate);
		this.appendAnd().append(dateColumnName).appendLessThanSign().append(toDate);
		return this;
	}

	/**
	 * Appends a condition where timestamp column specified is within the provided timestamps
	 * including the provided timestamps (see appendWithinStamps for included dates)
	 * @param dateColumnName
	 * @param fromStamp
	 * @param toStamp
	 * @return
	 */
	public IDOQuery appendBetweenStamps(String dateColumnName,Timestamp fromStamp,Timestamp toStamp){
		this.append(dateColumnName).appendGreaterThanSign().append(fromStamp);
		this.appendAnd().append(dateColumnName).appendLessThanSign().append(toStamp);
		return this;
	}
	/**
	 * Appends a condition where date column specified is within the provided dates
	 * including the provided dates (see appendBetweenDates for excluded dates)
	 * @param dateColumnName
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public IDOQuery appendWithinDates(String dateColumnName,Date fromDate,Date toDate){
		this.append(dateColumnName).appendGreaterThanOrEqualsSign().append(fromDate);
		this.appendAnd().append(dateColumnName).appendLessThanOrEqualsSign().append(toDate);
		return this;
	}
	/**
	 * Appends a condition where timestamp column specified is within the provided timestamps
	 * including the provided timestamps (see appendBetweenDates for excluded timestamp)
	 * @param dateColumnName
	 * @param fromStamp
	 * @param toStamp
	 * @return
	 */
	public IDOQuery appendWithinStamps(String dateColumnName,Timestamp fromStamp,Timestamp toStamp){
		this.append(dateColumnName).appendGreaterThanOrEqualsSign().append(fromStamp);
		this.appendAnd().append(dateColumnName).appendLessThanOrEqualsSign().append(toStamp);
		return this;
	}
	public IDOQuery appendWhereEqualsTimestamp(String dateColumnName, Timestamp stamp) {
		appendWhere(dateColumnName);
		this.appendEqualSign();
		this.append(stamp);
		return this;
	}

	/**
	 * Append condition for periods columns overlapping provided start and end date
	 * where the following criterions on a entry period are used:
	 *
	 * 1. Starts before start date and ends befor end date
	 *	2. Starts before start date and ends after end date
	 *	3. Starts after start date and ends before end date
	 *	4. Starts after start date and 2ends after end date
	 *
	 * @param validFromColumnName
	 * @param validToColumnName
	 * @param start
	 * @param end
	 * @return
	 */
	public IDOQuery appendOverlapPeriod(String validFromColumnName,String validToColumnName,Date start,Date end){
		String before = IDOQuery.LESS_THAN_OR_EQUAL_SIGN;
		String after = IDOQuery.GREATER_THAN_OR_EQUAL_SIGN;
		/*
		 * 1) starts before selected period, but end within
		 	2) start  before selected period, but end afterwards
		 	3) starts and end within selected period
		 	4) starts witin selected period, but end afterwards

		 		 validFrom <= start && validTo <= end
		 		 or
		 		 validFrom <= start && validTo >= end
		 		 or
		 		 validFrom >= start && validTo <= end
		 		 or
		 		 validFrom >= start && validTo >= end

		 		 // refined version by aron 24.02.04
		 		 validTo >= start && validTo <= end
		 		 or
		 		 validFrom >= start && validFrom <= end
		 		 or
		 		 validFrom <= start && validTo >= end
		 */
	/*
		append("(");
			append("(");
				append(validFromColumnName).append(before).append(start);
				appendAnd();
				append(validToColumnName).append(before).append(end);
				append(")");
		appendOr();
			append("(");
				append(validFromColumnName).append(before).append(start);
				appendAnd();
				append(validToColumnName).append(after).append(end);
			append(")");
		appendOr();
			append("(");
				append(validFromColumnName).append(after).append(start);
				appendAnd();
				append(validToColumnName).append(before).append(end);
			append(")");
		appendOr();
			append("(");
				append(validFromColumnName).append(after).append(start);
				appendAnd();
				append(validToColumnName).append(after).append(end);
			append(")");
		append(")");
		*/
		append("(");
			append("(");
				append(validToColumnName).append(after).append(start);
				appendAnd();
				append(validToColumnName).append(before).append(end);
			append(")");
		appendOr();
			append("(");
				append(validFromColumnName).append(after).append(start);
				appendAnd();
				append(validFromColumnName).append(before).append(end);
			append(")");
		appendOr();
			append("(");
				append(validFromColumnName).append(before).append(start);
				appendAnd();
				append(validToColumnName).append(after).append(end);
			append(")");
		append(")");
		return this;
	}

	public IDOQuery setToCount() {
		if (this._buffer != null) {
			String queryInUpperCase = this._buffer.toString().toUpperCase();
			int index = queryInUpperCase.indexOf(" FROM ");

			if (index > 0) {
				this._buffer.replace(0, index, IDOQuery.SELECT_COUNT);

			}

			queryInUpperCase = this._buffer.toString();
			int index2 = queryInUpperCase.indexOf(" ORDER BY ");

			if (index2 >0) {
				this._buffer = this._buffer.replace(index2,this._buffer.length(),"");
			}

		}
		return this;
	}

	public String setInPlaceHolder(Object value){
	    if (value != null) {
			this.objectValues.add(value);
		}
	    return QUESTIONMARK;
	}

	public IDOQuery appendPlaceHolder(Object value){
	    this.append(QUESTIONMARK);
	    this.objectValues.add(value);
	    return this;
	}

	protected List<Object> getObjectValues(){
	    return this.objectValues;
	}

	protected void setDataStore(DatastoreInterface datastore){
		this.dataStore = datastore;
	}

	protected DatastoreInterface getDatastore(){
		if(this.dataStore==null) {
			this.dataStore = DatastoreInterface.getInstance();
		}
		return this.dataStore;
	}

	@Override
	public Object clone() {
		IDOQuery clone = null;
		try {
			clone = (IDOQuery)super.clone();
			clone._buffer = new StringBuffer(this.toString());
			clone.dataStore = this.dataStore;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return clone;
	}
}